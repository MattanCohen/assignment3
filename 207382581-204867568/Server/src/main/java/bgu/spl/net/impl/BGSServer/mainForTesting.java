package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;
import bgu.spl.net.api.MessageEncoderDecoder;

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
//    String command="NOTIFICATION 0 name content";  //desired answer: ";0tnetnoc0eman09"
//    testEncode(command);

    String command="2Mattan023212377aA01;"; //desired answer:
    StringBuilder flipper=new StringBuilder(command);
    flipper.reverse();
    testDecode(Convertor.stringToBytes(flipper.toString()));

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
    byte[] fromFunction= encodeTest(command);
    System.out.println("a byte array has been created from the function \"encode(original command)\" in *BGSEncoderDecoder class*.");
    System.out.println();
    System.out.println("array from function written in Bytes: ");
    for (byte b: fromFunction)
        System.out.print(b);
    System.out.println();
    System.out.println("array from function written in String: ");
    //since the last two bytes represent a single short (and are not singly byte-able, needs to be read by twos)
    byte [] fromFunctionNoOp=new byte [fromFunction.length-2];
    for (int i=0; i< fromFunctionNoOp.length; i++) fromFunctionNoOp[i]=fromFunction[i];
    System.out.print(Convertor.bytesToString(fromFunctionNoOp)+Convertor.extractOpcodeAsShortFromString(command));
    /*
    for (byte b: fromFunction){
        //skip the last two digits to make short later
        if (b!=fromFunction[fromFunction.length-2] & b!=fromFunction[fromFunction.length-1]){
            byte[] B= {b};
            System.out.print(Convertor.bytesToString(B));

        }
    }
    //separate the last two digits to make short USING BIG ENDIAN
    byte[] x={fromFunction[fromFunction.length - 1], fromFunction[fromFunction.length - 2]};
    short twoLastDigits=Convertor.bytesToShort(x);
    //print the short
    System.out.println(String.valueOf(twoLastDigits));
   */
    System.out.println();
}
    private static byte [] encodeTest(String command) {
        LinkedList<Byte> ans = new LinkedList<Byte>();
        //get opCode from the command
        short opcode = Convertor.extractOpcodeAsShortFromString(command);
        //remove opCode from command
        command=Convertor.removeOpcodeFromString(command);
        //push opcode to answer
        ans.addFirst((byte)(opcode<<8));
        ans.addFirst((byte) opcode);
        //get either pm/public (0/1)
        byte pmPublic = (byte) command.charAt(0);
        //add pm or public to answer
        ans.addFirst(pmPublic);
        //the nextIndex we're checking from. right now: after PM/public
        int nextIndex = command.indexOf(' ');
        //remove (PM/public) 0/1 and the space after it from the command
        command = command.substring(nextIndex + 1);
        //get space after username (after removing pm/public)
        nextIndex = command.indexOf(' ');
        //get userName in bytes
        byte[] postingUsername = Convertor.stringToBytes(command.substring(0, nextIndex));
        //remove userName and space after it from command
        command = command.substring(nextIndex + 1);
        //now command only contains contentString.
        //add username to list
        for (byte b : postingUsername)
            ans.addFirst(b);
        //add 0 to inform end of string
        ans.addFirst((byte) '0');
        byte[] content = Convertor.stringToBytes(command);
        //add content to list
        for (byte b : content)
            ans.addFirst(b);
        //add 0 to inform end of string
        ans.addFirst((byte) '0');
        //add ; to inform end of message
        ans.addFirst((byte) ';');
        byte[] ret = new byte[ans.size()];
        for (int i = 0; i < ans.size(); i++)
            ret[i] = ans.get(i);
        return ret;
    }

    private static void testDecode(byte[] bytes){
        System.out.println("----------------------testDecode--------------------------");
        System.out.println("Original command in bytes: "+bytes);
        System.out.println("Original command in String: "+Convertor.bytesToString(bytes));
        String command=translateBytes(bytes);
        System.out.println("Flipped command in String: "+command);

        //get the opCode of the message
        short shortOpCode=Convertor.stringAsBytesToOpcode(command.toString().substring(0,1));
        String opCode=Convertor.opcodeToString(shortOpCode);

        System.out.println("OPcode of the message: "+'\n'+
                '\t'+   '\t'+"as String- "+opCode+'\n'+
                '\t'+   '\t'+"as Short- "+shortOpCode);


    }
    private static String translateBytes(byte[] bytes){
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
