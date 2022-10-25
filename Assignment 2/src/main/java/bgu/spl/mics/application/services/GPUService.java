package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.*;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {

    private GPU gpu;

    public GPUService(String name, GPU _gpu) {
        super(name);
        gpu = _gpu;
    }

    @Override
    protected void initialize() {
        Cluster.getInstance().addGpu(this.gpu);

        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                if( gpu.currStatus() == Model.Status.TRAINED ){
                    if (magicBus.getEventToFutureMap().get(gpu.getCurrModel()) != null) { //checks the future of the train model has been updated
                        magicBus.setEventToFutureMap(gpu.getCurrModel()); //if the model is trained meaning we should send the trained model back to the future
                    }

                    if (!gpu.getUnProModels().isEmpty() ){
                        gpu.prepareAndSendFirstBatches();
                    }
                }
                else if(gpu.currStatus() == Model.Status.TRAINING){
                    gpu.tickAction();
                }
            }
        });

        subscribeEvent(TrainModelEvent.class, new Callback<TrainModelEvent>() {
            @Override
            public void call(TrainModelEvent c) { //extract the model, create batches and send to the cluster the batches
                if (gpu.currStatus() == Model.Status.TRAINING) {
                    gpu.getUnProModels().add(c.getModel());
                } else { //means the model is trained or pretrained
                    gpu.setCurrModel(c.getModel());
                    gpu.prepareAndSendFirstBatches();
                }
            }
        });
        subscribeEvent(TestModelEvent.class, new Callback<TestModelEvent>() {
            @Override
            public void call(TestModelEvent c) {
                System.out.println("GPU recevied test");
                c.getModel().setStatus(Model.Status.TESTED);
                double chance = Math.random();
                if (c.getModel().getStudent().getStatus() == Student.Degree.MSc) {
                    if (chance < 0.6) {
                        c.getModel().setResult(Model.Result.GOOD);
                        magicBus.complete(c, Model.Result.GOOD);
                    } else {
                        c.getModel().setResult(Model.Result.BAD);
                        magicBus.complete(c, Model.Result.BAD);
                    }
                } else { // the degree is PhD
                    if (chance < 0.8) {
                        c.getModel().setResult(Model.Result.GOOD);
                        magicBus.complete(c, Model.Result.GOOD);

                    } else {
                        c.getModel().setResult(Model.Result.BAD);
                        magicBus.complete(c, Model.Result.BAD);
                    }
                }
            }
        });
        CRMSRunner.threadCount.countDown();
    }
}


