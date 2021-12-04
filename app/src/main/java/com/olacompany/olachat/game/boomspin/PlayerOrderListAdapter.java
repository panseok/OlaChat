package com.olacompany.olachat.game.boomspin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.olacompany.olachat.R;
import com.olacompany.olachat.netty.NettyClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerOrderListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private LayoutInflater inf;
    private List<PlayerInfo> players = new ArrayList<>();

    public PlayerOrderListAdapter(Context context,int layout , List<PlayerInfo> players){
        this.context = context;
        this.layout = layout;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.players = players;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return players.get(i);
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

        ImageView deadImg = view.findViewById(R.id.player_dead_img);
        ImageView boomImg = view.findViewById(R.id.player_boom_img);
        FrameLayout myImg = view.findViewById(R.id.player_me_img);
        TextView player_name = view.findViewById(R.id.player_name);


        player_name.setText(players.get(i).getUser().getUserName());
        deadImg.setVisibility(players.get(i).isAlive() ? View.INVISIBLE : View.VISIBLE);

        boomImg.setVisibility(!players.get(i).isMyturn() ? View.INVISIBLE : View.VISIBLE);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(boomImg);
        Glide.with(context).load(R.drawable.turn_img).into(gifImage);

        if(players.get(i).isMyturn()){
            myImg.setBackgroundResource(R.drawable.game_boom_spin_user_no_img);
        }else{
            myImg.setBackgroundResource(R.drawable.game_boom_spin_user);
        }

        if(players.get(i).getUser().getUserId() == NettyClient.getUserId()){
            player_name.setTextColor(Color.YELLOW);
        }else{
            player_name.setTextColor(Color.WHITE);
        }

        return view;
    }
}
