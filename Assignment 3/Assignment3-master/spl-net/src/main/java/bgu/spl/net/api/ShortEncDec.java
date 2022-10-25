package bgu.spl.net.api;

import java.util.Arrays;

public class ShortEncDec implements MessageEncoderDecoder<Short>{

    private byte[] bytes = new byte[2];
    private int len = 0;

    public Short decodeNextByte(byte nextByte){
        pushByte(nextByte);
        if( len == 2){
            len = 0;
            return bytesToShort(bytes);
        }
        return null;
    }

    public byte[] encode(Short input) {
        byte [] bytesArray = new byte[2];
        bytesArray[0] = (byte) ((input >> 8) & 0xFF);
        bytesArray[1] = (byte) ((input & 0xFF));
        return bytesArray;
    }

        private short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
            System.out.println("something went wrong decoding bytes to short");
        }

        bytes[len++] = nextByte;
    }
}
