package com.example.project2021.sport_actions;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Sports;
import com.hbb20.CountryCodePicker;

public class Sport_UpdateFragment extends Fragment {
    private EditText et_sport_name; // EditText στο sport_fragment_update.xml
    private RadioGroup radioGroup_sport_type, radioGroup_sport_gender; // RadioGroups στο sport_fragment_update.xml
    private RadioButton radioButton_sport_solo, radioButton_sport_team, radioButton_sport_men, radioButton_sport_women; // RadioButtons στο sport_fragment_update.xml
    private Button sport_update_btn; // Submit button - sport_fragment_update.xml
    View view = null; // Αρχικοποίηση του view

    // 19 April 2021 Spinner
    Integer var_id = 0; // Αρχική τιμή για το sport_id του πίνακα Sports
    public Sport_UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.sport_fragment_update, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        // Παίρνω τα στοιχεία από το Popup Window του Sport_QueryFragment και τα περνάω σε τοπικές μεταβλητές
        Bundle bundle = getArguments();
        var_id = bundle.getInt("ID");
        String var_sport_name = bundle.getString("SportName");
        String var_type = bundle.getString("SportType");
        String var_gender = bundle.getString("SportGender");

        init(); // Καλώ την μέθοδο init για να κάνω αρχικοποίηση

        //String var_sport_name = MainActivity.myDatabase.myDaoTemp().getSportName(var_id); // Παίρνω το όνομα του αθλήματος με το sport_id (κύριο κλειδί)

        // Περνώ τα στοιχεία του αθλήματος στο EditText του sport_fragment_update.xml
        et_sport_name.setText(var_sport_name);

        // Θέτω ενεργό RadioButton ανάλογα με την τιμή που έχει η μεταβλητή var_type
        if (var_type.equals("Solo")) {
            radioButton_sport_solo.setChecked(true);
        } else if (var_type.equals("Team")) {
            radioButton_sport_team.setChecked(true);
        } // End of if

        // Θέτω ενεργό RadioButton ανάλογα με την τιμή που έχει η μεταβλητή var_gender
        if (var_gender.equals("Men's")) {
            radioButton_sport_men.setChecked(true);
        } else if (var_gender.equals("Women's")) {
            radioButton_sport_women.setChecked(true);
        } // End of if

        // Αν το παρακάτω TextView περιέχει τιμή, ενεργοποιώ το sport_update_btn
        if (et_sport_name.getText().length() > 0)
            sport_update_btn.setEnabled(true);

        // Σε αντίθτετη περίπτωση καλώ την μέθοδο addTextChangedListener | πχ Ο χρήστης να αφήσει όλα τα πεδία κενά
        et_sport_name.addTextChangedListener(sumbitTextWatcher);

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί sport_insert_btn | Submit
        sport_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Διαχείριση του RadioGroup και RadioButtons
                radioGroup_sport_type = (RadioGroup) view.findViewById(R.id.rg_sport_type); // Αποθήκευση της ακέραιας τιμής του τύπου που επιλέχθηκε
                int radioButtonID_type = radioGroup_sport_type.getCheckedRadioButtonId(); // Αντιστοίχιση με το ενεργό RadioButton με βάση το int ID
                RadioButton radioButton_type = (RadioButton) radioGroup_sport_type.findViewById(radioButtonID_type);
                String rb_sport_type_text = (String) radioButton_type.getText(); // Το String [Solo ή Team] που έχουν τα radioButtons

                radioGroup_sport_gender = (RadioGroup) view.findViewById(R.id.rg_sport_gender); // Αποθήκευση της ακέραιας τιμής του τύπου που επιλέχθηκε
                int radioButtonID_gender = radioGroup_sport_gender.getCheckedRadioButtonId(); // Αντιστοίχιση με το ενεργό RadioButton με βάση το int ID
                RadioButton radioButton_gender = (RadioButton) radioGroup_sport_gender.findViewById(radioButtonID_gender);
                String rb_sport_gender_text = (String) radioButton_gender.getText(); // Το String [Men ή Women] που έχουν τα radioButtons

                int var_sport_id = var_id; // Αρχικές τιμή για το πεδίο ID του πίνακα Sports
                String var_sport_name = et_sport_name.getText().toString().trim(); // Παίρνω την τιμή που συμπλήρωσε ο χρήστης και αφαιρώ όποιο κενό υπάρχει στο τέλος

                Sports sport = new Sports(); // Δημιουργία αντικειμένου τύπου Sports για να περάσω τις τιμές που πήρα στον πίνακα Sports

                try {
                    // Set όλες τις μεταβλητές
                    sport.setId(var_sport_id);
                    sport.setName(var_sport_name);
                    sport.setType(rb_sport_type_text);
                    sport.setGender(rb_sport_gender_text);

                    MainActivity.myDatabase.myDaoTemp().updateSport(sport); // Καλώ την μέθοδο updateSport του MyDao και βάζω σαν παράμετρο το αντικείμενο sport
                    Toast.makeText(getActivity(), "Record updated", Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι η ενημέρωση ολοκληρώθηκε με επιτυχία
                } catch (Exception e) {
                    System.out.println(e.getMessage()); // Εμφάνιση σφάλματος για Debug
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι κάτι πήγε στραβά κατά την διάρκεια της ενημέρωσης
                } // End of try - catch

                // Καθαρίζω τα EditTexts πεδία του fragment ώστε να είναι έτοιμα για την επόμενη καταχώρηση ομάδας
                et_sport_name.setText("");
            } // End of onClick method
        }); // End of setOnClickListener method

        return view;
    }

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο sport_fragment_insert.xml
    private void init() {
        et_sport_name = view.findViewById(R.id.et_sport_name);
        sport_update_btn = view.findViewById(R.id.sport_update_btn);
        radioButton_sport_men = view.findViewById(R.id.rb_sport_men);
        radioButton_sport_women = view.findViewById(R.id.rb_sport_women);
        radioButton_sport_solo = view.findViewById(R.id.rb_sport_solo);
        radioButton_sport_team = view.findViewById(R.id.rb_sport_team);
    }  // End of init method

    // Disable Button When EditText is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String sport_name = et_sport_name.getText().toString().trim();
            sport_update_btn.setEnabled(!sport_name.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }; // End of TextWatcher
}