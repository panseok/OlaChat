package com.olacompany.olachat.friend;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.lobby.LobbyActivity;
import com.olacompany.olachat.lobby.ProfileImageGridAdapter;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.FriendPacket;
import com.olacompany.olachat.User.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FriendActivity extends AppCompatActivity {

    private ImageButton btn_search, btn_request;
    private EditText editText_searchFriend;
    private ListView friend_list;

    private List<UserInfo> friendList = Collections.synchronizedList(new ArrayList<>());
    private static List<UserInfo> requestList = Collections.synchronizedList(new ArrayList<>());
    private static FriendRequestListAdapter friendRequestListAdapter = null;

    private FriendListAdapter friendListAdapter;

    private static FriendActivity friendActivity;

    public static FriendActivity getInstance(){
        return friendActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        friendActivity = this;

        btn_request = (ImageButton) findViewById(R.id.btn_friend_request);
        btn_search = (ImageButton) findViewById(R.id.btn_friend_search);
        editText_searchFriend = (EditText) findViewById(R.id.edit_search_friend);
        friend_list = (ListView) findViewById(R.id.friend_list);


        friendListAdapter = new FriendListAdapter(this,R.layout.item_friend_userlist, friendList);
        friend_list.setAdapter(friendListAdapter);


        NettyClient.getSession().writeAndFlush(FriendPacket.sendJoinFriendActivity());


        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search_name = editText_searchFriend.getText().toString();
                if(!search_name.isEmpty()){
                    NettyClient.getSession().writeAndFlush(FriendPacket.sendSearchFriend(search_name));
                }
            }
        });

        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(FriendPacket.sendRequestWindow());
                openFriendRequestDialog();
            }
        });


    }


    public void readFriendList(LittleEndianReader r){

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
            r.readByte();
            friendList.add(new UserInfo(userId,userName,profileImageCode,userMemo,isLogin,false));
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                friendListAdapter.notifyDataSetChanged();
            }
        });

    }

    public void readRequestList(LittleEndianReader r){

        requestList.clear();

        int size = r.readInt();

        for(int i =0; i< size; i++){
            long n_userId = r.readLong();
            String n_userName = r.readLengthAsciiString();
            int n_profileImageCode = r.readInt();
            int n_userWin = r.readInt();
            int n_userLose = r.readInt();
            int n_userPopularity = r.readInt();
            String n_userMemo = r.readLengthAsciiString();
            boolean isLogin = r.readByte() == 1;
            r.readByte();
            requestList.add(new UserInfo(n_userId,n_userName,n_profileImageCode,n_userMemo,isLogin,false));
        }


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(requestList.size() > 0){
                    btn_request.setBackgroundResource(R.drawable.btn_friend_alarm_new);
                }else{
                    btn_request.setBackgroundResource(R.drawable.btn_friend_alarm);
                }
                if(friendRequestListAdapter != null){
                    friendRequestListAdapter.notifyDataSetChanged();
                }
            }
        });
    }


    public static void openFriendRequestDialog(){
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
                LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_firend_request,null);
                ListView dialog_requestF_list = linearLayout.findViewById(R.id.dialog_requestF_list);
                Button btn_close_requestF = linearLayout.findViewById(R.id.btn_close_requestF);

                friendRequestListAdapter  = new FriendRequestListAdapter(activity,R.layout.item_friend_requestlist,requestList);
                dialog_requestF_list.setAdapter(friendRequestListAdapter);


                builder.setView(linearLayout);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_close_requestF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });


            }
        },0);
    }





}
