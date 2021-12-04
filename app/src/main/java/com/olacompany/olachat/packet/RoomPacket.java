package com.olacompany.olachat.packet;

import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.room.ChatRoom;
import com.olacompany.olachat.opcode.SendOpcode;

public class RoomPacket {
    public static byte[] getJoinRoomRequest(ChatRoom room, long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_JOIN_REQUEST.ordinal());
        o.writeLong(userId);
        o.writeInt(room.getRoom_number());
        return o.getPacket();
    }

    public static byte[] getJoinRoomRequest(int roomnumber, long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_JOIN_REQUEST.ordinal());
        o.writeLong(userId);
        o.writeInt(roomnumber);
        return o.getPacket();
    }

    public static byte[] sendRoomChatMsg(long userId, String userName, int roomNumber, String msg, int userprofileImageCode){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_SEND_CHAT.ordinal());
        o.writeInt(roomNumber);
        o.writeLong(userId);
        o.writeLengthAsciiString(userName);
        o.writeLengthAsciiString(msg);
        o.writeInt(userprofileImageCode);
        return o.getPacket();
    }

    public static byte[] sendRoomExitUser(long userId, String userName, int roomNumber){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_EXIT_USER.ordinal());
        o.writeLong(userId);
        o.writeLengthAsciiString(userName);
        o.writeInt(roomNumber);
        return o.getPacket();
    }

    public static byte[] sendJoinRoomUser(long userId, String userName, int roomNumber){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_JOIN_USER.ordinal());
        o.writeLong(userId);
        o.writeLengthAsciiString(userName);
        o.writeInt(roomNumber);
        return o.getPacket();
    }

    public static byte[] sendGameStartRequest(int code){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.GAME_START_REQUEST.ordinal());
        o.writeInt(code);
        return o.getPacket();
    }

    public static byte[] sendOpenInviteFriendRequest(){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_OPEN_INVITE_FRIEND.ordinal());
        return o.getPacket();
    }

    public static byte[] sendInviteFriend(UserInfo friend){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_INVITE_FRIEND.ordinal());
        o.writeLong(friend.getUserId());
        return o.getPacket();
    }
    public static byte[] sendBanUser(long userId){
        LittleEndianWriter o = new LittleEndianWriter();
        o.writeShort(SendOpcode.ROOM_BAN_USER.ordinal());
        o.writeLong(userId);
        return o.getPacket();
    }
}
