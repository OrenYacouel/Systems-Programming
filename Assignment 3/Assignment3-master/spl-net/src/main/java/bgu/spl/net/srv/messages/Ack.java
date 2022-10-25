package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class Ack<T extends Message> extends Message {

    protected short msgOpcode;

    public Ack(short _msgOpcode){
        super((short) 10 );
        msgOpcode = _msgOpcode;
    }

    public Object[] getTemplate() {
        return new Short[]{msgOpcode};
    }



}
