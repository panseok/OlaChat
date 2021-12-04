package com.olacompany.olachat.game.boomspin;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.User.UserInfo;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.BoomSpinPacket;
import com.olacompany.olachat.room.RoomChat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

public class BoomSpinActivity extends AppCompatActivity {

    private GridView playerOrderView;
    private LinearLayout boom_spin_Boom_layout;
    private ListView game_boom_spin_notice_list, game_chat_listview, game_boom_spin_myskill_list, game_user_help_list, game_user_passive_skill_list, game_user_auction_skill_list;
    private DrawerLayout drawerLayout;
    private ImageView btn_game_boom_spin_drawer, btn_game_boom_skill_book,boom_img;
    private ImageButton btn_game_boom_spin_send_chat;
    private Button btn_boom_spin;
    private EditText chat_edittext;
    private TextView boom_time, boom_spin_mycoin, boom_spin_day;

    private PlayerOrderListAdapter playerOrderListAdapter;
    private BoomSpinNoticeListAdapter boomSpinNoticeListAdapter;
    private BoomSpinChatListAdapter boomSpinChatListAdapter;
    private PlayerSkillListAdapter playerSkillListAdapter,  userHelpListAdapter,  passiveSkillListAdapter,  auctionSkillListAdapter;

    private List<PlayerInfo> players = Collections.synchronizedList(new ArrayList<>());
    private List<RoomChat> notice = Collections.synchronizedList(new ArrayList<>());
    private List<RoomChat> chat = Collections.synchronizedList(new ArrayList<>());
    private List<PlayerSkill> playerskills = Collections.synchronizedList(new ArrayList<>());

    private List<PlayerSkill> help_comment_list = Collections.synchronizedList(new ArrayList<>());
    private List<PlayerSkill> passive_skill_List = Collections.synchronizedList(new ArrayList<>());
    private List<PlayerSkill> auction_skill_List = Collections.synchronizedList(new ArrayList<>());


    private static BoomSpinActivity boomSpinActivity;

    public static BoomSpinActivity getInstance(){
        return boomSpinActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_boom_spin);
        boomSpinActivity = this;

        boom_spin_Boom_layout = findViewById(R.id.boom_spin_mytrun_layout);
        playerOrderView = findViewById(R.id.game_boom_spin_grid);
        drawerLayout = findViewById(R.id.game_boom_spin_drawer_layout);
        btn_game_boom_spin_drawer = findViewById(R.id.btn_game_boom_spin_drawer);
        game_boom_spin_notice_list = findViewById(R.id.game_boom_spin_notice_list);
        game_chat_listview = findViewById(R.id.game_chat_listview);
        game_boom_spin_myskill_list = findViewById(R.id.game_boom_spin_myskill_list);
        game_user_help_list = findViewById(R.id.game_user_help_list);
        game_user_passive_skill_list = findViewById(R.id.game_user_passive_skill_list);
        game_user_auction_skill_list = findViewById(R.id.game_user_auction_skill_list);
        btn_game_boom_spin_send_chat = findViewById(R.id.btn_game_boom_spin_send_chat);
        btn_game_boom_skill_book = findViewById(R.id.btn_game_boom_skill_book);
        chat_edittext = findViewById(R.id.chat_edittext);
        boom_time = findViewById(R.id.boom_time);
        boom_spin_mycoin = findViewById(R.id.boom_spin_mycoin);
        boom_spin_day = findViewById(R.id.boom_spin_day);
        btn_boom_spin = findViewById(R.id.btn_boom_spin);
        boom_img = findViewById(R.id.boom_img);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(boom_img);
        Glide.with(this).load(R.drawable.boom_turn_unscreen).into(gifImage);

        playerOrderListAdapter = new PlayerOrderListAdapter(this,R.layout.item_game_boom_spin_player, players);
        playerOrderView.setAdapter(playerOrderListAdapter);

        boomSpinNoticeListAdapter = new BoomSpinNoticeListAdapter(this,R.layout.item_chat, notice);
        game_boom_spin_notice_list.setAdapter(boomSpinNoticeListAdapter);

        boomSpinChatListAdapter = new BoomSpinChatListAdapter(this,R.layout.item_chat, chat);
        game_chat_listview.setAdapter(boomSpinChatListAdapter);

        userHelpListAdapter = new PlayerSkillListAdapter(this, R.layout.item_boomspin_skill, help_comment_list);
        game_user_help_list.setAdapter(userHelpListAdapter);

        passiveSkillListAdapter = new PlayerSkillListAdapter(this,R.layout.item_boomspin_skill,passive_skill_List);
        game_user_passive_skill_list.setAdapter(passiveSkillListAdapter);

        auctionSkillListAdapter = new PlayerSkillListAdapter(this,R.layout.item_boomspin_skill,auction_skill_List);
        game_user_auction_skill_list.setAdapter(auctionSkillListAdapter);

        playerSkillListAdapter = new PlayerSkillListAdapter(this,R.layout.item_boomspin_skill,playerskills);
        game_boom_spin_myskill_list.setAdapter(playerSkillListAdapter);

        //서버에서 쏴줘야될 내용들이지만 짜긴 귀찮음 나중에 하기로..
        help_comment_list.add(new PlayerSkill(-1, "[폭탄돌리기]", "#p[최후의 1인]#l#w이 될때까지 살아남아라!#l"));
        help_comment_list.add(new PlayerSkill(-1, "[기본]", "#w날마다 폭탄이 1개씩 돌아가며#l #p[최후의 생존자]#l#w가 나올때까지 진행된다.#l"));
        help_comment_list.add(new PlayerSkill(-1, "[코인]", "#w날이 바뀔때 마다 #l#p[1000 코인]#l#w씩 지급되며 경매에서 쓸 수 있다.#l"));
        help_comment_list.add(new PlayerSkill(-1, "[능력]", "#w날이 바뀔때 마다 기본 능력이 초기화되며 기본 능력 목록 중 1개의 능력이 선정된다.#l"));
        help_comment_list.add(new PlayerSkill(-1, "[경매]", "#w날이 바뀔때 마다 시작되며 경매 능력 목록 중 1개의 능력이 판매된다.#l"));
        help_comment_list.add(new PlayerSkill(-1, "[채팅]", "#w오른쪽 상단의 말풍선 버튼을 이용하여 플레이어들과 채팅을 할 수 있다.#l"));

        passive_skill_List.add(new PlayerSkill(-1, "#r[사망]#l", "#w여긴 어디일까 할 수 있는 것은 없어 보인다. 상황을 지켜보자.#l"));
        passive_skill_List.add(new PlayerSkill(0, "#r[봉인]#l", "#w턴마다 #l#p[5초]#l#w간 폭탄을 돌릴 수 없다.#l"));
        passive_skill_List.add(new PlayerSkill(1, "#o[자폭]#l", "#w폭탄이 터지면 플레이어 중 한명을 데려간다.#l"));
        passive_skill_List.add(new PlayerSkill(2, "#r[오작동]#l", "#w폭탄을 돌리지 않으면 #l#p[5초]#l#w마다#l #p[1 * 경과된 초%]#l #w확률로 폭탄이 터진다.#l"));
        passive_skill_List.add(new PlayerSkill(3, "#g[방탄헬멧]#l", "#p[25%]#l #w확률로 #l#o[자폭] [럭키가이]#l#w  으로부터 생존 할 수 있다.#l"));
        passive_skill_List.add(new PlayerSkill(4, "#o[불장난]#l", "#w폭탄을 돌릴때 마다 폭파 시간을 #l#p[3초]#l#w씩 앞당긴다.#l"));
        passive_skill_List.add(new PlayerSkill(5, "#g[방패]#l", "#w폭탄이 터지는 것으로 부터 생존 할 수 있다.#l"));

        auction_skill_List.add(new PlayerSkill(10, "#g[방탄조끼]#l", "#w폭탄이 터지거나#l #o[자폭] [럭키가이]#l #w으로부터 생존 할 수 있다.#l"));
        auction_skill_List.add(new PlayerSkill(11, "#o[럭키가이]#l", "#p[25%]#l #w확률로 폭탄이 터지면 플레이어 중 한명이 대신 피해를 받는다.#l"));
        auction_skill_List.add(new PlayerSkill(12, "#g[천리안]#l", "#w폭탄이 언제 터지는지 타이머를 볼 수 있게 된다.#l"));
        auction_skill_List.add(new PlayerSkill(13, "#g[시간왜곡]#l", "#w나를 제외한 플레이어들에게 폭탄을 턴마다 5초간 돌릴 수 없게 만든다.#l"));
        auction_skill_List.add(new PlayerSkill(14, "#y[바이러스]#l", "#w모든 플레이어들이 폭탄을 돌릴때마다#l #p[1~10초]#l#w씩 폭파 시간을 앞당긴다.#l"));
        auction_skill_List.add(new PlayerSkill(15, "#y[새치기]#l", "#w폭탄이 플레이어 순서대로 돌아가지 않고#l #p[랜덤]#l#w으로 돌아간다.#l"));

        boom_spin_Boom_layout.setVisibility(View.GONE);

        btn_game_boom_spin_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawerLayout.isDrawerOpen(Gravity.RIGHT)){
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }else{
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                }
            }
        });

        btn_game_boom_skill_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!drawerLayout.isDrawerOpen(Gravity.LEFT)){
                    drawerLayout.openDrawer(Gravity.LEFT);
                }else{
                    drawerLayout.closeDrawer(Gravity.LEFT);
                }
            }
        });


        btn_game_boom_spin_send_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = chat_edittext.getText().toString();
                if(!text.equals("")){
                    sendChat(text);
                    chat_edittext.setText("");
                }
            }
        });

        btn_boom_spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NettyClient.getSession().writeAndFlush(BoomSpinPacket.sendBoomSpin());
            }
        });
    }



    public void sendChat(String msg){
        NettyClient.getSession().writeAndFlush(BoomSpinPacket.sendBoomSpinChat(NettyClient.getUserId(),NettyClient.getUserName(),msg,NettyClient.getUserProfileImageCode()));
    }



    public void readChat(LittleEndianReader r){

        long userId = r.readLong();
        String senderName = r.readLengthAsciiString();
        String msg = r.readLengthAsciiString();
        int userProfileImageCode = r.readInt();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chat.add(new RoomChat(userId,senderName,msg,userProfileImageCode,false));
                boomSpinChatListAdapter.notifyDataSetChanged();
            }
        });

    }


    public void readNotice(LittleEndianReader r){
        String msg = r.readLengthAsciiString();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notice.add(new RoomChat(-1L,"",msg,0,true));
                boomSpinNoticeListAdapter.notifyDataSetChanged();
            }
        });

    }

    public void readBoomTime(LittleEndianReader r){
        String time = r.readLengthAsciiString();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boom_time.setText(time);
            }
        });
    }

    public void readMoney(LittleEndianReader r){
        int money = r.readInt();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boom_spin_mycoin.setText(money+"");
            }
        });
    }

    public void readDay(LittleEndianReader r){
        int day = r.readInt();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                boom_spin_day.setText(day+"");
            }
        });
    }

    public void readIsGetBoom(LittleEndianReader r){
        boolean isGet = r.readByte() == 1;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(isGet){
                    boom_spin_Boom_layout.setVisibility(View.VISIBLE);
                }else{
                    boom_spin_Boom_layout.setVisibility(View.GONE);
                }
            }
        });
    }

    public void readPlayerInfo(LittleEndianReader r){
        players.clear();
        playerskills.clear();

        int playerSize = r.readInt();

        for(int i =0; i < playerSize; i++){
            long userId = r.readLong();
            String userName = r.readLengthAsciiString();
            int profileImageCode = r.readInt();
            int userWin = r.readInt();
            int userLose = r.readInt();
            int userPopularity = r.readInt();
            String userMemo = r.readLengthAsciiString();
            boolean isLogin = r.readByte() == 1;
            boolean isRoomLeader = r.readByte() == 1;

            int skillsize = r.readInt();
            for(int j = 0 ; j < skillsize; j++){
                int skillcode = r.readInt();
                String skillname = r.readLengthAsciiString();
                String skillcomment = r.readLengthAsciiString();
                if(NettyClient.getUserId() == userId){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            playerskills.add(new PlayerSkill(skillcode,skillname,skillcomment));
                            playerSkillListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }

            int money = r.readInt();
            boolean isAlive = r.readByte() == 1;
            boolean isTurn = r.readByte() == 1;
            UserInfo user = new UserInfo(userId,userName,profileImageCode,userMemo,isLogin,isRoomLeader);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    players.add(new PlayerInfo(user,new ArrayList<>(),money,isAlive,isTurn));
                    playerOrderListAdapter.notifyDataSetChanged();

                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(userId == NettyClient.getUserId()) {
                                boom_spin_Boom_layout.setVisibility(isTurn ? View.VISIBLE : View.GONE);
                                if(isTurn){
                                    DevTools.showToastGameBoomSpinMsg(9);
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DevTools.showToastShortMsg(this,"게임 중에는 나갈 수 없습니다.\n게임이 끝나면 자동으로 퇴장됩니다.");
        return;
    }


    private static AlertDialog  alertDialog = null;
    private static LinearLayout linearLayout;
    private static Button btn_bid;
    private static EditText editPrice;
    private static TextView low_bid, now_high_price, my_coin, a_skillname, a_skillcomment, auction_notice, auction_time;

    public static void AuctionAlertDialog(LittleEndianReader r){

        String skillName = r.readLengthAsciiString();
        String skillComment = r.readLengthAsciiString();
        int low_bidR = r.readInt();
        int high_priceR = r.readInt();
        int my_coinR = r.readInt();

        Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity.getLayoutInflater() == null) {
                    Log.e("Activity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_boomspin_auction,null);
                btn_bid = linearLayout.findViewById(R.id.auction_btn_bid);
                editPrice = linearLayout.findViewById(R.id.auction_edit_price);
                low_bid = linearLayout.findViewById(R.id.auction_low_bid);
                now_high_price = linearLayout.findViewById(R.id.auction_now_high_price);
                my_coin = linearLayout.findViewById(R.id.auction_my_coin);
                a_skillname = linearLayout.findViewById(R.id.auction_skillname);
                a_skillcomment = linearLayout.findViewById(R.id.auction_skillcomment);
                auction_notice = linearLayout.findViewById(R.id.auction_notice);
                auction_time = linearLayout.findViewById(R.id.auction_time);

                a_skillname.setTextColor(Color.GREEN);
                a_skillcomment.setTextColor(Color.YELLOW);

                a_skillname.setText(DevTools.setTextColor(skillName));
                a_skillcomment.setText(DevTools.setTextColor(skillComment));
                low_bid.setText(low_bidR+"");
                now_high_price.setText(high_priceR+"");
                my_coin.setText(my_coinR+"");
                editPrice.setText(low_bidR+"");

                builder.setView(linearLayout);
                alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.setCancelable(false);
                alertDialog.show();

                btn_bid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String priceS = editPrice.getText().toString();
                        if(!priceS.equals("")){
                            long l = Long.parseLong(priceS);
                            int price = 0;
                            if(Integer.MAX_VALUE < l){
                                price = Integer.MAX_VALUE;
                            }else{
                                price = Integer.parseInt(priceS);
                                NettyClient.getSession().writeAndFlush(BoomSpinPacket.sendBoomSpinAuctionMybid(price));
                            }
                        }
                    }
                });
            }
        },0);
    }

    public void readAuctionTime(LittleEndianReader r){
        int time = r.readInt();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                auction_time.setText(time+"");
            }
        });
    }

    public void readAuctionNotice(LittleEndianReader r){
        String msg = r.readLengthAsciiString();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                auction_notice.setText(DevTools.setTextColor(msg));
            }
        });

    }

    public void readAuctionInfo(LittleEndianReader r){
        String skillName = r.readLengthAsciiString();
        String skillComment = r.readLengthAsciiString();
        int low_bidR = r.readInt();
        int high_priceR = r.readInt();
        String msg = r.readLengthAsciiString();
        int my_coinR = r.readInt();


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                a_skillname.setText(DevTools.setTextColor(skillName));
                a_skillcomment.setText(DevTools.setTextColor(skillComment));
                auction_notice.setText(DevTools.setTextColor(msg));
                low_bid.setText(low_bidR+"");
                now_high_price.setText(high_priceR+"");
                editPrice.setText(low_bidR+"");
              //  my_coin.setText(my_coinR+"");
            }
        });
    }

    public void handleBoomSpinReceive(LittleEndianReader r){
        /*
         * 0  날
         * 1  알림판
         * 2  폭탄 시간
         * 3  자기폭탄 시간
         * 4  플레이어 리스트
         * 5  돈
         * 6  플레이어 채팅
         * 7  경매 다이얼로그 오픈
         * 8  경매 인포
         * 9  경매 알림
         * 10 경매 타이머
         * 11 경매 종료
         * 12 폭탄이 도착함
         * */
        int code = r.readInt();
        switch (code){
            case 0:{
                readDay(r);
                break;
            }
            case 1:{
                readNotice(r);
                break;
            }
            case 2:{
                readBoomTime(r);
                break;
            }
            case 4:{
                readPlayerInfo(r);
                break;
            }
            case 5:{
                readMoney(r);
                break;
            }
            case 6:{
                readChat(r);
                break;
            }
            case 7:{
                AuctionAlertDialog(r);
                break;
            }

            case 8:{
                readAuctionInfo(r);
                break;
            }

            case 9:{
                readAuctionNotice(r);
                break;
            }

            case 10:{
                readAuctionTime(r);
                break;
            }

            case 11:{
                if(alertDialog != null){
                    alertDialog.dismiss();
                }
                break;
            }
            case 12:{
                finish();
                break;
            }
            case 13:{
                DevTools.showToastGameBoomSpinMsg(r.readInt());
                break;
            }

        }
    }

}