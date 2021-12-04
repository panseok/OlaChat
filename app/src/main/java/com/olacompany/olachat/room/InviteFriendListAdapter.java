package com.olacompany.olachat.room;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.RoomPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InviteFriendListAdapter extends BaseAdapter{

    private List<UserInfo> friendList = Collections.synchronizedList(new ArrayList<>());
    private int layout;
    private LayoutInflater inf;
    private Context context;

    public InviteFriendListAdapter(Context context, int layout, List<UserInfo> onlineList){
        this.context = context;
        this.layout = layout;
        this.friendList = onlineList;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int i) {
        return friendList.get(i);
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

        LinearLayout friend_requestlist_layout = view.findViewById(R.id.friend_invitelist_layout);
        FrameLayout profileImage = view.findViewById(R.id.dialog_inviteF_profile_image_layout);
        ImageView profileBar = view.findViewById(R.id.dialog_inviteF_default_bar_image);
        TextView userName = view.findViewById(R.id.dialog_inviteF_username);
        userName.setTextColor(Color.WHITE);
        Button btn_inviteF = view.findViewById(R.id.btn_invite);

        profileImage.setBackgroundResource(DevTools.getProfileImageId(friendList.get(i).getProfile_code()));
        userName.setText(friendList.get(i).getUserName());

        btn_inviteF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(RoomPacket.sendInviteFriend(friendList.get(i)));
            }
        });

        return view;
    }
}
