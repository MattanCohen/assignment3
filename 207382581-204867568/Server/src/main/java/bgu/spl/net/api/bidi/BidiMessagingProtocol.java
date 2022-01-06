package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.bidi.ConnectionHandler;

public interface BidiMessagingProtocol<T> {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, Connections<T> connections);

	void process(T message);

	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();

    void addHandler(ConnectionHandler<T> handler);
}
