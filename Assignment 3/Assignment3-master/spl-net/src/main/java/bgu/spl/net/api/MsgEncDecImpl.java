package bgu.spl.net.api;

import bgu.spl.net.srv.Message;

public class MsgEncDecImpl implements MessageEncoderDecoder<Message>{

    private MessageDecoderImpl decoder = new MessageDecoderImpl();
    private MessageEncoderImpl encoder = new MessageEncoderImpl();


    @Override
    public Message decodeNextByte(byte nextByte) {
        return decoder.decodeNextByte(nextByte);
    }

    @Override
    public byte[] encode(Message message) {
        return encoder.encode(message);
    }
}
