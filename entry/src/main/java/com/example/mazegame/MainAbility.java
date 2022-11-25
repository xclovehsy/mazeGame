package com.example.mazegame;

import com.example.mazegame.slice.*;
import ohos.aafwk.ability.Ability;
import ohos.aafwk.content.Intent;

public class MainAbility extends Ability {
    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
//        super.setMainRoute(MainAbilitySlice.class.getName());
//        super.setMainRoute(ChooseCharacterSlice.class.getName());
//        super.setMainRoute(MainSlice.class.getName());
//        super.setMainRoute(StoreSlice.class.getName());
//        super.setMainRoute(ChooseGameSlice.class.getName());
        super.setMainRoute(EntrySlice.class.getName());
    }
}
