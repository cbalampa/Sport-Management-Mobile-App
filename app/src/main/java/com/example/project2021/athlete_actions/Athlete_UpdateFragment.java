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
import com.hbb20.CountryCodePicker;

import java.util.ArrayList;

public class Athlete_UpdateFragment extends Fragment {
    private EditText et_fname, et_lname, et_age; // EditTexts στο athlete_fragment_update.xml
    private RadioGroup radioGroup_athlete_gender; // RadioGroup στο athlete_fragment_update.xml
    private RadioButton radioButton_athlete_male, radioButton_athlete_female; // RadioButtons στο athlete_fragment_update.xml
    private CountryCodePicker ccp_athlete_country; // CCP στο athlete_fragment_update.xml
    private Button athlete_update_btn; // Submit button - athlete_fragment_update.xml
    View view = null; // Αρχικοποίηση του view
    private String sport_name; // Αποθηκεύω το όνομα του αθλήματος με βάση το sport_id (ξένο κλειδί)
    private Integer spinnerPosition = 0; // Χρησιμοποιείται για να θέσω ως ενεργή επιλογή το σωστό αθλήμα στο Spinner

    // 19 April 2021 Spinner
    private Spinner athlete_spinner; // Spinner (DropDown menu) στο athlete_fragment_update.xml
    private String sport_gender = "Men's"; // Αρχική τιμή για να γεμίσει το spinner με τιμές
    private Integer var_id = 0; // Αρχική τιμή για το athlete_id του πίνακα Athletes

    public Athlete_UpdateFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.athlete_fragment_update, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        // Παίρνω τα στοιχεία από το Popup Window του Athlete_QueryFragment και τα περνάω σε τοπικές μεταβλητές
        Bundle bundle = getArguments();
        var_id = bundle.getInt("ID");
        String var_fname = bundle.getString("FirstName");
        String var_lname = bundle.getString("LastName");
        String var_gender = bundle.getString("Gender");
        Integer var_ath_sport_id = bundle.getInt("SportID");
        Integer var_age = bundle.getInt("Age");

        init(); // Καλώ την μέθοδο init για να κάνω αρχικοποίηση

        sport_name = MainActivity.myDatabase.myDaoTemp().getSportName(var_ath_sport_id); // Παίρνω το όνομα του αθλήματος με το sport_id (ξένο κλειδί)

        // Περνώ τα στοιχεία του αθλητή στα EditText του athlete_fragment_update.xml
        et_fname.setText(var_fname);
        et_lname.setText(var_lname);
        et_age.setText(String.valueOf(var_age));

        // Θέτω ενεργό RadioButton ανάλογα με την τιμή που έχει η μεταβλητή var_gender
        if (var_gender.equals("Male")) {
            radioButton_athlete_male.setChecked(true);
            sport_gender = "Men's";
        } else if (var_gender.equals("Female")) {
            radioButton_athlete_female.setChecked(true);
            sport_gender = "Women's";
        } // End of if

        // 19 April 2021 Spinner
        getSportNamesPerGender(sport_gender); // Καλώ την μέθοδο για να γεμίσει το spinner (πρώτη φορά) ανάλογα με το ενεργό RadioButton
        athlete_spinner.setSelection(spinnerPosition);

        // Αν τα παρακάτω TextViews περιέχουν τιμές, ενεργοποιώ το athlete_update_btn
        if (et_fname.getText().length() > 0 && et_lname.getText().toString().length() > 0 && et_age.getText().toString().length() > 0)
            athlete_update_btn.setEnabled(true);

        // Σε αντίθτετη περίπτωση καλώ την μέθοδο addTextChangedListener | πχ Ο χρήστης να αφήσει όλα τα πεδία κενά
        et_fname.addTextChangedListener(sumbitTextWatcher);
        et_lname.addTextChangedListener(sumbitTextWatcher);
        et_age.addTextChangedListener(sumbitTextWatcher);

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

        // Εκτέλεση που παρακάτω κώδικα στην περίπτωση που γίνει κλικ στο κουμπί athlete_update_btn | Submit
        athlete_update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Διαχείριση του RadioGroup και RadioButtons
                int radioButtonID_gender = radioGroup_athlete_gender.getCheckedRadioButtonId(); // Αποθήκευση της ακέραιας τιμής του φύλου που επιλέχθηκε
                RadioButton radioButton_gender = (RadioButton) radioGroup_athlete_gender.findViewById(radioButtonID_gender); // Αντιστοίχιση με το ενεργό RadioButton με βάση το int ID
                String var_athlete_gender_text = (String) radioButton_gender.getText(); // Το String [Male ή Female] που έχουν τα radioButtons

                String var_athlete_country = ccp_athlete_country.getSelectedCountryEnglishName(); // Αποθήκευση της χώρας που επιλέχθηκε
                int var_athlete_id = var_id, var_athlete_age = 0; // Αρχικές τιμές για τα πεδία ID του πίνακα Athletes

                // Ελέγχω αν δώθηκε ακέραιος αριθμός για εισαγωγή
                try {
                    // Παίρνω τις τιμές που έδωσε ο χρήστης και αφαιρώ όποιο κενό υπάρχει στο τέλος
                    var_athlete_age = Integer.parseInt(et_age.getText().toString().trim());
                } catch (NumberFormatException ex) {
                    System.out.println("Could not parse " + ex); // Εμφάνιση σφάλματος για Debug
                } // End of try - catch

                // Παίρνω τις τιμές και των υπόλοιπων πεδίων που συμπλήρωσε ο χρήστης, αφαιρώ όποιο κενό υπάρχει στο τέλος
                String var_athlete_fname = et_fname.getText().toString().trim();
                String var_athlete_lname = et_lname.getText().toString().trim();

                int var_sport_id = 0;
                try {
                    String var_sport_name = athlete_spinner.getSelectedItem().toString(); // Όνομα ενεργού αθλήματος στο DropDown menu [Spinner]
                    var_sport_id = MainActivity.myDatabase.myDaoTemp().ath_getSportID(var_sport_name, sport_gender); // Παίρνω το sport_id του αθλήματος από τον πίνακα Sports
                } catch (Exception e) {
                    System.out.println("Athlete_UpdateFragment: " + e.getMessage());
                    Log.e("Athlete_UpdateFragment", "getSportID Error");
                } // End of try - catch

                Athletes athlete = new Athletes(); // Δημιουργία αντικειμένου τύπου Athletes για να περάσω τις τιμές που πήρα στον πίνακα Athletes

                try {
                    // Set όλες τις μεταβλητές
                    athlete.setId(var_athlete_id);
                    athlete.setFname(var_athlete_fname);
                    athlete.setLname(var_athlete_lname);
                    athlete.setGender(var_athlete_gender_text);
                    athlete.setCountry(var_athlete_country);
                    athlete.setSport_id(var_sport_id);
                    athlete.setAge(var_athlete_age);

                    MainActivity.myDatabase.myDaoTemp().updateAthlete(athlete); // Καλώ την μέθοδο updateAthlete του MyDao και βάζω σαν παράμετρο το αντικείμενο athlete
                    Toast.makeText(getActivity(), "Record updated", Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι η ενημέρωση ολοκληρώθηκε με επιτυχία
                } catch (Exception e) {
                    System.out.println(e.getMessage()); // Εμφάνιση σφάλματος για Debug
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show(); // Ενημέρωση του χρήστη ότι κάτι πήγε στραβά κατά την διάρκεια της ενημέρωσης
                } // End of try - catch

                // Καθαρίζω τα EditTexts πεδία του fragment ώστε να είναι έτοιμα για την επόμενη καταχώρηση αθλητή
                et_fname.setText("");
                et_lname.setText("");
                et_age.setText("");

                athlete_spinner.setSelection(0); // Αρχική θέση για το Spinner
            } // End of onClick method
        }); // End of setOnClickListener method

        return view;
    } // End of onCreateView

    // Εκτελώ το Query που επιστρέφει τα sport_name ανάλογα το sport_gender και γεμίζω το spinner
    public void getSportNamesPerGender(String gender) {
        // 19 April 2021 Spinner
        String[] msportsList = MainActivity.myDatabase.myDaoTemp().getSportsPerGender(gender);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity(), R.layout.dropdown_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(R.layout.dropdown_item);
        athlete_spinner.setAdapter(spinnerAdapter);
        sport_gender = gender;

        // Γεμίζω το spinner με όλα τα διαθέσιμα αθλήματα ανά φύλο
        for (int i = 0; i < msportsList.length; i++) {
            spinnerAdapter.add(msportsList[i]);
        } // End of for loop

        // Παίρνω την θέση του αθλήματος που έχει ως πεδίο ο αθλητής που κάνω Update
        for (int i = 0; i < msportsList.length; i++) {
            if (msportsList[i].equals(sport_name))
                spinnerPosition = spinnerAdapter.getPosition(sport_name);
        } // End of for loop

        spinnerAdapter.notifyDataSetChanged();
    } // End of getSportsNamesPerGender method

    // Disable Button When EditExt is Empty
    private TextWatcher sumbitTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String athlete_fname = et_fname.getText().toString().trim();
            String athlete_lname = et_lname.getText().toString().trim();
            String athlete_sport_yob = et_age.getText().toString().trim();

            if (athlete_spinner.getSelectedItem() != null)
                athlete_update_btn.setEnabled(!athlete_fname.isEmpty() && !athlete_lname.isEmpty() && !athlete_sport_yob.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }; // End of TextWatcher

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο athlete_fragment_update.xml
    private void init() {
        et_fname = view.findViewById(R.id.et_athlete_fname);
        et_lname = view.findViewById(R.id.et_athlete_lname);
        et_age = view.findViewById(R.id.et_athlete_yob);
        athlete_update_btn = view.findViewById(R.id.athlete_update_btn);
        radioGroup_athlete_gender = (RadioGroup) view.findViewById(R.id.rg_athlete_gender);
        ccp_athlete_country = view.findViewById(R.id.ccp);

        radioButton_athlete_male = view.findViewById(R.id.et_athlete_male_rb);
        radioButton_athlete_female = view.findViewById(R.id.et_athlete_female_rb);

        // 19 April 2021 Spinner code
        athlete_spinner = view.findViewById(R.id.athlete_sport_name_spinner);
    }  // End of init method
}