package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class Error extends Message {
    protected short msgOpcode;

    public Error(short _msgOpcode){
        super((short) 11);
        msgOpcode = _msgOpcode;
    }

    public Short[] getTemplate() {
        return new Short[]{msgOpcode};
    }

}
