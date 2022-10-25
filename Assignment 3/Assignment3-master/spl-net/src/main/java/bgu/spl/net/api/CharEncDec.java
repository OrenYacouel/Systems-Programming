package bgu.spl.net.api;

import java.util.Arrays;

public class CharEncDec implements MessageEncoderDecoder<Character>{

    private byte[] bytes = new byte[2];
    private int len = 0;

    public Character decodeNextByte(byte nextByte){
        if(len == 2){
            len = 0;
            return bytesToChar(bytes);
        }
        pushByte(nextByte);
        return null;
    }

    public byte[] encode(Character msg){
        char[] arr = {msg};
        return new String(arr).getBytes();
    }

    public void pushByte(byte nextByte){
        if( len >= bytes.length){
            bytes = Arrays.copyOf(bytes, len * 2);
            System.out.println("something went wrong decoding bytes to char");
        }

        bytes[len++] = nextByte;
    }

    public char bytesToChar( byte[] byteArr){
        String output = new String(byteArr);
        return output.charAt(0);
    }


}
