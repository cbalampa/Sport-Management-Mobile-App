package com.example.project2021.database;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity (tableName = "team",
        foreignKeys = {
                @ForeignKey(entity = Sports.class,
                        parentColumns = "sport_id",
                        childColumns = "team_sport_id",
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )}
)
public class Teams {
    // Κωδικός Ομάδας
    @PrimaryKey(autoGenerate = true) @ColumnInfo (name = "team_id") @NonNull
    private int id;
    // Όνομα
    @ColumnInfo (name = "team_name") @NonNull
    private String name;
    // Όνομα γηπέδου
    @ColumnInfo (name = "team_stadium") @NonNull
    private String stadium;
    // Πόλη (έδρα)
    @ColumnInfo (name = "team_city") @NonNull
    private String city;
    // Χώρα
    @ColumnInfo (name = "team_country") @NonNull
    private String country;
    // Κωδικός Αθλήματος
    @ColumnInfo (name = "team_sport_id") @NonNull
    private int sport_id = 0;
    // Έτος ίδρυσης
    @ColumnInfo (name = "team_yoe") @NonNull
    private int yoe; // year of establishment

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getStadium() { return stadium; }
    public String getCity() { return city; }
    public String getCountry() { return country; }
    public int getSport_id() { return sport_id; }
    public int getYoe() { return yoe; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setStadium(String stadium) { this.stadium = stadium; }
    public void setCity(String city) { this.city = city; }
    public void setCountry(String country) { this.country = country; }
    public void setSport_id(int sport_id) { this.sport_id = sport_id; }
    public void setYoe(int yoe) { this.yoe = yoe; }
}

