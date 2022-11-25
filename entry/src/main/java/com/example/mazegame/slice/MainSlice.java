package com.example.mazegame.slice;

import com.example.mazegame.ResourceTable;
import com.example.mazegame.model.Character;
import com.example.mazegame.model.CharacterDbStore;
import com.example.mazegame.source.Const;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.List;

public class MainSlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0, "MY_TAG");

    private Button chooseCha_btn;
    private Character character;
    private Text blood_text;
    private Text aggre_text;
    private Text defense_text;
    private Text money_text;
    private Text level_text;
    private Image girl_image;
    private TextField playerName_text;
    private TextField dialog_text;
    private Image playerImage;
    private Button store_btn;
    private Button new_begin_btn;
    private Button choose_btn;
    private Button begin_btn;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_main_layout);

        initComponent();

        // 获取数据
        character = getCharaterData();
        character.setPicId(intent.getIntParam("playerImageId", character.getPicId()));

        // 设置玩家信息
        setPlayerInfo();

        // 保存玩家是数据
        saveCharacterData();

        // 添加响应事件
        addlistener();

    }



    private void addlistener() {
        chooseCha_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                saveCharacterData();


                ChooseCharacterSlice slice = new ChooseCharacterSlice();
                Intent intent = new Intent();
                present(slice, intent);
            }
        });

        // 新的开始
        new_begin_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                newBegin();
            }
        });

        begin_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                saveCharacterData();
                MainAbilitySlice slice = new MainAbilitySlice();
                Intent intent = new Intent();
                intent.setParam("gameLevel", character.getLevel());
                present(slice, intent);
            }
        });

        store_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                saveCharacterData();
                StoreSlice slice = new StoreSlice();
                Intent intent = new Intent();
                present(slice, intent);
            }
        });

        choose_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                saveCharacterData();
                ChooseGameSlice slice = new ChooseGameSlice();
                Intent intent = new Intent();
                present(slice, intent);
            }
        });




    }

    /**
     * 设置玩家信息
     */
    private void setPlayerInfo(){
        level_text.setText("level."+character.getLevel());
        blood_text.setText(character.getBlood()+"");
        aggre_text.setText(character.getAggressivity()+"");
        defense_text.setText(character.getDefense()+"");
        money_text.setText(character.getMoney()+"");
        playerName_text.setText(character.getName());
        dialog_text.setText(character.getDialog());

        playerImage.setPixelMap(character.getPicId());

        girl_image = (Image) findComponentById(ResourceTable.Id_girl_image);
        if(character.isIsHaveGirl()){
            girl_image.setVisibility(Image.VISIBLE);
            girl_image.setPixelMap(character.getGrilId());
        }else{
            girl_image.setVisibility(Image.HIDE);

        }
    }

    private void initComponent() {
        chooseCha_btn = (Button) findComponentById(ResourceTable.Id_choosecha_btn);
        level_text = (Text) findComponentById(ResourceTable.Id_level_text);
        blood_text = (Text) findComponentById(ResourceTable.Id_xue_text);
        aggre_text = (Text) findComponentById(ResourceTable.Id_gongji_text);
        defense_text = (Text) findComponentById(ResourceTable.Id_fangyu_text);
        money_text = (Text) findComponentById(ResourceTable.Id_money_text);

        playerName_text = (TextField) findComponentById(ResourceTable.Id_player_name_textfield);
        dialog_text = (TextField) findComponentById(ResourceTable.Id_player_dialog_textfield);
        playerImage = (Image) findComponentById(ResourceTable.Id_player_image);

        store_btn = (Button) findComponentById(ResourceTable.Id_store_btn);
        new_begin_btn = (Button) findComponentById(ResourceTable.Id_new_begin_btn);
        choose_btn = (Button) findComponentById(ResourceTable.Id_choose_level_btn);
        begin_btn = (Button) findComponentById(ResourceTable.Id_play_btn);

    }


    /**
     * 如果数据库内无数据，则添加数据
     */
    private void addCharacterData() {
//         ormContext为对象数据库的操作接口，之后的增删等操作都是通过该对象进行操作
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);


        OrmPredicates ormPredicates = ormContext.where(Character.class).equalTo("id", Const.playerId);

        List<Character> recordList = ormContext.query(ormPredicates);
        if(recordList.size() == 0){
            // long id, String name, String dialog, boolean isHaveGirl, int grilId, int picId, int blood, int aggressivity, int defense, int level, int money
            Character Character = new Character(5201314, "player", "又是美好的一天", false, ResourceTable.Media_girl1,
                    ResourceTable.Media_player1, 100, 50, 50, 1, 520);

            ormContext.insert(Character);   //插入内存
        }
        ormContext.flush();
        ormContext.close();
    }


    /**
     *  将玩家数据保存到数据库中
     */
    private void saveCharacterData(){

        character.setName(playerName_text.getText());
        character.setDialog(dialog_text.getText());


        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);

        if (ormContext.update(character)) {
            HiLog.info(label, "updataCharacter success "+character.toString());
        } else {
            HiLog.info(label, "updataCharacter fail "+character.toString());
        }
        ormContext.flush();
        ormContext.close();
    }

    /**
     * 从数据库中获取数据
     * @return
     */
    private Character getCharaterData() {
        Character temp = null;
        //         ormContext为对象数据库的操作接口，之后的增删等操作都是通过该对象进行操作
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);

        OrmPredicates ormPredicates = ormContext.where(Character.class).equalTo("id", Const.playerId);

        List<Character> recordList = ormContext.query(ormPredicates);

        if(recordList.isEmpty()){
            temp = new Character(5201314, "player", "又是美好的一天", false, ResourceTable.Media_girl1,
                    ResourceTable.Media_player1, 99, 50, 50, 1, 520);
            ormContext.insert(temp);   //插入内存
            HiLog.info(label, "insertCharacter:"+temp.toString());

        }else{
            temp = recordList.get(0);

        }
        ormContext.flush();
        ormContext.close();
        return temp;
    }

    /**
     * 更新数据
     */
    private void newBegin(){
        Character temp = null;
        //         ormContext为对象数据库的操作接口，之后的增删等操作都是通过该对象进行操作
        DatabaseHelper helper = new DatabaseHelper(this);
        OrmContext ormContext = helper.getOrmContext(Const.DB_ALIAS, Const.DB_NAME, CharacterDbStore.class);

        OrmPredicates ormPredicates = ormContext.where(Character.class).equalTo("id", Const.playerId);

        List<Character> recordList = ormContext.query(ormPredicates);

        if(recordList.isEmpty()){
            temp = new Character(5201314, "player", "又是美好的一天", false, ResourceTable.Media_girl1,
                    ResourceTable.Media_player1, 99, 50, 50, 1, 520);
            ormContext.insert(temp);   //插入内存
            HiLog.info(label, "insertCharacter:"+temp.toString());

        }else{
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
            HiLog.info(label, "updataCharacter success "+character.toString());
        } else {
            HiLog.info(label, "updataCharacter fail "+character.toString());
        }

        ormContext.flush();
        ormContext.close();

        character = temp;
        setPlayerInfo();


    }
}
