package bgu.spl.mics.application.services;
import bgu.spl.mics.application.objects.GPU;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;
import java.util.LinkedList;

/**
 * Student is responsible for sending the {@link TrainModelEvent},
 * {@link TestModelEvent} and {@link PublishResultsEvent}.
 * In addition, it must sign up for the conference publication broadcasts.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class StudentService extends MicroService {
        Student student;
        LinkedList<GPU> gpuList = new LinkedList<>();

    public StudentService(String name, Student student, LinkedList<GPU> _gpuList) {
        super(name);
        this.student = student;
        gpuList = _gpuList;
    }

    @Override
    protected void initialize() {

        this.subscribeBroadcast(TickBroadcast.class, (TickBroadcast broadcast) -> {

            if(student.getCurrentModel() != null) {
                Model currentModel = student.getCurrentModel();

                if (currentModel.getStatus().equals(Model.Status.PreTrained)) {
                    if(checkForGPU()) {
                        student.setResolvedFuture(sendEvent(new TrainModelEvent(student.getCurrentModel())));
                        student.getCurrentModel().setStatus(Model.Status.Training);
                    }
                }
                else if (student.getResolvedFuture().isDone()) {
                    if (currentModel.getStatus().equals(Model.Status.Trained)) {
                        student.setResolvedFuture(sendEvent(new TestModelEvent(student.getCurrentModel(), student)));
                    }
                    else if (currentModel.getStatus().equals(Model.Status.Tested)) {
                        if (currentModel.getResult().equals(Model.Result.Good)) {
                            this.sendEvent(new PublishResultsEvent(currentModel, student));
                            student.addToPublishedNames(currentModel.getName());
                            student.increasePublications();
                        }
                        student.updateCurrentModelIndex();
                    }
                }
            }
            else {
                terminate();
            }
        });

        this.subscribeBroadcast(PublishConferenceBroadcast.class, (confBroadMsg)->{
            LinkedList<String> publishedModels = confBroadMsg.modelListByName();
            for(String modelName : publishedModels){
                if(!student.getPublishedNamesList().contains(modelName)){
                    student.increasePapersRead();
                }
            }
        });

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast) -> {
            terminate();
        });
        CRMSRunner.initializeCountLatch.countDown();
    }
    private boolean checkForGPU(){
        for(GPU gpu: gpuList) {
            if (gpu.tryToReserve())
                return true;
        }
        return false;
        }
    }