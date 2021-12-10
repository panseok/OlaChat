package com.olacompany.olachat.lobby;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.friend.FriendActivity;
import com.olacompany.olachat.login.LoadingActivity;
import com.olacompany.olachat.login.LoginActivity;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.FriendPacket;
import com.olacompany.olachat.packet.LobbyPacket;
import com.olacompany.olachat.packet.LoginPacket;
import com.olacompany.olachat.packet.RoomPacket;
import com.olacompany.olachat.room.ChatRoomActivity;
import com.olacompany.olachat.room.ChatRoom;
import com.olacompany.olachat.room.InviteFriendListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static androidx.constraintlayout.widget.ConstraintLayoutStates.TAG;

public class LobbyActivity extends AppCompatActivity {

    public ViewPager viewPager;
    public TabLayout tabLayout;
    public ImageButton btn_createRoom, btn_refresh, btn_friend, btn_setting;
    public LobbyPagerAdapter adapter;
    public ServerNoticeAdapter serverNoticeAdapter;
    public ListView server_notice_listview;

    public static LobbyActivity lobbyActivity;
    private static LayoutInflater inflater;
    private List<ServerNotice> serverNotices = Collections.synchronizedList(new ArrayList<>());


    private static List<ChatRoom> lobbyRoomList = Collections.synchronizedList(new ArrayList<>());
    private static boolean isEnableName;

    public static LobbyActivity getInstance() {
        return lobbyActivity;
    }

    @Override
    protected void onResume() {
        super.onResume();
        NettyClient.getSession().writeAndFlush(LobbyPacket.getLobbyRoomlist());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        lobbyActivity = this;
        inflater = getLayoutInflater();

        NettyClient.getSession().writeAndFlush(LobbyPacket.getLobbyRoomlist());
        NettyClient.getSession().writeAndFlush(LobbyPacket.getServerNoticeList());

        viewPager = findViewById(R.id.lobby_viewpager);
        tabLayout = findViewById(R.id.lobby_tablayout);
        btn_createRoom = (ImageButton) findViewById(R.id.btn_make_room);
        btn_refresh = (ImageButton) findViewById(R.id.btn_refresh);
        btn_friend = (ImageButton) findViewById(R.id.btn_friend);
        btn_setting = (ImageButton) findViewById(R.id.btn_setting);
        server_notice_listview = findViewById(R.id.server_notice_listview);

        adapter = new LobbyPagerAdapter(this);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);


        serverNoticeAdapter = new ServerNoticeAdapter(this, R.layout.item_server_notice, serverNotices);
        server_notice_listview.setAdapter(serverNoticeAdapter);

        //tabLayout.setupWithViewPager(viewPager);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        btn_createRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRoomDialog();
            }
        });

        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DevTools.startProgressDialog(getInstance());
                NettyClient.getSession().writeAndFlush(LobbyPacket.getLobbyRoomlist());
            }
        });

        btn_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getInstance(), FriendActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                LobbyActivity.getInstance().startActivity(intent);
            }
        });

        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingDialog();
            }
        });

        DevTools.stopProgressDialog();

    }

    public static void settingDialog() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inflater == null) {
                    Log.e("lobbyacty ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getInstance());
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.alertdialog_setting, null);
                builder.setView(linearLayout);

                Button btn_logout = linearLayout.findViewById(R.id.btn_logout);
                Button btn_secession = linearLayout.findViewById(R.id.btn_secession);
                Button btn_close = linearLayout.findViewById(R.id.btn_close_setting);


                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_logout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // 연결 끊기
                        UserApiClient.getInstance().logout(error -> {
                            if (error != null) {
                                Log.e(TAG, "로그아웃 실패, SDK에서 토큰 삭제됨", error);
                            } else {
                                requestLogout();
                            }
                            return null;
                        });
                    }
                });

                btn_secession.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                    }
                });

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
            }
        }, 0);
    }

    public static void requestLogout() {
        LoadingActivity.setConnected(false);
        NettyClient.getSession().disconnect();
        Intent backIntent = new Intent(getInstance(), LoadingActivity.class); // 로그인 화면으로 이동
        getInstance().startActivity(backIntent);
        getInstance().finish();
    }

    public static void createRoomDialog() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (inflater == null) {
                    Log.e("lobbyacty ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getInstance());
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.alertdialog_create_room, null);
                EditText editText = linearLayout.findViewById(R.id.createRoomNameEditText);
                TextView textViewHint = linearLayout.findViewById(R.id.createRoomNameTextView);
                Button btn_lobby_createroom_cancel = linearLayout.findViewById(R.id.btn_lobby_createroom_cancel);
                Button btn_lobby_createroom_ok = linearLayout.findViewById(R.id.btn_lobby_createroom_ok);
                textViewHint.setText("부적절한 제목 사용시 제제될 수 있습니다.");
                textViewHint.setTextColor(Color.GRAY);

                builder.setView(linearLayout);

                editText.addTextChangedListener(new TextWatcher() {

                    String text = "";
                    String textOld = "";

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        textOld = charSequence.toString();
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        text = editable.toString();
                        if (TextUtils.isEmpty(text)) {
                            textViewHint.setText("부적절한 방제목을 사용시 제제될 수 있습니다.");
                            textViewHint.setTextColor(Color.GRAY);
                        } else if (DevTools.isOxfordText(String.valueOf(text))) {
                            textViewHint.setText("사용 불가능한 제목입니다.");
                            textViewHint.setTextColor(Color.RED);
                            isEnableName = false;
                        } else {
                            textViewHint.setText("사용 가능한 제목 입니다.");
                            textViewHint.setTextColor(Color.GREEN);
                            isEnableName = true;
                        }
                    }
                });


                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_lobby_createroom_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_lobby_createroom_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isEnableName) {
                            DevTools.startProgressDialog(getInstance());
                            NettyClient.getSession().writeAndFlush(LobbyPacket.sendCreateRoom(NettyClient.getUserId(), editText.getText().toString()));
                            alertDialog.dismiss();
                        } else {
                            DevTools.showToastShortMsg(getInstance(), "사용 불가능한 방제목입니다.");
                        }
                    }
                });
            }
        }, 0);
    }

    public List<ChatRoom> getLobbyRoomList() {
        return lobbyRoomList;
    }

    public void getLobbyRoomListPacket(LittleEndianReader r) {
        lobbyRoomList.clear();
        int size = r.readInt();
        for (int i = 0; i < size; i++) {
            lobbyRoomList.add(new ChatRoom(r.readInt(), r.readLengthAsciiString(), r.readInt(), r.readInt()));
        }
        refresh(r);
    }

    public void refresh(LittleEndianReader r) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
        DevTools.stopProgressDialog();
    }

    public void getServerNoticeList(LittleEndianReader r) {
        serverNotices.clear();
        int size = r.readInt();
        for (int i = 0; i < size; i++) {
            serverNotices.add(new ServerNotice(r.readLengthAsciiString(), r.readLengthAsciiString(), r.readLengthAsciiString(), r.readLengthAsciiString()));
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                serverNoticeAdapter.notifyDataSetChanged();
            }
        });
    }


    public void getUserJoinRoomRequestResult(LittleEndianReader r) {
        Intent intent = new Intent(getInstance(), ChatRoomActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        boolean isBan = r.readByte() == 1;

        if(isBan){
            DevTools.showToastShortMsg(this, "강제퇴장 당한 방에는 입장하실 수 없습니다.");
            DevTools.stopProgressDialog();
            return;
        }

        boolean isSucced = r.readByte() == 1;

        if (isSucced) {
            long userId = r.readLong();
            int roomNumber = r.readInt();
            String roomName = r.readLengthAsciiString();
            int roomMemberCount = r.readInt();
            int roomMemberMaxCount = r.readInt();

            intent.putExtra("userId", userId);
            intent.putExtra("roomNumber", roomNumber);
            intent.putExtra("roomName", roomName);
            intent.putExtra("roomMemberCount", roomMemberCount);
            intent.putExtra("roomMemberMaxCount", roomMemberMaxCount);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LobbyActivity.getInstance().startActivity(intent);
        } else {
            DevTools.showToastShortMsg(this, "참여할 수 없는 방, 이미 참여한 방 또는 삭제된 방입니다.");
        }

        DevTools.stopProgressDialog();

    }


    private static AlertDialog  alertDialogAccpet = null;
    private static LinearLayout linearLayoutAccpet;
    private static Button btn_invitef,btn_invitet;
    private static FrameLayout dialog_invite_profile_image_layout;
    private static ImageView dialog_invite_default_bar_image;
    private static TextView dialog_invite_username, dialog_invite_roominfo;

    public static void InviteRoomAccpetDialog(LittleEndianReader r){
        long userId = r.readLong();
        String userName = r.readLengthAsciiString();
        int profileImageCode = r.readInt();
        int userWin = r.readInt();
        int userLose = r.readInt();
        int userPopularity = r.readInt();
        String userMemo = r.readLengthAsciiString();
        boolean isLogin = r.readByte() == 1;
        boolean isRoomLeader = r.readByte() == 1;

        int roomNumber = r.readInt();
        String roomName = r.readLengthAsciiString();

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
                linearLayoutAccpet = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_invite_room_accpet,null);
                btn_invitet = linearLayoutAccpet.findViewById(R.id.btn_invite_t);
                btn_invitef = linearLayoutAccpet.findViewById(R.id.btn_invite_f);
                dialog_invite_profile_image_layout = linearLayoutAccpet.findViewById(R.id.dialog_invite_profile_image_layout);
                dialog_invite_default_bar_image = linearLayoutAccpet.findViewById(R.id.dialog_invite_default_bar_image);
                TextView dialog_invite_username = linearLayoutAccpet.findViewById(R.id.dialog_invite_username);
                TextView dialog_invite_roominfo = linearLayoutAccpet.findViewById(R.id.dialog_invite_roominfo);

                dialog_invite_profile_image_layout.setBackgroundResource(DevTools.getProfileImageId(profileImageCode));
                dialog_invite_username.setText(userName);

                dialog_invite_roominfo.setText(DevTools.setTextColor("#y["+roomNumber+"]번#l"+" "+"#w"+roomName+"#l"));

                builder.setView(linearLayoutAccpet);
                alertDialogAccpet = builder.create();
                alertDialogAccpet.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialogAccpet.show();

                btn_invitef.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialogAccpet.dismiss();
                    }
                });

                btn_invitet.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NettyClient.getSession().writeAndFlush(RoomPacket.getJoinRoomRequest(roomNumber,NettyClient.getUserId()));
                        alertDialogAccpet.dismiss();
                    }
                });

            }
        },0);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        ServerNoticeAlertDialog();
    }

    public static void ServerNoticeAlertDialog() {
        Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());
        DevTools.stopProgressDialog();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activity.getLayoutInflater() == null) {
                    Log.e("Activity ERROR", "인플레이터가 NULL 입니다.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_game_exit, null);
                Button btn_ok = linearLayout.findViewById(R.id.btn_game_exit_ok);
                Button btn_no = linearLayout.findViewById(R.id.btn_game_exit_no);


                builder.setView(linearLayout);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });

                btn_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        activity.finish();
                        LoadingActivity.setConnected(false);
                        NettyClient.getSession().disconnect();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });

            }
        }, 0);
    }
}