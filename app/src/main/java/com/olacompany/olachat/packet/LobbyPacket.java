package com.olacompany.olachat.packet;

import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.opcode.SendOpcode;


public class LobbyPacket {

    public static byte[] sendCreateRoom(long userid, String roomname){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.LOBBY_CREATE_ROOM.ordinal());
        o.writeLong(userid);
        o.writeLengthAsciiString(roomname);
        return o.getPacket();
    }

    public static byte[] getLobbyRoomlist(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.LOBBY_ROOM_GETLIST.ordinal());
        return o.getPacket();
    }

    public static byte[] getServerNoticeList(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.SERVER_NOTICE_REQUEST.ordinal());
        return o.getPacket();
    }

    public static byte[] sendProfileImageChange(int code){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.USER_PROFILE_IMAGE_CHANGE.ordinal());
        o.writeInt(code);
        return o.getPacket();
    }

    public static byte[] sendOpenProfile(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.USER_OPEN_PROFILE.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }

    public static byte[] sendChangeUserMemo(String memo){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.USER_MEMO_CHANGE.ordinal());
        o.writeLengthAsciiString(memo);
        return o.getPacket();

    }




}
