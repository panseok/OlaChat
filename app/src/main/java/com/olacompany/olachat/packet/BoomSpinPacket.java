package com.olacompany.olachat.packet;

import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.opcode.SendOpcode;

public class BoomSpinPacket {

    public static byte[] sendBoomSpinChat(long userId, String userName, String msg, int userprofileImageCode){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.GAME_BOOM_SPIN.ordinal());
        o.writeInt(6);
        o.writeLong(userId);
        o.writeLengthAsciiString(userName);
        o.writeLengthAsciiString(msg);
        o.writeInt(userprofileImageCode);
        return o.getPacket();
    }

    public static byte[] sendBoomSpinAuctionMybid(int price){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.GAME_BOOM_SPIN.ordinal());
        o.writeInt(7);
        o.writeInt(price);
        return o.getPacket();
    }

    public static byte[] sendBoomSpin(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.GAME_BOOM_SPIN.ordinal());
        o.writeInt(8);
        return o.getPacket();
    }

}
