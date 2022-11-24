package com.example.mazegame.slice;

import com.example.mazegame.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.AttrHelper;
import ohos.agp.components.Component;
import ohos.agp.components.Image;
import ohos.agp.components.TableLayout;


public class ChooseCharacterSlice extends AbilitySlice {

    private int[] playerId = {ResourceTable.Media_player1,
            ResourceTable.Media_play2,
            ResourceTable.Media_play3,
            ResourceTable.Media_play4,
            ResourceTable.Media_play5,
            ResourceTable.Media_play6,
            ResourceTable.Media_play7,
            ResourceTable.Media_play8,
            ResourceTable.Media_play9,
            ResourceTable.Media_play10,
            ResourceTable.Media_play11,
            ResourceTable.Media_play12,
            ResourceTable.Media_play13,
            ResourceTable.Media_play14,
            ResourceTable.Media_play15,
            ResourceTable.Media_play16,
    };
    private int pm_px;
    private int pg_px;

    public void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_choose_character_layout);

        pm_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().width, this);
        pg_px = AttrHelper.vp2px(getContext().getResourceManager().getDeviceCapability().height, this);

        initComponent();

    }

    private void initComponent() {

        int mapLayoutWidth = (int) (pm_px * 0.7);
        int ChipWidth = (int) (mapLayoutWidth / 2);
        int blockMargin = (int) (ChipWidth * 0.15);

        TableLayout tableLayout = (TableLayout) findComponentById(ResourceTable.Id_player_layout);
        tableLayout.setRowCount((int)(playerId.length/2));
        tableLayout.setColumnCount(2);

        for(int i = 0;i<playerId.length; i++){
            Image image = new Image(this);
            image.setPixelMap(playerId[i]);
            image.setWidth(ChipWidth);
            image.setHeight(ChipWidth);
            image.setMarginRight(blockMargin);
            image.setMarginTop(blockMargin);
            image.setMarginLeft(blockMargin);
            image.setMarginBottom(blockMargin);
            image.setScaleMode(Image.ScaleMode.STRETCH);

            int finalI = i;
            image.setClickedListener(new Component.ClickedListener() {
                @Override
                public void onClick(Component component) {
                    MainSlice slice = new MainSlice();
                    Intent intent = new Intent();
                    intent.setParam("playerImageId", playerId[finalI]);
                    present(slice, intent);
                }
            });

            tableLayout.addComponent(image);
        }

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
