package com.olacompany.olachat.game.aliveboom;
import android.os.Bundle;
import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;

import androidx.appcompat.app.AppCompatActivity;

public class AliveBoomActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_alive_boom);
    }


    @Override
    public void onBackPressed() {
        DevTools.showToastShortMsg(this,"게임 중에는 나갈 수 없습니다.\n게임이 끝나면 자동으로 퇴장됩니다.");
        return;
    }
}
