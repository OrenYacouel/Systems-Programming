package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class FollowReq extends Message {

    public FollowReq() {
        this("", "");
    }

    private String followOrUnF;
    private String username;

    public FollowReq(String _follow, String _username){
        super((short) 4);
        followOrUnF = _follow;
        username = _username;
    }

    public short bytesToShort(Byte[] byteArr){
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    public String getFollowOrUnF() {
        return followOrUnF;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String[] getTemplate() {
        return new String[]{followOrUnF, username };
    }
}
