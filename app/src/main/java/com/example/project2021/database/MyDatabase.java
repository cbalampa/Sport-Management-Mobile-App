package com.example.project2021.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// Inside {} put your array class
@Database(entities = {Athletes.class, Sports.class, Teams.class}, version = 5)
public abstract class MyDatabase extends RoomDatabase {
    public abstract MyDao myDaoTemp();
}
