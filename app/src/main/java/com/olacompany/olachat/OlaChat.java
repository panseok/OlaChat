package com.olacompany.olachat;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.kakao.sdk.common.KakaoSdk;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.devtool.LittleEndianReader;
import com.olacompany.olachat.netty.NettyClient;
import com.olacompany.olachat.packet.FriendPacket;

import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;

public class OlaChat extends Application {

    private static OlaChat olaChat;

    AppCompatDialog progressDialog;

    @Override
    public void onCreate() {
        super.onCreate();
        olaChat = this;
        KakaoSdk.init(this, "461f0cfae5ca4803e6385d9c1d6dbce1");
    }


    public static OlaChat getInstance() {
        return olaChat;
    }


    public void startProgress(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            setProgress(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_frame_loading);
            progressDialog.show();

        }

        ImageView loader = (ImageView) progressDialog.findViewById(R.id.loading_imageview);
        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(loader);
        Glide.with(this).load(R.drawable.loader).into(gifImage);

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.frame_loading_textview);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void setProgress(String message) {
        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }
        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.frame_loading_textview);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }


    public void stopProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public String getTopActivityName(){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(1);
        ComponentName componentName= info.get(0).topActivity;

        return componentName.getShortClassName().substring(1);
    }

    public static void ServerNoticeAlertDialog(){
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
                LinearLayout linearLayout = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.alertdialog_server_notice,null);
                TextView serverText = linearLayout.findViewById(R.id.server_notice_text);
                Button btn_close = linearLayout.findViewById(R.id.btn_close_server_notice);


                serverText.setTextColor(Color.WHITE);
                serverText.setText("서버와 연결이 끊어져 앱을 종료합니다.");

                builder.setView(linearLayout);
                AlertDialog alertDialog = builder.create();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                alertDialog.setCancelable(false);
                alertDialog.show();

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                        activity.moveTaskToBack(true);
                        activity.finish();
                        activity.finishAffinity();
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }
                });



            }
        },0);
    }

}
