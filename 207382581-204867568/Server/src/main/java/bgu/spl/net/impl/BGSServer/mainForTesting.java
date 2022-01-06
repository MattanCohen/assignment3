package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;

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
//    String command="NOTIFICATION 1 ROI HOWAERYOU";  //desired answer: ";911"
//    testEncode(command);

    // @inv command ~ "ERROR"_"ErroredMessageOpCode"
//    String command2="ERROR NOTIFICATION";  //desired answer: ";911"
//    testEncode(command2);

    // @inv command ~ "ACK"_"Optional" or
    // @inv command ~ "ACK"_"FollowOpcodeAsShort"_"Follow/UnfollowIndicator"_"UserName"
//   String command3="ACK 8 47 1 2 0 ACK 8 23 2 4 8";  //desired answer: ";911"
//    testEncode(command3);

     //testDecode

//test Follow
//    LinkedList<Byte> followToConvert=new LinkedList<>();
//    short followOp=4;
//    followToConvert.add((byte)(followOp<<8));
//    followToConvert.add((byte)(followOp));
//    char follow='0';
//    followToConvert.add((byte)follow);
//    followToConvert.addLast((byte)'\0');
//    String followUserName="Ronald McDonald's";
//    for (byte b: Convertor.stringToBytes(followUserName))
//        followToConvert.add(b);
//    followToConvert.addLast((byte)'\0');
//    testDecode(Convertor.linkedListToByteArray(followToConvert));

//test UNFOLLOW
//    LinkedList<Byte> unFollowToConvert=new LinkedList<>();
//    short unFollowOp=4;
//    unFollowToConvert.add((byte)(unFollowOp<<8));
//    unFollowToConvert.add((byte)(unFollowOp));
//    char unFollow='1';
//    unFollowToConvert.add((byte)unFollow);
//    unFollowToConvert.addLast((byte)'\0');
//    String unFollowUserName="MattanKing1234";
//    for (byte b: Convertor.stringToBytes(unFollowUserName))
//        unFollowToConvert.add(b);
//    unFollowToConvert.addLast((byte)'\0');
//    testDecode(Convertor.linkedListToByteArray(unFollowToConvert));

//test Block:
//    LinkedList<Byte> blockToConvert=new LinkedList<>();
//    short blockOp=12;
//    blockToConvert.add((byte)(blockOp<<8));
//    blockToConvert.add((byte)blockOp);
//    String userName="Nir Ahla Gever hallas taazvoo oto BYEEEEEEEEEE";
//    for (byte b: Convertor.stringToBytes(userName))
//        blockToConvert.add(b);
//    blockToConvert.addLast((byte)'\0');
//    testDecode(Convertor.linkedListToByteArray(blockToConvert));

//test LOGOUT:
//    LinkedList<Byte> logoutToConvert=new LinkedList<>();
//    short logoutOp=3;
//    logoutToConvert.add((byte)(logoutOp<<8));
//    logoutToConvert.add((byte)logoutOp);
//    testDecode(Convertor.linkedListToByteArray(logoutToConvert));

//test LOGSTAT:
//    LinkedList<Byte> logstatToConvert=new LinkedList<>();
//    short logstatOp=7;
//    logstatToConvert.add((byte)(logstatOp<<8));
//    logstatToConvert.add((byte)logstatOp);
//    testDecode(Convertor.linkedListToByteArray(logstatToConvert));

//test Stat
//    LinkedList<Byte> statToConvert=new LinkedList<>();
//    LinkedList<Byte> registerToConvert=new LinkedList<>();
//    short registerOp=8;
//    registerToConvert.add((byte)(registerOp<<8));
//    registerToConvert.add((byte)registerOp);
//    String statUserNames="Byonce|Rihana|PoorGirl";
//    for (byte b: Convertor.stringToBytes(statUserNames))
//        statToConvert.addLast(b);
//    statToConvert.addLast((byte)'\0');
//    testDecode(Convertor.linkedListToByteArray(statToConvert));

//test Post
//    LinkedList<Byte> postToConvert=new LinkedList<>();
//    short postOp=5;
//    postToConvert.add((byte)(postOp<<8));
//    postToConvert.add((byte)(postOp));
//    String postContent="Zevel Rezini Noder";
//    for (byte b: Convertor.stringToBytes(postContent))
//        postToConvert.add(b);
//    postToConvert.add((byte)'\0');
//
//    testDecode(Convertor.linkedListToByteArray(postToConvert));


//test LOGIN
//    LinkedList<Byte> loginToConvert=new LinkedList<>();
//    short loginOp=2;
//    loginToConvert.add((byte)(loginOp<<8));
//    loginToConvert.add((byte)(loginOp));
//
//    String loginUserName="MrPickles";
//    for (byte b: Convertor.stringToBytes(loginUserName))
//        loginToConvert.add(b);
//    loginToConvert.add((byte)'\0');
//
//    String loginPassword="%%666%%";
//    for (byte b: Convertor.stringToBytes(loginPassword))
//        loginToConvert.add(b);
//    loginToConvert.add((byte)'\0');
//
//    char loginCaptcha='1';
//    loginToConvert.add((byte)loginCaptcha);
//
//    testDecode(Convertor.linkedListToByteArray(loginToConvert));

//test PM
//    LinkedList<Byte> pmToConvert=new LinkedList<>();
//    short pmOp=6;
//    pmToConvert.add((byte)(pmOp<<8));
//    pmToConvert.add((byte)(pmOp));
//
//    String pmUserName="MattanKing15";
//    for (byte b: Convertor.stringToBytes(pmUserName))
//        pmToConvert.add(b);
//    pmToConvert.add((byte)'\0');
//
//    String pmContent="hey ani mekave she ata pseder";
//    for (byte b: Convertor.stringToBytes(pmContent))
//        pmToConvert.add(b);
//    pmToConvert.add((byte)'\0');
//
//    String pmTime="15/04/2022";
//    for (byte b: Convertor.stringToBytes(pmTime))
//        pmToConvert.add(b);
//    pmToConvert.add((byte)'\0');
//
//    testDecode(Convertor.linkedListToByteArray(pmToConvert));

//test Register
//    LinkedList<Byte> registerToConvert=new LinkedList<>();
//    short registerOp=1;
//    registerToConvert.add((byte)(registerOp<<8));
//    registerToConvert.add((byte)registerOp);
//    String username="babydeathmetal";
//    for(byte b: Convertor.stringToBytes(username))
//        registerToConvert.add(b);
//    registerToConvert.add((byte)'\0');
//    String password ="666";
//    for(byte b: Convertor.stringToBytes(password))
//        registerToConvert.add(b);
//    registerToConvert.add((byte)'\0');
//    String birthday="16-6-1666";
//    for (byte b: Convertor.stringToBytes(birthday))
//        registerToConvert.add(b);
//    registerToConvert.add((byte)'\0');
//    testDecode(Convertor.linkedListToByteArray(registerToConvert));

//test post in BGSprotocol
    String command7="POST heyimyourmom";
    testBGSProtocol(command7.split(" "));
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
    System.out.print("[");
    for (byte b: fromFunction){
        System.out.print(b);
        System.out.print(',');
    }
    System.out.print("]");
    System.out.println();
//    System.out.println("is found ack? "+(fromFunction[fromFunction.length-2]==(byte)(10)));
    System.out.println("array from function written in String: ");
    //if the second (BIG ENDIAN) byte is 9, print notification
    if ((char)(fromFunction[fromFunction.length-2])==(byte)(9))
        printNotificationTest(fromFunction);
    //print ACK
    else if (fromFunction[fromFunction.length-2]==(byte)(10))
        printAckTest(fromFunction);
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
    public static void printAckTest(byte [] b){
        LinkedList<Byte> toPrint=Convertor.byteArrayToLinkedList(b);
        while (toPrint.size()>1){
            //extract short from b
            byte[] opCodeArr= {toPrint.get(toPrint.size()-1),toPrint.get(toPrint.size()-2)};
            short opCode=Convertor.bytesToShort(opCodeArr);
            toPrint.removeLast();
            toPrint.removeLast();
            //extract errored message opCode from b
            System.out.print(opCode);
        }
        System.out.println(Convertor.bytesToString(Convertor.linkedListToByteArray(toPrint)));
    }


    /**
     *
     * @param bytes
     */
    private static void testDecode(byte[] bytes){
        System.out.println("----------------------testDecode--------------------------");
        System.out.println("Original command in ugly bytes: ");
        LinkedList<Byte> toPrint=Convertor.byteArrayToLinkedList(bytes);
        System.out.print("[");
        while (toPrint.size()>1){
            System.out.print(toPrint.removeLast()+",");
        }
        System.out.print("]");
        System.out.println();

        //-----------from here the function is the same as in BGSEncoder Decoder
        BGSEncoderDecoder encDec=new BGSEncoderDecoder();
        String ans=encDec.decodeBytes(bytes);
        //----------------------
        System.out.println("Beautiful string for protocol: "+'\n'+ans);
        System.out.println();
    }

//    private static String testTranslateBytes(byte[] bytes){
//        //make flippedCommand to be the message we ought to do ordered BIG ENDIAN
//        String flippedCommand=Convertor.bytesToString(bytes);
//        //save the command as ans and flip it
//        StringBuilder ans=new StringBuilder(flippedCommand);
//        ans.reverse();
//        //return reversed and translated bytes message as normal String
//        return ans.toString();
//    }


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


    /**
     * BGSProtocolTests
     */
    public static void testBGSProtocol(String[] msg){
        System.out.println("----------------------testBGSProtocol--------------------------");
        System.out.println("             ---------test "+msg[0]+"---------");
        System.out.println("original message in array: "+msg);
        String originalCommand="";
        for (String s: msg){
            originalCommand+=s+" ";
        }
        System.out.println("original message in String: "+originalCommand);
//        Object ans= (new BGSProtocol()).process(msg);
//        System.out.println();
//        System.out.println("response from protocol: "+(String)ans);
        System.out.println();
    }
}
