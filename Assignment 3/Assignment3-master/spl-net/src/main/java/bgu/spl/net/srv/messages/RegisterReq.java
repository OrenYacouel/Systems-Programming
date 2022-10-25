package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class RegisterReq extends Message {
    private String userName;
    private String password;
    private String bDay;

    public RegisterReq(String _userName , String _password, String _bDay){
        super((short) 1);
        userName = _userName;
        password = _password;
        bDay = _bDay;
    }

    public RegisterReq() {
        this("", "", "");
    }

    @Override
    public String[] getTemplate() {
        return new String[]{userName, password, bDay};
    }

    //    Getters
    public String getPassword() {
        return password;
    }
    public String getUserName() {
        return userName;
    }
    public String getbDay(){ return bDay;}
}
