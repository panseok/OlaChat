package com.olacompany.olachat.game.boomspin;

import android.content.Context;
import android.view.Gravity;
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
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.room.RoomChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoomSpinChatListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<RoomChat> gameChatList = Collections.synchronizedList(new ArrayList<>());
    private LayoutInflater inf;
    private FrameLayout notice_layout,chat_profile_image_layout;

    public BoomSpinChatListAdapter(Context context, int layout ,List<RoomChat> gameChatList){
        this.context = context;
        this.layout = layout;
        this.gameChatList = gameChatList;
        inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }
    @Override
    public int getCount() {
        return gameChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameChatList.get(position);
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

        chat_profile_image_layout = convertView.findViewById(R.id.chat_profile_imagelayout);
        notice_layout = convertView.findViewById(R.id.room_notice_layout);
        ImageView chat_profile_bar_image = convertView.findViewById(R.id.chat_profile_bar_image);

        LinearLayout chat_layout = convertView.findViewById(R.id.chat_layout);

        TextView userName = convertView.findViewById(R.id.chat_userName);
        TextView userChat = convertView.findViewById(R.id.chat_data);

        TextView noticeChat = convertView.findViewById(R.id.chat_notice);

        userName.setText(gameChatList.get(i).getUserName());
        userChat.setText(gameChatList.get(i).getUserChat());

        if(gameChatList.get(i).getUserName().equals(NettyClient.getUserName())){
            notice_layout.setVisibility(View.GONE);

            chat_profile_image_layout.setVisibility(View.GONE);
            chat_layout.setVisibility(View.VISIBLE);
            userName.setVisibility(View.GONE);
            userChat.setBackgroundResource(R.drawable.chat_my);
            chat_layout.setGravity(Gravity.RIGHT);

            if(i > 0){
                if(gameChatList.get(i-1).getUserName().equals(gameChatList.get(i).getUserName())){
                    userChat.setBackgroundResource(R.drawable.chat_all);
                }
            }

        }else{
            notice_layout.setVisibility(View.GONE);
            chat_profile_image_layout.setVisibility(View.VISIBLE);
            chat_layout.setVisibility(View.VISIBLE);
            userName.setVisibility(View.VISIBLE);

            chat_profile_image_layout.setBackgroundResource(DevTools.getProfileImageId(gameChatList.get(i).getUserProfileImageCode()));
            chat_profile_bar_image.setBackgroundResource(R.drawable.profile_bar_default_image);
            userChat.setBackgroundResource(R.drawable.chat_user);
            chat_layout.setGravity(Gravity.LEFT);

            if(i > 0){
                if(gameChatList.get(i-1).getUserName().equals(gameChatList.get(i).getUserName())){
                    userChat.setBackgroundResource(R.drawable.chat_all);
                    chat_profile_image_layout.setVisibility(View.GONE);
                    userName.setVisibility(View.GONE);
                }
            }

        }

        if(gameChatList.get(i).isNotice()){  //알림 및 공지 레이아웃
            chat_profile_image_layout.setVisibility(View.GONE);
            chat_layout.setVisibility(View.GONE);
            notice_layout.setVisibility(View.VISIBLE);
            noticeChat.setText(gameChatList.get(i).getUserChat());
        }

        return convertView;
    }
}
