package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class LogStatReq extends Message {

    public LogStatReq(){
        super((short) 7 );
    }

    @Override
    public Object[] getTemplate() {
        return new Object[0];
    }
}
