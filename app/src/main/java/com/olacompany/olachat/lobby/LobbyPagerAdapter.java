package com.olacompany.olachat.lobby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.lobby.fragment.FragmentLobbyRoomlist;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.LobbyPacket;

import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

public class LobbyPagerAdapter extends PagerAdapter {

    // LayoutInflater 서비스 사용을 위한 Context 참조 저장.
    private Context mContext = null ;
    private LobbyActivity lobbyActivity;


    //프로필
    public ImageButton lobby_profile_bar_imageBtn;
    public FrameLayout lobby__profile_image;
    public TextView lobby_userName, lobby_userPopularity, lobby_userWin, lobby_userLose, lobby_userMemo, lobby_userRecordPercent;

    public static LobbyPagerAdapter lobbyPagerAdapter;


    //방 목록
    public ListView lobby_listview_room;
    public LobbyRoomAdapter lobbyRoomAdapter;
    public LayoutInflater inflater;
    public FragmentLobbyRoomlist fragmentLobbyRoomlist;


    public static LobbyPagerAdapter getInstance(){
        return lobbyPagerAdapter;
    }

    public LobbyPagerAdapter(){

    }

    public LobbyPagerAdapter(LobbyActivity lobbyActivity) {
        lobbyPagerAdapter = this;
        this.mContext = lobbyActivity;
        this.lobbyActivity = lobbyActivity;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = null ;
        if (mContext != null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch (position){
                case 0:{ //프로필
                    view = inflater.inflate(R.layout.fragment_lobby_profile, container, false);
                    lobby__profile_image = (FrameLayout) view.findViewById(R.id.lobby_profile_image_layout);
                    lobby_profile_bar_imageBtn = (ImageButton) view.findViewById(R.id.profile_default_bar_imagebtn);
                    lobby_userName = (TextView) view.findViewById(R.id.lobby_userName);
                    lobby_userPopularity = (TextView) view.findViewById(R.id.lobby_userPopularity);
                    lobby_userWin = (TextView) view.findViewById(R.id.lobby_userWin);
                    lobby_userLose = (TextView) view.findViewById(R.id.lobby_userLose);
                    lobby_userRecordPercent = (TextView) view.findViewById(R.id.lobby_userRecordPercent);
                    lobby_userMemo = (TextView) view.findViewById(R.id.lobby_userMemo);


                    lobby_userPopularity.setTextColor(Color.YELLOW);
                    lobby_userWin.setTextColor(Color.WHITE);
                    lobby_userLose.setTextColor(Color.WHITE);
                    lobby_userRecordPercent.setTextColor(Color.WHITE);

                    lobby_profile_bar_imageBtn.setBackgroundResource(R.drawable.profile_bar_default_image);
                    lobby__profile_image.setBackgroundResource(DevTools.getProfileImageId(NettyClient.getUserProfileImageCode()));
                    lobby_userName.setText(NettyClient.getUserName());
                    lobby_userPopularity.setText("인기도 : "+NettyClient.getUserPopularity());
                    lobby_userWin.setText(NettyClient.getUserWin()+" 승");
                    lobby_userLose.setText(NettyClient.getUserLose()+" 패");

                    int count = NettyClient.getUserWin() + NettyClient.getUserLose();
                    double win_percent = ((double) NettyClient.getUserWin() / count) * 100.0;
                    lobby_userRecordPercent.setText("("+String.format("%.2f",win_percent)+"%)");
                    lobby_userMemo.setText(NettyClient.getUserMemo());

                    lobby_profile_bar_imageBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CreateProfileImageSettingAlertDialog(inflater,lobbyActivity);
                        }
                    });

                    lobby_userMemo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            CreateUserMemoAlertDialog(inflater,lobbyActivity);
                        }
                    });

                    break;
                }
                case 1:{ //방목록
                    view = inflater.inflate(R.layout.fragment_lobby_roomlist, container, false);
                    //fragmentLobbyRoomlist = new FragmentLobbyRoomlist();
                    //view = fragmentLobbyRoomlist.onCreateView(inflater,container,null);
                    lobby_listview_room = (ListView) view.findViewById(R.id.lobby_listview_room);
                    lobbyRoomAdapter = new LobbyRoomAdapter(mContext,R.layout.item_lobbyroom,lobbyActivity.getLobbyRoomList());
                    lobby_listview_room.setAdapter(lobbyRoomAdapter);
                    break;
                }
               /* case 2:{
                    view = inflater.inflate(R.layout.fragment_lobby_test, container, false);
                    break;
                }*/

            }
        }

        container.addView(view) ;

        return view ;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // 뷰페이저에서 삭제.
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public int getItemPosition(@NonNull Object object){
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return (view == (View)object);
    }


    public static void CreateProfileImageSettingAlertDialog(LayoutInflater inflater,LobbyActivity activity){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inflater == null) {
                    Log.e("LoginActivity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.alertdialog_profileimage_setting,null);
                GridView tet = (GridView) linearLayout.findViewById(R.id.lobby_grid);
                Button btnClose = (Button) linearLayout.findViewById(R.id.btn_close_profile_setting);

                ProfileImageGridAdapter adapter = new ProfileImageGridAdapter(activity,R.layout.item_profile);
                tet.setAdapter(adapter);
                builder.setView(linearLayout);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

            }
        },0);
    }

    public static void CreateUserMemoAlertDialog(LayoutInflater inflater,LobbyActivity activity){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inflater == null) {
                    Log.e("LoginActivity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.alertdialog_usermemo,null);
                EditText edit_usermemo = (EditText) linearLayout.findViewById(R.id.edit_usermemo);
                Button btnClose = (Button) linearLayout.findViewById(R.id.btn_close_usermemo);
                Button btnOk = (Button) linearLayout.findViewById(R.id.btn_ok_usermemo);

                edit_usermemo.setText(NettyClient.getUserMemo());

                builder.setView(linearLayout);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btnClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String memo = edit_usermemo.getText().toString();
                        NettyClient.getSession().writeAndFlush(LobbyPacket.sendChangeUserMemo(memo));
                        edit_usermemo.setText("");
                        alertDialog.dismiss();
                    }
                });

            }
        },0);
    }

    public void getChangeProfileImageResult(LittleEndianReader r){
        boolean result = r.readByte() == 1;
        int imageCode = r.readInt();
        if(result){
            NettyClient.setUserProfileImageCode(imageCode);
            lobby_profile_bar_imageBtn.setBackgroundResource(R.drawable.profile_bar_default_image);
            lobby__profile_image.setBackgroundResource(DevTools.getProfileImageId(NettyClient.getUserProfileImageCode()));
            DevTools.showToastShortMsg(mContext,"정상적으로 변경되었습니다.");
        }else{
            DevTools.showToastShortMsg(mContext,"에러가 발생하였습니다.");
        }
        DevTools.stopProgressDialog();
    }

    public void getChangeUserMemoResult(LittleEndianReader r){
        boolean result = r.readByte() == 1;
        String memo = r.readLengthAsciiString();
        if(result){
            NettyClient.setUserMemo(memo);
            lobby_userMemo.setText(memo);
            DevTools.showToastShortMsg(mContext,"정상적으로 변경되었습니다.");
        }else{
            DevTools.showToastShortMsg(mContext,"에러가 발생하였습니다.");
        }
    }
}
