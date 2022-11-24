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
    private Button store_btn;


    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_ability_main);

        pm_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().width, this);
        pg_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().height, this);

        // 需要传的参数
        character = getCharaterData();  // 从数据库中获取数据
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
                if(gameLevel<new MapData().getGameCount()){
                    clearElements();
                    int newLevel = Math.max(character.getLevel(), gameLevel+1);
                    character.setLevel(newLevel);
                    // 保存用户数据
                    saveCharacterData();

                    MainAbilitySlice slice = new MainAbilitySlice();
                    Intent intent = new Intent();
                    intent.setParam("gameLevel", newLevel);
                    present(slice, intent);
                }else{
                    // 购买失败
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



//                CommonDialog cd = new CommonDialog(getContext());
//                cd.setCornerRadius(50);
//                DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_gameover_dialog, null, false);
//
//                Button nextGame_btn = (Button) dl.findComponentById(ResourceTable.Id_new_game_btn);
//                Button replay_btn = (Button) dl.findComponentById(ResourceTable.Id_replay_btn);
//
//                nextGame_btn.setClickedListener(new Component.ClickedListener() {
//                    @Override
//                    public void onClick(Component component) {
//                        cd.destroy();
//                    }
//                });
//
//                replay_btn.setClickedListener(new Component.ClickedListener() {
//                    @Override
//                    public void onClick(Component component) {
//                        cd.destroy();
//                    }
//                });
//
//                cd.setSize(1000, 1000);
//                cd.setContentCustomComponent(dl);
//                cd.show();

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
            player.setContentPositionX(mapImage[x][y].getLeft() + mapLayout.getLeft());
            player.setContentPositionY(mapImage[x][y].getTop() + mapLayout.getTop());
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

        } else if (type == MapData.small_monster) {
            return attackMonster(x, y);
//            return true;


        } else if (type == MapData.big_monster) {
            return attackMonster(x, y);

        } else if (type == MapData.shield) {
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

                play_blood.setText(play_blood_ + "");
                mon_blood.setText(mon_blood_ + "");

                play_aggre.setText(play_attack_ + "");
                play_defense.setText(play_defense_ + "");
                mon_aggre.setText(mon_attack_ + "");
                mon_defense.setText(mon_defense_ + "");

                character.setBlood(play_blood_);
                character.setDefense(play_defense_);
                character.setAggressivity(play_attack_);
                setPlayerInfo();

                element.setBlood(mon_blood_);
                element.setAggre(mon_attack_);
                element.setDefense(mon_defense_);

//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                cd.destroy();
//                if(mon_blood_<=0){
//
//                    return true;
//                }
//              Text mon_blood =   cd.destroy();
            }
        });


        cd.setSize(MATCH_CONTENT, MATCH_CONTENT);
        cd.setContentCustomComponent(dl);
        cd.show();
        return false;
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

    private void clearElements(){
        for(int i = 0; i<mapsize; i++){
            for(int j = 0; j<mapsize;j++){
                if(mapElements[i][j]!=null){
                    mainLayout.removeComponent(mapElements[i][j].getImage());
                }
            }
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
        player.setContentPositionX(mapImage[beginx][beginy].getLeft() + mapLayout.getLeft());
        player.setContentPositionY(mapImage[beginx][beginy].getTop() + mapLayout.getTop());

        // 道具
        for (int i = 0; i < mapsize; i++) {
            for (int j = 0; j < mapsize; j++) {
                if (mapElements[i][j] != null && !mapElements[i][j].isUsed()) {
                    Image image = mapElements[i][j].getImage();
                    image.setContentPositionX(mapImage[i][j].getLeft() + mapLayout.getLeft() - 10);
                    image.setContentPositionY(mapImage[i][j].getTop() + mapLayout.getTop() - 10);
                    image.setVisibility(Image.VISIBLE);
                }
            }
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
                        case MapData.money:
                            image.setPixelMap(ResourceTable.Media_money);
                            break;
                        case MapData.shield:
                            image.setPixelMap(ResourceTable.Media_dunpai);
                            break;
                        case MapData.sword:
                            image.setPixelMap(ResourceTable.Media_dao);
                            break;
                        case MapData.aid:
                            image.setPixelMap(ResourceTable.Media_aid);
                            break;
                        case MapData.small_monster:
                            image.setPixelMap(ResourceTable.Media_monster_small);
                            break;
                        case MapData.big_monster:
                            image.setPixelMap(ResourceTable.Media_monster_big);
                            break;
                        case MapData.key:
                            image.setPixelMap(ResourceTable.Media_key);
                            break;
                        default:
                            image.setPixelMap(ResourceTable.Media_money);
                            break;

                    }
                    image.setScaleMode(Image.ScaleMode.STRETCH);
                    image.setWidth((int) (mapChipWidth * 1.2));
                    image.setHeight((int) (mapChipWidth * 1.2));
                    mainLayout.addComponent(image);
                    image.setVisibility(Component.INVISIBLE);

                    if (map[i][j] == MapData.small_monster) {
                        Random random = new Random();
                        //Image image, int type, String name, String dialog, int blood, int ai ggre, int defense
                        mapElements[i][j] = new Element(image, map[i][j], "小丑", "你才是小丑", random.nextInt(20) + 20, random.nextInt(20) + 20, random.nextInt(20) + 20);
                    } else if (map[i][j] == MapData.big_monster) {
                        Random random = new Random();
                        Random random1 = new Random();
                        mapElements[i][j] = new Element(image, map[i][j], "小火龙", "fire！！", random1.nextInt(20) + 40, random.nextInt(20) + 40, random1.nextInt(20) + 40);
//                        mapElements[i][j] = new Element(image, map[i][j]);
                    } else {
                        mapElements[i][j] = new Element(image, map[i][j]);
                    }

                }

            }
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
