package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
import bgu.spl.net.srv.Server;

public class TPCMain {

    public static void main (String[]args){

        String port=args[0];


        Server.threadPerClient(
                Integer.parseInt(port),//port
                BGSProtocol::new, // the protocol factory
                BGSEncoderDecoder::new, //message encoder decoder factory
                new BGSConnections()).serve();

    }

}



//
//
//        //                    BGSProtocol prot=new BGSProtocol();
//        //                    prot.start(Tools.incrementAndGetConId(),new BGSConnections ());
//        Server.reactor(
//                Integer.parseInt(NumOfThreads),//numOfThreads
//                Integer.parseInt(port),//port
//                BGSProtocol::new, // the protocol factory
//                BGSEncoderDecoder::new, //message encoder decoder factory
//                new BGSConnections()).serve();


/*
 */
