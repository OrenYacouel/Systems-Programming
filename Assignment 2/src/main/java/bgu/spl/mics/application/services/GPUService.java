package bgu.spl.mics.application.services;

import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TestModelEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrainModelEvent;
import bgu.spl.mics.application.objects.Cluster;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.InputReader;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * GPU service is responsible for handling the
 * {@link TrainModelEvent} and {@link TestModelEvent},
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class GPUService extends MicroService {
    GPU myGPU;
    Cluster cluster;
    int trainCounter;
    TrainModelEvent trainModelEvent;
    Model currentGPUModel;

    public GPUService(String name, GPU _gpu) {
        super(name);
        this.myGPU = _gpu;
        this.cluster = Cluster.getInstance();
        this.trainCounter = 0;
    }

    @Override
    protected void initialize() {
        subscribeEvent(TestModelEvent.class, (TestModelEvent event)->{
            Model model = event.getModel();
            myGPU.testModel(model, event.getStudent().getStatus());
            this.complete(event, model);
        });

        subscribeEvent(TrainModelEvent.class, (TrainModelEvent event) ->{
            this.currentGPUModel = event.getModel();
            this.trainModelEvent = event;
            myGPU.prepBatches(currentGPUModel);
            myGPU.initialClusterTransfer();
        });

        subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast)-> {
            trainCounter++;
            if(!cluster.getGpuQueueMap().get(myGPU.getId()).isEmpty() && myGPU.getProcessedDataBatches().size() < myGPU.VRAMCapacity()){

                if(myGPU.getProcessedDataBatches().isEmpty())
                    trainCounter = 0;

                myGPU.takeProcessedDataBatch(myGPU.VRAMCapacity()-myGPU.getProcessedDataBatches().size());
            }
            if(!myGPU.getProcessedDataBatches().isEmpty()) {
                if (trainCounter == myGPU.trainTime()) {
                    myGPU.getProcessedDataBatches().remove();
                    myGPU.trainData();
                    myGPU.sendDataBatches(1);
                    myGPU.upGpuWorkTime(trainCounter);
                    trainCounter = 0;
                }
                if (myGPU.getUnprocessedDataBatches().isEmpty()
                        && myGPU.getProcessedDataBatches().isEmpty()
                        && myGPU.getTrainedDataCounter() >= (myGPU.getModel().getData().getSize())/1000) {

                    myGPU.completeTraining();
                    this.complete(trainModelEvent, currentGPUModel);
                }
            }
        });

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast) -> {
            terminate();
        });
        CRMSRunner.initializeCountLatch.countDown();
    }
}