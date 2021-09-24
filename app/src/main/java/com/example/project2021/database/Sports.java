package com.example.project2021.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "sport")
public class Sports {
    // Κωδικός Αθλήματος
    @PrimaryKey(autoGenerate = true) @ColumnInfo (name = "sport_id") @NonNull
    private int id = 0;
    // Όνομα Αθλήματος
    @ColumnInfo (name = "sport_name") @NonNull
    private String name;
    // Είδος Αθλήματος (Ατομικό/Ομαδικό)
    @ColumnInfo (name = "sport_type") @NonNull
    private String type;
    // Φύλλο
    @ColumnInfo (name = "sport_gender") @NonNull
    private String gender;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getGender() { return gender; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setGender(String gender) { this.gender = gender; }
}
