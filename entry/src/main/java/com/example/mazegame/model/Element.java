package com.example.mazegame.model;

import ohos.agp.components.Image;

public class Element {
    private Image image;
    private int type;
    private String name;
    private String dialog;
    private int blood;
    private int aggre;
    private int defense;

    public Element() {
    }

    public Element(Image image, int type) {
        this.image = image;
        this.type = type;
    }

    public Element(Image image, int type, String name, int blood) {
        this.image = image;
        this.type = type;
        this.name = name;
        this.blood = blood;
    }

    public Element(Image image, int type, String name, String dialog, int blood, int aggre, int defense) {
        this.image = image;
        this.type = type;
        this.name = name;
        this.dialog = dialog;
        this.blood = blood;
        this.aggre = aggre;
        this.defense = defense;
    }

    public String getDialog() {
        return dialog;
    }

    public void setDialog(String dialog) {
        this.dialog = dialog;
    }

    public int getAggre() {
        return aggre;
    }

    public void setAggre(int aggre) {
        this.aggre = aggre;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBlood() {
        return blood;
    }

    public void setBlood(int blood) {
        this.blood = blood;
    }
}
