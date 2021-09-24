package com.example.project2021.sport_actions;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Sports;

public class Sport_InsertFragment extends Fragment {
    // Πεδία-μεταβλητές του πίνακα Sports
    private EditText et_sport_name;
    private RadioGroup radioGroup_sport_type, radioGroup_sport_gender;
    private Button sport_insert_btn; // Submit button - sport_fragment_insert.xml
    View view = null; // Αρχικοποίηση του view

    public Sport_InsertFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.sport_fragment_insert, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        init(); // Καλώ την μέθοδο init για να κάνω αρχικοποίηση

        // 20 April 2021
        et_sport_name.addTextChangedListener(sumbitTextWatcher);

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί sport_insert_btn | Submit
        sport_insert_btn.setOnClickListener(new View.OnClickListener() {
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

                int var_sport_id = 0; // Αρχικές τιμή για το πεδίο ID του πίνακα Sports

                String var_sport_name = et_sport_name.getText().toString().trim(); // Παίρνω την τιμή που συμπλήρωσε ο χρήστης και αφαιρώ όποιο κενό υπάρχει στο τέλος

                Sports sport = new Sports(); // Δημιουργία αντικειμένου τύπου Sports για να περάσω τις τιμές που πήρα στον πίνακα Sports

                boolean exists = MainActivity.myDatabase.myDaoTemp().sportExists(var_sport_name, rb_sport_gender_text);

                if(!exists){
                    try {
                        // Set όλες τις μεταβλητές
                        sport.setName(var_sport_name);
                        sport.setType(rb_sport_type_text);
                        sport.setGender(rb_sport_gender_text);

                        MainActivity.myDatabase.myDaoTemp().addSport(sport); // Καλώ την μέθοδο addSport του MyDao και βάζω σαν παράμετρο το αντικείμενο sport
                        Toast.makeText(getActivity(), "Record added", Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι η καταχώρηση ολοκληρώθηκε με επιτυχία
                    } catch (Exception e) {
                        System.out.println(e.getMessage()); // Εμφάνιση σφάλματος για Debug
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι κάτι πήγε στραβά κατά την διάρκεια της καταχώρησης
                    } // End of try - catch

                    // Καθαρίζω τα EditTexts πεδία του fragment ώστε να είναι έτοιμα για την επόμενη καταχώρηση ομάδας
                    et_sport_name.setText("");
                }else{
                    Toast.makeText(getActivity(), "Record already exists", Toast.LENGTH_LONG).show();
                }

            } // End of onClick method
        }); // End of setOnClickListener method

        return view;
    } // End of onCreateView method

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο sport_fragment_insert.xml
    private void init() {
        //et_sport_id = view.findViewById(R.id.et_sport_id);
        et_sport_name = view.findViewById(R.id.et_sport_name);
        sport_insert_btn = view.findViewById(R.id.sport_submit_btn);
    }  // End of init method

    // Disable Button When EditText is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String sport_name = et_sport_name.getText().toString().trim();
            sport_insert_btn.setEnabled(!sport_name.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    }; // End of TextWatcher
} // End of class