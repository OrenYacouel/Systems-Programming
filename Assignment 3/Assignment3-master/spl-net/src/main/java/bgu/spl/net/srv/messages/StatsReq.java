package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

import java.util.regex.Pattern;


public class StatsReq extends Message {
    private String[] usernameArr;

    public StatsReq(String _userList){
        super((short) 8);
        usernameArr = _userList.split(Pattern.quote("|")); //TODO: or changed at 06.01 16:25  i hope its OK
    }

    public StatsReq() {
        this("");
    }

    public String[] getUsernameArr() {
        return usernameArr;
    }

    @Override
    public String[] getTemplate() {
        return usernameArr;
    }
}
