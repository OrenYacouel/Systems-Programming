package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class PostReq extends Message {
    private String content;

    public PostReq(String _content){
        super((short) 5);
        content = _content;
    }

    public PostReq() {
        this("");
    }

    public String getContent() {
        return content;
    }

    public String[] getTemplate(){
        return new String[]{content};
    }
}
