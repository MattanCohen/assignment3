package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;
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
            //set to Do as the full command translated and flipped
            String toDo= translateBytes();
            //get the opCode of the message
            short shortOpCode=Convertor.stringAsBytesToOpcode(toDo.toString().substring(0,1));
            String opCode=Convertor.opcodeToString(shortOpCode);

        }
        //if nextByte isn't ';', add nextByte to bytes array and return null (message is still readeded)
        pushByte(nextByte);
        return null;
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
    /**
     * The server recieves all Client-To-Server commands.
     * @inv when reaching this function, bytes is ready to be read (meaning the current message has finished
     *      readiung meaning reached ';')
     * @inv every message most significant value index and one index before it represents a short for opCode
     *      opCode options:
                         *Opcode        Operation
                         * 1        Register request (REGISTER)
                         * 2        Login request (LOGIN)
                         * 3        Logout request (LOGOUT)
                         * 4        Follow / Unfollow request (FOLLOW)
                         * 5        Post request (POST)
                         * 6        PM request (PM)
                         * 7        Logged in States request (LOGSTAT)
                         * 8        Stats request (STAT)
                         * 12       Block (BLOCK)
     *
     * The server has to return Ack or Error in order for the client to continue running
     * @inv every message that finished reading receives send(Ack) or send(Error)
     * @return the decoded task represented in String for protocol
     */
    private String translateBytes(){
        //make flippedCommand to be the message we ought to do ordered BIG ENDIAN
        String flippedCommand=Convertor.bytesToString(bytes);
        //save the command as ans and flip it
        StringBuilder ans=new StringBuilder(flippedCommand);
        ans.reverse();
        resetByte();
        //return reversed and translated bytes message as normal String
         return ans.toString();
    }
    private void resetByte(){
        bytes = new byte[1 << 10]; //start with 1k
        len = 0;
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
        ans.addFirst((byte)'0');
        byte[] content=Convertor.stringToBytes(command);
        //add content to list
        for (byte b: content)
            ans.addFirst(b);
        //add 0 to inform end of string
        ans.addFirst((byte)'0');
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
        // each command always ends with a ';'
        B.addFirst((byte) ';');

        String[] commandSplit = command.split(" ");

        short ackOpCode = Convertor.extractOpcodeAsShortFromString(commandSplit[0]);
        short messageOpCode = Convertor.extractOpcodeAsShortFromString(commandSplit[1]);

        // No optional information in ACK
        if (commandSplit.length==2) {
            // add 2 bytes for ackCode and message opCode in reversed order + BigEndian
            B.addFirst(Convertor.shortToBytes(ackOpCode)[1]);
            B.addFirst(Convertor.shortToBytes(ackOpCode)[0]);
            B.addFirst(Convertor.shortToBytes(messageOpCode)[1]);
            B.addFirst(Convertor.shortToBytes(messageOpCode)[0]);

        }
        // Additional information based on messageOpCode
        else {
            switch (messageOpCode) {
                // ACK-Opcode FOLLOW-OpCODE <username>
                case 4: {
                    String commandNoOpCodes = Convertor.removeOpcodeFromString(Convertor.removeOpcodeFromString(command));
                    byte[] userNBytes = Convertor.stringToBytes(commandNoOpCodes);

                    B.addFirst(Convertor.shortToBytes(ackOpCode)[1]);
                    B.addFirst(Convertor.shortToBytes(ackOpCode)[0]);
                    B.addFirst(Convertor.shortToBytes(messageOpCode)[1]);
                    B.addFirst(Convertor.shortToBytes(messageOpCode)[0]);

                    // add <username> bytes in reversed order + BigEndian
                    for (int i = 0; i < userNBytes.length; i++) {
                        B.addFirst(userNBytes[userNBytes.length - i - 1]);
                    }
                    break;
                }
                // for every user
                // ACK-Opcode LOGSTAT-Opcode/Stat-Opcode <Age> <NumPosts> <NumFollowers> <NumFollowing>
                case 7: case 8: {
                    // go over each line
                    for(int i=0; i<commandSplit.length;i+=6) {
                        // create byte array with all of the details of a single user
                        String[] userStatLine = {commandSplit[i],commandSplit[i+1],commandSplit[i+2],commandSplit[i+3],commandSplit[i+4],commandSplit[i+5]};
                        byte[] userStatBytes = USLToBytes(userStatLine);
                        //?????????????????????????????????????///
                        // do we want to add '/n' for each line or will we add them on the Client side?
                        //????????????????????????????????????//
                        // add all user details (all short values, 2 bytes per detail)
                        for(int j=0;j<userStatBytes.length;j+=2) {
                            // insert in Big Endian
                            B.addFirst(userStatBytes[j]);
                            B.addFirst(userStatBytes[j+1]);
                        }
                    }
                }
            }

        }
        return Convertor.linkedListToByteArray(B);
    }

    /**
     * @inv String[] userStatLine = {ackOpCode,messageOpCode,age,numPosts,NumFollowers,NumFollowing}
     * @inv messageOpCode is of LOGSTAT or STAT
     * @param userStatLine - String array include ack of user stats (LOGSTAT and STAT options)
     * @return byte array ~ 10 + 'MessageOPCodeAsShort' + 'NumPosts' + 'NUMFollowers' + 'NumFollowing'
     * */
    public byte[] USLToBytes(String[] userStatLine){
        int numOfDetails = userStatLine.length;
        // 2 bytes for each 'short'
        byte[] userStatBytes = new byte[numOfDetails*2];

        // byte[] for each detail
        byte[] ackOpCodeB = Convertor.shortToBytes(Short.parseShort(userStatLine[0]));
        byte[] messageOpCodeB = Convertor.shortToBytes(Short.parseShort(userStatLine[1]));
        byte[] ageB = Convertor.shortToBytes(Short.parseShort(userStatLine[2]));
        byte[] numPostsB = Convertor.shortToBytes(Short.parseShort(userStatLine[3]));
        byte[] numFollowersB = Convertor.shortToBytes(Short.parseShort(userStatLine[4]));
        byte[] numFollowingB = Convertor.shortToBytes(Short.parseShort(userStatLine[5]));

        // merge all byte arrays to a single byteArray
        byte[][] bytesLists = {ackOpCodeB,messageOpCodeB,ageB,numPostsB,numFollowersB,numFollowingB};
        int cellCounter = 0;
        for(int i=0; i<bytesLists.length; i++) {
            for(int j=0; j<bytesLists[i].length; j++) {
                userStatBytes[cellCounter] = bytesLists[i][j];
                cellCounter++;
            }
        }

        return userStatBytes;
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
