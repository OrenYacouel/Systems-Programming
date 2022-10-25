package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;
import bgu.spl.mics.application.objects.Model;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PublishConferenceBroadcast implements Broadcast {
    private ConcurrentLinkedQueue<Model> goodModels;

    public PublishConferenceBroadcast(ConcurrentLinkedQueue<Model> _goodModels){
        goodModels = _goodModels;
    }

    public ConcurrentLinkedQueue<Model> getGoodModels(){
        return goodModels;
    }
}
