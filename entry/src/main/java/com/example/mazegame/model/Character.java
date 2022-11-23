package com.example.mazegame.model;


import com.example.mazegame.ResourceTable;

public class Character {
    private String name = "player";
    private String dialog = "win!";
    private int picId = ResourceTable.Media_player1;
    private int blood = 100;
    private int aggressivity = 99;
    private int defense = 89;
    private int level = 1;
    private int money = 520;

    public Character() {
    }

    public Character(String name, String dialog, int picId, int blood, int aggressivity, int defense, int level, int money) {
        this.name = name;
        this.dialog = dialog;
        this.picId = picId;
        this.blood = blood;
        this.aggressivity = aggressivity;
        this.defense = defense;
        this.level = level;
        this.money = money;
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
}
