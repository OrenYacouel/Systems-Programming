package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.Message;

import java.util.List;

public class LogStatAck extends Message {

    protected short msgOpcode;
    private short age;
    private short numPosts;
    private short numFollowers;
    private short numFollowing;

    public LogStatAck(short _msgOpcode , short _age , short _numPosts , short _numFollowers , short _numFollowing){
        super((short) 10 );
        msgOpcode = _msgOpcode;
        age = _age;
        numPosts = _numPosts;
        numFollowers = _numFollowers;
        numFollowing = _numFollowing;
    }

    @Override
    public Object[] getTemplate() {
        return new Object[]{(short) 7, age, numPosts, numFollowers, numFollowing};
    }
}
