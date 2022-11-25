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
    public static final int girl = 4;
    public static final int a = 10;  //金币
    public static final int b = 11;  //盾牌
    public static final int c = 12;  //剑
    public static final int d = 13;  //血包
    public static final int e = 14;  //钥匙
    public static final int f = 15;  //汽水
    public static final int g = 16;  //面包
    public static final int h = 17;  //奶油蛋糕
    public static final int i = 18;  //剪刀
    public static final int j = 19;  //水果刀
    public static final int k = 20;  //小怪兽
    public static final int l = 21;  //大怪兽
    public static final int m = 22;  //弓箭
    public static final int n = 23;  //小盾牌
    public static final int o = 24;  //大盾牌
    public static final int p = 25;  //高级盾牌
    public static final int q = 26;  //生命药水
    public static final int r = 27;  //防御药水
    public static final int s = 28;  //战斗药水
    public static final int t = 29;  //幽灵
    public static final int u = 30;  //黑头
    public static final int v = 31;  //独眼怪
    public static final int w = 32;  //绿头
    public static final int x = 33;  //猪怪
    public static final int y = 34;  //科学巨人
    public static final int z = 35;  //绿巨人


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
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 1, 1, 1, 1, 1, 1, 0, 1},
                    {1, 0, 1, 0, 0, 0, 0, 1, 0, 1},
                    {1, 0, 1, 0, 1, 0, 2, 1, 0, 1},
                    {1, 0, 1, 0, 1, 1, 1, 1, 0, 1},
                    {1, 0, 1, 0, 0, 0, 0, 0, 0, 1},
                    {1, 0, 1, 1, 1, 1, 1, 10, 10, 1},
                    {1, 0, 0, 0, 0, 0, 1, 10, 10, 1},
                    {1, 1, 1, 1, 1, 0, 1, 1, 1, 1},
            },
            {
                    {2, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                    {11, 0, 3, 0, 1, 14, 14, 0, 1, 0},
                    {0, 12, 1, 0, 1, 1, 1, 10, 1, 0},
                    {1, 1, 1, 0, 20, 20, 0, 0, 1, 0},
                    {14, 0, 3, 0, 1, 1, 1, 1, 1, 0},
                    {0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                    {1, 0, 1, 1, 1, 3, 1, 1, 1, 3},
                    {0, 0, 20, 1, 14, 0, 1, 0, 0, 0},
                    {0, 12, 0, 1, 0, 0, 1, 0, 10, 10},
            },
            {
                    {0, 0, 0, 0, 0, 14, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1, 0, 1, 14, 14, 14},
                    {0, 1, 21, 0, 3, 0, 3, 14, 14, 14},
                    {0, 1, 0, 0, 1, 0, 1, 1, 1, 1},
                    {0, 1, 1, 1, 1, 0, 1, 12, 0, 12},
                    {0, 1, 10, 10, 3, 0, 3, 0, 12, 0},
                    {0, 1, 10, 10, 1, 0, 1, 1, 1, 1},
                    {0, 1, 1, 3, 1, 0, 1, 13, 0, 13},
                    {0, 1, 0, 0, 1, 0, 3, 0, 13, 0},
                    {2, 1, 4, 0, 1, 0, 1, 13, 0, 13},
            },
            {
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                    {1, a, i, q, t, 1, 0, 0, 0, 1},
                    {1, b, j, r, u, 1, 0, 0, 0, 1},
                    {0, c, k, s, v, 1, 0, 0, 0, 1},
                    {1, d, l, 0, w, 1, 0, 0, 0, 1},
                    {1, e, m, 0, x, 1, 0, 0, 0, 1},
                    {1, f, n, 0, y, 3, 0, 0, 0, 1},
                    {1, g, o, 0, z, 1, 0, 0, 0, 2},
                    {1, h, p, 0, 0, 1, 0, 0, 0, 1},
                    {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
            }
    };
//    public int beginX = 9, beginY = 5;
    public int [] beginX = {9, 9, 0, 3};
    public int [] beginY = {5, 5, 0, 0};

    public MapData() {
    }

    public Map getMap(int mapSize, int level){
        if(mapSize == 10){
            return new Map(map10[level], beginX[level], beginY[level]);
        }
        return new Map(map10[level], beginX[level], beginY[level]);

    }

    public int getGameCount(){
        return map10.length;
    }



}
