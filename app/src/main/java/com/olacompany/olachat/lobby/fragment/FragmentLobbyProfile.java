package com.olacompany.olachat.lobby.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.olacompany.olachat.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentLobbyProfile extends Fragment {
    public FragmentLobbyProfile(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_lobby_profile, container, false);
        return rootView;
        //xml 레이아웃이 인플레이트 되고 자바소스 코드와 연결이된다.
    }
}
