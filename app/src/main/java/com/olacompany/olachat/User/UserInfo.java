package com.olacompany.olachat.User;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.lobby.LobbyActivity;
import com.olacompany.olachat.lobby.ProfileImageGridAdapter;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.FriendPacket;

import java.util.List;

import androidx.appcompat.app.AlertDialog;

public class UserInfo {


    private int profile_code = -1;
    private long userId;
    private String userName = "";
    private String userMemo = "";
    private boolean isLogin = false, isRoomLeader = false;

    public UserInfo(long userId, String userName, int profile_code, String userMemo, boolean isLogin, boolean isRoomLeader){
        this.userId = userId;
        this.userName = userName;
        this.profile_code = profile_code;
        this.userMemo = userMemo;
        this.isLogin = isLogin;
        this.isRoomLeader = isRoomLeader;
    }

    public void setRoomLeader(boolean roomLeader) {
        isRoomLeader = roomLeader;
    }

    public boolean isRoomLeader() {
        return isRoomLeader;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getUserName() {
        return userName;
    }

    public int getProfile_code() {
        return profile_code;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserMemo() {
        return userMemo;
    }

    public void setProfile_code(int profile_code) {
        this.profile_code = profile_code;
    }

    public void setUserMemo(String userMemo) {
        this.userMemo = userMemo;
    }

    public static void ProfileAlertDialog(LittleEndianReader r){

        boolean isExist = r.readByte() == 1;

        Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());

        if(!isExist){
            DevTools.showToastShortMsg(activity,"해당 유저를 찾을 수 없습니다.");
            return;
        }

        long n_userId = r.readLong();
        String n_userName = r.readLengthAsciiString();
        int n_profileImageCode = r.readInt();
        int n_userWin = r.readInt();
        int n_userLose = r.readInt();
        int n_userPopularity = r.readInt();
        String n_userMemo = r.readLengthAsciiString();
        boolean isLogin = r.readByte() == 1;

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity.getLayoutInflater() == null) {
                    Log.e("Activity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_user_profile,null);
                FrameLayout profileImage = linearLayout.findViewById(R.id.dialog_profile_image_layout);
                ImageView profileBar = linearLayout.findViewById(R.id.dialog_profile_default_bar_image);
                TextView userName = linearLayout.findViewById(R.id.dialog_profile_username);
                TextView userWin = linearLayout.findViewById(R.id.dialog_profile_userWin);
                TextView userLose = linearLayout.findViewById(R.id.dialog_profile_userLose);
                TextView userRecord = linearLayout.findViewById(R.id.dialog_profile__userRecordPercent);
                TextView userPopularity = linearLayout.findViewById(R.id.dialog_profile_userPopularity);
                TextView userMemo = linearLayout.findViewById(R.id.dialog_profile_userMemo);
                TextView isOnline_text = linearLayout.findViewById(R.id.dialog_profile_isOnline_text);
                ImageButton btn_close = linearLayout.findViewById(R.id.btn_dialog_profile_close);
                ImageButton btn_dialog_removeFriend = linearLayout.findViewById(R.id.btn_dialog_removeFriend);
                ImageButton btn_dialog_addFriend = linearLayout.findViewById(R.id.btn_dialog_addFriend);
                ImageView dialog_profile_isOnline = linearLayout.findViewById(R.id.dialog_profile_isOnline);

                userName.setTextColor(Color.WHITE);
                userWin.setTextColor(Color.WHITE);
                userLose.setTextColor(Color.WHITE);
                userRecord.setTextColor(Color.WHITE);
                userPopularity.setTextColor(Color.WHITE);
                userMemo.setTextColor(Color.WHITE);
                isOnline_text.setTextColor(Color.WHITE);

                profileImage.setBackgroundResource(DevTools.getProfileImageId(n_profileImageCode));
                userName.setText(n_userName);
                userWin.setText(n_userWin+"승");
                userLose.setText(n_userLose+"패");
                double win_percent = ((double) n_userWin / (n_userWin + n_userLose) ) * 100.0;
                userRecord.setText("("+String.format("%.2f",win_percent)+"%)");
                userPopularity.setText("인기도 : "+n_userPopularity);
                userMemo.setText(n_userMemo);
                dialog_profile_isOnline.setBackgroundResource(isLogin ? R.drawable.img_user_on : R.drawable.img_user_off);
                isOnline_text.setText(isLogin ? "온라인" : "오프라인");

                builder.setView(linearLayout);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_dialog_removeFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NettyClient.getSession().writeAndFlush(FriendPacket.sendRemoveFriend(n_userId));
                    }
                });

                btn_dialog_addFriend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NettyClient.getSession().writeAndFlush(FriendPacket.sendRequestAddFriend(n_userId));
                    }
                });

            }
        },0);
    }

}
