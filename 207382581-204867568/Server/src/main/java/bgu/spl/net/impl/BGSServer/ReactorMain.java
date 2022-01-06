package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
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



        //                    BGSProtocol prot=new BGSProtocol();
        //                    prot.start(Tools.incrementAndGetConId(),new BGSConnections ());
        Server.reactor(
                Integer.parseInt(NumOfThreads),//numOfThreads
                Integer.parseInt(port),//port
                BGSProtocol::new, // the protocol factory
                BGSEncoderDecoder::new, //message encoder decoder factory
                new BGSConnections ()).serve();

    }

}
