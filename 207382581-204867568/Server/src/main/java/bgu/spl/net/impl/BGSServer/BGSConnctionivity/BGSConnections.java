package bgu.spl.net.impl.BGSServer.BGSConnctionivity;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.HashMap;

public class BGSConnections implements Connections<String> {

    HashMap <Integer, ConnectionHandler<String>> connections;


    @Override
    public boolean send(int connectionId, String msg) {
        return false;
    }

    @Override
    public void broadcast(String msg) {

    }

    @Override
    public void disconnect(int connectionId) {

    }
}
