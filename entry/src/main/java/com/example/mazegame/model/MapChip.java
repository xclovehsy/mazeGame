package com.example.mazegame.model;

import ohos.agp.components.Image;

public class MapChip {
    private Image image;
    private boolean isBlock;
    private int x;
    private int y;

    public MapChip(Image image, int x, int y, boolean isblock) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.isBlock = isblock;
//        this.isOccupy = false;
    }



    public MapChip() {
    }

    public Image getImage() {
        return image;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setBlock(boolean block) {
        isBlock = block;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }
}
