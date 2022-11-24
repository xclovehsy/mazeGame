package com.example.mazegame.slice;

import com.example.mazegame.ResourceTable;
import com.example.mazegame.model.Character;
import com.example.mazegame.model.CharacterDbStore;
import com.example.mazegame.model.Element;
import com.example.mazegame.source.Const;
import com.example.mazegame.source.MapData;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.colors.RgbColor;
import ohos.agp.components.*;
import ohos.agp.components.element.ShapeElement;
import ohos.agp.utils.Color;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.List;

public class ChooseGameSlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0, "MY_TAG");

    private int gameCnt = 0;
    private int pm_px;
    private int pg_px;
    private int mapChipWidth;
    private int blockMargin;
    private int playerLevel;
    private Character character;
    private Image menu_image;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_choose_game_layout);

        character = getCharaterData();
        playerLevel = character.getLevel();

        pm_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().width, this);
        pg_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().height, this);

        int layoutWidth = (int) (pm_px * 0.8);
        mapChipWidth = (int) (layoutWidth / 2);
        blockMargin = (int) (mapChipWidth * 0.1);

        gameCnt = new MapData().getGameCount();
//        gameCnt = 20;

        initComponent();

        addLisenter();

    }

    private void addLisenter() {

        menu_image.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                MainSlice slice = new MainSlice();
                Intent intent = new Intent();
                present(slice, intent);
            }
        });
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
//            HiLog.info(label, "insertCharacter:"+temp.toString());

        }else{
            temp = recordList.get(0);

        }
        ormContext.flush();
        ormContext.close();
        return temp;
    }

    private void initComponent() {
        menu_image = (Image) findComponentById(ResourceTable.Id_menu_image);
//        Image menu_image = (Image) findComponentById(ResourceTable.Media_menu_icon);
//        menu_image.setClickedListener(new Component.ClickedListener() {
//            @Override
//            public void onClick(Component component) {
//                MainSlice slice = new MainSlice();
//                Intent intent = new Intent();
//                present(slice, intent);
//            }
//        });

        TableLayout tableLayout = (TableLayout) findComponentById(ResourceTable.Id_table_layout);

        tableLayout.setColumnCount(2);
        tableLayout.setRowCount((int)Math.ceil((float)gameCnt/2));
        for(int i = 1; i<=gameCnt; i++){
            Button button = new Button(this);
            button.setWidth(mapChipWidth);
            button.setHeight((int)(mapChipWidth*0.3));
            button.setTextSize(80);
            button.setMarginLeft(blockMargin);
            button.setMarginRight(blockMargin);
            button.setMarginBottom(blockMargin*2);

            if(i<=playerLevel){
                ShapeElement shapeElement = new ShapeElement();
                // 设置红色背景
                shapeElement.setRgbColor(new RgbColor(40, 175, 234));
                button.setBackground(shapeElement);
                int finalI = i;
                button.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        HiLog.info(label, "chooseButton=" + finalI);
                        MainAbilitySlice slice = new MainAbilitySlice();
                        Intent intent = new Intent();
                        intent.setParam("gameLevel", finalI);
                        present(slice, intent);
                    }
                });

            }else{
                ShapeElement shapeElement = new ShapeElement();
                // 设置红色背景
                shapeElement.setRgbColor(new RgbColor(159, 217, 243));
                button.setBackground(shapeElement);
                button.setClickable(false);
            }

            button.setTextColor(Color.WHITE);




//            button.setTextAlignment(1);
            button.setText("关卡."+i);
            tableLayout.addComponent(button);

        }


    }
}
