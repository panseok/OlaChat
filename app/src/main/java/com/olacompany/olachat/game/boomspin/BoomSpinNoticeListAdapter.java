package com.olacompany.olachat.game.boomspin;

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
import com.olacompany.olachat.room.RoomChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoomSpinNoticeListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private LayoutInflater inf;
    private List<RoomChat> gameNoticeList = Collections.synchronizedList(new ArrayList<>());
    private FrameLayout notice_layout,chat_profile_image_layout;
    private LinearLayout user_chat_layout;

    public BoomSpinNoticeListAdapter(Context context , int layout, List<RoomChat> gameNoticeList){
        this.context = context;
        this.layout = layout;
        this.gameNoticeList = gameNoticeList;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return gameNoticeList.size();
    }

    @Override
    public Object getItem(int position) {
        return gameNoticeList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inf.inflate(layout, viewGroup,false);
        }

        user_chat_layout = view.findViewById(R.id.user_chat_layout);
        chat_profile_image_layout = view.findViewById(R.id.chat_profile_imagelayout);
        notice_layout = view.findViewById(R.id.room_notice_layout);
        LinearLayout chat_layout = view.findViewById(R.id.chat_layout);
        TextView noticeChat = view.findViewById(R.id.chat_notice);

        if(gameNoticeList.get(i).isNotice()){  //알림 및 공지 레이아웃
            user_chat_layout.setVisibility(View.GONE);
            chat_profile_image_layout.setVisibility(View.GONE);
            chat_layout.setVisibility(View.GONE);
            notice_layout.setVisibility(View.VISIBLE);
            noticeChat.setTextColor(Color.WHITE);
            noticeChat.setText(DevTools.setTextColor(gameNoticeList.get(i).getUserChat()));
        }

        return view;
    }
}
