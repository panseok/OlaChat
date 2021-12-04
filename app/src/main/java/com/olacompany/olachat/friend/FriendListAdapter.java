package com.olacompany.olachat.friend;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.LobbyPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendListAdapter extends BaseAdapter {

    private Activity mActivity;
    private Context mContext;
    private int layout;
    private LayoutInflater inf;
    private List<UserInfo> friendList = Collections.synchronizedList(new ArrayList<>());



    public FriendListAdapter(Context context, int layout, List<UserInfo> friendList){
        this.mContext = context;
        this.layout = layout;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.friendList = friendList;
    }


    @Override
    public int getCount() {
        return friendList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return friendList.get(i).getUserId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = inf.inflate(layout, viewGroup,false);
        }
        LinearLayout friend_userlist_layout = view.findViewById(R.id.friend_userlist_layout);


        FrameLayout profileImage = view.findViewById(R.id.friend_user_profile_image_layout);
        ImageView profileBar = view.findViewById(R.id.friend_user_default_bar_image);
        ImageView online = view.findViewById(R.id.friend_isOnline);
        TextView userName = view.findViewById(R.id.friend_username);
        TextView userMemo = view.findViewById(R.id.friend_usermemo);
        TextView isOnline_text = view.findViewById(R.id.friend_isOnline_text);

        userName.setTextColor(Color.WHITE);
        isOnline_text.setTextColor(Color.WHITE);

        profileImage.setBackgroundResource(DevTools.getProfileImageId(friendList.get(i).getProfile_code()));
        userName.setText(friendList.get(i).getUserName());
        userMemo.setText(friendList.get(i).getUserMemo());
        online.setBackgroundResource(friendList.get(i).isLogin() ? R.drawable.img_user_on : R.drawable.img_user_off);
        isOnline_text.setText(friendList.get(i).isLogin() ? "온라인" : "오프라인");

        friend_userlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(LobbyPacket.sendOpenProfile(friendList.get(i).getUserId()));
            }
        });

        return view;
    }
}
