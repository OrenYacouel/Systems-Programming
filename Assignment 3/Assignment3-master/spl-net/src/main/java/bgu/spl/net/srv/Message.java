package bgu.spl.net.srv;

public abstract class Message {
    protected short opcode;

    public Message(short _opcode){
        opcode = _opcode;
    }

    public short getOpcode() {
        return opcode;
    }

    public abstract Object[] getTemplate();

}
