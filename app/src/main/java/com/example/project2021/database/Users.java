package com.example.project2021.database;


public class Users {
    // Μεταβλητές
    private String username; // Όνομα
    private String email; // Email
    private String password; // Κωδικός

    // Constructor
    public Users(){};

    // Getters
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }

    // Setters
    public void setUsername(String username){ this.username = username; }
    public void setEmail(String email){ this.email = email; }
    public void setPassword(String password){ this.password = password; }
}
