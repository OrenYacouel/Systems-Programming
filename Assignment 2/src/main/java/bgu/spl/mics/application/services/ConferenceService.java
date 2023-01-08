package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;
import bgu.spl.mics.application.messages.TerminateBroadcast;

/**
 * Conference service is in charge of
 * aggregating good results and publishing them via the {@link PublishConferenceBroadcast},
 * after publishing results the conference will unregister from the system.
 * This class may not hold references for objects which it is not responsible for.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ConferenceService extends MicroService {

    private final ConfrenceInformation conferenceInfo;
    private int time;

    public ConferenceService(String name , ConfrenceInformation _confInfo) {
        super(name);
        conferenceInfo = _confInfo;
        this.time = 1;
    }

    @Override
    protected void initialize() {

        this.subscribeBroadcast(TerminateBroadcast.class, (TerminateBroadcast) -> {
            terminate();
        });

        subscribeBroadcast(TickBroadcast.class, (tickBroadcast) -> {
            this.increaseTime();
            if(this.getTime() == conferenceInfo.getDate() ){
                this.publish();
                this.terminate();
            }

        });
        subscribeEvent(PublishResultsEvent.class ,
                (publishResultEvent)->{
            conferenceInfo.addToAggregatedModels(publishResultEvent.getModel());

        });
        CRMSRunner.initializeCountLatch.countDown();
    }

    private void publish(){
        this.sendBroadcast(new PublishConferenceBroadcast(conferenceInfo.getAggregatedModels()));
    }
    public int getTime(){
        return time;
    }
    public void increaseTime() {
        time++;
    }
}