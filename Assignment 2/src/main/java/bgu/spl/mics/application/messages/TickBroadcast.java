package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    private String name ="TickBroadcast";

    public TickBroadcast(){}

    public String getName(){
        return name;
    }
}
