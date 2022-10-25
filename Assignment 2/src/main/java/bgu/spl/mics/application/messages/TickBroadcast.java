package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TickBroadcast implements Broadcast {
    int tickId;

    public TickBroadcast(int _TickId){
        tickId = _TickId;
    }

    public int getId(){
        return tickId;
    }
}
