package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T>{

    ConcurrentHashMap < Integer, ConnectionHandler > idToConnectionHandler;

    public ConnectionsImpl() {
        idToConnectionHandler = new ConcurrentHashMap<>();
    }


    @Override
    public boolean send(int connectionId, Object msg) {
        if( idToConnectionHandler.get(connectionId) != null ){
            idToConnectionHandler.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(Object msg) {
        for (Map.Entry< Integer, ConnectionHandler> pair : idToConnectionHandler.entrySet() ){
            pair.getValue().send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        idToConnectionHandler.remove(connectionId);
    }

    public void connect(int connectionId, ConnectionHandler handler){
        idToConnectionHandler.putIfAbsent(connectionId, handler);
    }
}
