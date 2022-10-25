package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class LogoutReq extends Message {

    public LogoutReq(){
        super((short)3);
    }

    @Override
    public Object[] getTemplate() {
        return new Object[0];
    }
}
