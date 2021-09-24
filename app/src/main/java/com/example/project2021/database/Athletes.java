package com.example.project2021.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "athlete",
        foreignKeys = {
            @ForeignKey(entity = Sports.class,
                parentColumns = "sport_id",
                childColumns = "athlete_sport_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)
        }
)
public class Athletes {
    // Κωδικός Αθλητή
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "athlete_id")
    @NonNull
    private int id = 0;
    // Όνομα
    @ColumnInfo(name = "athlete_fname")
    @NonNull
    private String fname;
    // Επώνυμο
    @ColumnInfo(name = "athlete_lname")
    @NonNull
    private String lname;
    // Φύλλο
    @ColumnInfo(name = "athlete_gender")
    @NonNull
    private String gender;
    // Χώρα
    @ColumnInfo(name = "athlete_country")
    @NonNull
    private String country;
    // Κωδικός Αθλήματος
    @ColumnInfo(name = "athlete_sport_id")
    @NonNull
    private int sport_id = 0;
    // Ηλικία Αθλητή
    @ColumnInfo(name = "athlete_age")
    @NonNull
    private int age;

    // Getters
    public int getId() {
        return id;
    }
    public String getFname() {
        return fname;
    }
    public String getLname() {
        return lname;
    }
    public String getGender() {
        return gender;
    }
    public String getCountry() {
        return country;
    }
    public int getSport_id() {
        return sport_id;
    }
    public int getAge() { return age; }

    // Setters
    public void setId(int id) {
        this.id = id;
    }
    public void setFname(String fname) {
        this.fname = fname;
    }
    public void setLname(String lname) {
        this.lname = lname;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setSport_id(int sport_id) {
        this.sport_id = sport_id;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
