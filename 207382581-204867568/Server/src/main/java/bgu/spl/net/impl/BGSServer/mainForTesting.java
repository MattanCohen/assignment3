package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;

import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class mainForTesting {

public static void main(String[]args){

    // @inv short==string
//    testShortToStringStringToShort((short)3,"3");

    // @inv command contains opCode as first word
//    testExtractOpcodeFromString("POST HEY HRU");

    // @inv command contains opCode as first word
//    testExtractOpcodeAsShortFromString("BLOCK HEY IR LOL FISEL?");

    // @inv command contains opCode as first word
//    testExtractOpcodeAsShortFromString("REGISTER");

    // @inv command contains opCode as first word
//    testRemoveOpcodeFromString("PM Nir that he is an ahla gever");

    // @inv command contains opCode as first word
//    testRemoveOpcodeFromString("LOGSTAT");

    // @inv command ~ "NOTIFICATION"_"0/1"_"postingUserString"_"contentString"
    // @inv command ~ "ERROR"_"ErroredMessageOpCode"
    // @inv command ~ "ACK"_"Optional"
//    String command="ERROR NOTIFICATION";  //desired answer: ";911"
//    testEncode(command);
//    String command2="NOTIFICATION 1 ROI HOWAERYOU";  //desired answer: ";911"
//    testEncode(command2);
    String command3="ACK 2";  //desired answer: ";911"
    testEncode(command3);

//
//    String command="2Mattan023212377aA01;"; //desired answer:
//    StringBuilder flipper=new StringBuilder(command);
//    flipper.reverse();
//    testDecode(Convertor.stringToBytes(flipper.toString()));

}


/**
 BGSEncoderDecoterTests
 */

    /**
     *
     * @param command is a command as given to notify someone
     * @inv command ~ "NOTIFICATION"_"0/1"_"postingUserString"_"contentString"
     */
    private static void testEncode(String command){
    System.out.println("----------------------testEncode--------------------------");
    System.out.println("original command: "+command);
    //get the byte array from encode
    byte[] fromFunction= BGSEncoderDecoderDecodeTest(command);
    System.out.println("a byte array has been created from the function \"encode(original command)\" in *BGSEncoderDecoder class*.");
    System.out.println();
    System.out.println("array from function written in Bytes: ");
    for (byte b: fromFunction)
        System.out.print(b);
    System.out.println();
    System.out.println("array from function written in String: ");
    //if the second (BIG ENDIAN) byte is 9, print notification
    if ((char)(fromFunction[fromFunction.length-2])==(byte)(9))
        printNotificationTest(fromFunction);
        //print error
    else if (fromFunction[fromFunction.length-2]==(byte)(11))
        printErrorTest(fromFunction);
    System.out.println();
}
    public static byte[] BGSEncoderDecoderDecodeTest(String command) {
        //get opCode:
        switch (Convertor.extractOpcodeAsShortFromString(command)){
            //if command is NOTIFICATION
            case 9:
                return testNotificationToBytes(command);
            // if command is ACK
            case 10:
                return testAckToBytes(command);
            //if command is ERROR
            case 11:
                return testErrorToBytes(command);
            default: return null;
        }
    }
    private static byte [] testNotificationToBytes(String command) {
        BGSEncoderDecoder prot=new BGSEncoderDecoder();
        return prot.notificationToBytes(command);
    }
    public static void printNotificationTest(byte[] fromFunction){
        byte [] opCodeExtract={fromFunction[fromFunction.length-1],fromFunction[fromFunction.length-2]};
        short opCode= Convertor.bytesToShort(opCodeExtract);
        LinkedList<Byte> fromFunctionInList=Convertor.byteArrayToLinkedList(fromFunction);
        fromFunctionInList.removeLast();
        fromFunctionInList.removeLast();
        opCodeExtract=Convertor.linkedListToByteArray(fromFunctionInList);
        System.out.println(Convertor.bytesToString(opCodeExtract)+Convertor.shortToString(opCode));
    }

    /**
     *
     * @param command
     * @return
     */
    public static byte[] testAckToBytes(String command){
        BGSEncoderDecoder prot=new BGSEncoderDecoder();
        return prot.ackToBytes(command);
    }

    /**
     * @inv command  ~ "ERROR"_"ErroredMessageOpcode"
     * @param command satisfies @inv
     * @return byte array ~ ';' + 'ErroredMessageOpCodeAsShort' + 'ErrorOpCode'
     *                      ;   +               x               +       11
     */
    public static byte[] testErrorToBytes(String command){
        BGSEncoderDecoder prot=new BGSEncoderDecoder();
        return prot.errorToBytes(command);
    }
    public static void printErrorTest(byte [] b){
        LinkedList<Byte> toPrint=Convertor.byteArrayToLinkedList(b);
        //extract opCode from b
        byte[] opCodeArr= {toPrint.get(toPrint.size()-1),toPrint.get(toPrint.size()-2)};
        short opCode=Convertor.bytesToShort(opCodeArr);
        toPrint.removeLast();
        toPrint.removeLast();
        //extract errored message opCode from b
        byte[] ErrprOpCodeArr= {toPrint.get(toPrint.size()-1),toPrint.get(toPrint.size()-2)};
        short erroredOpCode=Convertor.bytesToShort(ErrprOpCodeArr);
        toPrint.removeLast();
        toPrint.removeLast();
        System.out.println(Convertor.bytesToString(toPrint)+""+erroredOpCode+""+opCode);
    }

    /**
     *
     * @param bytes
     */
    private static void testDecode(byte[] bytes){
        System.out.println("----------------------testDecode--------------------------");
        System.out.println("Original command in bytes: "+bytes);
        System.out.println("Original command in raw String: "+Convertor.bytesToString(bytes));
        String command=testTranslateBytes(bytes);
        System.out.println("Flipped command in String: "+command);

        //get the opCode of the message
        short shortOpCode=Convertor.stringAsBytesToOpcode(command.toString().substring(0,1));
        String opCode=Convertor.opcodeToString(shortOpCode);

        System.out.println("OPcode of the message: "+'\n'+
                '\t'+   '\t'+"as String- "+opCode+'\n'+
                '\t'+   '\t'+"as Short- "+shortOpCode);


    }
    private static String testTranslateBytes(byte[] bytes){
        //make flippedCommand to be the message we ought to do ordered BIG ENDIAN
        String flippedCommand=Convertor.bytesToString(bytes);
        //save the command as ans and flip it
        StringBuilder ans=new StringBuilder(flippedCommand);
        ans.reverse();
        //return reversed and translated bytes message as normal String
        return ans.toString();
    }


    /**
     * ConvertorTests
     */
    /**
     *
     * @param ff the short to check
     * @param string the string to check
     * @inv ff == string (as in string is a short in string type and vice versa
     */
    private static void testShortToStringStringToShort(short ff, String string){
        System.out.println("----------------------testShortToStringStringToShort--------------------------");
        System.out.println("short: "+ff+" string: "+string);
        System.out.println("is shortToString(short) == string ?");
        System.out.println(Convertor.shortToString(ff).equals(string));
        System.out.println("is stringToShort(string) == short ?");
        System.out.println(Convertor.stringToShort(string)==ff);
        System.out.println();
    }

    public static void testExtractOpcodeFromString (String string){
        System.out.println("----------------------testExtractOpcodeFromString--------------------------");
        System.out.println("original string: "+string);
        System.out.println("extracted short: "+ Convertor.extractOpcodeAsShortFromString(string));
        System.out.println();
    }

    public static void testRemoveOpcodeFromString (String string){
        System.out.println("----------------------testRemoveOpcodeFromString--------------------------");
        System.out.println("original string: "+string);
        System.out.println("string without opCode: "+ Convertor.removeOpcodeFromString(string));
        System.out.println();
    }

    public static void testExtractOpcodeAsShortFromString (String string){
        System.out.println("----------------------testExtractOpcodeAsShortFromString--------------------------");
        System.out.println("original string: "+string);
        System.out.println("string without opCode: "+ Convertor.extractOpcodeAsShortFromString(string));
        System.out.println();
    }


}
