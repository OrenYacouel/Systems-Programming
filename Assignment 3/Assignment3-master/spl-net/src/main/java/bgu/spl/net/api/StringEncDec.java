package bgu.spl.net.api;

import java.util.Arrays;
import java.nio.charset.StandardCharsets;


public class StringEncDec implements MessageEncoderDecoder<String>{

    private byte[] bytes = new byte[1 << 10];
    private int len = 0;

    public String decodeNextByte(byte nextByte){
        if ( nextByte == '\0'){
            return popString();
        }

        pushByte(nextByte);
        return null;
    }

    public byte[] encode(String msg){
        return ( msg + "\0").getBytes();
    }

    private void pushByte(byte nextByte){
        if( len >= bytes.length){
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }

    private String popString(){
        String output = new String(bytes, 0, len, StandardCharsets.UTF_8);
        len = 0;
        return output;
    }
}
