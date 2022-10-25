package bgu.spl.net.impl;

import bgu.spl.net.api.MsgEncDecImpl;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.srv.DataBase;
import bgu.spl.net.srv.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TPCMain {
    public static void main(String[] args) {
        if (args.length < 1)
            throw new IllegalArgumentException("requires port");
        DataBase dataBase = new DataBase();
        Server.threadPerClient(
                Integer.parseInt(args[0]),
                () -> new BidiMessagingProtocolImpl(dataBase),
                MsgEncDecImpl::new
        ).serve();
    }
}