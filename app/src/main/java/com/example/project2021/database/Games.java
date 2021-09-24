package com.example.project2021.database;

import com.google.firebase.database.Exclude;

// Πίνακας Αγώνων
public class Games {
    // Μεταβλητές
	private String gameID; // Κωδικός αγώνα
    private String gameSport; // Όνομα αθλήματος
    private String teamHome; // Όνομα ομάδας Εντός Έδρας
    private String teamAway; // Όνομα ομάδας Εκτός Έδρας
    private int teamHomeScore; // Score της Εντός Έδρας  ομάδας
	private int teamAwayScore; // Score της Εκτός Έδρας  ομάδας
	private String gameDate; // Ημερομηνία Αγώνα
    private String gameCity; // Πόλη που θα διεξαχθεί ο αγώνας
    private String gameCountry; // Χώρα που θα διεξαχθεί ο αγώνας

    // Constructor
    public Games(){};

    // Getters
    @Exclude
    public String getId() { return gameID; }
    public String getGameSport() { return gameSport; }
    public String getTeamHome() { return teamHome; }
    public String getTeamAway() { return teamAway; }
	public int getTeamHomeScore() { return teamHomeScore; }
	public int getTeamAwayScore() { return teamAwayScore; }
	public String getGameDate() { return gameDate; }
	public String getGameCity() { return gameCity; }
	public String getGameCountry() { return  gameCountry; }
	
    // Setters
    public void setId(String id) { this.gameID = id; }
    public void setGameSport(String sportName) { this.gameSport = sportName; }
    public void setTeamHome(String teamHome) { this.teamHome = teamHome; }
    public void setTeamAway(String teamAway) { this.teamAway = teamAway; }
	public void setTeamHomeScore(int teamHomeScore) { this.teamHomeScore = teamHomeScore; }
	public void setTeamAwayScore(int teamAwayScore) { this.teamAwayScore = teamAwayScore; }
	public void setGameDate(String gameDate) { this.gameDate = gameDate; }
	public void setGameCity(String gameCity) { this.gameCity = gameCity; }
	public void setGameCountry(String gameCountry) { this.gameCountry = gameCountry; }
}
