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
    //hey!!
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
     *
     * @param command in string to encode in order to write to client. only command that isn't push notification is only notification
     * @inv   command ~ "NOTIFICATION"_"0/1"_"postingUserString"_"contentString"
     * @inv command starst with 9
     * @return byte array of encoded notification message ~ { ';' , '0' , content string big endian , '0' , user string big endian , 0/1 for pm/public , 9 as short }
 *                                                           [opcodeInShort,0/1forPmOrPublic,PostingUserInUTF8,0,ContentInUTF8,0,;]
     */
    @Override
    public byte[] encode(String command) {
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
        ans.addFirst((byte)';');
        byte [] ret=new byte[ans.size()];
        for (int i=0; i<ans.size(); i++)
            ret[i]=ans.get(i);
        return ret;
    }

    /*
    @Override
    public byte[] encode(String command) {
        //get opCode:
        short opcode=getOpCode(command);
        switch (opcode){
            //if command is NOTIFICATION
            case 9:
                return notificationToBytes(opcode,command);
            // if command is ACK
            case 10:
                return ackToBytes(opcode,command);
            //if command is ERROR
            case 11:
                return errorToBytes(opcode,command);
            default: return null;
        }
    }


//    private short getOpCode(String command){
        //if we only need to return ACK
//        if (command=="ACK"){
//            return Convertor.commandToOpcode(command);
//        }
        //if we need to return ERROR (Something) or ACK (Something) or NOTIFICATION (Something):
        //we make a substring from the start to the first space (subtract Something)

//        return Convertor.commandToOpcode(command);
//    }



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
     */


}
