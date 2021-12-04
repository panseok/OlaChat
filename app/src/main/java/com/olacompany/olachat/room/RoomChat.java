package com.olacompany.olachat.room;

public class RoomChat {
    private long userId = 0;
    private String userName = "";
    private String userChat = "";
    private int userProfileImageCode;
    private boolean isNotice;

    public RoomChat(Long userId, String userName, String userChat, int userProfileImageCode, boolean isNotice){
        this.userId = userId;
        this.userName = userName;
        this.userChat = userChat;
        this.userProfileImageCode = userProfileImageCode;
        this.isNotice = isNotice;
    }

    public long getUserId() {
        return userId;
    }

    public String getUserChat() {
        return userChat;
    }

    public String getUserName() {
        return userName;
    }

    public boolean isNotice() {
        return isNotice;
    }

    public void setUserProfileImageCode(int userProfileImageCode) {
        this.userProfileImageCode = userProfileImageCode;
    }

    public int getUserProfileImageCode() {
        return userProfileImageCode;
    }
}
