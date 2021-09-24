package com.example.project2021.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MyDao {
    /* Μέθοδοι για την αλληλεπίδραση με την βάση δεδομένων
    Ενέργειες SQL προς την βάση δεδομένων από το προγραμμά μου */

    // Sports
    @Insert
    public void addSport(Sports sports);
    @Query("SELECT DISTINCT * FROM Sport ORDER BY sport_name, sport_gender")
    public List<Sports> getSports();
    @Delete
    public void deleteSport(Sports sports);
    @Update
    public void updateSport(Sports sports);

    // Athletes
    @Insert
    public void addAthlete(Athletes athletes);
    @Query("SELECT * FROM Athlete ORDER BY athlete_sport_id, athlete_fname, athlete_lname")
    public List<Athletes> getAthletes();
    @Delete
    public void deleteAthlete(Athletes athletes);
    @Update
    public void updateAthlete(Athletes athletes);

    // Teams
    @Insert
    public void addTeam(Teams teams);
    @Query("SELECT * FROM Team ORDER BY team_name")
    public List<Teams> getTeams();
    @Delete
    public void deleteTeam(Teams teams);
    @Update
    public void updateTeam(Teams teams);

    @Query("SELECT * FROM Athlete WHERE EXISTS (SELECT athlete_fname, athlete_lname FROM Athlete " +
            "WHERE athlete_fname LIKE :fname AND athlete_lname LIKE :lname)")
    public boolean athleteExists(String fname, String lname);

    @Query("SELECT * FROM Sport WHERE EXISTS (SELECT sport_name FROM Sport WHERE sport_name LIKE :sport_name AND sport_gender LIKE :sport_gender)")
    public boolean sportExists(String sport_name, String sport_gender);

    @Query("SELECT * FROM Team WHERE EXISTS (SELECT team_name FROM Team WHERE team_name LIKE :team_name AND team_sport_id LIKE :team_sport_id)")
    public boolean teamExists(String team_name, int team_sport_id);

    // Παίρνω το όνομα του αθλήματος με βάση το id του
    @Query("SELECT DISTINCT sport_name FROM Sport WHERE sport_id = :id")
    public String getSportName(int id);

    // Athlete_InsertFragment.java γεμίζω το spinner με τα διαθέσιμα sport_name με βάση το gender
    @Query("SELECT DISTINCT sport_name FROM Sport WHERE sport_gender LIKE :gender")
    public String[] getSportsPerGender(String gender);
    // Athlete_InsertFragment.java παίρνω το sport_id του sport_name με βάση το gender για να κάνω καταχώρηση Αθλητή
    // χρειάζομαι sport_id (ξένο κλειδί) του πίνακα Sports
    @Query("SELECT DISTINCT sport_id FROM Sport WHERE sport_name LIKE :name AND sport_gender LIKE :gender")
    public int ath_getSportID(String name, String gender);

    @Query("SELECT COUNT(athlete_id) FROM Athlete WHERE athlete_gender LIKE :gender")
    public int ath_count(String gender);

    // Team_InsertFragment.java γεμίζω το spinner με όλα τα διαθέσιμα sport_name με βάση το type
    @Query("SELECT DISTINCT sport_name FROM Sport WHERE sport_type LIKE :type")
    public String[] getSportsPerType(String type);
    @Query("SELECT DISTINCT sport_id FROM Sport WHERE sport_name LIKE :name")
    public int team_getSportID(String name);

    // Game_InsertFragment.java γεμίζω τα spinners των ομάδων ανάλογα με το άθλημα
    @Query("SELECT DISTINCT * FROM Team WHERE team_sport_id = :sport_id")
    public List<Teams> getAllTeamsByID(int sport_id);

    @Query("SELECT * FROM Team AS T1 WHERE T1.team_sport_id = :sport_id " +
            "EXCEPT SELECT * FROM Team AS T2 " +
            "WHERE T2.team_sport_id = :sport_id AND T2.team_name LIKE :teamHome")
    public List<Teams> getAllTeamAway(int sport_id, String teamHome);

    // Stats
    @Query("SELECT COUNT(athlete_id) FROM athlete")
    public int number_Of_Athletes();
    @Query("SELECT AVG(athlete_age) FROM athlete")
    public int avg_yob_Of_Athletes();

    @Query("SELECT COUNT(*) FROM athlete WHERE athlete_sport_id LIKE :sport_id")
    public int players(int sport_id);
}
