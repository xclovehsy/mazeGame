package com.example.mazegame.slice;

import com.example.mazegame.Provider.StoreItemProvider;
import com.example.mazegame.ResourceTable;
import com.example.mazegame.model.Character;
import com.example.mazegame.model.CharacterDbStore;
import com.example.mazegame.model.StoreItemBean;
import com.example.mazegame.source.Const;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.*;
import ohos.agp.utils.LayoutAlignment;
import ohos.agp.window.dialog.ToastDialog;
import ohos.data.DatabaseHelper;
import ohos.data.orm.OrmContext;
import ohos.data.orm.OrmPredicates;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

import java.util.LinkedList;
import java.util.List;

public class StoreSlice extends AbilitySlice {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0, "MY_TAG");
    private Character character = null;
    private Image player_image;
    private Text money_text;
    private Text blood_text;
    private Text aggre_text;
    private Text defense_text;
    private int gameLevel = 1;
    private Image back_image;
    private Image menu_image;

    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_store_layout);
        gameLevel = intent.getIntParam("gameLevel", 1);

        initComponent();

        character = getCharaterData();

        setplayerInfo();

        addLisenter();

    }

    private void addLisenter() {

        back_image.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                MainAbilitySlice slice = new MainAbilitySlice();
                Intent intent = new Intent();
                intent.setParam("gameLevel", gameLevel);
                present(slice, intent);
            }
        });

        menu_image.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                MainSlice slice = new MainSlice();
                Intent intent = new Intent();
                present(slice, intent);
            }
        });
    }

    private void setplayerInfo(){
        player_image.setPixelMap(character.getPicId());
        money_text.setText(character.getMoney()+"");
        blood_text.setText(character.getBlood()+"");
        aggre_text.setText(character.getAggressivity()+"");
        defense_text.setText(character.getDefense()+"");
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

    private void initComponent() {
        player_image = (Image) findComponentById(ResourceTable.Id_player_image);
        money_text = (Text) findComponentById(ResourceTable.Id_money_text);
        blood_text = (Text) findComponentById(ResourceTable.Id_xue_text);
        aggre_text = (Text) findComponentById(ResourceTable.Id_gongji_text);
        defense_text = (Text) findComponentById(ResourceTable.Id_fangyu_text);

        back_image = (Image) findComponentById(ResourceTable.Id_back_image);
        menu_image = (Image) findComponentById(ResourceTable.Id_menu_image);


        ListContainer listContainer = (ListContainer) findComponentById(ResourceTable.Id_store_item_layout);
        StoreItemProvider provider = new StoreItemProvider(getStoreData(), this);
        provider.setListener(new StoreItemProvider.ItemListener() {
            @Override
            public void click(int i, StoreItemBean bean) {
                HiLog.info(label, "bean=" + bean.toString());
                purchaseGoods(bean);


            }

        });
        listContainer.setItemProvider(provider);
    }

    private void purchaseGoods(StoreItemBean bean) {
        if(bean.getCost()>character.getMoney()){
            // 购买失败
            DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(this)
                    .parse(ResourceTable.Layout_layout_toast, null, false);
            Text text = (Text) toastLayout.findComponentById(ResourceTable.Id_msg_toast);
            text.setText("余额不足,购买失败!");
            new ToastDialog(getContext())
                    .setContentCustomComponent(toastLayout)
                    .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();
        }else{
            character.setMoney(character.getMoney()-bean.getCost());
            character.setDefense(character.getDefense()+bean.getDefense_up());
            character.setAggressivity(character.getAggressivity()+bean.getAggre_up());
            character.setBlood(character.getBlood()+bean.getBlood_up());
            setplayerInfo();
            saveCharacterData();

            // 购买成功
            DirectionalLayout toastLayout = (DirectionalLayout) LayoutScatter.getInstance(this)
                    .parse(ResourceTable.Layout_layout_toast, null, false);
            Text text = (Text) toastLayout.findComponentById(ResourceTable.Id_msg_toast);
            text.setText("恭喜,购买成功!");
            new ToastDialog(getContext())
                    .setContentCustomComponent(toastLayout)
                    .setSize(DirectionalLayout.LayoutConfig.MATCH_CONTENT, DirectionalLayout.LayoutConfig.MATCH_CONTENT)
                    .setAlignment(LayoutAlignment.CENTER)
                    .show();
        }
    }

    /**
     *  将玩家数据保存到数据库中
     */
    private void saveCharacterData(){

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

    private List<StoreItemBean> getStoreData() {
        List<StoreItemBean> listContainer = new LinkedList<>();

        int[] imageId = {
                ResourceTable.Media_chi1,
                ResourceTable.Media_chi2,
                ResourceTable.Media_chi3,
                ResourceTable.Media_dao1,
                ResourceTable.Media_dao2,
                ResourceTable.Media_dao3,
                ResourceTable.Media_fangyu1,
                ResourceTable.Media_fangyu2,
                ResourceTable.Media_fangyu3,
                ResourceTable.Media_yaosui1,
                ResourceTable.Media_yaosui2,
                ResourceTable.Media_yaosui3,
        };

        String[] name = {
                "柠檬汽水",
                "面包",
                "奶油蛋糕",
                "小剪刀",
                "水果刀",
                "弓箭",
                "小盾牌",
                "大盾牌",
                "高级盾牌",
                "生命药水",
                "防御药水",
                "战斗药水"
        };
        String[] describe = {
                "增加生命力",
                "增加生命力",
                "增加生命力",
                "提高攻击力",
                "提高攻击力",
                "提高攻击力",
                "增强防御力",
                "增强防御力",
                "增强防御力",
                "神秘作用",
                "神秘作用",
                "神秘作用"
        };

        int[] cost = {
                40,
                80,
                120,
                40,
                80,
                120,
                40,
                80,
                120,
                200,
                300,
                400
        };

        int[] blood_up = {
                50,
                100,
                150,
                0, 0, 0,
                0, 0, 0,
                100,
                100,
                0,
        };

        int[] aggre_up = {
                0, 0, 0,
                50,
                100,
                150,
                0, 0, 0,
                0, 0, 300,
        };

        int[] defense_up = {
                0, 0, 0,
                0, 0, 0,
                50,
                100,
                150,
                50, 200, 0
        };

        for (int i = 0; i < imageId.length; i++) {
            StoreItemBean bean = new StoreItemBean(i, imageId[i], name[i], describe[i],
                    cost[i], blood_up[i], aggre_up[i], defense_up[i]
            );
            listContainer.add(bean);
        }
        return listContainer;

//        private int id;
//        private int picId;
//        private String name;
//        private String describe;
//        private int cost;
//        private int blood_up;
//        private int aggre_up;
//        private int defense_up;
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
