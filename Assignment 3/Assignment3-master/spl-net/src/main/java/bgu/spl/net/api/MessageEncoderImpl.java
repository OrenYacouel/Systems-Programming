package bgu.spl.net.api;

import bgu.spl.net.srv.Message;
import bgu.spl.net.srv.messages.FollowAck;
import bgu.spl.net.srv.messages.LogStatAck;
import bgu.spl.net.srv.messages.Error;
import bgu.spl.net.srv.messages.StatsAck;

import java.util.LinkedList;

public class MessageEncoderImpl {
    private Object[] template;
    private LinkedList<Byte> bytesArray = new LinkedList<>();
    private ShortEncDec shortEncoder = new ShortEncDec();
    private CharEncDec charEncoder = new CharEncDec();
    private StringEncDec stringEncoder = new StringEncDec();

    public byte[] encode(Message msg){
        createTemplate(msg);
        byte []opcode = shortEncoder.encode(msg.getOpcode());
        for(byte b: opcode){
            bytesArray.add(b);
        }

        for(Object obj : template){ //goes over the fields of the msg

            if( obj.getClass() == byte.class || obj.getClass() == Byte.class){
                bytesArray.add((byte) obj);
            }

            if( obj.getClass() == char.class || obj.getClass() == Character.class ){
                byte[] encodedChar = charEncoder.encode((Character) obj);
                for( byte b : encodedChar)
                    bytesArray.add(b);
            }

            if(obj.getClass() == short.class || obj.getClass() == Short.class){
                byte[] encodedShort = shortEncoder.encode((Short) obj);
                for( byte b : encodedShort)
                    bytesArray.add(b);
            }

            if(obj.getClass() == String.class){
                byte[] encodedString = stringEncoder.encode((String) obj);
                for( byte b : encodedString)
                    bytesArray.add(b);
            }

            if (obj.getClass() == String[].class) {
                for (String string : (String[]) obj) {
                    byte[] encodedString = stringEncoder.encode(string);
                    for (byte b : encodedString)
                        bytesArray.add(b);
                }
            }
        }

        byte[] encodedMsg = new byte[bytesArray.size()];
        for( int i=0 ; i<bytesArray.size() ; i++)
            encodedMsg[i] = bytesArray.get(i);
        bytesArray = new LinkedList<>(); //cleans the array of the bytes in order to be ready for the next message
        return encodedMsg;

    }

    private void createTemplate(Message message) {
        short opcode = message.getOpcode();
        switch (opcode) {
            case 9:
                template = message.getTemplate();
                break;
            case 10:
                if (message.getClass() == LogStatAck.class)
                    template = message.getTemplate();
                else {
                    if (message.getClass() == FollowAck.class)
                        template = message.getTemplate();
                    else {
                        if (message.getClass() == StatsAck.class)
                            template = message.getTemplate();
                        else
                            template = message.getTemplate();
                    }
                }
                break;
            case 11:
                template = ((Error) message).getTemplate();
        }
    }

}
