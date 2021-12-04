package com.olacompany.olachat.room;

public class ChatRoom {
    private Integer room_number = -1;
    private String room_name = "";
    private Integer room_member_count = -1;
    private Integer room_member_maxcount = -1;

    public ChatRoom(Integer room_number, String room_name, Integer room_member_count, Integer room_member_maxcount){
        this.room_number = room_number;
        this.room_name = room_name;
        this.room_member_count = room_member_count;
        this.room_member_maxcount = room_member_maxcount;
    }

    public Integer getRoom_member_count() {
        return room_member_count;
    }

    public Integer getRoom_member_maxcount() {
        return room_member_maxcount;
    }

    public Integer getRoom_number() {
        return room_number;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

    public void setRoom_member_count(Integer room_member_count) {
        this.room_member_count = room_member_count;
    }

    public void setRoom_member_maxcount(Integer room_member_maxcount) {
        this.room_member_maxcount = room_member_maxcount;
    }

    public void setRoom_number(Integer room_number) {
        this.room_number = room_number;
    }
}
