package com.olacompany.olachat.game.boomspin;

import java.util.ArrayList;
import java.util.List;

public class PlayerSkill {

    private int skillcode;
    private String skillname, skillcomment;

    public PlayerSkill(int skillcode, String skillName, String skillComment){
        this.skillcode = skillcode;
        this.skillname = skillName;
        this.skillcomment = skillComment;
    }

    public int getSkillcode() {
        return skillcode;
    }

    public String getSkillcomment() {
        return skillcomment;
    }

    public String getSkillname() {
        return skillname;
    }
}
