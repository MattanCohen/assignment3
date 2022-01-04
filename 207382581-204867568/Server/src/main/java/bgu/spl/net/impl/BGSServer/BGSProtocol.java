package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

/*
Opcode Operation
1 Register request (REGISTER)
2 Login request (LOGIN)
3 Logout request (LOGOUT)
4 Follow / Unfollow request (FOLLOW)
5 Post request (POST)
6 PM request (PM)
7 Logged in States request (LOGSTAT)
8 Stats request (STAT)
9 Notification (NOTIFICATION)
10 Ack (ACK)
11 Error (ERROR)
12 Block (BLOCK)
 */
public class BGSProtocol implements BidiMessagingProtocol<String> {

    int conId;
    Connections<String > allClientsServerConnections;
    private boolean shouldTerminate = false;

    /**
     * upon receiving register, make protocol
     * @param connectionId
     * @param connections
     */
    @Override
    public void start(int connectionId, Connections<String> connections) {
        conId=connectionId;
        this.allClientsServerConnections=connections;
    }


    // if msg is "login" and user wasn't logged in (check via connections), add it to connections with conId.incrementAndGet
    // if msg is register, connections.register
    @Override
    public Object process(Object msg) {
        return null;
    }

    public void terminate(){
        try{
            allClientsServerConnections.disconnect(conId);

        }finally {
            shouldTerminate=true;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
