package bgu.spl.net.api.BIDI;


import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class Convertor {

    public static byte[] reverese (byte[]byteArr){
        LinkedList<Byte> flipped = new LinkedList<>();
         for (byte b : byteArr)
             flipped.push(b);
         return linkedListToByteArray(flipped);
    }

    /**
     *
     * @param  byteArr array size 2 representing short
     * @return the short from byte array
     */
    public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        if (result>256){
            int dif = result/256;
            result -= 256*dif;
            result += dif*10;
        }
        return result;
    }

    /**
     *
     * @param num the short to change to byte array
     * @return byte array size 2 representing num
     */
    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }

    /**
     *
     * @param byteArr byte array input, meant to be string
     * @return string out of byteArr
     */
    public static String bytesToString (byte[] byteArr){
//        byte zero='\n';
//        byte[] zeroArr={zero};
//        System.out.println("zero array to string:"+(new String(zeroArr, StandardCharsets.UTF_8))+"!");
        return new String(byteArr, StandardCharsets.UTF_8);
    }
    public static String bytesToString (LinkedList<Byte> byteArr){
        return new String(Convertor.linkedListToByteArray(byteArr), StandardCharsets.UTF_8);
    }

    /**
     *
     * @param string string to convert
     * @return bytes array from string
     */
    public static byte[] stringToBytes (String string){
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String shortToString (short x){
        return String.valueOf(x);
    }

    //SMALL ENDIAN
    public static short stringToShort(String string){
        return (short)Integer.parseInt(string);
    }

    /**
     * if the command is an opCode, return the opCode it represents
     * else return the opCode it represents until first space
     * @inv command's first word (the string is a command or until first space appearance) is opCode
     * @param command legal command
     * @return short opCode
     */
    public static Short extractOpcodeAsShortFromString(String command) {
        if (stringToOpcode(command)!=-1)
            return  stringToOpcode(command);
        return stringToOpcode(command.substring(0,command.indexOf(' ')));
    }
    /**
     * if the command is an opCode, return it
     * else return the command without whatever there is after the space (return only the opCode)
     * @inv command's first word (the string is a command or until first space appearance) is opCode
     * @param command legal command
     * @return opCode (out of command)
     */
    public static String extractOpCodeAsStringFromString(String command){
        if (stringToOpcode(command)!=-1)
            return command;
        return command.substring(0,command.indexOf(' '));
    }


    /**
     * if the command is an opCode, return empty string
     * else return the command without whatever there is before the space (return command without the opcode)
     * @inv command's first word (the string is a command or until first space appearance) is opCode
     * @param command legal command
     * @return the command without opCode or empty string if the command is an opcode
     */
    public static String removeOpcodeFromString(String command){
        if (stringToOpcode(command)!=-1)
            return "";
        return command.substring(command.indexOf(' ')+1);
    }
        /**
         Opcode Operation
         1 Register request (REGISTER)
         2 Login request (LOGIN)
         3 Logout request (LOGOUT)
         4 Follow / Unfollow request (FOLLOW)
         5 Post request (POST)
         6 PM request (PM)
         7 Logged in States request (LOGSTAT)
         8 Stats request (STAT)
         9 Notification (NOTIFICATION)
         10 Ack (ACK)
         11 Error (ERROR)
         12 Block (BLOCK)

         * @param cmnd the command to make short represanting relevant opcode
         * @return relevant Opcode or -1 if cmnd isn't relevant
         */
    public static short stringToOpcode(String cmnd){
        switch(cmnd) {
            case "REGISTER":
                return 1;
            case "LOGIN":
                return 2;
            case "LOGOUT":
                return 3;
            case "FOLLOW":
                return 4;
            case "POST":
                return 5;
            case "PM":
                return 6;
            case "LOGSTAT":
                return 7;
            case "STAT":
                return 8;
            case "NOTIFICATION":
                return 9;
            case "ACK":
                return 10;
            case "ERROR":
                return 11;
            case "BLOCK":
                return 12;
            default: return -1;
        }
    }
    public static short stringAsBytesToOpcode(String cmnd){
        switch(cmnd) {
            case "1":
                return 1;
            case "2":
                return 2;
            case "3":
                return 3;
            case "4":
                return 4;
            case "5":
                return 5;
            case "6":
                return 6;
            case "7":
                return 7;
            case "8":
                return 8;
            case "9":
                return 9;
            case "10":
                return 10;
            case "11":
                return 11;
            case "12":
                return 12;
            default: return -1;
        }
    }
    /**
     Opcode Operation
     1 Register request (REGISTER)
     2 Login request (LOGIN)
     3 Logout request (LOGOUT)
     4 Follow / Unfollow request (FOLLOW)
     5 Post request (POST)
     6 PM request (PM)
     7 Logged in States request (LOGSTAT)
     8 Stats request (STAT)
     9 Notification (NOTIFICATION)
     10 Ack (ACK)
     11 Error (ERROR)
     12 Block (BLOCK)

     * @param opCode: the short represanting relevant opcode to make command
     * @return relevant Opcode as cmnd String or null if  isn't relevant
     */
    public static String opcodeToString(short opCode){
        switch (opCode){
//        switch(Character.getNumericValue((char)((byte)opCode))) {
            case 1:
                return "REGISTER";
            case 2:
                return "LOGIN";
            case 3:
                return "LOGOUT";
            case 4:
                return "FOLLOW";
            case 5:
                return "POST";
            case 6:
                return "PM";
            case 7:
                return "LOGSTAT";
            case 8:
                return "STAT";
            case 9:
                return "NOTIFICATION";
            case 10:
                return "ACK";
            case 11:
                return "ERROR";
            case 12:
                return "BLOCK";
            default: return null;
        }
    }
    public static String opcodeToStringAsBytes(short opCode){
        switch(opCode) {
            case 1:
                return "1";
            case 2:
                return "2";
            case 3:
                return "3";
            case 4:
                return "4";
            case 5:
                return "5";
            case 6:
                return "6";
            case 7:
                return "7";
            case 8:
                return "8";
            case 9:
                return "9";
            case 10:
                return "10";
            case 11:
                return "11";
            case 12:
                return "12";
            default: return null;
        }
    }

    public static byte[] linkedListToByteArray(LinkedList<Byte> B){
        byte [] ans=new byte[B.size()];
        for (int i=0; i<ans.length; i++)
            ans[i]=B.get(i);
        return ans;
    }
    public static LinkedList<Byte> byteArrayToLinkedList(byte[] B){
        LinkedList<Byte> ans=new LinkedList<>();
        for (byte b: B)
            ans.add(b);
        return ans;
    }
}
