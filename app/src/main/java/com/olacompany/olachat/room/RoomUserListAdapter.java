package com.olacompany.olachat.room;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.opcode.SendOpcode;
import com.olacompany.olachat.packet.LobbyPacket;
import com.olacompany.olachat.packet.RoomPacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomUserListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<UserInfo> userLists = Collections.synchronizedList(new ArrayList<>());
    private LayoutInflater inf;
    private FrameLayout notice_layout,chat_profile_layout;

    public RoomUserListAdapter(Context context, int layout , List<UserInfo> userLists){
        this.context = context;
        this.layout = layout;
        this.userLists = userLists;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return userLists.size();
    }

    @Override
    public Object getItem(int position) {
        return userLists.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = inf.inflate(layout, viewGroup,false);
        }

        LinearLayout userlist_layout = convertView.findViewById(R.id.room_userlist_layout);
        FrameLayout profileImage = convertView.findViewById(R.id.room_user_profile_image_layout);
        ImageView profileBar = convertView.findViewById(R.id.room_user_default_bar_image);
        ImageView leaderImg = convertView.findViewById(R.id.room_leader_img);
        ImageView room_player_ban = convertView.findViewById(R.id.room_player_ban);
        TextView userName = convertView.findViewById(R.id.room_username);

        profileImage.setBackgroundResource(DevTools.getProfileImageId(userLists.get(i).getProfile_code()));
        userName.setText(userLists.get(i).getUserName());

        leaderImg.setVisibility(userLists.get(i).isRoomLeader() ? View.VISIBLE : View.GONE);
        if(NettyClient.getUserId() == userLists.get(i).getUserId()){
            room_player_ban.setVisibility(View.GONE);
        }else{
            room_player_ban.setVisibility(View.VISIBLE);
        }


        userlist_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(LobbyPacket.sendOpenProfile(userLists.get(i).getUserId()));
            }
        });

        room_player_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatRoomActivity.getInstance().OpenBanUserDialog(userLists.get(i));
            }
        });

        return convertView;
    }

    public void setLeaderView(

    ){

    }
}
