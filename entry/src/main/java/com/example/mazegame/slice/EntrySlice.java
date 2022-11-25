package com.example.mazegame.slice;

import com.example.mazegame.ResourceTable;
import ohos.aafwk.ability.AbilitySlice;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Button;
import ohos.agp.components.Component;
import ohos.agp.components.DirectionalLayout;
import ohos.agp.components.LayoutScatter;
import ohos.agp.window.dialog.CommonDialog;

import static ohos.agp.components.ComponentContainer.LayoutConfig.MATCH_CONTENT;

public class EntrySlice extends AbilitySlice {
    @Override
    protected void onStart(Intent intent) {
        super.onStart(intent);
        super.setUIContent(ResourceTable.Layout_gamebeginlayout);
        Button begin_btn = (Button) findComponentById(ResourceTable.Id_begin_btn);
        Button guide_btn = (Button) findComponentById(ResourceTable.Id_guide_btn);
        Button info_btn = (Button) findComponentById(ResourceTable.Id_info_btn);

        begin_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                MainSlice slice= new MainSlice();
                Intent intent1 = new Intent();
                present(slice, intent1);
            }
        });

        guide_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                CommonDialog cd = new CommonDialog(getContext());
                cd.setCornerRadius(50);
                DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_info_layout, null, false);

                Button btn = (Button) dl.findComponentById(ResourceTable.Id_ok_btn);
                btn.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        cd.destroy();
                    }
                });

                cd.setSize(1000, 1000);
                cd.setContentCustomComponent(dl);
                cd.show();
            }
        });

        info_btn.setClickedListener(new Component.ClickedListener() {
            @Override
            public void onClick(Component component) {
                CommonDialog cd = new CommonDialog(getContext());
                cd.setCornerRadius(50);
                DirectionalLayout dl = (DirectionalLayout) LayoutScatter.getInstance(getContext()).parse(ResourceTable.Layout_authorinfo_layout, null, false);

                Button btn = (Button) dl.findComponentById(ResourceTable.Id_ok_btn);
                btn.setClickedListener(new Component.ClickedListener() {
                    @Override
                    public void onClick(Component component) {
                        cd.destroy();
                    }
                });

                cd.setSize(1000, 1000);
                cd.setContentCustomComponent(dl);
                cd.show();
            }
        });




    }
}
