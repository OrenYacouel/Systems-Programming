package bgu.spl.net.srv.messages;

import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.srv.Message;

public class LoginReq extends Message {
    private String username;
    private String password;
    private String captcha;

    public LoginReq(String _username, String _password, String _captcha){
        super((short) 2);
        username  = _username;
        password = _password;
        captcha = _captcha;
    }

    public LoginReq() {
        this("", "", "");
    }

    public short bytesToShort(Byte[] byteArr){
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public String getUserName() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isCaptchaGood() {
        return captcha.equals("1");
    }

    public String[] getTemplate() {
        return new String[]{username, password, captcha};
    }

}

