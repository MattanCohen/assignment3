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

            case 9:

            // if command is ACK
            case 10: {
                //if command was just to send ack
                if (command=="ACK"){
                    byte [] B=
                            {Convertor.shortToBytes(opcode)[1],
                             Convertor.shortToBytes(opcode)[0]};
                    return B;
                }
                //else command was send ack and OPTIONAL

            }


            //if command is error
            case 11:{
                //get index of message opcode (after space)
                int index=command.indexOf(' ')+1;
                short erroredMessage=getOpCode(command.substring(index));
                // [ SHORT opcode = 11 = "ERROR" ][ SHORT erroredMessage ]
                byte [] B=
                        {Convertor.shortToBytes(opcode)[1],         Convertor.shortToBytes(opcode)[0],
                        Convertor.shortToBytes(erroredMessage)[1],  Convertor.shortToBytes(erroredMessage)[0]};
                return B;
            }
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

}
