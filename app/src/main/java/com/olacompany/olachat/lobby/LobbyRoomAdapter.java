package com.olacompany.olachat.lobby;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.RoomPacket;
import com.olacompany.olachat.room.ChatRoom;

import java.util.List;

public class LobbyRoomAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private List<ChatRoom> lobbyRoomList;
    private LayoutInflater inf;

    public LobbyRoomAdapter(Context context, int layout, List<ChatRoom> lobbyRoomList){
        this.context = context;
        this.layout = layout;
        this.lobbyRoomList = lobbyRoomList;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return lobbyRoomList.size();
    }

    @Override
    public Object getItem(int position) {
        return lobbyRoomList.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if (convertView == null) {
            convertView = inf.inflate(layout, parent,false);
        }
        TextView num = (TextView)convertView.findViewById(R.id.lobbyroom_num);
        TextView name = (TextView)convertView.findViewById(R.id.lobbyroom_name);
        TextView membercount = (TextView)convertView.findViewById(R.id.lobbyroom_member_count);
        TextView memberMaxcount = (TextView)convertView.findViewById(R.id.lobbyroom_member_maxcount);
        num.setTextColor(Color.YELLOW);
        membercount.setTextColor(Color.GREEN);
        memberMaxcount.setTextColor(Color.GREEN);
        num.setText(lobbyRoomList.get(position).getRoom_number().toString()+"번");
        name.setText(lobbyRoomList.get(position).getRoom_name());
        membercount.setText(lobbyRoomList.get(position).getRoom_member_count()+"");
        memberMaxcount.setText(lobbyRoomList.get(position).getRoom_member_maxcount()+"");

        if(lobbyRoomList.get(position).getRoom_member_maxcount() == lobbyRoomList.get(position).getRoom_member_count()){
            membercount.setTextColor(Color.RED);
            memberMaxcount.setTextColor(Color.RED);
        }

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.lobby_layout);


        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevTools.startProgressDialog(LobbyActivity.getInstance());
                NettyClient.getSession().writeAndFlush(RoomPacket.getJoinRoomRequest(lobbyRoomList.get(position),NettyClient.getUserId()));
            }
        });


        return convertView;
    }
}
