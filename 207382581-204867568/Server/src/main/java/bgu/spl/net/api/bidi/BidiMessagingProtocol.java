package bgu.spl.net.api.bidi;

import bgu.spl.net.api.MessagingProtocol;

public interface BidiMessagingProtocol<T>  extends MessagingProtocol {
	/**
	 * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
	**/
    void start(int connectionId, Connections<T> connections);
    
	/**
     * @return true if the connection should be terminated
     */
    boolean shouldTerminate();
}
