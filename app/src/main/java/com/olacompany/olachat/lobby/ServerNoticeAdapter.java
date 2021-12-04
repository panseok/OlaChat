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
import com.olacompany.olachat.game.boomspin.PlayerSkill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ServerNoticeAdapter extends BaseAdapter {

    List<ServerNotice> ServerNotices = Collections.synchronizedList(new ArrayList<>());
    private Context context;
    private int layout;
    private LayoutInflater inf;

    public ServerNoticeAdapter(Context context, int layout , List<ServerNotice> serverNotices){
        this.context = context;
        this.layout = layout;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ServerNotices = serverNotices;
    }


    @Override
    public int getCount() {
        return ServerNotices.size();
    }

    @Override
    public Object getItem(int i) {
        return ServerNotices.get(i);
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
        LinearLayout showCommentLayout = view.findViewById(R.id.server_notice_layout);
        TextView noticeType = view.findViewById(R.id.notice_type);
        TextView noticeName = view.findViewById(R.id.notice_name);
        TextView noticeComment = view.findViewById(R.id.notice_comment);
        TextView noticeDate = view.findViewById(R.id.notice_date);

        noticeType.setTextColor(Color.GREEN);

        if(ServerNotices.get(i).getType().equals("[보안]")){
            noticeType.setTextColor(Color.RED);
        }else if(ServerNotices.get(i).getType().equals("[이벤트]")){
            noticeType.setTextColor(Color.YELLOW);
        }else if(ServerNotices.get(i).getType().equals("[업데이트]")){
            noticeType.setTextColor(Color.MAGENTA);
        }else if(ServerNotices.get(i).getType().equals("[알림]")){
            noticeType.setTextColor(Color.GREEN);
        }else{
            noticeType.setTextColor(Color.WHITE);
        }


        noticeName.setTextColor(Color.WHITE);
        noticeComment.setTextColor(Color.WHITE);
        noticeDate.setTextColor(Color.GRAY);

        noticeType.setText(ServerNotices.get(i).getType());
        noticeName.setText(ServerNotices.get(i).getName());
        noticeComment.setText(ServerNotices.get(i).getComment());
        noticeDate.setText(ServerNotices.get(i).getDate());

        noticeComment.setVisibility(View.GONE);

        showCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (noticeComment.getVisibility()){
                    case View.VISIBLE:{
                        noticeComment.setVisibility(View.GONE);
                        break;
                    }
                    case View.GONE:{
                        noticeComment.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }
        });

        return view;
    }
}
