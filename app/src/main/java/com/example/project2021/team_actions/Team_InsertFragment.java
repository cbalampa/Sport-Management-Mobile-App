package com.example.project2021.team_actions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Sports;
import com.example.project2021.database.Teams;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;


public class Team_InsertFragment extends Fragment {
    // Πεδία-μεταβλητές του πίνακα Teams
    private EditText et_team_name, et_team_stadium, et_team_city, et_team_yoe;
    private CountryCodePicker cpp_team_country;

    private Button team_insert_btn; // Submit button - team_fragment_insert.xml
    View view = null; // Αρχικοποίηση του view

    // 20 April 2021 Spinner
    private Spinner team_spinner;

    public Team_InsertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.team_fragment_insert, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        init(); // Καλώ την μέθοδο init για να κάνω αρχικοποίηση

        // 19 April 2021 Spinner
        getSportNames(); // Καλώ την μέθοδο για να γεμίσει το spinner (πρώτη φορά) με όλα τα sport_names

        // 20 April 2021 Disable button
        et_team_name.addTextChangedListener(sumbitTextWatcher);
        et_team_stadium.addTextChangedListener(sumbitTextWatcher);
        et_team_city.addTextChangedListener(sumbitTextWatcher);
        et_team_yoe.addTextChangedListener(sumbitTextWatcher);

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί team_insert_btn | Submit
        team_insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String var_team_country = cpp_team_country.getSelectedCountryEnglishName(); // Αποθήκευση της χώρας που επιλέχθηκε
                int var_team_id = 0, var_team_sport_id = 0, var_team_yoe = 0; // Αρχικές τιμές για τα πεδία ID του πίνακα Teams

                // Ελέγχω αν δώθηκε ακέραιος αριθμός για εισαγωγή
                try {
                    // Παίρνω τις τιμές που έδωσε ο χρήστης και αφαιρώ όποιο κενό υπάρχει στο τέλος
                    var_team_yoe = Integer.parseInt(et_team_yoe.getText().toString().trim());
                } catch (NumberFormatException ex) {
                    System.out.println("Could not parse " + ex); // Εμφάνιση σφάλματος για Debug
                } // End of try - catch

                // Παίρνω τις τιμές και των υπόλοιπων πεδίων που συμπλήρωσε ο χρήστης, αφαιρώ όποιο κενό υπάρχει στο τέλος
                String var_team_name = et_team_name.getText().toString().trim();
                String var_team_stadium = et_team_stadium.getText().toString().trim();
                String var_team_city = et_team_city.getText().toString().trim();

                try {
                    String var_sport_name = team_spinner.getSelectedItem().toString();
                    var_team_id = MainActivity.myDatabase.myDaoTemp().team_getSportID(var_sport_name);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                Teams team = new Teams(); // Δημιουργία αντικειμένου τύπου Teams για να περάσω τις τιμές που πήρα στον πίνακα Teams

                boolean exists = MainActivity.myDatabase.myDaoTemp().teamExists(var_team_name, var_team_id);

                if (!exists) {
                    try {
                        // Set όλες τις μεταβλητές
                        team.setName(var_team_name);
                        team.setStadium(var_team_stadium);
                        team.setCity(var_team_city);
                        team.setCountry(var_team_country);
                        team.setSport_id(var_team_id);
                        team.setYoe(var_team_yoe);

                        MainActivity.myDatabase.myDaoTemp().addTeam(team); // Καλώ την μέθοδο addTeam του MyDao και βάζω σαν παράμετρο το αντικείμενο team
                        Toast.makeText(getActivity(), "Record added", Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι η καταχώρηση ολοκληρώθηκε με επιτυχία
                    } catch (Exception e) {
                        System.out.println(e.getMessage()); // Εμφάνιση σφάλματος για Debug
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι κάτι πήγε στραβά κατά την διάρκεια της καταχώρησης
                    } // Εnd of try - catch

                    // Καθαρίζω τα EditTexts πεδία του fragment ώστε να είναι έτοιμα για την επόμενη καταχώρηση ομάδας
                    et_team_name.setText("");
                    et_team_city.setText("");
                    et_team_stadium.setText("");
                    et_team_yoe.setText("");

                    team_spinner.setSelection(0); // 19 April 2021
                } else {
                    Toast.makeText(getActivity(), "Record already exists", Toast.LENGTH_LONG).show();
                }
            } // End of onClick method
        }); // End of setOnClickListener method

        return view;
    } // End of onCreateView method

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο team_fragment_insert.xml
    private void init() {
        //et_team_id = view.findViewById(R.id.et_team_id);
        et_team_name = view.findViewById(R.id.et_team_name);
        et_team_stadium = view.findViewById(R.id.et_team_stadium);
        et_team_city = view.findViewById(R.id.et_team_city);
        //et_team_sport_id = view.findViewById(R.id.et_team_sport_id);
        et_team_yoe = view.findViewById(R.id.et_team_yoe);
        team_insert_btn = view.findViewById(R.id.team_insert_btn);
        cpp_team_country = view.findViewById(R.id.ccp);

        // 19 April 2021 Spinner code
        team_spinner = view.findViewById(R.id.team_sport_name_spinner);
    } // End of init

    // Εκτελώ το Query που επιστρέφει τα sport_name ανάλογα το sport_gender και γεμίζω το spinner
    public void getSportNames() {
        // 19 April 2021 Spinner
        String[] msportsList = MainActivity.myDatabase.myDaoTemp().getSportsPerType("Team");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        team_spinner.setAdapter(spinnerAdapter);

        for (int i = 0; i < msportsList.length; i++) {
            spinnerAdapter.add(msportsList[i]);
        }

        spinnerAdapter.notifyDataSetChanged();
    } // End of getSportsNamesPerGender method

    // Disable Button When EditExt is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String team_name = et_team_name.getText().toString().trim();
            String stadium_name = et_team_stadium.getText().toString().trim();
            String city = et_team_city.getText().toString().trim();
            String team_yoe = et_team_yoe.getText().toString().trim();

            if (team_spinner.getSelectedItem() != null)
                team_insert_btn.setEnabled(!team_name.isEmpty() && !stadium_name.isEmpty() && !city.isEmpty() && !team_yoe.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }; // End of TextWatcher
} // End of class