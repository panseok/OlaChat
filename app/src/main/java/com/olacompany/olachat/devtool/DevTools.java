package com.olacompany.olachat.devtool;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.olacompany.olachat.OlaChat;
import com.olacompany.olachat.R;
import com.olacompany.olachat.netty.NettyClient;

import java.util.ArrayList;
import java.util.List;

public class DevTools {

    public static Toast mToast = null;

    public static void showToastDebugMsg(Context context , String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(mToast != null){
                    mToast.cancel();
                }
                mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                mToast.show();
            }
        }, 0);

    }

    public static void showToastShortMsg(Context context , String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(mToast != null){
                    mToast.cancel();
                }
                mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
                mToast.show();
            }
        }, 0);

    }

    public static void showToastLongMsg(Context context , String msg) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(mToast != null){
                    mToast.cancel();
                }
                mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
                mToast.show();
            }
        }, 0);

    }

    public static void showToastGameBoomSpinMsg(int code){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());
                LayoutInflater inflater = activity.getLayoutInflater();
                View toastDesign = inflater.inflate(R.layout.toast_boomspin_imgview_layout, null);

                ImageView toast_boom_img = toastDesign.findViewById(R.id.toast_boom_img);
                if(mToast != null){
                    mToast.cancel();
                }
                mToast = new Toast(activity.getApplicationContext());
                mToast.setGravity(Gravity.CENTER, 0, 0); // CENTER를 기준으로 0, 0 위치에 메시지 출력


                switch (code){
                    case 0:
                        break;
                    case 1:
                        break;
                    case 8:
                        toast_boom_img.setBackgroundResource(R.drawable.auction_start_img);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                        break;
                    case 9:
                        toast_boom_img.setBackgroundResource(R.drawable.boom_cooming_img);
                        mToast.setDuration(Toast.LENGTH_SHORT);
                        break;
                    case 10:
                        toast_boom_img.setBackgroundResource(R.drawable.boomboom_img);
                        mToast.setDuration(Toast.LENGTH_LONG);
                        break;
                }
                mToast.setView(toastDesign);
                mToast.show();
            }
        }, 0);
    }

    public static void showToastGameBoomDaymsg(int day){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = NettyClient.getNowActivity(OlaChat.getInstance().getTopActivityName());
                LayoutInflater inflater = activity.getLayoutInflater();
                View toastDesign = inflater.inflate(R.layout.toast_boomspin_daytext_layout, null);

                TextView toast_boom_img = toastDesign.findViewById(R.id.toast_day_text);

                toast_boom_img.setText("Day "+day);
                if(mToast != null){
                    mToast.cancel();
                }
                mToast = new Toast(activity.getApplicationContext());
                mToast.setGravity(Gravity.CENTER, 0, 0); // CENTER를 기준으로 0, 0 위치에 메시지 출력

                mToast.setDuration(Toast.LENGTH_SHORT);
                mToast.setView(toastDesign);
                mToast.show();
            }
        }, 0);
    }

    public static void startProgressDialog(Activity activity) {
        OlaChat.getInstance().startProgress(activity, null);
    }

    public static void startProgressDialogHandleLooper(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run()
            {
                OlaChat.getInstance().startProgress(activity, null);
            }
        }, 0);
    }

    public static void startProgressDialog(Activity activity, String message) {
        OlaChat.getInstance().startProgress(activity, message);
    }

    public static void stopProgressDialog() {
        OlaChat.getInstance().stopProgress();
    }

    public static boolean isNoAssemblyKorean(String text){
        return text.matches(".*[ㄱ-ㅎㅏ-ㅣ\\u318D\\u119E\\u11A2\\u2022\\u2025a\\u00B7\\uFE55]+.*");
    }

    public static SpannableString setTextColor(String text){
        String ctext = text;
        String content = text;
        String[] colorSet = {"#r","#g","#b","#w","#g","#y","#o","#p","#l"};

        for(String color : colorSet){
            content = content.replaceAll(color,"");
        }

        SpannableString spannableString = new SpannableString(content);

        int count = 0;
        for(int i = 0; i < ctext.length(); i++){
            char a = ctext.charAt(i);
            if(a == '#'){
                i++;
                char b = ctext.charAt(i);
                int start = -1;
                int color = 0;
                switch (b){
                    case 'r':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.RED;
                        break;
                    case 'b':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.parseColor("#00FFFF");
                        break;
                    case 'y':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.YELLOW;
                        break;
                    case 'g':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.GREEN;
                        break;
                    case 'w':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.WHITE;
                        break;
                    case 'o':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.parseColor("#FF9849");
                        break;
                    case 'p':
                        count++;
                        start = i-count -((count-1)*3);
                        color = Color.MAGENTA;
                        break;
                }

                if(start >= 0){
                    int end = -1;
                    for(int j = i; j < ctext.length(); j++) {
                        char c = ctext.charAt(j);
                        if(c == '#'){
                            j++;
                            char d = ctext.charAt(j);
                            if(d == 'l'){
                                end = j-3*count -(count-1);
                                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                break;
                            }
                        }
                    }
                    if(end < 0){
                        end = content.length();
                        spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                }
            }
        }

        for(int i = 0; i < content.length(); i++) {
            char a = content.charAt(i);
            int start = -1;
            if(a == '['){
                start = i;
                int end = -1;
                for(int j = i; j < content.length(); j++) {
                    char c = content.charAt(j);
                    if(c == ']'){
                        end = j+1;
                        i = j+1;
                        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        break;
                    }
                }
                if(end < 0){
                    end = content.length();
                    spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    break;
                }
            }
        }

           /* spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.3f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);*/

        return spannableString;
    }
    
    public static int getProfileImageId(int code){
        int a;
        switch (code){
            case 0:{
               return R.drawable.profile_default_image;
            }
            case 1:{
                return R.drawable.profile_1_image;
            }
            case 2:{
                return R.drawable.profile_2_image;
            }
            case 3:{
                return R.drawable.profile_3_image;
            }
            case 4:{
                return R.drawable.profile_4_image;
            }
            case 5:{
                return R.drawable.profile_5_image;
            }
            case 6:{
                return R.drawable.profile_6_image;
            }
            case 7:{
                return R.drawable.profile_7_image;
            }
            case 8:{
                return R.drawable.profile_8_image;
            }
            case 9:{
                return R.drawable.profile_9_image;
            }
            case 10:{
                return R.drawable.profile_10_image;
            }
            case 11:{
                return R.drawable.profile_11_image;
            }
            default:{
                return R.drawable.profile_default_image;
            }

        }
    }


    public static boolean isOxfordText(String text){
        List<String> oxfordlist = new ArrayList<>();
        oxfordlist.add("애미");
        oxfordlist.add("새끼");
        oxfordlist.add("섹스");
        oxfordlist.add("시발");
        oxfordlist.add("병신");
        oxfordlist.add("한남");
        oxfordlist.add("한녀");
        oxfordlist.add("개년");
        oxfordlist.add("개놈");
        for(String str : oxfordlist){
            if(text.contains(str)){
                return true;
            }
        }

        return false;

    }


}
