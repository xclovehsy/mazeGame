package com.example.mazegame.model;

public class StoreItemBean {
    private int id;
    private int picId;
    private String name;
    private String describe;
    private int cost;
    private int blood_up;
    private int aggre_up;
    private int defense_up;

    public StoreItemBean() {
    }

    public StoreItemBean(int id, int picId, String name, String describe, int cost, int blood_up, int aggre_up, int defense_up) {
        this.id = id;
        this.picId = picId;
        this.name = name;
        this.describe = describe;
        this.cost = cost;
        this.blood_up = blood_up;
        this.aggre_up = aggre_up;
        this.defense_up = defense_up;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPicId() {
        return picId;
    }

    public void setPicId(int picId) {
        this.picId = picId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getBlood_up() {
        return blood_up;
    }

    public void setBlood_up(int blood_up) {
        this.blood_up = blood_up;
    }

    public int getAggre_up() {
        return aggre_up;
    }

    public void setAggre_up(int aggre_up) {
        this.aggre_up = aggre_up;
    }

    public int getDefense_up() {
        return defense_up;
    }

    public void setDefense_up(int defense_up) {
        this.defense_up = defense_up;
    }

    @Override
    public String toString() {
        return "StoreItemBean{" +
                "id=" + id +
                ", picId=" + picId +
                ", name='" + name + '\'' +
                ", describe='" + describe + '\'' +
                ", cost=" + cost +
                ", blood_up=" + blood_up +
                ", aggre_up=" + aggre_up +
                ", defense_up=" + defense_up +
                '}';
    }
}
