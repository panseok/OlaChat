package com.olacompany.olachat.login;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.LinearLayout;

import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;
import com.olacompany.olachat.netty.NettyClient;

import androidx.annotation.UiThread;

public class LoadingActivity extends Activity {

    private static boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        
        ConnectLoginServerThread connectThread = new ConnectLoginServerThread(this);

        DevTools.startProgressDialog(this);
        connectThread.start();


    }

    private class ConnectLoginServerThread extends Thread{
        Context context;

        public ConnectLoginServerThread(Context context){
             this.context = context;
        }

        @Override
        public void run(){
            while(!isConnected()){
                NettyClient.initLoginServer();
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            DevTools.stopProgressDialog();
        }
    }

    public static void setConnected(boolean connected) {
        isConnected = connected;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public boolean isNoAssemblyKorean(String text){
        return text.matches(".*[ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+.*");
    }

}