package bgu.spl.net.api.bidi;

import java.io.IOException;

//should map a unique ID for each active client connceted to the server. part of the server pattern
public interface Connections<T> {

    //sends a message T to client represented by the given con id
    boolean send(int connectionId, T msg);

    //sends a message T to all active clients
    void broadcast(T msg);

    //remove client con id if active
    void disconnect(int connectionId);

}
