package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.io.IOException;
import java.util.HashMap;

/**
 * Allows communi cation between different clients
 * */
public interface Connections<T> {
    /**
     * Allows sending a message to the client with given connectionId
     * */
    boolean send(int connectionId, T msg);

    /**
     * Send message to all active connections
     * */
    void broadcast(T msg);

    /**
     * Remove active connection
     * */
    void disconnect(int connectionId);
}
