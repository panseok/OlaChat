package com.olacompany.olachat.game.boomspin;

import com.olacompany.olachat.User.UserInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerInfo {

    private UserInfo user;
    private List<PlayerSkill> skills = Collections.synchronizedList(new ArrayList<>());
    private int money = 0;
    private boolean isMyturn = false;
    private boolean isAlive = false;

    public PlayerInfo(UserInfo user,List<PlayerSkill> skills, int money, boolean isAlive,boolean isMyturn) {
        this.user = user;
        this.skills = skills;
        this.money = money;
        this.isAlive = isAlive;
        this.isMyturn = isMyturn;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public boolean isMyturn() {
        return isMyturn;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public List<PlayerSkill> getSkills() {
        return skills;
    }

    public void setSkills(List<PlayerSkill> skills) {
        this.skills = skills;
    }
}