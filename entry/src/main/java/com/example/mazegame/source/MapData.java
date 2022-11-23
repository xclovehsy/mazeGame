package com.example.mazegame.source;

import ohos.utils.Pair;

public class MapData {
    public static final int money = 10;
    public static final int shield = 11;
    public static final int sword = 12;
    public static final int aid = 13;
    public static final int key = 14;
    public static final int small_monster = 20;
    public static final int big_monster = 21;
    public static final int finish = 2;
    public static final int door = 3;


    public class Map{
        public int map[][];
        public int beginX, beginY;

        public Map() {
        }

        public Map(int[][] map, int beginX, int beginY) {
            this.map = map;
            this.beginX = beginX;
            this.beginY = beginY;
        }
    }

    // 0地板  1墙  2终点  3门
    // 10金币 11防御  12攻击 13血包 14钥匙
    // 20小怪兽  21大怪兽
    public int[][][] map10 = {
            {
                    {1, 1, 1, 1, 1, 2, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
            },
            {
                    {1, 1, 1, 1, 1, 2, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 10, 0, 0, 0, 0, 21, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 10, 0, 0, 0, 0, 20, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
            },
            {
                    {1, 1, 1, 1, 1, 2, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 13, 0, 0, 0, 10, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 2},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 11, 0, 1},
                    {1, 0, 12, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, MapData.key, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 3, 0, 0, 0, 1},
                    {1, 0, 0, 0, 0, 1, 0, 0, 0, 2},
                    {1, 0, 0, 0, 0, 1, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            }
    };
//    public int beginX = 9, beginY = 5;
    public int [] beginX = {9, 9, 9, 3};
    public int [] beginY = {5, 5, 5, 0};

    public MapData() {
    }

    public Map getMap(int mapSize, int level){
        if(mapSize == 10){
            return new Map(map10[level], beginX[level], beginY[level]);
        }
        return new Map(map10[level], beginX[level], beginY[level]);

    }



}
