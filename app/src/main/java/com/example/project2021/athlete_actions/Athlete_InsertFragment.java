package com.example.project2021.athlete_actions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Athletes;
import com.example.project2021.database.Sports;
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;
import java.util.List;

public class Athlete_InsertFragment extends Fragment {
    // Πεδία-μεταβλητές του πίνακα Athletes
    private EditText et_athlete_id, et_athlete_fname, et_athlete_lname, et_sport_id_test, et_athlete_yob;
    private RadioGroup radioGroup_athlete_gender;
    private CountryCodePicker ccp_athlete_country;

    private Button athlete_insert_btn; // Submit button - athlete_fragment_insert.xml
    View view = null; // Αρχικοποίηση του view

    // 19 April 2021 Spinner
    List<Sports> sportsList;
    private Spinner athlete_spinner;
    String sport_gender = "Men's"; // Αρχική τιμή για να γεμίσει το spinner με τιμές

    public Athlete_InsertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.athlete_fragment_insert, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        init(); // Καλώ την μέθοδο init για να κάνω αρχικοποίηση

        // 19 April 2021 Spinner
        getSportNamesPerGender(sport_gender); // Καλώ την μέθοδο για να γεμίσει το spinner (πρώτη φορά) ανάλογα με το ενεργό RadioButton

        // Ελέγχω αν έχουν συμπληρωθεί όλα τα πεδία ώστε να ενεργοποιήσω το Submit button
        et_athlete_fname.addTextChangedListener(sumbitTextWatcher);
        et_athlete_lname.addTextChangedListener(sumbitTextWatcher);
        et_athlete_yob.addTextChangedListener(sumbitTextWatcher);

        // Κάθε φορά που θα αλλάζει το ενεργό RadioButton θα γεμίζω το spinner με τα ανάλογα Sports με βάση το gender
        RadioButton checkedRadioButton = radioGroup_athlete_gender.findViewById(radioGroup_athlete_gender.getCheckedRadioButtonId());
        radioGroup_athlete_gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId); // Παίρνω το ενεργό RadioButton
                String gender = (String) radioButton.getText(); // Παίρνω την τιμή String του RadioButton

                // Ανάλογα με το gender, περνώ στην μέθοδο getSportsNamesPerGender τις τιμές που δέχεται ο πίνακας Sports στην στήλη sport_gender
                if (gender.equals("Male"))
                    getSportNamesPerGender("Men's");
                else if (gender.equals("Female"))
                    getSportNamesPerGender("Women's");

            } // End of onCheckedChanged
        }); // End of setOnCheckedChangeListener

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί team_insert_btn | Submit
        athlete_insert_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Διαχείριση του RadioGroup και RadioButtons
                int radioButtonID_gender = radioGroup_athlete_gender.getCheckedRadioButtonId(); // Αποθήκευση της ακέραιας τιμής του φύλου που επιλέχθηκε
                RadioButton radioButton_gender = (RadioButton) radioGroup_athlete_gender.findViewById(radioButtonID_gender); // Αντιστοίχιση με το ενεργό RadioButton με βάση το int ID
                String var_athlete_gender_text = (String) radioButton_gender.getText(); // Το String [Male ή Female] που έχουν τα radioButtons

                String var_athlete_country = ccp_athlete_country.getSelectedCountryEnglishName(); // Αποθήκευση της χώρας που επιλέχθηκε
                int var_athlete_id = 0, var_sport_id_test = 0, var_athlete_age = 0; // Αρχικές τιμές για τα πεδία ID του πίνακα Athletes

                // Ελέγχω αν δώθηκε ακέραιος αριθμός για εισαγωγή
                try {
                    // Παίρνω τις τιμές που έδωσε ο χρήστης και αφαιρώ όποιο κενό υπάρχει στο τέλος
                    var_athlete_age = Integer.parseInt(et_athlete_yob.getText().toString().trim());
                } catch (NumberFormatException ex) {
                    System.out.println("Could not parse " + ex); // Εμφάνιση σφάλματος για Debug
                } // End of try - catch

                // Παίρνω τις τιμές και των υπόλοιπων πεδίων που συμπλήρωσε ο χρήστης, αφαιρώ όποιο κενό υπάρχει στο τέλος
                String var_athlete_fname = et_athlete_fname.getText().toString().trim();
                String var_athlete_lname = et_athlete_lname.getText().toString().trim();

                int var_sport_id = 0;
                try {
                    String var_sport_name = athlete_spinner.getSelectedItem().toString();
                    var_sport_id = MainActivity.myDatabase.myDaoTemp().ath_getSportID(var_sport_name, sport_gender);
                } catch (Exception e) {
                    System.out.println("Athlete_InsertFragment: " + e.getMessage());
                    Log.e("Athlete_InsertFragment", "gerSportID error");
                }

                Athletes athlete = new Athletes(); // Δημιουργία αντικειμένου τύπου Athletes για να περάσω τις τιμές που πήρα στον πίνακα Athletes

                boolean exists = MainActivity.myDatabase.myDaoTemp().athleteExists(var_athlete_fname, var_athlete_lname);

                if (!exists) {
                    try {
                        // Set όλες τις μεταβλητές
                        athlete.setFname(var_athlete_fname);
                        athlete.setLname(var_athlete_lname);
                        athlete.setGender(var_athlete_gender_text);
                        athlete.setCountry(var_athlete_country);
                        athlete.setSport_id(var_sport_id); // 19 April 2021
                        athlete.setAge(var_athlete_age);

                        MainActivity.myDatabase.myDaoTemp().addAthlete(athlete); // Καλώ την μέθοδο addAthlete του MyDao και βάζω σαν παράμετρο το αντικείμενο athlete
                        Toast.makeText(getActivity(), "Record added", Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι η καταχώρηση ολοκληρώθηκε με επιτυχία
                    } catch (Exception e) {
                        System.out.println(e.getMessage()); // Εμφάνιση σφάλματος για Debug
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι κάτι πήγε στραβά κατά την διάρκεια της καταχώρησης
                    } // End of try - catch

                    // Καθαρίζω τα EditTexts πεδία του fragment ώστε να είναι έτοιμα για την επόμενη καταχώρηση αθλητή
                    et_athlete_fname.setText("");
                    et_athlete_lname.setText("");
                    et_athlete_yob.setText("");

                    athlete_spinner.setSelection(0); // 19 April 2021
                } else {
                    Toast.makeText(getActivity(), "Record already exists", Toast.LENGTH_LONG).show();
                }
            } // End of onClick method
        }); // End of setOnClickListener method

        return view;
    } // End of onCreateView method

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο athlete_fragment_insert.xml
    private void init() {
        et_athlete_fname = view.findViewById(R.id.et_athlete_fname);
        et_athlete_lname = view.findViewById(R.id.et_athlete_lname);
        et_athlete_yob = view.findViewById(R.id.et_athlete_yob);
        athlete_insert_btn = view.findViewById(R.id.athlete_insert_btn);
        radioGroup_athlete_gender = (RadioGroup) view.findViewById(R.id.rg_athlete_gender);
        ccp_athlete_country = view.findViewById(R.id.ccp);


        // 19 April 2021 Spinner code
        athlete_spinner = view.findViewById(R.id.athlete_sport_name_spinner);
    }  // End of init method

    // Εκτελώ το Query που επιστρέφει τα sport_name ανάλογα το sport_gender και γεμίζω το spinner
    public void getSportNamesPerGender(String gender) {
        // 19 April 2021 Spinner
        String[] msportsList = MainActivity.myDatabase.myDaoTemp().getSportsPerGender(gender);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        athlete_spinner.setAdapter(spinnerAdapter);
        sport_gender = gender;

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
            String athlete_fname = et_athlete_fname.getText().toString().trim();
            String athlete_lname = et_athlete_lname.getText().toString().trim();
            String athlete_sport_yob = et_athlete_yob.getText().toString().trim();

            if (athlete_spinner.getSelectedItem() != null)
                athlete_insert_btn.setEnabled(!athlete_fname.isEmpty() && !athlete_lname.isEmpty() && !athlete_sport_yob.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }; // End of TextWatcher
} // End of class