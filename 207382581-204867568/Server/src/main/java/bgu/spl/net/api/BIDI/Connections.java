package bgu.spl.net.api.BIDI;

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
