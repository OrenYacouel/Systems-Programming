package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Future;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.Model;
import bgu.spl.mics.application.objects.Student;

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
    private Student myStudent;
    private int ticksPassed;
    private Model currModel;
    private Future currFuture;

    public StudentService(String name, Student _student) {
//      from reading the json we know the student's name is "name"
        super(name);
        myStudent = _student;
        currModel = null;
        currFuture = null;
        ticksPassed = 1;
    }

    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                ticksPassed++;
                if (currModel == null ){
                    if(myStudent.getModels().size() > 0){ //if this student has models that haven't been trained
                        currModel = myStudent.getModels().poll();
                        TrainModelEvent trainModel = new TrainModelEvent(currModel);
                        currFuture = sendEvent(trainModel); //sends the preTrained model and gets the model trained
                    }
                }
                else if (currModel.getStatus() == Model.Status.TRAINED){
                    if (!myStudent.getTrainedModels().contains(currModel)) {
                        myStudent.getTrainedModels().add(currModel); //TODO notice that this was done before updating of currModel- i think its correct
//                    currModel = (Model) currFuture.get();
                        TestModelEvent testModel = new TestModelEvent(currModel);
                        currFuture = sendEvent(testModel);
                    }

                }
                else if (currModel.getStatus() == Model.Status.TESTED){
                    if (!myStudent.getPublishedModels().contains(currModel)) {
                        if (currModel.getResult() == Model.Result.GOOD) {
                            myStudent.getPublishedModels().add(currModel); //TODO notice that this was done before updating of currModel- i think its correct
//                        currModel = (Model) currFuture.get();
                            PublishResultsEvent publishResult = new PublishResultsEvent(currModel);
                            sendEvent(publishResult);
                        }
                    }
                    currModel = null;
                }
            }
        });

        //publish results
        subscribeBroadcast(PublishConferenceBroadcast.class, new Callback<PublishConferenceBroadcast>() {
            @Override
            public void call(PublishConferenceBroadcast c) {
                for(Model m: c.getGoodModels()) {
                    if(m.getStudent( )== myStudent)
                        myStudent.incrementPublication();
                    else
                        myStudent.incrementPapersRead();
                }
            }
        });


    }
}
