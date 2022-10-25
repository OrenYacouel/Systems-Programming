package bgu.spl.net.api.bidi;

import java.io.IOException;

public interface Connections<T> {

    boolean send(int connectionId, T msg);

    /**
     * go through all the clients and send a message to each of them
     */
    void broadcast(T msg);

    void disconnect(int connectionId);
}
