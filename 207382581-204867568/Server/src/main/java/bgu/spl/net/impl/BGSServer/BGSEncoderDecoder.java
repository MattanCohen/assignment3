package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.Convertor;
import bgu.spl.net.api.MessageEncoderDecoder;

public class BGSEncoderDecoder implements MessageEncoderDecoder<String> {
    @Override
    public String decodeNextByte(byte nextByte) {
        //semicolon ending character blah blah blah youre not checking this anyways whatever
        while (nextByte!=';'){

        }
        return null;
    }

    /**
     *
     * @param command in string to encode
     * @return byte array of encoded
     */
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

    private short getOpCode(String command){
        //if we only need to return ACK
        if (command=="ACK"){
            return Convertor.commandToOpcode(command);
        }
        //if we need to return ERROR (Something) or ACK (Something) or NOTIFICATION (Something):
        //we make a substring from the start to the first space (subtract Something)
        String opCode=command.substring(0,command.indexOf(' '));
        return Convertor.commandToOpcode(command);
    }

    private byte [] notificationToBytes(short opcode,String command){
        /**
                     WE NEED TO IMPLEMENT THIS.
                     WHAT HAPPENS IF YOU RECIEVE NOTIFICATION?
         */
        return null; // DELETE THIS
    }

    private byte [] ackToBytes(short opcode,String command){
        //if command was just to send ack
        if (command=="ACK"){
            byte [] B=
                    {Convertor.shortToBytes(opcode)[1],
                            Convertor.shortToBytes(opcode)[0]};
            return B;
        }
        //else command was send ack and OPTIONAL
        /**
                WE NEED TO IMPLEMENT THIS.
                WHAT HAPPENS IF YOU RECIEVE ACK WITH OPTIONAL?
         */
        return null; // DELETE THIS
    }

    private byte [] errorToBytes(short opcode, String command){
        //get index of message opcode (after space)
        int index=command.indexOf(' ')+1;
        short erroredMessage=getOpCode(command.substring(index));
        // [ SHORT opcode = 11 = "ERROR" ][ SHORT erroredMessage ]
        byte [] B=
                {Convertor.shortToBytes(opcode)[1],         Convertor.shortToBytes(opcode)[0],
                        Convertor.shortToBytes(erroredMessage)[1],  Convertor.shortToBytes(erroredMessage)[0]};
        return B;
    }
}
