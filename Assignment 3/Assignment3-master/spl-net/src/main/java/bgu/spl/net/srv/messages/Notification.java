package bgu.spl.net.srv.messages;

import bgu.spl.net.srv.Message;

public class Notification extends Message {
//    private NotificationType notificationType;
    private byte notificationType;
    private String postingUser;
    private String content;

    enum NotificationType{
        PM, PUBLIC
    }


    public Notification(byte _notificationType, String _postingUser, String _content){
        super((short) 9 );
        this.notificationType = _notificationType;
        this.postingUser = _postingUser;
        this.content = _content;
    }

    public short bytesToShort(Byte[] byteArr){
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    @Override
    public Object[] getTemplate() {
        return new Object[]{notificationType, postingUser, content};
    }
}
