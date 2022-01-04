package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.rci.ObjectEncoderDecoder;
import bgu.spl.net.srv.NonBlockingConnectionHandler;
import bgu.spl.net.srv.Server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class ReactorMain {

    public static void main (String[]args){
        String port=args[0];
        String NumOfThreads=args[1];

        AtomicInteger conId= new AtomicInteger(1);

        Connections<String> handlerConnections=new Connections<String>() {


            HashMap<Integer, NonBlockingConnectionHandler> connections=new HashMap<>();
            HashMap<Integer, LinkedList<Integer>> blockingList=new HashMap<>();
            HashMap<Integer, LinkedList<Integer>> followingList=new HashMap<>();

            /**
             * send is used by the connection handler.
             * when a connection handler wants to send a message to another client, we will have to check
             * if the other client is following the handler (that is sending).
             *
             * @param connectionId - the user IO in connections to send to
             * @param msg - the message we want to transfer (returned beautifully from protocol)
             * @return true if success in sending false otherwise
             */
            @Override
            public boolean send(int connectionId, String msg) {
                return false;
            }


            /**
             * when a handler wants to broadcast a message, all following users
             * should recieve said message.
             *
             * @param msg the message to broadcast to said users
             */
            @Override
            public void broadcast(String msg) {

            }

            /**
             *
             * @param connectionId
             */
            @Override
            public void disconnect(int connectionId) {

            }
        };


        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                7777, //port
                BGSProtocol::new, // the protocol factory
                ObjectEncoderDecoder::new //message encoder decoder factory
        ).serve();

    }

}
