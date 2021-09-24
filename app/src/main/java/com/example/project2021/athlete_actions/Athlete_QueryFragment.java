package com.example.project2021.athlete_actions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Athletes;

import java.util.ArrayList;

public class Athlete_QueryFragment extends Fragment {
    // Μεταβλητές
    private TextView empty_list_textView; // TextView μέσα στο athlete_fragment_query.xml | Visible μόνο όταν δεν υπάρχουν δεδομένα στον πίνακα Athletes
    private Toolbar toolbar;
    private RecyclerView recyclerView; // Περιέχει το RecyclerView athlete_fragment_query.xml
    private ArrayList<Athletes> list = new ArrayList<>(); // Λίστα με τους αθλητές
    private Athlete_Adapter adapter;
    private TextView text_toolbar; // TextView μέσα στο athlete_fragment_query.xml | Εμφανίζει τον αριθμό των επιλεγμένων αθλητών
    private ImageButton backButton_toolbar; // BackButton του toolbar μέσα στο athlete_fragment_query.xml | Visible μόνο όταν το isActionMode είναι true
    public boolean isActionMode = false;
    private ArrayList<Athletes> selectionList = new ArrayList<>(); // Λίστα με τους επιλεγμένους αθλητές
    private int counter = 0; // Αρχική τιμή για τους επιλεγμένους αθλήτες
    public int position = -1;

    private TextView textView_counter;
    private int male_counter = 0, female_counter = 0;

    public Athlete_QueryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.athlete_fragment_query, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        // Εμφάνιση Toolbar
        toolbar = view.findViewById(R.id.athlete_query_toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Details"); // Θέτω ως τίτλο του toolbar το Details

        text_toolbar = view.findViewById(R.id.ath_text_toolbar);
        backButton_toolbar = view.findViewById(R.id.ath_back_btn);

        empty_list_textView = view.findViewById(R.id.tv_athlete_empty_list);
        textView_counter = view.findViewById(R.id.tv_athlete_counter);

        recyclerView = view.findViewById(R.id.athlete_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Γεμίζω την μεταβλητή list με όλους τους αθλητές της τοπικής βάσης
        list = (ArrayList<Athletes>) MainActivity.myDatabase.myDaoTemp().getAthletes();

        // Αρχικοποίηση του Athlete_Adapter
        adapter = new Athlete_Adapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        // Αν η λίστα είναι κενή εμφάνιση κατάλληλου μηνύματος
        if (list.size() == 0)
            empty_list_textView.setVisibility(View.VISIBLE);

        // Αν κάνω κλικ στο backButton του toolbar κάνω reset
        backButton_toolbar.setOnClickListener(v -> {
            clearActionMode();
        });

        return view;
    } // End of onCreateView method

    // Γραμμές 83-96 χρειάζονται για την λειτουργία του deleteButton στο contexual_menu.xml
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete: // Αν κάνω κλικ στο deleteButton καλώ την μέθοδο deleteButton και διαγράφω αθλητές
                deleteButton();
                break;
            case R.id.menu_counter: // Αν κάνω κλικ στο show stats καλώ την μέθοδο getCounter και παίρνω τον αριθμό των αθλητών ανά φύλο
                getCounter();
                textView_counter.setVisibility(View.VISIBLE);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    // Reset όλων των στοιχείων
    private void clearActionMode() {
        textView_counter.setVisibility(View.GONE);
        getCounter();

        isActionMode = false;
        text_toolbar.setVisibility(View.GONE);
        text_toolbar.setText("0 item selected");
        backButton_toolbar.setVisibility(View.GONE);
        counter = 0;
        selectionList.clear();
        toolbar.getMenu().clear();
        adapter.notifyDataSetChanged();
    } // End of clearActionMode

    // Εκτέλεση στο πρώτο onLongClick | Ξεκινά η διαδικασία του multiple selection
    public void startSelection(int index) {
        if (!isActionMode) {
            isActionMode = true;
            selectionList.add(list.get(index));
            counter++;
            updateToolbarText(counter);
            text_toolbar.setVisibility(View.VISIBLE);
            backButton_toolbar.setVisibility(View.VISIBLE);
            toolbar.inflateMenu(R.menu.contexual_menu);
            position = index;
            adapter.notifyDataSetChanged();
        } // End of if
    } // End of startSelection

    public void check(View v, int index, Athlete_Adapter.MyHolder holder) {
        if (((CheckBox) v).isChecked()) {
            selectionList.add(list.get(index));
            counter++;
            holder.itemView.animate().translationX(100f); // Slide to right
        } else {
            selectionList.remove(list.get(index));
            counter--;
            holder.itemView.animate().translationX(0f); // Επιστροφή στην αρχική του θέση
        } // End of if
        updateToolbarText(counter);
    } // End of check

    // Ενημέρωση του counter στο toolbar
    private void updateToolbarText(int counter) {
        if (counter == 0) {
            text_toolbar.setText("0 item selected");
        } else if (counter == 1) {
            text_toolbar.setText("1 item selected");
        } else {
            text_toolbar.setText(counter + " items selected");
        } // End of if
    } // End of updateToolbarText

    // Αριθμός ανδρών και γυναικών
    public void getCounter(){
        male_counter = MainActivity.myDatabase.myDaoTemp().ath_count("Male");
        female_counter = MainActivity.myDatabase.myDaoTemp().ath_count("Female");
        textView_counter.setText(" Men: "+String.valueOf(male_counter)+"   Women: "+String.valueOf(female_counter)+" ");
    } // End of getCounter

    // Διαγραφή αθλητή
    public void deleteButton() {
        if (selectionList.size() > 0) {
            // Δημιουργία μηνύματος ΠΡΙΝ την εκτέλεση διαγραφής | Παίρνω τελευταίο confirmation από τον χρήστη
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirm");
            builder.setMessage("Delete " + selectionList.size() + " items?");
            // Κουμπί για την εκτέλεση της διαγραφής
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Διατρέχω την λίστα με τους επιλεγμένους αθλητές
                    for (Athletes athlete : selectionList) {
                        list.remove(athlete); // Αφαιρώ τον επιλεγμένο αθλητή από την αρχική λίστα
                        MainActivity.myDatabase.myDaoTemp().deleteAthlete(athlete); // Αφαιρώ τον επιλεγμένο αθλητή από την τοπική βάση [Πίνακα Athletes]
                    } // End of for loop

                    // Αν η λίστα είναι πλέον κενή εμφάνιση κατάλληλου μηνύματος
                    if (list.size() == 0)
                        empty_list_textView.setVisibility(View.VISIBLE);

                    updateToolbarText(0); // Ενημέρωση του counter στο Toolbar
                    clearActionMode(); // Καλώ την μέθοδο clearActionMode για να κάνω reset
                } // End of onClick
            }); // End of setPositiveButton
            // Κουμπί για την ακύρωση της διαγραφής
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }); // End of setNegativeButton
            builder.show(); // Εμφάνισε του μηνυματος
        } // End of if selection.size() > 0
    } // End of deleteButton method
} // End of class