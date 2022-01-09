package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.BIDI.Convertor;
import bgu.spl.net.impl.BGSServer.BGSConnctionivity.BGSConnections;
import bgu.spl.net.srv.Server;

import java.util.Arrays;

public class    TPCMain {

    public static void main (String[]args){

        String port=args[0];

//
//        byte [] orig = {0,3};
//        System.out.println("ORIGINAL ARRAY "+Arrays.toString(orig));
//        short shortTOByte = Convertor.bytesToShort(orig);
//        System.out.println("SHORT FROM ORIG "+shortTOByte);
//        System.out.println("CHECK     "+(byte)'0'+" "+(byte)'3');
//        byte [] origToChar = new byte[orig.length];
//        for (int i=0; i<orig.length; i++){
//            origToChar[i] = Byte.toString(orig[i]).toCharArray()[0];
//        }
//
//        System.out.println(Arrays.toString(origToChar));


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
