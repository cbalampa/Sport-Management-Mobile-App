package com.example.project2021;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.project2021.database.MyDatabase;
import com.example.project2021.welcome_page_files.OnboardingActivity;
import com.example.project2021.welcome_page_files.WelcomeLeftFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class MainActivity extends AppCompatActivity implements myBottomSheetDialog.BottomSheetListener {
    public static FragmentManager fragmentManager;
    public static MyDatabase myDatabase;

    // Μεταβλητές για το Drawer menu
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    // Μεταβλητές για το BottomNavigation View [βλ. Instagram app]
    private BottomNavigationView bottomNavigationView;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        myDatabase = Room.databaseBuilder(getApplicationContext(), MyDatabase.class, "userDB").allowMainThreadQueries().build();

        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            } else {
                fragmentManager.beginTransaction().replace(R.id.fragment_container, new HomePage_Fragment()).commit();
            } // End of if savedInstanceState
        } // End of if


        // Εμφάνιση του Drawer menu tool bar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundResource(R.color.white); // Θέτω το χρώμα του background σε λευκό
        setTitle("Dashboard");

        // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο activity_main.xml
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Εμφάνιση fragment ανάλογα με το αντικείμενο που επιλέχθηκε από το Drawer menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.people:
                        menuItem.setChecked(true); // Θέτω ως ενεργό αντικείμενο το αντικείμενο που επιλέχθηκε
                        displayMessage("Open Contacts"); // Καλώ την μέθοδο displayMessage
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        return true;
                    case R.id.find:
                        menuItem.setChecked(true); // Θέτω ως ενεργό αντικείμενο το αντικείμενο που επιλέχθηκε
                        displayMessage("Search"); // Καλώ την μέθοδο displayMessage
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        // Θέτω ως ενεργό αντικείμενο το αντίστοιχο αντικείμενο του BottomNavigationView
                        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_favorites);
                        return true;
                    case R.id.home:
                        menuItem.setChecked(true); // Θέτω ως ενεργό αντικείμενο το αντικείμενο που επιλέχθηκε
                        displayMessage("Home"); // Καλώ την μέθοδο displayMessage
                        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                new HomePage_Fragment()).commit(); // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml]
                        // με αυτό του homepage_fragment.xml
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        // Θέτω ως ενεργό αντικείμενο το αντίστοιχο αντικείμενο του BottomNavigationView
                        bottomNavigationView.setSelectedItemId(R.id.bottom_nav_home);
                        return true;
                    case R.id.logout:
                        menuItem.setChecked(true); // Θέτω ως ενεργό αντικείμενο το αντικείμενο που επιλέχθηκε
                        displayMessage("Logout"); // Καλώ την μέθοδο displayMessage
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
                        startActivity(intent);
                        finish();
                         return true;
                    case R.id.stats:
                        menuItem.setChecked(true); // Θέτω ως ενεργό αντικείμενο το αντικείμενο που επιλέχθηκε
                        displayMessage("Stats"); // Καλώ την μέθοδο displayMessage
                        fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                new StatsFragment()).commit();
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        return true;
                    case R.id.nav_share:
                        displayMessage("Share"); // Καλώ την μέθοδο displayMessage
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        return true;
                    case R.id.nav_send:
                        displayMessage("Send"); // Καλώ την μέθοδο displayMessage
                        drawerLayout.closeDrawers(); // Κλείνω το Drawer menu
                        return true;
                } // End of switch case
                return false;
            } // End of onNavigationItemSelected
        }); // End of setNavigationItemSelectedListener

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        firebaseFirestore = FirebaseFirestore.getInstance();
        String email = getIntent().getStringExtra("email");

        DocumentReference documentReference = firebaseFirestore.collection("Users").document(email);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View headerView = navigationView.getHeaderView(0);
                TextView var_username = (TextView) headerView.findViewById(R.id.drawer_menu_username);
                TextView var_email = (TextView) headerView.findViewById(R.id.drawer_menu_email);
                var_username.setText(value.getString("username"));
                var_email.setText(value.getString("email"));
            }
        });

        // // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο bottom_navigation.xml
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    } // End of onCreate method

    // Εμφάνιση fragment ανάλογα με το αντικείμενο που επιλέχθηκε από το BottomNavigation menu
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_nav_home:
                            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                    new HomePage_Fragment()).commit(); // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του homepage_fragment.xml
                            navigationView.setCheckedItem(R.id.home); // Θέτω ως ενεργό αντικείμενο το αντίστοιχο αντικείμενο του Drawer menu
                            return true;
                        case R.id.bottom_nav_favorites:
                            fragmentManager.beginTransaction().replace(R.id.fragment_container,
                                    new WelcomeLeftFragment()).commit(); // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του welcome_page_left.xml
                            navigationView.setCheckedItem(R.id.find); // Θέτω ως ενεργό αντικείμενο το αντίστοιχο αντικείμενο του Drawer menu
                            return true;
                    } // End of switch case
                    return false;
                } // End of onNavigationItemSelected
            }; // End of OnNavigationItemSelectedListener

    // Εκτελείται όταν κάνω κλικ στο back button του navigation bar
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Ενημέρωση του χρήστη με το κατάλληλο μήνυμα
    void displayMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
} // End of class