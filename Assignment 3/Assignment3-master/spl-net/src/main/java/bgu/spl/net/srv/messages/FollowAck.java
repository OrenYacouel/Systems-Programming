package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class FollowAck extends Message {

    protected short msgOpcode;
    private String followName;

    public FollowAck(short _msgOpcode , String _followName){
        super((short) 10 );
        msgOpcode = _msgOpcode;
        followName = _followName;
    }

    @Override
    public Object[] getTemplate() {
        return new Object[]{(short)4, followName};
    }
}
