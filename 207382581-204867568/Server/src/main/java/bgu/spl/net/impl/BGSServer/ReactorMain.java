package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main (String[]args){

        String port=args[0];
        String NumOfThreads=args[1];


        Server.reactor(
                Integer.parseInt(NumOfThreads),//numOfThreads
                Integer.parseInt(port),//port
                BGSProtocol::new, // the protocol factory
                BGSEncoderDecoder::new, //message encoder decoder factory
                new BGSConnections ()).serve();

   }

}



/*
      //                    BGSProtocol prot=new BGSProtocol();
        //                    prot.start(Tools.incrementAndGetConId(),new BGSConnections ());

//        Server.threadPerClient(
//                7777, //port
//                () -> new RemoteCommandInvocationProtocol<>(feed), //protocol factory
//                ObjectEncoderDecoder::new //message encoder decoder factory
//        ).serve();
 */
