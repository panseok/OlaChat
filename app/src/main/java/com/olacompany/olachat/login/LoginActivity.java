package com.olacompany.olachat.login;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.devtool.LittleEndianWriter;
import com.olacompany.olachat.lobby.LobbyActivity;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.LoginPacket;


import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    public static LoginActivity loginActivity;
    private static  LayoutInflater inflater;
    private static Activity activity;
    private static boolean isEnableName = false;

    public static LoginActivity getInstance(){
        return loginActivity;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginActivity = this;

        setContentView(R.layout.activity_login);
        inflater = getLayoutInflater();


        ImageButton kakaoLogin_btn = (ImageButton) findViewById(R.id.kakaoLogin_btn);



        kakaoLogin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(getInstance())) {
                    UserApiClient.getInstance().loginWithKakaoTalk(getInstance(), (oAuthToken, throwable) -> {
                        if (throwable != null) {
                            DevTools.showToastDebugMsg(getApplicationContext(),"????????? ?????????????????????.");
                        } else {
                            requestUserInfo();
                        }
                        return null;
                    });
                }else{
                    UserApiClient.getInstance().loginWithKakaoAccount(getInstance(),(oAuthToken, throwable) -> {
                        if (throwable != null) {
                            DevTools.showToastDebugMsg(getApplicationContext(),"????????? ?????????????????????.");
                        } else {
                            requestUserInfo();
                        }
                        return null;
                    });
                }
            }
        });

    }

    private void requestUserInfo() { // ????????? ?????? ??????
        UserApiClient.getInstance().me((user, throwable) -> {
            if (throwable != null) {
                DevTools.showToastDebugMsg(getApplicationContext(),"????????? ?????????????????????. ?????? ???????????? ?????????.");
            } else {

                Account kakaoAccount = user.getKakaoAccount();
                if (kakaoAccount != null) { //?????? ????????? ????????? ????????? ??? ??????
                }

                NettyClient.getSession().writeAndFlush(LoginPacket.sendLogin(user.getId()));
                NettyClient.setUserId(user.getId());

            }
            return null;
        });
    }



    public static void showLoginStatusMsg(LittleEndianReader r){

        /*
         * 0 : ?????? ??????
         * 1 : ?????? ??????
         * 2 : ????????? ?????? ??????
         * 3 : ????????? ?????? ??????
         * 4 : ????????? ?????? (?????? ?????? ??????)
         * 5 : ?????? ?????? ?????????
         * 6 : ??????
         * */

        short type = r.readShort();
        String msg = "";
        if(type == 0){
            msg = "????????? ?????????????????????.";
        }else if(type == 1){
            msg = "????????? ?????? ???????????????.";
        }else if( type == 2){
            msg = "????????? ????????? ?????????????????????.";
        }else if(type == 3){
            msg = "????????? ????????? ?????????????????????.";
        }else if(type == 4){
            msg = "???????????? ?????????????????????.";
        }else if(type == 5){
            msg = "?????? ???????????? ????????? ?????????.";
        }else{
            msg = r.readLengthAsciiString();
        }
        DevTools.showToastShortMsg(getInstance(), msg);
    }

    public static void CreateUserNameAlertDialog(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(inflater == null){
                    Log.e("LoginActivity ERROR","?????????????????? NULL ?????????.");
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(getInstance());
                LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.alertdialog_create_username,null);
                EditText editText = linearLayout.findViewById(R.id.createUserNameEditText);
                TextView textViewHint = linearLayout.findViewById(R.id.createUserNameTextView);
                Button btn_login_createusername_ok = linearLayout.findViewById(R.id.btn_login_createusername_ok);

                textViewHint.setText("???????????? ????????? ????????? ????????? ??? ????????????.");
                textViewHint.setTextColor(Color.GRAY);

                builder.setView(linearLayout);

                editText.addTextChangedListener(new TextWatcher() {

                    String text="";
                    String textOld="";
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

                        int length = editable.toString().length();
                        if( length > 0 ){
                            Pattern ps = Pattern.compile("^[a-zA-Z0-9???-??????-??????-???\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+$");//??????, ??????, ????????? ??????
                            if(!ps.matcher(editable).matches()){
                                editText.setText(textOld);
                                editText.setSelection(editText.length());
                            }
                        }
                        if(TextUtils.isEmpty(text)){
                            textViewHint.setText("???????????? ????????? ????????? ????????? ??? ????????????.");
                            textViewHint.setTextColor(Color.GRAY);
                        }else if(DevTools.isOxfordText(String.valueOf(text))
                                || DevTools.isNoAssemblyKorean(String.valueOf(text))){
                            textViewHint.setText("?????? ???????????? ??????????????????");
                            textViewHint.setTextColor(Color.RED);
                            isEnableName = false;
                        }else{
                            textViewHint.setText("?????? ????????? ????????? ?????????.");
                            textViewHint.setTextColor(Color.GREEN);
                            isEnableName = true;
                        }
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.show();

                btn_login_createusername_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(isEnableName){
                            NettyClient.getSession().writeAndFlush(LoginPacket.sendCheckName(editText.getText().toString()));
                            alertDialog.dismiss();
                        }else{
                            DevTools.showToastShortMsg(getInstance(),"?????? ???????????? ??????????????????.");
                        }
                    }
                });
            }
        }, 0);
    }

    public void startLobbyActivity(){
        finish();
        Intent intent = new Intent(getInstance(), SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getHashKey(){
        Log.e("getHashKey : ",Utility.INSTANCE.getKeyHash(this));
    }
}