package com.olacompany.olachat.packet;

import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.opcode.SendOpcode;

public class LoginPacket {

    public static byte[] sendPong(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.SERVER_PONG.ordinal());
        return o.getPacket();
    }

    public static byte[] sendLoginServerConnect(boolean isSucceed){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.CONNECT_LOGIN_SERVER.ordinal());
        o.write(isSucceed);
        return o.getPacket();
    }


    public static byte[] sendLogin(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.USER_LOGIN.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }

    public static byte[] sendCheckName(String name){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.USER_CHECK_NAME.ordinal());
        o.writeLengthAsciiString(name);
        return o.getPacket();
    }

    public static byte[] sendConnectChannel(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.USER_CONNECT_CHANNEL.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }



}
