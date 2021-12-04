package com.olacompany.olachat.room;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.kakao.sdk.user.model.User;
import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.game.boomspin.BoomSpinActivity;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.BoomSpinPacket;
import com.olacompany.olachat.packet.RoomPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class ChatRoomActivity extends AppCompatActivity {

    private long backKeyPressedTime = 0;
    private ImageButton btn_send_chat, btn_drawer, btn_room_invite_friend;
    private DrawerLayout drawerLayout;
    private EditText editChat;
    private ListView chat_list, room_userlist;
    private TextView room_name_textview;
    public static ChatListAdapter chatListAdapter;
    public static RoomUserListAdapter userListAdapter;
    public static InviteFriendListAdapter inviteFriendListAdapter;
    private LayoutInflater inflater;
    private List<RoomChat> roomChats = Collections.synchronizedList(new ArrayList<>());
    private List<UserInfo> userInfo = Collections.synchronizedList(new ArrayList<>());


    private RadioGroup gameGroup;
    private RadioButton btn_game_boom_spin;
    private Button btn_room_game_start;

    private int roomNumber;



    private static ChatRoomActivity chatRoomActivity;

    public static ChatRoomActivity getInstance(){
        return chatRoomActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);
        chatRoomActivity = this;
        inflater = getLayoutInflater();

        drawerLayout = (DrawerLayout) findViewById(R.id.room_drawer_layout);

        room_name_textview = (TextView) findViewById(R.id.room_name_textview);
        btn_send_chat = (ImageButton) findViewById(R.id.btn_send_chat);
        btn_drawer = (ImageButton) findViewById(R.id.btn_room_drawer);
        btn_room_invite_friend = (ImageButton) findViewById(R.id.btn_room_invite_friend);
        editChat = (EditText) findViewById(R.id.chat_edittext);
        chat_list = (ListView) findViewById(R.id.room_chat_listview);
        room_userlist = (ListView) findViewById(R.id.room_userlist_listview);
        btn_room_game_start = (Button) findViewById(R.id.btn_room_game_start);

        Intent intent = getIntent();

        intent.getExtras().getLong("userId");
        roomNumber = intent.getExtras().getInt("roomNumber");
        String roomName = intent.getExtras().getString("roomName");
        intent.getExtras().getInt("roomMemberCount");
        intent.getExtras().getInt("roomMemberMaxCount");

        room_name_textview.setText(roomName);

        chatListAdapter = new ChatListAdapter(this,R.layout.item_chat,roomChats);
        chat_list.setAdapter(chatListAdapter);

        userListAdapter = new RoomUserListAdapter(this,R.layout.item_room_userlist,userInfo);
        room_userlist.setAdapter(userListAdapter);


        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        btn_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = editChat.getText().toString();
                if(!text.equals("")){
                    sendChat(text);
                    editChat.setText("");
                }
            }
        });

        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }else{
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        });

        btn_room_game_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(RoomPacket.sendGameStartRequest(0));
            }
        });

        btn_room_invite_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(RoomPacket.sendOpenInviteFriendRequest());
            }
        });



        sendJoinRoom();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            DevTools.showToastShortMsg(this,"방에서 나가려면 뒤로가기 버튼을 한번 더 누르세요.");
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            sendExitRoom();
            finish();
        }
    }

    public void sendChat(String msg){
        NettyClient.getSession().writeAndFlush(RoomPacket.sendRoomChatMsg(NettyClient.getUserId(),NettyClient.getUserName(),roomNumber,msg,NettyClient.getUserProfileImageCode()));
    }

    public void readChat(LittleEndianReader r){

        r.readInt(); //방번호
        long userId = r.readLong();
        String senderName = r.readLengthAsciiString();
        String msg = r.readLengthAsciiString();
        int userProfileImageCode = r.readInt();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomChats.add(new RoomChat(userId, senderName,msg,userProfileImageCode,false));
                chatListAdapter.notifyDataSetChanged();
            }
        });

    }

    public void readNotice(LittleEndianReader r){
        String msg = r.readLengthAsciiString();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roomChats.add(new RoomChat(-1L,"",msg,0,true));
                chatListAdapter.notifyDataSetChanged();
            }
        });

    }

    public void readUserList(LittleEndianReader r){

        userInfo.clear();

        int userSize = r.readInt();
        for(int i =0; i < userSize; i++){
            long userId = r.readLong();
            String userName = r.readLengthAsciiString();
            int profileImageCode = r.readInt();
            int userWin = r.readInt();
            int userLose = r.readInt();
            int userPopularity = r.readInt();
            String userMemo = r.readLengthAsciiString();
            boolean isLogin = r.readByte() == 1;
            boolean isRoomLeader = r.readByte() == 1;
            userInfo.add(new UserInfo(userId,userName,profileImageCode,userMemo,isLogin,isRoomLeader));
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(UserInfo user : userInfo){
                    if(user.getUserId() == NettyClient.getUserId()){
                        if(user.isRoomLeader()){
                            btn_room_game_start.setVisibility(View.VISIBLE);
                        }else{
                            btn_room_game_start.setVisibility(View.GONE);
                        }
                        break;
                    }
                }
                userListAdapter.notifyDataSetChanged();
            }
        });

    }


    private static AlertDialog  alertDialog = null;
    private static LinearLayout linearLayout;
    private static Button btn_close_inviteF;
    private static ListView dialog_inviteF_list;

    private static List<UserInfo> friendList = Collections.synchronizedList(new ArrayList<>());

    public static void OpenInviteFriendDialog(LittleEndianReader r){

        friendList.clear();
        int userSize = r.readInt();
        for(int i =0; i < userSize; i++){
            long userId = r.readLong();
            String userName = r.readLengthAsciiString();
            int profileImageCode = r.readInt();
            int userWin = r.readInt();
            int userLose = r.readInt();
            int userPopularity = r.readInt();
            String userMemo = r.readLengthAsciiString();
            boolean isLogin = r.readByte() == 1;
            boolean isRoomLeader = r.readByte() == 1;
            if(isLogin){
                friendList.add(new UserInfo(userId,userName,profileImageCode,userMemo,isLogin,isRoomLeader));
            }
        }

        Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity.getLayoutInflater() == null) {
                    Log.e("Activity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_invite_friend,null);
                dialog_inviteF_list = linearLayout.findViewById(R.id.dialog_inviteF_list);
                btn_close_inviteF = linearLayout.findViewById(R.id.btn_close_inviteF);

                inviteFriendListAdapter = new InviteFriendListAdapter(activity,R.layout.item_friend_invitelist,friendList);
                dialog_inviteF_list.setAdapter(inviteFriendListAdapter);

                inviteFriendListAdapter.notifyDataSetChanged();

                builder.setView(linearLayout);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_close_inviteF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       alertDialog.dismiss();
                    }
                });

            }
        },0);

    }

    private static Button btn_ban_ok, btn_ban_no;
    private static TextView tv_bantext;

    public static void OpenBanUserDialog(UserInfo banUser){

        Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity.getLayoutInflater() == null) {
                    Log.e("Activity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_room_ban_user,null);
                btn_ban_ok = linearLayout.findViewById(R.id.btn_yes_ban_user);
                btn_ban_no = linearLayout.findViewById(R.id.btn_no_ban_user);
                tv_bantext = linearLayout.findViewById(R.id.room_ban_tv);

                tv_bantext.setText(DevTools.setTextColor("#y["+banUser.getUserName()+"]#l 님을 #r추방#l하시겠습니까?"));
                builder.setView(linearLayout);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_ban_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_ban_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NettyClient.getSession().writeAndFlush(RoomPacket.sendBanUser(banUser.getUserId()));
                        alertDialog.dismiss();
                    }
                });

            }
        },0);

    }


    public void sendExitRoom(){
        NettyClient.getSession().writeAndFlush(RoomPacket.sendRoomExitUser(NettyClient.getUserId(),NettyClient.getUserName(),roomNumber));
    }

    public void sendJoinRoom(){
        NettyClient.getSession().writeAndFlush(RoomPacket.sendJoinRoomUser(NettyClient.getUserId(),NettyClient.getUserName(),roomNumber));
    }

    public void startBoomSpinActivity(){
        Intent intent = new Intent(getInstance(), BoomSpinActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ChatRoomActivity.getInstance().startActivity(intent);
    }

    public void handleJoinGameActivity(LittleEndianReader r){
        int code = r.readInt();
        switch (code){
            case 0:{
                ChatRoomActivity.getInstance().startBoomSpinActivity();
                break;
            }

        }
    }

    public void exitRoom(){
        finish();
    }
}
