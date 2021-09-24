package com.example.project2021.game_actions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Games;
import com.example.project2021.database.Teams;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class Game_UpdateFragment extends Fragment {
    private Spinner sport_names_spinner, game_teamHome_spinner, game_teamAway_spinner;
    private int var_sport_id;
    private String gameSport, teamHome, teamAway;
    private Button update_btn;
    private FirebaseFirestore firebaseFirestore;
    private EditText et_teamHome_score, et_teamAway_score, et_game_date;
    private List<Teams> allTeams;
    private String var_teamHome_score, var_teamAway_score, var_date;

    View view = null; // Αρχικοποίηση του view

    private Integer spinnerSportPosition = 0; // Χρησιμοποιείται για να θέσω ως ενεργή επιλογή το σωστό αθλήμα στο Spinner
    private Integer spinnerHomeTeamPosition = 0;
    private Integer spinnerAwayTeamPosition = 0;

    private GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.game_fragment_update, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        // Initialize Map fragment
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.game_map_layout);

        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // Add a marker in Sydney and move the camera
                LatLng thessaloniki = new LatLng(40.6141, 	22.9718);
                mMap.addMarker(new MarkerOptions().position(thessaloniki).title("Marker in Thessaloniki"));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(thessaloniki, 12));
            }
        });

        // Παίρνω τα στοιχεία από το Popup Window του Game_QueryFragment και τα περνάω σε τοπικές μεταβλητές
        Bundle bundle = getArguments();
        String gameID = bundle.getString("gameID");
        gameSport = bundle.getString("gameSport");
        teamHome = bundle.getString("teamHome");
        teamAway = bundle.getString("teamAway");
        Integer teamHomeScore = bundle.getInt("teamHomeScore");
        Integer teamAwayScore = bundle.getInt("teamAwayScore");
        String gameDate = bundle.getString("gameDate");
        String gameCity = bundle.getString("gameCity");
        String gameCountry = bundle.getString("gameCountry");

        init(); // Καλώ την μέθοδο init για να κάνω αρχικοποίηση

        getSportNames(); // Καλώ την μέθοδο για να γεμίσει το spinner (πρώτη φορά) με όλα τα sport_names
        sport_names_spinner.setSelection(spinnerSportPosition);

        if(sport_names_spinner.getSelectedItem() != null)
            getSportIDFromName();

        getHomeTeamPosition(var_sport_id);
        getAwayTeamsPosition(var_sport_id, teamHome);

        // Περνώ τα στοιχεία του αγώνα στα EditText του game_fragment_update.xml
        et_teamHome_score.setText(String.valueOf(teamHomeScore));
        et_teamAway_score.setText(String.valueOf(teamAwayScore));
        et_game_date.setText(String.valueOf(gameDate));

        sport_names_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getHomeTeamPosition(var_sport_id);
                getAwayTeamsPosition(var_sport_id, teamHome);

                getSportIDFromName();
                getAllTeams(var_sport_id);

                if(game_teamHome_spinner.getSelectedItem() != null) {
                    teamHome = game_teamHome_spinner.getSelectedItem().toString();
                    getAwayTeams(var_sport_id, teamHome);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        game_teamHome_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(game_teamAway_spinner.getSelectedItem() != null){
                    teamHome = game_teamHome_spinner.getSelectedItem().toString();
                    getAwayTeams(var_sport_id, teamHome);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        et_teamHome_score.addTextChangedListener(sumbitTextWatcher);
        et_teamAway_score.addTextChangedListener(sumbitTextWatcher);
        et_game_date.addTextChangedListener(sumbitTextWatcher);

        // Αν τα παρακάτω TextViews περιέχουν τιμές, ενεργοποιώ το game_update_btn
        if (et_teamHome_score.getText().length() > 0 && et_teamAway_score.getText().toString().length() > 0
                && et_game_date.getText().toString().length() > 0)
            update_btn.setEnabled(true);

        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Games game = new Games();
                try {
                    game.setTeamHome(game_teamHome_spinner.getSelectedItem().toString());
                    game.setTeamAway(game_teamAway_spinner.getSelectedItem().toString());
                    game.setTeamHomeScore(Integer.valueOf(et_teamHome_score.getText().toString().trim()));
                    game.setTeamAwayScore(Integer.valueOf(et_teamAway_score.getText().toString().trim()));
                    game.setGameCity(allTeams.get(game_teamHome_spinner.getSelectedItemPosition()).getCity());
                    game.setGameCountry(allTeams.get(game_teamHome_spinner.getSelectedItemPosition()).getCountry());
                    game.setGameDate(et_game_date.getText().toString().trim());
                    game.setGameSport(gameSport);

                    firebaseFirestore.collection("Games")
                            .document(gameID)
                            .set(game)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    resetTextViews();
                                    Toast.makeText(getActivity(), "Record added", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Oops! Something went wrong.", Toast.LENGTH_SHORT).show();
                                }
                            });
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Εκτελώ το Query που επιστρέφει τα sport_name ανάλογα το sport_type και γεμίζω το spinner
    public void getSportNames() {
        // 19 April 2021 Spinner
        String[] msportsList = MainActivity.myDatabase.myDaoTemp().getSportsPerType("Team");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        sport_names_spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < msportsList.length; i++)
            spinnerAdapter.add(msportsList[i]);

        // Παίρνω την θέση του αθλήματος που έχει ως πεδίο ο αγώνας που κάνω Update
        for (int i = 0; i < msportsList.length; i++) {
            if(msportsList[i].equals(gameSport))
                spinnerSportPosition = spinnerAdapter.getPosition(gameSport);
        } // End of for loop

        spinnerAdapter.notifyDataSetChanged();
    } // End of getSportsNamesPerGender method

    public void getAllTeams(int sport_id){
        allTeams = MainActivity.myDatabase.myDaoTemp().getAllTeamsByID(sport_id);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        game_teamHome_spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < allTeams.size(); i++)
            spinnerAdapter.add(allTeams.get(i).getName());

        spinnerAdapter.notifyDataSetChanged();
    }

    public void getAwayTeams(int sport_id, String teamHome){
        List<Teams> allTeams = MainActivity.myDatabase.myDaoTemp().getAllTeamAway(sport_id, teamHome);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        game_teamAway_spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < allTeams.size(); i++)
            spinnerAdapter.add(allTeams.get(i).getName());

        spinnerAdapter.notifyDataSetChanged();
    }

    public void getAwayTeamsPosition(int sport_id, String teamHome){
        List<Teams> allTeams = MainActivity.myDatabase.myDaoTemp().getAllTeamAway(sport_id, teamHome);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        game_teamAway_spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < allTeams.size(); i++)
            spinnerAdapter.add(allTeams.get(i).getName());

        // Παίρνω την θέση της Εκτός Έδρας Ομάδας που έχει ως πεδίο ο αγώνας που κάνω Update
        for (int i = 0; i < allTeams.size(); i++) {
            if (allTeams.get(i).getName().equals(teamAway))
                spinnerSportPosition = spinnerAdapter.getPosition(teamAway);
        }

        spinnerAdapter.notifyDataSetChanged();
    }

    public void getHomeTeamPosition(int sport_id){
        allTeams = MainActivity.myDatabase.myDaoTemp().getAllTeamsByID(sport_id);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        game_teamHome_spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < allTeams.size(); i++)
            spinnerAdapter.add(allTeams.get(i).getName());

        // Παίρνω την θέση της Εντός Έδρας Ομάδας που έχει ως πεδίο ο αγώνας που κάνω Update
        for (int i = 0; i < allTeams.size(); i++) {
            if (allTeams.get(i).getName().equals(teamHome))
                spinnerSportPosition = spinnerAdapter.getPosition(teamHome);
        }

        spinnerAdapter.notifyDataSetChanged();
    }

    public void getSportIDFromName(){
        gameSport = sport_names_spinner.getSelectedItem().toString();
        var_sport_id = MainActivity.myDatabase.myDaoTemp().team_getSportID(gameSport);
    }

    // Disable Button When EditExt is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            getValuesFromTextView();

            // Ενεργοποίηση sign up όταν το μήκος του password είναι μεγαλύτερο ή ίσο του 6
            update_btn.setEnabled(!var_teamHome_score.isEmpty() && !var_teamAway_score.isEmpty() && !var_date.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }; // End of TextWatcher

    // Παίρνω τις τιμές που έχουν τα TextViews στο activity_sign_up.xml και τα αντιστοιχώ στις μεταβλητές
    public void getValuesFromTextView() {
        var_teamHome_score = et_teamHome_score.getText().toString().trim();
        var_teamAway_score = et_teamAway_score.getText().toString().trim();
        var_date = et_game_date.getText().toString().trim();
    }

    // Καθαρίζω τα EditTexts πεδία του fragment ώστε να είναι έτοιμα για την επόμενη προσθήκη αγώνα
    public void resetTextViews() {
        et_teamHome_score.setText("");
        et_teamAway_score.setText("");
        et_game_date.setText("");

        sport_names_spinner.setSelection(0);
        game_teamHome_spinner.setSelection(0);
    }

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο game_fragment_insert.xml
    private void init(){
        sport_names_spinner = view.findViewById(R.id.team_sport_name_spinner);
        game_teamHome_spinner = view.findViewById(R.id.game_teamHome_spinner);
        game_teamAway_spinner = view.findViewById(R.id.game_teamAway_spinner);

        update_btn = view.findViewById(R.id.game_insert_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();

        et_teamHome_score = view.findViewById(R.id.et_teamHome_score);
        et_teamAway_score = view.findViewById(R.id.et_teamAway_score);
        et_game_date = view.findViewById(R.id.et_game_date);
    } // End of init
}