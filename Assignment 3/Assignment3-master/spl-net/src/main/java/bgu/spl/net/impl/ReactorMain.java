package bgu.spl.net.impl;
import bgu.spl.net.api.MsgEncDecImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;
public class ReactorMain {
    public static void main(String[] args) {
        if (args.length < 2)
            throw new IllegalArgumentException("requires port and threads number");
        DataBase dataBase = new DataBase();
        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]),
                () -> new BidiMessagingProtocolImpl(dataBase),
                MsgEncDecImpl::new
        ).serve();
    }
}
