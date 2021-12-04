package com.olacompany.olachat.game.boomspin;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.olacompany.olachat.R;
import com.olacompany.olachat.devtool.DevTools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.Triple;

public class PlayerSkillListAdapter extends BaseAdapter {
    List<PlayerSkill> playerSkills = Collections.synchronizedList(new ArrayList<>());
    private Context context;
    private int layout;
    private LayoutInflater inf;

    public PlayerSkillListAdapter(Context context, int layout , List<PlayerSkill> playerSkills){
        this.context = context;
        this.layout = layout;
        this.inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.playerSkills = playerSkills;
    }

    @Override
    public int getCount() {
        return playerSkills.size();
    }

    @Override
    public Object getItem(int i) {
        return playerSkills.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inf.inflate(layout, viewGroup,false);
        }

        TextView skillname = view.findViewById(R.id.skillname);
        TextView skillcomment = view.findViewById(R.id.skillcomment);

        skillname.setTextColor(Color.GREEN);
        skillcomment.setTextColor(Color.YELLOW);

        skillname.setText(DevTools.setTextColor(playerSkills.get(i).getSkillname()));
        skillcomment.setText(DevTools.setTextColor(playerSkills.get(i).getSkillcomment()));

        return view;
    }
}
