package com.example.mazegame.Provider;


import com.example.mazegame.ResourceTable;
import com.example.mazegame.model.StoreItemBean;
import ohos.agp.components.*;
import ohos.app.Context;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.usb.USBEndpoint;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StoreItemProvider extends BaseItemProvider {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0, "MY_TAG");

    private List<StoreItemBean> recordBeanList;
    private Context context;
    ItemListener listener;

    public ItemListener getListener() {
        return listener;
    }

    public void setListener(ItemListener listener) {
        this.listener = listener;
    }

    public static interface ItemListener {
        public void click(int i, StoreItemBean bean);
    }

    public StoreItemProvider(List<StoreItemBean> recordBeanList, Context context) {
        this.recordBeanList = recordBeanList;
        this.context = context;
    }

    public StoreItemProvider() {
    }

    @Override
    public int getCount() {
        return recordBeanList.size();
    }

    @Override
    public Object getItem(int i) {
        return recordBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return recordBeanList.get(i).getId();
    }

    @Override
    public Component getComponent(int i, Component component, ComponentContainer componentContainer) {
        ComponentContainer container = (ComponentContainer) LayoutScatter.getInstance(context).parse(ResourceTable.Layout_storeitemlayout, null, false);


        StoreItemBean bean = recordBeanList.get(i);

        DirectionalLayout layout = (DirectionalLayout) container.findComponentById(ResourceTable.Id_recorditem_layout);
        Image image = (Image) container.findComponentById(ResourceTable.Id_kind_image);
        image.setPixelMap(bean.getPicId());

        Text name = (Text) container.findComponentById(ResourceTable.Id_nametext);
        Text des = (Text) container.findComponentById(ResourceTable.Id_describetext);

        name.setText(bean.getName());
        des.setText(bean.getDescribe());

        Text money = (Text) container.findComponentById(ResourceTable.Id_money_text);
        money.setText(bean.getCost() + "￥");

        Image image1 = (Image) container.findComponentById(ResourceTable.Id_image_1);
        Image image2 = (Image) container.findComponentById(ResourceTable.Id_image_2);
        Text text1 = (Text) container.findComponentById(ResourceTable.Id_value_1);
        Text text2 = (Text) container.findComponentById(ResourceTable.Id_value_2);
        DirectionalLayout dl2 = (DirectionalLayout) container.findComponentById(ResourceTable.Id_improve_2);


        Integer[] temp = {bean.getBlood_up(), bean.getAggre_up(), bean.getDefense_up()};
        Arrays.sort(temp, Collections.reverseOrder());
        HiLog.info(label, "arraytest=" + Arrays.toString(temp));

        // 设置第一张

        if (temp[0] == bean.getBlood_up()) {
            image1.setPixelMap(ResourceTable.Media_xue);
            text1.setText(bean.getBlood_up() + "");
        }else if(temp[0] == bean.getAggre_up()){
            image1.setPixelMap(ResourceTable.Media_dao);
            text1.setText(bean.getAggre_up() + "");
        }else if(temp[0] == bean.getDefense_up()){
            image1.setPixelMap(ResourceTable.Media_dunpai);
            text1.setText(bean.getDefense_up() + "");
        }

        if(temp[1] == 0){
               dl2.setVisibility(DirectionalLayout.HIDE);
        }else{
            dl2.setVisibility(DirectionalLayout.VISIBLE);
            if(temp[1] == bean.getDefense_up()){
                image2.setPixelMap(ResourceTable.Media_dunpai);
                text2.setText(bean.getDefense_up() + "");
            }else if (temp[1] == bean.getBlood_up()) {
                image2.setPixelMap(ResourceTable.Media_xue);
                text2.setText(bean.getBlood_up() + "");
            }else if(temp[1] == bean.getAggre_up()){
                image2.setPixelMap(ResourceTable.Media_dao);
                text2.setText(bean.getAggre_up() + "");
            }
        }


        layout.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                listener.click(i, bean);
            }
        });

        return container;
    }
}

