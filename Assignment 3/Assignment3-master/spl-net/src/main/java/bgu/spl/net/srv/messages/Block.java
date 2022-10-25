package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class Block extends Message {
    private String username;

    public Block(String _username){
        super((short) 12);
        username = _username;
    }

    public Block() {
        this("");
    }

    public String getUsername() {
        return username;
    }

    public Object[] getTemplate() {
        return new Object[]{username};
    }



}
