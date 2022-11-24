package com.example.mazegame.model;

import ohos.data.orm.OrmDatabase;
import ohos.data.orm.annotation.Database;

@Database(entities = {Character.class}, version = 1)
public abstract class CharacterDbStore extends OrmDatabase {
}
