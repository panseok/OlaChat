package com.olacompany.olachat.packet;

import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.opcode.SendOpcode;
import com.olacompany.olachat.room.ChatRoom;

public class FriendPacket {

    public static byte[] sendJoinFriendActivity(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_JOIN_ACTIVITY.ordinal());
        return o.getPacket();
    }

    public static byte[] sendSearchFriend(String userName){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_SEARCH.ordinal());
        o.writeLengthAsciiString(userName);
        return o.getPacket();
    }

    public static byte[] sendRequestWindow(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_OPEN_REQUEST.ordinal());
        return o.getPacket();
    }

    public static byte[] sendRequestAddFriend(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_REQUEST_ADD.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }

    public static byte[] sendRequestFriendAccept(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_REQUEST_ACCEPT.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }

    public static byte[] sendRequestFriendRefuse(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_REQUEST_REFUSE.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }

    public static byte[] sendRemoveFriend(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.FRIEND_REMOVE.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }
}
