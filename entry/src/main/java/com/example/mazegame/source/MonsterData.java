package com.example.mazegame.source;

import com.example.mazegame.ResourceTable;

public class MonsterData {
    public int getMonsterImageId(int type){
        int id;
        switch (type){
            case MapData.small_monster: id = ResourceTable.Media_monster_small;break;
            case MapData.big_monster: id = ResourceTable.Media_monster_big;break;
            default:id = ResourceTable.Media_monster_small;break;
        }
        return id;
    }

}
