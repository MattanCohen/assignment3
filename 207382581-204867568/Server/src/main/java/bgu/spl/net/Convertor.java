package bgu.spl.net;

import sun.nio.cs.UTF_8;

public class Convertor {
    /**
     *
     * @param  byteArr array size 2 representing short
     * @return the short from byte array
     */
    public static short bytesToShort(byte[] byteArr)
    {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
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
        return new String(byteArr,0,byteArr.length);
    }

    /**
     *
     * @param string string to convert
     * @return bytes array from string
     */
    public static byte[] stringToBytes (String string){
        return string.getBytes();
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
    public static short commandToOpcode(String cmnd){
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
    public static String opcodeTocommand(short opCode){
        switch(opCode) {
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
}
