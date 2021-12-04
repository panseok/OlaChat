package com.olacompany.olachat.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.olacompany.olachat.lobby.LobbyActivity;
import com.olacompany.olachat.netty.NettyClient;

public class SplashActivity extends Activity {
    @Override protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        if(!LoadingActivity.isConnected()){
            startActivity(new Intent(this, LoadingActivity.class));
        }else{
            if(!NettyClient.getSession().isOpen()){
                startActivity(new Intent(this, LoadingActivity.class));
            }else{
                startActivity(new Intent(this, LobbyActivity.class));
            }
        }
        finish();
    }

}
