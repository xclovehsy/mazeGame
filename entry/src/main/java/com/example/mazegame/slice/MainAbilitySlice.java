package com.example.mazegame.slice;

import com.example.mazegame.MainAbility;
import com.example.mazegame.ResourceTable;
import com.example.mazegame.model.Character;
import com.example.mazegame.model.CharacterDbStore;
import com.example.mazegame.model.Element;
import com.example.mazegame.model.MapChip;
import com.example.mazegame.source.Const;
import com.example.mazegame.source.MapData;
import com.example.mazegame.source.MonsterData;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.animation.Animator;
import ohos.agp.animation.AnimatorValue;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.CommonDialog;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;
import ohos.org.xml.sax.helpers.DefaultHandler;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

import java.nio.InvalidMarkException;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainAbilitySlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0, "MY_TAG");
    private TableLayout mapLayout;
    private int mapsize = 10;
    private int pm_px;  // 手机设备的宽
    private int pg_px;  // 手机设备的高
    private Button play_bnt;
    int[][] map;
    int beginx, beginy;
    private Image player;
    private Image[][] mapImage;
    private int mapChipWidth;
    private Image left_btn, right_btn, up_btn, down_btn;
    private int curX, curY;
    private Element[][] mapElements;
    private DirectionalLayout mainLayout;
    private int blockMargin;
    private Character character;
    private Text level_text, blood_text, aggre_text, defense_text, money_text;
    private int keycnt = 0;
    private int gameLevel = 0;
        private Button back_btn;
    private Image girl;
    private Button store_btn;
    private int shift = 30;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        pm_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().width, this);
        pg_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().height, this);

        // 需要传的参数
        character = getCharaterData();  // 从数据库中获取数据
//        character.setLevel(5);
//
//
//        character.setIsHaveGirl(true);
        gameLevel = intent.getIntParam("gameLevel", 1);
        mapsize = 10;

        mapImage = new Image[mapsize][mapsize];
        mapElements = new Element[mapsize][mapsize];

        MapData.Map mapData = new MapData().getMap(mapsize, gameLevel - 1);
        beginx = mapData.beginX;
        beginy = mapData.beginY;
        map = mapData.map;

        initComponent();

        setPlayerInfo();

        addClickListener();

//        play_bnt.callOnClick();
    }

    /**
     * 从数据库中获取数据
     *
     * @return
     */
    private Character getCharaterData() {
        Character temp = null;
        //         ormContext为对象数据库的操作接口，之后的增删等操作都是通过该对象进行操作
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);

        OrmPredicates ormPredicates = ormContext.where(Character.class).equalTo("id", Const.playerId);

        List<Character> recordList = ormContext.query(ormPredicates);

        if (recordList.isEmpty()) {
            temp = new Character(5201314, "player", "又是美好的一天", false, ResourceTable.Media_girl1,
                    ResourceTable.Media_player1, 99, 50, 50, 1, 520);
            ormContext.insert(temp);   //插入内存
            HiLog.info(label, "insertCharacter:" + temp.toString());

        } else {
            temp = recordList.get(0);

        }
        ormContext.flush();
        ormContext.close();
        return temp;
    }

    /**
     * 设置玩家信息
     */
    private void setPlayerInfo() {
        level_text.setText("level." + character.getLevel());
        blood_text.setText(character.getBlood() + "");
        aggre_text.setText(character.getAggressivity() + "");
        defense_text.setText(character.getDefense() + "");
        money_text.setText(character.getMoney() + "");
        player.setPixelMap(character.getPicId());
    }

    /**
     * 移动玩家
     *
     * @param moveX
     * @param moveY
     */
    private void playerMove(int moveX, int moveY) {
        int x = curX + moveX, y = curY + moveY;
//        HiLog.info(label, "curX="+curX + ", curY="+curY + ", moveX="+x + ", moveY="+y +", map="+map[x][y]);
        if (x >= 0 && x < this.mapsize && y >= 0 && y < this.mapsize && map[x][y] != 1) {
            if (map[x][y] == MapData.finish) {
                if (gameLevel < new MapData().getGameCount()) {
                    clearElements();
                    int newLevel = Math.max(character.getLevel(), gameLevel + 1);
                    character.setLevel(newLevel);
                    // 保存用户数据
                    saveCharacterData();

                    MainAbilitySlice slice = new MainAbilitySlice();
                    Intent intent = new Intent();
                    intent.setParam("gameLevel", newLevel);
                    present(slice, intent);
                } else {
                    DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(this)
                            .parse(ResourceTable.Layout_layout_toast, null, false);
                    Text text = (Text) toastLayout.findComponentById(ResourceTable.Id_msg_toast);
                    text.setText("您已通关!");
                    new ToastDialog(getContext())
                            .setContentCustomComponent(toastLayout)
                            .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                            .setAlignment(LayoutAlignment.CENTER)
                            .show();
                }
                return;
            }else if(x == beginx && y == beginy){  //回到起点
                if(gameLevel!=1){
                    clearElements();
                    // 保存用户数据
                    saveCharacterData();

                    MainAbilitySlice slice = new MainAbilitySlice();
                    Intent intent = new Intent();
                    intent.setParam("gameLevel", gameLevel-1);
                    present(slice, intent);
                }
                return;
            } else if (map[x][y] == 3) {//men
                if (keycnt > 0) {
                    keycnt -= 1;
                    mapImage[x][y].setPixelMap(ResourceTable.Media_floor);
                    map[x][y] = 0;
                    return;
                } else {
                    return;
                }

            } else if (mapElements[x][y] != null && !mapElements[x][y].isUsed()) {
                if (!interactWithElement(x, y)) {
                    return;
                }
            }  // 直接移动
            // 移动
            curX = x;
            curY = y;
            if (character.isIsHaveGirl()) {
                girl.setContentPositionX(player.getContentPositionX());
                girl.setContentPositionY(player.getContentPositionY());
            }

            player.setContentPositionX(mapImage[x][y].getLeft() + mapLayout.getLeft() + shift);
            player.setContentPositionY(mapImage[x][y].getTop() + mapLayout.getTop() + shift);
        }
    }

    /**
     * 与道具交互
     *
     * @param x
     * @param y
     * @return 是否移动玩家
     */
    private boolean interactWithElement(int x, int y) {
        int type = mapElements[x][y].getType();

        if (type == MapData.money) {  // 金币
            Random random = new Random();
            character.setMoney(character.getMoney() + random.nextInt(40) + 10);
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        } else if (type == MapData.small_monster || type == MapData.big_monster || (type>=MapData.t && type<=MapData.z)) {
            return attackMonster(x, y);
//            return true;

        }  else if (type == MapData.shield) {
            Random random = new Random();
            character.setDefense(Math.min(character.getLevel() * 100, character.getDefense() + random.nextInt(10) + 10));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        } else if (type == MapData.sword) {

            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setAggressivity(Math.min(character.getLevel() * 100, character.getAggressivity() + random.nextInt(10) + 10));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        } else if (type == MapData.aid) {

            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setBlood(Math.min(character.getLevel() * 100, character.getBlood() + random.nextInt(10) + 10));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        } else if (type == MapData.key) {
            keycnt += 1;
            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;
        } else if (type == MapData.girl) {
            character.setIsHaveGirl(true);
            girl = mapElements[x][y].getImage();
            map[x][y] = 0;
            mapElements[x][y].setUsed(true);

            DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(this)
                    .parse(ResourceTable.Layout_layout_toast, null, false);
            Text text = (Text) toastLayout.findComponentById(ResourceTable.Id_msg_toast);
            text.setText("你好呀！");
            new ToastDialog(getContext())
                    .setContentCustomComponent(toastLayout)
                    .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();

            return false;
        }else if(type == MapData.f){
            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setBlood(Math.min(character.getLevel() * 100, character.getBlood() + random.nextInt(10) + 10));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        } else if (type == MapData.g) {
            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setBlood(Math.min(character.getLevel() * 100, character.getBlood() + random.nextInt(20) + 20));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        } else if(type == MapData.h){
            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setBlood(Math.min(character.getLevel() * 100, character.getBlood() + random.nextInt(20) + 60));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.i){
            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setAggressivity(Math.min(character.getLevel() * 100, character.getAggressivity() + random.nextInt(10) + 10));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;


        }else if(type == MapData.j){
            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setAggressivity(Math.min(character.getLevel() * 100, character.getAggressivity() + random.nextInt(20) + 20));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.m){
            Random random = new Random();
//            character.setDefense(Math.min(character.getLevel()*100, character.getDefense()+random.nextInt(10)+10));
            character.setAggressivity(Math.min(character.getLevel() * 100, character.getAggressivity() + random.nextInt(20) + 40));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.n){
            Random random = new Random();
            character.setDefense(Math.min(character.getLevel() * 100, character.getDefense() + random.nextInt(10) + 10));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.o){
            Random random = new Random();
            character.setDefense(Math.min(character.getLevel() * 100, character.getDefense() + random.nextInt(20) + 20));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.p){
            Random random = new Random();
            character.setDefense(Math.min(character.getLevel() * 100, character.getDefense() + random.nextInt(20) + 40));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.q){
            Random random = new Random();
            character.setDefense(Math.min(character.getLevel() * 100, character.getDefense() + random.nextInt(100) + 100));
            character.setBlood(Math.min(character.getLevel() * 100, character.getBlood() + random.nextInt(50) + 50));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;



        }else if(type == MapData.r){
            Random random = new Random();
            character.setDefense(Math.min(character.getLevel() * 100, character.getDefense() + random.nextInt(50) + 50));
            character.setBlood(Math.min(character.getLevel() * 100, character.getBlood() + random.nextInt(100) + 100));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }else if(type == MapData.s){
            Random random = new Random();
            character.setAggressivity(Math.min(character.getLevel() * 100, character.getAggressivity() + random.nextInt(100) + 100));
            setPlayerInfo();

            mapElements[x][y].getImage().setVisibility(Image.INVISIBLE);
            map[x][y] = 0;
//            mapElements[x][y] = null;
            mapElements[x][y].setUsed(true);
            return true;

        }



        return true;
    }

    private boolean attackMonster(int x, int y) {
        Element element = mapElements[x][y];
        CommonDialog cd = new CommonDialog(getContext());
        cd.setCornerRadius(50);
        DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_attack_layout, null, false);

        Text play_aggre = (Text) dl.findComponentById(ResourceTable.Id_gong_play);
        Text play_defense = (Text) dl.findComponentById(ResourceTable.Id_fang_text);
        Text play_blood = (Text) dl.findComponentById(ResourceTable.Id_xue_play);
        Text mon_aggre = (Text) dl.findComponentById(ResourceTable.Id_gong_monster);
        Text mon_defense = (Text) dl.findComponentById(ResourceTable.Id_fang_monster);
        Text mon_blood = (Text) dl.findComponentById(ResourceTable.Id_xue_monster);

        Text mon_name = (Text) dl.findComponentById(ResourceTable.Id_monster_name);
        Text mon_memo = (Text) dl.findComponentById(ResourceTable.Id_monster_memo);
        mon_name.setText(element.getName());
        mon_memo.setText(element.getDialog());


        play_aggre.setText(character.getAggressivity() + "");
        play_defense.setText(character.getDefense() + "");
        play_blood.setText(character.getBlood() + "");
        mon_aggre.setText(element.getAggre() + "");
        mon_blood.setText(element.getBlood() + "");
        mon_defense.setText(element.getDefense() + "");

        Image play_image = (Image) dl.findComponentById(ResourceTable.Id_play_image);
        Image monster_image = (Image) dl.findComponentById(ResourceTable.Id_monster_image);
        play_image.setPixelMap(character.getPicId());
        monster_image.setPixelMap(new MonsterData().getMonsterImageId(element.getType()));
        monster_image.setPixelMap(element.getImage().getPixelMap());

        Button give_up = (Button) dl.findComponentById(ResourceTable.Id_give_up_btn);
        Button attack = (Button) dl.findComponentById(ResourceTable.Id_attack_btn);
        give_up.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                if (element.getBlood() <= 0) {
                    element.getImage().setVisibility(Image.INVISIBLE);
                    map[x][y] = 0;
//                    mapElements[x][y] = null;
                    mapElements[x][y].setUsed(true);
                }

                cd.destroy();
            }
        });

        attack.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                Random random = new Random();
                int sub_blood_mon = Math.max(character.getAggressivity() - element.getDefense(), 0);
                int sub_blood_play = Math.max(0, element.getAggre() - element.getDefense());
                int play_blood_ = character.getBlood() - sub_blood_play;
                int mon_blood_ = element.getBlood() - sub_blood_mon;

                int play_defense_ = (int) (character.getDefense() * (float) (random.nextInt(20) + 80) / 100);
                int mon_defense_ = (int) (element.getDefense() * (float) (random.nextInt(20) + 80) / 100);
                int play_attack_ = (int) (character.getAggressivity() * (float) (random.nextInt(20) + 80) / 100);
                int mon_attack_ = (int) (element.getAggre() * (float) (random.nextInt(20) + 80) / 100);


                int beginBlood = character.getBlood();
                int endBlood = character.getBlood() - sub_blood_play;
                int beginBlood_mon = element.getBlood();
                int endBlood_mon = element.getBlood() - sub_blood_mon;

                int beginAggre = character.getAggressivity();
                int beginAggre_mon = element.getAggre();
                int beginDefense = character.getDefense();
                int beginDefense_mon = element.getDefense();

                AnimatorValue animatorValue = new AnimatorValue();
                animatorValue.setDuration(1000);
                animatorValue.setValueUpdateListener(new AnimatorValue.ValueUpdateListener() {
                    @Override
                    public void onUpdate(AnimatorValue animatorValue, float v) {
//                        HiLog.info(label, "animatorValue=" + v);
                        play_blood.setText((int) (beginBlood - (beginBlood - endBlood) * v) + "");
                        mon_blood.setText((int) (beginBlood_mon - (beginBlood_mon - endBlood_mon) * v) + "");

                        play_aggre.setText((int) (beginAggre - (beginAggre - play_attack_) * v) + "");
                        play_defense.setText((int) (beginDefense - (beginDefense - play_defense_) * v) + "");
                        mon_aggre.setText((int) (beginAggre_mon - (beginAggre_mon - mon_attack_) * v) + "");
                        mon_defense.setText((int) (beginDefense_mon - (beginDefense_mon - mon_defense_) * v) + "");

                        if (play_blood_ <= 0) {
                            DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(getContext())
                                    .parse(ResourceTable.Layout_layout_toast, null, false);
                            Text text = (Text) toastLayout.findComponentById(ResourceTable.Id_msg_toast);
                            text.setText("您被怪物击败了!");
                            new ToastDialog(getContext())
                                    .setContentCustomComponent(toastLayout)
                                    .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                                    .setAlignment(LayoutAlignment.CENTER)
                                    .show();
                            cd.destroy();
                            animatorValue.stop();


                            // 设置新的人物

                            newBegin();

                            clearElements();

                            // 保存数据
                            saveCharacterData();

                            MainSlice slice = new MainSlice();
                            Intent intent = new Intent();
                            present(slice, intent);
                        }

                        if (mon_blood_ <= 0) {

                            DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(getContext())
                                    .parse(ResourceTable.Layout_layout_toast, null, false);
                            Text text = (Text) toastLayout.findComponentById(ResourceTable.Id_msg_toast);
                            text.setText("怪物已被你击败!");
                            new ToastDialog(getContext())
                                    .setContentCustomComponent(toastLayout)
                                    .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                                    .setAlignment(LayoutAlignment.CENTER)
                                    .show();
                            cd.destroy();
                            animatorValue.stop();

                            element.getImage().setVisibility(Image.INVISIBLE);
                            map[x][y] = 0;
                            mapElements[x][y].setUsed(true);
                        }
                    }
                });
                animatorValue.start();

                character.setBlood(play_blood_);
                character.setDefense(play_defense_);
                character.setAggressivity(play_attack_);
                setPlayerInfo();

                element.setBlood(mon_blood_);
                element.setAggre(mon_attack_);
                element.setDefense(mon_defense_);

            }
        });


        cd.setSize(MATCH_CONTENT, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.show();
        return false;
    }

    /**
     * 更新数据
     */
    private void newBegin() {

        Character temp = null;
        //         ormContext为对象数据库的操作接口，之后的增删等操作都是通过该对象进行操作
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);

        OrmPredicates ormPredicates = ormContext.where(Character.class).equalTo("id", Const.playerId);

        List<Character> recordList = ormContext.query(ormPredicates);

        if (recordList.isEmpty()) {
            temp = new Character(5201314, "player", "又是美好的一天", false, ResourceTable.Media_girl1,
                    ResourceTable.Media_player1, 99, 50, 50, 1, 520);
            ormContext.insert(temp);   //插入内存
            HiLog.info(label, "insertCharacter:" + temp.toString());

        } else {
            temp = recordList.get(0);

        }

        temp.setName("player");
        temp.setDialog("又是美好的一天");
        temp.setIsHaveGirl(false);
        temp.setGrilId(ResourceTable.Media_girl1);
        temp.setPicId(ResourceTable.Media_player1);
        temp.setBlood(99);
        temp.setAggressivity(50);
        temp.setDefense(50);
        temp.setLevel(1);
        temp.setMoney(520);


        if (ormContext.update(temp)) {
            HiLog.info(label, "updataCharacter success " + character.toString());
        } else {
            HiLog.info(label, "updataCharacter fail " + character.toString());
        }

        ormContext.flush();
        ormContext.close();

        character = temp;
        setPlayerInfo();


    }

    /**
     * 将玩家数据保存到数据库中
     */
    private void saveCharacterData() {
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);

        if (ormContext.update(character)) {
            HiLog.info(label, "updataCharacter success " + character.toString());
        } else {
            HiLog.info(label, "updataCharacter fail " + character.toString());
        }
        ormContext.flush();
        ormContext.close();
    }

    private void clearElements() {
        for (int i = 0; i < mapsize; i++) {
            for (int j = 0; j < mapsize; j++) {
                if (mapElements[i][j] != null) {
                    mainLayout.removeComponent(mapElements[i][j].getImage());
                }
            }
        }

        if (character.isIsHaveGirl()) {
            mainLayout.removeComponent(girl);
        }
    }

    private void addClickListener() {
        store_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {

                clearElements();

                // 保存数据
                saveCharacterData();

                StoreSlice slice = new StoreSlice();
                Intent intent = new Intent();
                intent.setParam("gameLevel", gameLevel);
                present(slice, intent);
            }
        });

        back_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                clearElements();

                // 保存数据
                saveCharacterData();

                MainSlice slice = new MainSlice();
                Intent intent = new Intent();
                present(slice, intent);
            }
        });

        left_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                playerMove(0, -1);
            }
        });

        right_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                playerMove(0, 1);
            }
        });

        up_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                playerMove(-1, 0);
//                play_bnt.callOnClick();
            }
        });

        down_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                playerMove(1, 0);
            }
        });


        play_bnt.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                playGame();

            }
        });
    }

    /**
     * 开始游戏
     */
    private void playGame() {

        // 玩家
        player.setVisibility(Component.VISIBLE);
        curX = beginx;
        curY = beginy;
        player.setContentPositionX(mapImage[beginx][beginy].getLeft() + mapLayout.getLeft() + shift);
        player.setContentPositionY(mapImage[beginx][beginy].getTop() + mapLayout.getTop() + shift);

        // 道具
        for (int i = 0; i < mapsize; i++) {
            for (int j = 0; j < mapsize; j++) {
                if (mapElements[i][j] != null && !mapElements[i][j].isUsed()) {
                    Image image = mapElements[i][j].getImage();
                    image.setContentPositionX(mapImage[i][j].getLeft() + mapLayout.getLeft() - 10 + shift);
                    image.setContentPositionY(mapImage[i][j].getTop() + mapLayout.getTop() - 10 + shift);
                    image.setVisibility(Image.VISIBLE);
                }
            }
        }

        if (character.isIsHaveGirl()) {
            girl.setVisibility(Image.VISIBLE);
            girl.setContentPositionX(mapImage[beginx][beginy].getLeft() + mapLayout.getLeft() + shift);
            girl.setContentPositionY(mapImage[beginx][beginy].getTop() + mapLayout.getTop() + shift);
        }

    }

    /**
     * 初始化组件
     */
    private void initComponent() {
        level_text = (Text) findComponentById(ResourceTable.Id_level_text);
        blood_text = (Text) findComponentById(ResourceTable.Id_xue_text);
        aggre_text = (Text) findComponentById(ResourceTable.Id_gongji_text);
        defense_text = (Text) findComponentById(ResourceTable.Id_fangyu_text);
        money_text = (Text) findComponentById(ResourceTable.Id_money_text);

        mainLayout = (DirectionalLayout) findComponentById(ResourceTable.Id_game_layout);
        left_btn = (Image) findComponentById(ResourceTable.Id_left);
        right_btn = (Image) findComponentById(ResourceTable.Id_right);
        up_btn = (Image) findComponentById(ResourceTable.Id_up);
        down_btn = (Image) findComponentById(ResourceTable.Id_down);


        player = (Image) findComponentById(ResourceTable.Id_player);
        player.setVisibility(Component.INVISIBLE);
        play_bnt = (Button) findComponentById(ResourceTable.Id_play_btn);
        back_btn = (Button) findComponentById(ResourceTable.Id_back_btn);
        store_btn = (Button) findComponentById(ResourceTable.Id_store_btn);

        int mapLayoutWidth = (int) (pm_px * 0.9);
        mapChipWidth = (int) (mapLayoutWidth / this.mapsize);
        blockMargin = (int) (mapChipWidth * 0.01);

        mapLayout = (TableLayout) findComponentById(ResourceTable.Id_map_layout);
        mapLayout.removeAllComponents();
        mapLayout.setRowCount(this.mapsize);
        mapLayout.setColumnCount(this.mapsize);

        for (int i = 0; i < this.mapsize; i++) {
            for (int j = 0; j < this.mapsize; j++) {

                if (map[j][i] == 1) {   // 墙体
                    Image block = new Image(this);
                    block.setPixelMap(ResourceTable.Media_block);
                    block.setWidth(mapChipWidth);
                    block.setHeight(mapChipWidth);
                    block.setScaleMode(Image.ScaleMode.STRETCH);
                    block.setMarginBottom(blockMargin);
                    block.setMarginLeft(blockMargin);
                    block.setMarginRight(blockMargin);
                    block.setMarginTop(blockMargin);
                    mapLayout.addComponent(block);
//                    mapChipList.add(new MapChip(block, j, i, true));
                    mapImage[j][i] = block;
                } else if (map[j][i] == MapData.door) {
                    Image door = new Image(this);
                    door.setPixelMap(ResourceTable.Media_door);
                    door.setWidth(mapChipWidth);
                    door.setHeight(mapChipWidth);
                    door.setScaleMode(Image.ScaleMode.STRETCH);
                    door.setMarginBottom(blockMargin);
                    door.setMarginLeft(blockMargin);
                    door.setMarginRight(blockMargin);
                    door.setMarginTop(blockMargin);
                    mapLayout.addComponent(door);
//                    mapChipList.add(new MapChip(block, j, i, true));
                    mapImage[j][i] = door;
                } else {  // 地板
                    Image floor = new Image(this);
                    floor.setPixelMap(ResourceTable.Media_floor);
                    floor.setWidth(mapChipWidth);
                    floor.setHeight(mapChipWidth);
                    floor.setScaleMode(Image.ScaleMode.STRETCH);
                    floor.setMarginBottom(blockMargin);
                    floor.setMarginLeft(blockMargin);
                    floor.setMarginRight(blockMargin);
                    floor.setMarginTop(blockMargin);
                    mapLayout.addComponent(floor);
                    mapImage[j][i] = floor;
                }

                // 0地板  1墙  2终点
                // 10金币 11防御  12攻击
                // 20小怪兽  21大怪兽
                if (map[i][j] != 0 && map[i][j] != 1 && map[i][j] != 3) {
                    Image image = new Image(this);
                    switch (map[i][j]) {
                        case MapData.finish:
                            image.setPixelMap(ResourceTable.Media_finish);
                            break;
                        case MapData.girl:
                            image.setPixelMap(ResourceTable.Media_girl1);
                            break;
                        case MapData.a: image.setPixelMap(ResourceTable.Media_money); break;
                        case MapData.b: image.setPixelMap(ResourceTable.Media_dunpai); break;
                        case MapData.c: image.setPixelMap(ResourceTable.Media_dao); break;
                        case MapData.d: image.setPixelMap(ResourceTable.Media_aid); break;
                        case MapData.e: image.setPixelMap(ResourceTable.Media_key); break;
                        case MapData.f: image.setPixelMap(ResourceTable.Media_chi1); break;
                        case MapData.g: image.setPixelMap(ResourceTable.Media_chi2); break;
                        case MapData.h: image.setPixelMap(ResourceTable.Media_chi3); break;
                        case MapData.i: image.setPixelMap(ResourceTable.Media_dao1); break;
                        case MapData.j: image.setPixelMap(ResourceTable.Media_dao2); break;
                        case MapData.k: image.setPixelMap(ResourceTable.Media_monster_small); break;
                        case MapData.l: image.setPixelMap(ResourceTable.Media_monster_big); break;
                        case MapData.m: image.setPixelMap(ResourceTable.Media_dao3); break;
                        case MapData.n: image.setPixelMap(ResourceTable.Media_fangyu1); break;
                        case MapData.o: image.setPixelMap(ResourceTable.Media_fangyu2); break;
                        case MapData.p: image.setPixelMap(ResourceTable.Media_fangyu3); break;
                        case MapData.q: image.setPixelMap(ResourceTable.Media_yaosui1); break;
                        case MapData.r: image.setPixelMap(ResourceTable.Media_yaosui2); break;
                        case MapData.s: image.setPixelMap(ResourceTable.Media_yaosui3); break;
                        case MapData.t: image.setPixelMap(ResourceTable.Media_guai1); break;
                        case MapData.u: image.setPixelMap(ResourceTable.Media_guai2); break;
                        case MapData.v: image.setPixelMap(ResourceTable.Media_guai3); break;
                        case MapData.w: image.setPixelMap(ResourceTable.Media_guai4); break;
                        case MapData.x: image.setPixelMap(ResourceTable.Media_guai5); break;
                        case MapData.y: image.setPixelMap(ResourceTable.Media_guai6); break;
                        case MapData.z: image.setPixelMap(ResourceTable.Media_guai7); break;
                        default:
                            image.setPixelMap(ResourceTable.Media_money);
                            break;
                    }
                    image.setScaleMode(Image.ScaleMode.STRETCH);
                    if (map[i][j] == 4) {
                        image.setWidth((int) (mapChipWidth));
                        image.setHeight((int) (mapChipWidth));
                        if (!character.isIsHaveGirl()) {
                            mainLayout.addComponent(image);
                        } else {
                            map[i][j] = 0;
                        }
                        image.setVisibility(Component.INVISIBLE);
                    } else {
                        image.setWidth((int) (mapChipWidth * 1.2));
                        image.setHeight((int) (mapChipWidth * 1.2));
                        mainLayout.addComponent(image);
                        image.setVisibility(Component.INVISIBLE);
                    }

                    Random random = new Random();

                    switch (map[i][j]){
                        case MapData.k: mapElements[i][j] = new Element(image, map[i][j], "小丑", "你才是小丑", random.nextInt(20) + 20, random.nextInt(20) + 20, random.nextInt(20) + 20); break;
                        case MapData.l: mapElements[i][j] = new Element(image, map[i][j], "小火龙", "fire！！", random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.t: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.u: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.v: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.w: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.x: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.y: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        case MapData.z: mapElements[i][j] = new Element(image, map[i][j], "guai", "memo",random.nextInt(20) + 40, random.nextInt(20) + 40, random.nextInt(20) + 40); break;
                        default:mapElements[i][j] = new Element(image, map[i][j]); break;
                    }

                }

            }
        }


        // 添加起点
        Image begin_point = new Image(this);
        begin_point.setPixelMap(ResourceTable.Media_begin);
        begin_point.setScaleMode(Image.ScaleMode.STRETCH);

        begin_point.setWidth((int) (mapChipWidth * 1.2));
        begin_point.setHeight((int) (mapChipWidth * 1.2));
        mainLayout.addComponent(begin_point);
        begin_point.setVisibility(Component.INVISIBLE);
        mapElements[beginx][beginy] = new Element(begin_point, map[beginx][beginy]);


        // 添加女孩
        if (character.isIsHaveGirl()) {
            girl = new Image(this);
            girl.setPixelMap(character.getGrilId());
            girl.setScaleMode(Image.ScaleMode.STRETCH);
            girl.setWidth((int) (mapChipWidth));
            girl.setHeight((int) (mapChipWidth));
            mainLayout.addComponent(girl);
            girl.setVisibility(Component.INVISIBLE);

        }
//        player.setContentPosition();


    }

    @Override
    public void onActive() {
        super.onActive();
    }

    @Override
    public void onForeground(Intent intent) {
        super.onForeground(intent);
    }
}
