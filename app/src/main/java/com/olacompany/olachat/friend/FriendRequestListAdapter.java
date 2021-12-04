package com.olacompany.olachat.friend;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.FriendPacket;
import com.olacompany.olachat.packet.LobbyPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendRequestListAdapter extends BaseAdapter {

    private List<UserInfo> requestList = Collections.synchronizedList(new ArrayList<>());
    private Context context;
    private LayoutInflater inf;
    int layout;

    public FriendRequestListAdapter(Context context, int layout, List<UserInfo> requestList){
        this.context = context;
        this.layout = layout;
        this.requestList = requestList;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return requestList.size();
    }

    @Override
    public Object getItem(int i) {
        return requestList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inf.inflate(layout, viewGroup,false);
        }

        LinearLayout friend_requestlist_layout = view.findViewById(R.id.friend_requestlist_layout);


        FrameLayout profileImage = view.findViewById(R.id.dialog_requestF_profile_image_layout);
        ImageView profileBar = view.findViewById(R.id.dialog_requestF_default_bar_image);
        TextView userName = view.findViewById(R.id.dialog_requestF_username);
        userName.setTextColor(Color.WHITE);
        ImageButton btn_dialog_requestF_succeed = view.findViewById(R.id.btn_dialog_requestF_succeed);
        ImageButton btn_dialog_requestF_refuse = view.findViewById(R.id.btn_dialog_requestF_refuse);


        profileImage.setBackgroundResource(DevTools.getProfileImageId(requestList.get(i).getProfile_code()));
        userName.setText(requestList.get(i).getUserName());

        friend_requestlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(LobbyPacket.sendOpenProfile(requestList.get(i).getUserId()));
            }
        });

        btn_dialog_requestF_succeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(FriendPacket.sendRequestFriendAccept(requestList.get(i).getUserId()));
            }
        });

        btn_dialog_requestF_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(FriendPacket.sendRequestFriendRefuse(requestList.get(i).getUserId()));

            }
        });



        return view;
    }
}
