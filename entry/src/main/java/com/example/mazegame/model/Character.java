package com.example.mazegame.model;


import com.example.mazegame.ResourceTable;
import com.example.mazegame.source.Const;
import ohos.data.orm.OrmObject;
import ohos.data.orm.annotation.Entity;
import ohos.data.orm.annotation.PrimaryKey;

@Entity(tableName = "character") //这里也可以添加索引，这里我简单处理
public class Character extends OrmObject {

    @PrimaryKey(autoGenerate = true)//将ID设置为主键
    private long id = Const.playerId;
    private String name = "player";
    private String dialog = "win!";
    private boolean isHaveGirl = false;
    private int grilId = ResourceTable.Media_girl2;
    private int picId = ResourceTable.Media_player1;
    private int blood = 100;
    private int aggressivity = 50;
    private int defense = 50;
    private int level = 1;
    private int money = 520;

    public Character() {
    }

    public Character(long id, String name, String dialog, boolean isHaveGirl, int grilId, int picId, int blood, int aggressivity, int defense, int level, int money) {
        this.id = id;
        this.name = name;
        this.dialog = dialog;
        this.isHaveGirl = isHaveGirl;
        this.grilId = grilId;
        this.picId = picId;
        this.blood = blood;
        this.aggressivity = aggressivity;
        this.defense = defense;
        this.level = level;
        this.money = money;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getGrilId() {
        return grilId;
    }

    public void setGrilId(int grilId) {
        this.grilId = grilId;
    }

    public boolean isIsHaveGirl() {
        return isHaveGirl;
    }

    public void setIsHaveGirl(boolean haveGirl) {
        isHaveGirl = haveGirl;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }

    public int getAggressivity() {
        return aggressivity;
    }

    public void setAggressivity(int aggressivity) {
        this.aggressivity = aggressivity;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dialog='" + dialog + '\'' +
                ", isHaveGirl=" + isHaveGirl +
                ", grilId=" + grilId +
                ", picId=" + picId +
                ", blood=" + blood +
                ", aggressivity=" + aggressivity +
                ", defense=" + defense +
                ", level=" + level +
                ", money=" + money +
                '}';
    }
}
