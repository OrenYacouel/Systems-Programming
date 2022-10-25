package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

import java.util.List;

public class PMReq extends Message {
    private String username;
    private String content;
    private String dateTime;

    public PMReq(String _username, String _content, String _dateTime){
        super((short) 6);
        username = _username;
        content = _content;
        dateTime = _dateTime;
    }

    public PMReq() {
        this("", "", "");
    }

    public void censorContent (String censoredContent){
        content = censoredContent;
    }
//    Getters

    public String getContent() {
        return content;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getUsername() {
        return username;
    }


    @Override
    public short getOpcode() {
        return super.getOpcode();
    }

    @Override
    public String[] getTemplate() {
        return new String[]{username, content, dateTime};
    }
}
