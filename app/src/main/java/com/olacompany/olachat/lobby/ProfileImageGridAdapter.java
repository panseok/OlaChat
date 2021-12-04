package com.olacompany.olachat.lobby;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.LobbyPacket;
import com.olacompany.olachat.packet.RoomPacket;

public class ProfileImageGridAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private LayoutInflater inf;

    private Integer profileImages[] = {
            R.drawable.profile_default_image, R.drawable.profile_1_image,
            R.drawable.profile_2_image,R.drawable.profile_3_image,
            R.drawable.profile_4_image,R.drawable.profile_5_image,
            R.drawable.profile_6_image,R.drawable.profile_7_image,
            R.drawable.profile_8_image,R.drawable.profile_9_image,
            R.drawable.profile_10_image,R.drawable.profile_11_image
    };

    public ProfileImageGridAdapter(Context context, int layout){
        this.context = context;
        this.layout = layout;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return profileImages.length;
    }

    @Override
    public Object getItem(int i) {
        return profileImages[i];
    }

    @Override
    public long getItemId(int i) {
        return profileImages[i];
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inf.inflate(layout, parent,false);
        }

        FrameLayout frameLayout = convertView.findViewById(R.id.dialog_profile_image_layout);

        frameLayout.setBackgroundResource(profileImages[i]);

        ImageView barImage = convertView.findViewById(R.id.dialog_default_bar_image);
        //imageView.setBackgroundResource(profileImages[i]);


        barImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevTools.startProgressDialog(LobbyActivity.getInstance());
                NettyClient.getSession().writeAndFlush(LobbyPacket.sendProfileImageChange(i));
            }
        });



        return convertView;
    }
}
