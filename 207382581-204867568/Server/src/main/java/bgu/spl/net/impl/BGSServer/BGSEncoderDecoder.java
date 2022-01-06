package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.BIDI.Convertor;
import bgu.spl.net.api.MessageEncoderDecoder;

import java.util.Arrays;
import java.util.LinkedList;

public class BGSEncoderDecoder implements MessageEncoderDecoder<String> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0;


    /**
     * The server can receive any message for our concern.
     * @inv every message ends with and only with ';'
     * @param nextByte the next byte to consider for the current message we're decoding
     * @return the command translated from bytes to string for the protocol to process
     */
    @Override
    public String decodeNextByte(byte nextByte) {
        //if that byte ends, command now has all information.
        if (nextByte==';'){
            String ans=decodeBytes(bytes);
            resetBytes();
            return ans;
        }
        //if nextByte isn't ';', add nextByte to bytes array and return null (message is still readeded)
        pushByte(nextByte);
        return null;
    }
    public String decodeBytes(byte[] byteArr){
        String ans="";
        //get the opCode of the message
        byte [] opCodeArr={byteArr[0],byteArr[1]};
        short opCode=Convertor.bytesToShort(opCodeArr);
        //get opCode as string as well
        String opCodeString=Convertor.opcodeToString(opCode);
        //create temporary linked list as bytes
        LinkedList<Byte> bytesAsLinkedList=Convertor.byteArrayToLinkedList(byteArr);
        //remove opccode from temp
        bytesAsLinkedList.removeFirst();
        bytesAsLinkedList.removeFirst();;
        switch (opCode){
            // incase command is REGISTER
            case 1:
                ans = handleRegister(opCodeString,bytesAsLinkedList);
                break;
            // incase command is LOGIN
            case 2:
                // nir handeling login
                ans = handleLogin(opCodeString, bytesAsLinkedList);
                break;
            // incase command is LOGOUT or LOGSTAT
            case 3: case 7:
            {
                ans=opCodeString;
                break;
            }
            // incase command is FOLLOW
            case 4:
                ans = handleFollow(opCodeString,bytesAsLinkedList);
                break;
            // incase command is POST
            case 5:{
                LinkedList<Byte> contentNameBytes=new LinkedList<>();
                while (bytesAsLinkedList.size()!=0){
                    Byte b=bytesAsLinkedList.removeFirst();
                    contentNameBytes.add(b);
                    if (b=='\0') {
                        break;
                    }
                }
                String content=Convertor.bytesToString(contentNameBytes);

                ans=opCodeString+" "+content;
                break;
            }

            // incase command is PM
            case 6:
                ans = handlePm(opCodeString,bytesAsLinkedList);
                break;
            // incase command is STAT
            case 8: {
                // bytesAsLinkedList = <reversedusernamelist UTF8>0
                bytesAsLinkedList.removeLast();
                // convert byte list to string of reversed user names
                byte[] UserNameBytes = Convertor.linkedListToByteArray(bytesAsLinkedList);
                String userNames = Convertor.bytesToString(UserNameBytes);

                ans = opCodeString+" "+userNames;
                break;
            }
            // incase command is Block
            case 12:{
                String userToBlock=Convertor.bytesToString(bytesAsLinkedList);
                ans=opCodeString+" "+userToBlock;
                break;
            }
            // if command opCode isn't legal
            default:
                System.out.println("hey, this command isn't legal! wtf????");


        }

        return ans;
    }

    /**
     * add nextByte to bytes. if len >= bytes.len then duplicate bytes capacity
     * @inv len < bytes.len
     * @param nextByte byte to add to bytes
     */
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }

        bytes[len++] = nextByte;
    }
//    /**
//     * The server recieves all Client-To-Server commands.
//     * @inv when reaching this function, bytes is ready to be read (meaning the current message has finished
//     *      readiung meaning reached ';')
//     * @inv every message most significant value index and one index before it represents a short for opCode
//     *      opCode options:
//                         *Opcode        Operation
//                         * 1        Register request (REGISTER)
//                         * 2        Login request (LOGIN)
//                         * 3        Logout request (LOGOUT)
//                         * 4        Follow / Unfollow request (FOLLOW)
//                         * 5        Post request (POST)
//                         * 6        PM request (PM)
//                         * 7        Logged in States request (LOGSTAT)
//                         * 8        Stats request (STAT)
//                         * 12       Block (BLOCK)
//     *
//     * The server has to return Ack or Error in order for the client to continue running
//     * @inv every message that finished reading receives send(Ack) or send(Error)
//     * @return the decoded task represented in String for protocol
//     */
//    private String translateBytes(){
//        //make flippedCommand to be the message we ought to do ordered BIG ENDIAN
//        String flippedCommand=Convertor.bytesToString(bytes);
//        //save the command as ans and flip it
//        StringBuilder ans=new StringBuilder(flippedCommand);
//        ans.reverse();
//        resetByte();
//        //return reversed and translated bytes message as normal String
//         return ans.toString();
//    }
    public void resetBytes(){
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
    }

    public String handleRegister(String opCodeString,LinkedList<Byte> bytes){
        // linked list for each field, '\0' as delimiter
        LinkedList<Byte> usernameBytesL = new LinkedList<>();
        LinkedList<Byte> passwordBytesL = new LinkedList<>();
        LinkedList<Byte> birthdayBytesL = new LinkedList<>();
        // create seperate linked lists for each field
        while (bytes.size()!=0){
            Byte b=bytes.removeFirst();
            usernameBytesL.add(b);
            if (b=='\0') {
                break;
            }
        }
        while (bytes.size()!=0) {
            Byte b = bytes.removeFirst();
            passwordBytesL.add(b);
            if (b == '\0') {
                break;
            }
        }
        while (bytes.size()!=0) {
            Byte b = bytes.removeFirst();
            birthdayBytesL.add(b);
            if (b == '\0') {
                break;
            }
        }
        String userName = Convertor.bytesToString(usernameBytesL);
        String password = Convertor.bytesToString(passwordBytesL);
        String birthday = Convertor.bytesToString(birthdayBytesL);

        return opCodeString+" "+userName+" "+password+" "+birthday;
    }

    public String handleFollow(String opCodeString,LinkedList<Byte> bytes){
        //get if it's follow or unfollow
        String followUnfollow="";
        char followUnfollowC=(char)((byte)bytes.removeFirst());
        //System.out.println("followUnfollowChar "+followUnfollowC);
        switch (followUnfollowC){
            case '0':
                followUnfollow="0";
                break;
            case '1':
                followUnfollow="1";
                break;
        }

        //get userName
        String userName=Convertor.bytesToString(bytes);
        System.out.println("userName "+userName);
        return opCodeString+" "+followUnfollow+" "+userName;
    }

    public String handlePm(String opCode,LinkedList<Byte> bytes){
        //get userName
        LinkedList<Byte> userNameBytes=new LinkedList<>();
        while (bytes.size()!=0){
            Byte b=bytes.removeFirst();
            userNameBytes.add(b);
            if (b=='\0') {
                break;
            }
        }
        String userName=Convertor.bytesToString(userNameBytes);

        //get content
        LinkedList<Byte> contentBytes=new LinkedList<>();
        while (bytes.size()!=0){
            Byte b=bytes.removeFirst();
            contentBytes.add(b);
            if (b=='\0') {
                break;
            }
        }
        String content=Convertor.bytesToString(contentBytes);


        //get time
        LinkedList<Byte> timeBytes=new LinkedList<>();
        while (bytes.size()!=0){
            Byte b=bytes.removeFirst();
            timeBytes.add(b);
            if (b=='\0') {
                break;
            }
        }
        String time=Convertor.bytesToString(timeBytes);

        return opCode+" "+userName+" "+content+" "+time;
    }


    public String handleLogin(String opCodeString, LinkedList<Byte> bytes) {
        //get userName as byte list
        LinkedList<Byte> userNameBytes=new LinkedList<>();
        while (bytes.size()!=0){
            Byte b=bytes.removeFirst();
            userNameBytes.add(b);
            if (b=='\0') {
                break;
            }
        }

        //get password as byte list
        LinkedList<Byte> passwordBytes=new LinkedList<>();
        while (bytes.size()!=0){
            Byte b=bytes.removeFirst();
            passwordBytes.add(b);
            if (b=='\0') {
                break;
            }
        }

        // string representation of boolean capcha
        String capcha = "";
        if(bytes.getLast()==(byte)'1'){
            capcha+="1";
        }
        else {capcha+="0";}

        String username = Convertor.bytesToString(userNameBytes);
        String password = Convertor.bytesToString(passwordBytes);

        return opCodeString+" "+username+" "+password+" "+capcha;
    }









    /**
     * notification to encode.
     * @inv command's first word
     * @param command
     * @return
     */
    @Override
    public byte[] encode(String command) {
        //get opCode:
        switch (Convertor.extractOpcodeAsShortFromString(command)){
            //if command is NOTIFICATION
            case 9:
                return notificationToBytes(command);
            // if command is ACK
            case 10:
                return ackToBytes(command);
            //if command is ERROR
            case 11:
                return errorToBytes(command);
            default: return null;
        }
    }

    /**
     *
     * @param command in string to encode in order to write to client notification
     * @inv   command ~ "NOTIFICATION"_"0/1"_"postingUserString"_"contentString"
     * @inv command starts with 9
     * @return byte array of encoded notification message ~ { ';' , '0' , content string big endian , '0' , user string big endian , 0/1 for pm/public , 9 as short }
 *                                                           [opcodeInShort,0/1forPmOrPublic,PostingUserInUTF8,0,ContentInUTF8,0,;]
     */
    public byte[] notificationToBytes(String command) {
        LinkedList<Byte> ans=new LinkedList<Byte>();
        //get opCode from the command
        short opcode=Convertor.extractOpcodeAsShortFromString(command);
        //remove opCode from command
        command=Convertor.removeOpcodeFromString(command);
        //push opcode to answer
        ans.addFirst((byte)(opcode<<8));
        ans.addFirst((byte)opcode);
        //get either pm/public (0/1)
        byte pmPublic=(byte)command.charAt(0);
        //add pm or public to answer
        ans.addFirst(pmPublic);
        //the nextIndex we're checking from. right now: after PM/public
        int nextIndex=command.indexOf(' ');
        //remove (PM/public) 0/1 and the space after it from the command
        command=command.substring(nextIndex+1);
        //get space after username (after removing pm/public)
        nextIndex=command.indexOf(' ');
        //get userName in bytes
        byte[] postingUsername=Convertor.stringToBytes(command.substring(0,nextIndex));
        //remove userName and space after it from command
        command=command.substring(nextIndex+1);
        //now command only contains contentString.
        //add username to list
        for (byte b: postingUsername)
            ans.addFirst(b);
        //add 0 to inform end of string
        ans.addFirst((byte)'\0');
        byte[] content=Convertor.stringToBytes(command);
        //add content to list
        for (byte b: content)
            ans.addFirst(b);
        //add 0 to inform end of string
        ans.addFirst((byte)'\0');
        //add ; to inform end of message
        ans.addFirst((byte) ';');
        return Convertor.linkedListToByteArray(ans);
    }

    /**
     * @inv command = "ACK MessageOpCode optionalInformation"
     * @param command ACK meant to be converted to bytes and sent to client
     * @return byte[] representing command in BigEndian
    * */
    public byte[] ackToBytes(String command){
        // use linked List and convert to byte[] at the end
        LinkedList<Byte> B = new LinkedList<>();

        String[] commandSplit = command.split(" ");

        short ackOpCode = Convertor.extractOpcodeAsShortFromString(commandSplit[0]);
        short messageOpCode = Convertor.stringAsBytesToOpcode(commandSplit[1]);

        // No optional information in ACK
        if (commandSplit.length==2) {
            // add 2 bytes for ackCode and message opCode in reversed order + BigEndian
            B.addFirst((byte)(ackOpCode<<8));
            B.addFirst((byte)(ackOpCode));
            B.addFirst((byte)(messageOpCode<<8));
            B.addFirst((byte)(messageOpCode));
        }
        // Additional information based on messageOpCode
        else {
            switch (messageOpCode) {
                // ACK FOLLOW-OpCODE Follow/Unfollow <username>
                case 4:{
                    B.addFirst((byte)(ackOpCode<<8));
                    B.addFirst((byte)(ackOpCode));
                    B.addFirst((byte)(messageOpCode<<8));
                    B.addFirst((byte)(messageOpCode));

                    // follow/unfollow
                    if(commandSplit[2]=="0"){
                        B.addFirst((byte)'0');
                    }
                    else{
                        B.addFirst((byte)'1');
                    }
                    String userString = "";
                    for(int i=3; i<commandSplit.length;i++) {
                        userString+=commandSplit[i];
                        // in cae username has space
                        if (i< commandSplit.length-1) {
                            userString+=" ";
                        }
                    }

                    // add <username> bytes in reversed order + BigEndian
                    byte[] postingUsername = Convertor.stringToBytes(userString);
                    //add username to list
                    for (byte b: postingUsername)
                        B.addFirst(b);
                    //add notify string gotten
                    B.addFirst((byte)'\0');
                    break;
                }
                //-----------------------------
                // for every user
                // ACK-Opcode LOGSTAT-Opcode/Stat-Opcode <Age> <NumPosts> <NumFollowers> <NumFollowing>
                case 7: case 8: {
                    /* go over each line from start (ACK) to finish (NumFollowing). each line is a bitch and looks as such:
                        (ACK_String) (7/8_short) (AGE_short) (NumPosts_short) (NumFollowers_short) (NumFollowing_short)
                     */
                    for(int i=0; i<commandSplit.length;i+=6) {
                        // create byte array with all of the details of a single user
                        String[] userStatLine = {commandSplit[i],commandSplit[i+1],commandSplit[i+2],commandSplit[i+3],commandSplit[i+4],commandSplit[i+5]};
                        // create Byte list of the bitch stats row to bytes
                        LinkedList<Byte> userStatBytes = USLToBytes(userStatLine);
                        // insert bytes in reversed order BIG ENDIAN
                        for(Byte b:userStatBytes) {
                            B.addFirst(b);
                        }
                    }
                }
                //---------------------------
            }

        }

        // each command always ends with a ';'
        B.addFirst((byte) ';');

        return Convertor.linkedListToByteArray(B);
    }

    /**
     * @inv String[] userStatLine = {ACK,8/9,age,numPosts,NumFollowers,NumFollowing}
     * @inv messageOpCode is of LOGSTAT or STAT
     * @param userStatLine - String array include ack of user stats (LOGSTAT and STAT options)
     * @return byte linked list BIG ENDIAN ~ NumFollowing + NumFollowers + NumPosts + MessageOpCode + 10
     * */
    public LinkedList<Byte> USLToBytes(String[] userStatLine){
        int numOfDetails = userStatLine.length;
        // all values are shorts that each is represented by 2 bytes
        LinkedList<Byte> UserBytesStats = new LinkedList<>();

        // short for each detail
        for(int i=0;i< userStatLine.length;i++){
            // insert as big ENDIAN
            if (i==0){
                short ackVal=Convertor.stringToOpcode(userStatLine[i]);
                UserBytesStats.add((byte)(ackVal<<8));
                UserBytesStats.add((byte)ackVal);
            }
            else{
                short shortVal = Short.parseShort(userStatLine[i]);
                UserBytesStats.add((byte)(shortVal<<8));
                UserBytesStats.add((byte)shortVal);
            }
        }
        // bytes in BigEndian
        return UserBytesStats;
    }


    /**
     * @inv command  ~ "ERROR"_"ErroredMessageOpcode"
     * @param command satisfies @inv
     * @return byte array ~ ';' + 'ErroredMessageOpCodeAsShort' + 'ErrorOpCode'
     *                      ;   +               x               +       11
     */

    public byte[] errorToBytes(String command){
        LinkedList<Byte> ans = new LinkedList<Byte>();
        //get opCode from the command
        short opcode = Convertor.extractOpcodeAsShortFromString(command);
        //remove opCode from command
        command=Convertor.removeOpcodeFromString(command);
        //push opcode to answer
        ans.addFirst((byte)(opcode<<8));
        ans.addFirst((byte) opcode);
        //now remaining string in "command" is the opCode of the failed command (the one that returned error)
        short erroredOpCode=Convertor.extractOpcodeAsShortFromString(command);
        //push opcode to answer
        ans.addFirst((byte)(erroredOpCode<<8));
        ans.addFirst((byte) erroredOpCode);
        //add ; to inform end of message
        ans.addFirst((byte) ';');
        return Convertor.linkedListToByteArray(ans);
    }



//    private byte [] ackToBytes(short opcode,String command){
//        //if command was just to send ack
//        if (command=="ACK"){
//            byte [] B=
//                    {Convertor.shortToBytes(opcode)[1],
//                            Convertor.shortToBytes(opcode)[0]};
//            return B;
//        }
//        //else command was send ack and OPTIONAL
//
//                WE NEED TO IMPLEMENT THIS.
//                WHAT HAPPENS IF YOU RECIEVE ACK WITH OPTIONAL?
//        return null; // DELETE THIS
//    }
//
//    private byte [] errorToBytes(short opcode, String command){
//        //get index of message opcode (after space)
//        int index=command.indexOf(' ')+1;
//        short erroredMessage=getOpCode(command.substring(index));
//        // [ SHORT opcode = 11 = "ERROR" ][ SHORT erroredMessage ]
//        byte [] B=
//                {Convertor.shortToBytes(opcode)[1],         Convertor.shortToBytes(opcode)[0],
//                        Convertor.shortToBytes(erroredMessage)[1],  Convertor.shortToBytes(erroredMessage)[0]};
//        return B;
//    }


}
