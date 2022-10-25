package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.CRMSRunner;
import bgu.spl.mics.application.messages.PublishConferenceBroadcast;
import bgu.spl.mics.application.messages.PublishResultsEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.ConfrenceInformation;

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

    private ConfrenceInformation conference;
    private int ticksPassed;


    public ConferenceService(String name, ConfrenceInformation _conference) {
        //      from reading the json we know the conference's name is "name"
        super(name);
        ticksPassed = 1;
        conference = _conference;
    }

    @Override
    protected void initialize() {
        ConferenceService me = this;
        subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
            @Override
            public void call(TickBroadcast c) {
                ticksPassed++;
                if(ticksPassed == conference.getPublicationDate()){ //i think that the publicationDate is in ticks not in Millisecs, Itay and Miki agreed
                    if (conference.getGoodModels().size() > 0 ) {
                        sendBroadcast(new PublishConferenceBroadcast(conference.getGoodModels()));
                        magicBus.unregister(me);
                    }
                }
            }
        });

        subscribeEvent(PublishResultsEvent.class, new Callback<PublishResultsEvent>() {
            @Override
            public void call(PublishResultsEvent c) {
                System.out.println("Conf recevied publishResult");
                conference.getGoodModels().add(c.getModel());
            }
        });
        CRMSRunner.threadCount.countDown();
    }
}
