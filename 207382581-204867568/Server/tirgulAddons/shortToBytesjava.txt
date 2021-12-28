public byte[] shortToBytes(short num)
{
    byte[] bytesArr = new byte[2];
    bytesArr[0] = (byte)((num >> 8) & 0xFF);
    bytesArr[1] = (byte)(num & 0xFF);
    return bytesArr;
}