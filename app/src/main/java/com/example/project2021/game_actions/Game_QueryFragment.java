package com.example.project2021.game_actions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.SignUpActivity;
import com.example.project2021.database.Games;
import com.example.project2021.database.Sports;
import com.example.project2021.sport_actions.Sport_Adapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class Game_QueryFragment extends Fragment {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference docRef = firebaseFirestore.collection("Games");
    private String id;

    // Μεταβλητές
    private TextView empty_list_textView; // TextView μέσα στο game_fragment_query.xml | Visible μόνο όταν δεν υπάρχουν δεδομένα στον πίνακα Games
    private Toolbar toolbar;
    private RecyclerView recyclerView; // Περιέχει το RecyclerView game_fragment_query.xml
    private ArrayList<Games> list = new ArrayList<>(); // Λίστα με τους Αγώνες
    private Game_Adapter adapter;
    private TextView text_toolbar; // TextView μέσα στο game_fragment_query.xml | Εμφανίζει τον αριθμό των επιλεγμένων αγώνων
    private ImageButton backButton_toolbar; // BackButton του toolbar μέσα στο game_fragment_query.xml | Visible μόνο όταν το isActionMode είναι true
    public boolean isActionMode = false;
    private ArrayList<Games> selectionList = new ArrayList<>(); // Λίστα με τους επιλεγμένους αγώνες
    private int counter = 0; // Αρχική τιμή για τους επιλεγμένους αγώνες
    public int position = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.game_fragment_query, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide(); // Κρύβω το Drawer menu toolbar

        // Εμφάνιση Toolbar
        toolbar = view.findViewById(R.id.game_query_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        text_toolbar = view.findViewById(R.id.game_text_toolbar);
        backButton_toolbar = view.findViewById(R.id.game_back_btn);

        empty_list_textView = view.findViewById(R.id.tv_game_empty_list);

        recyclerView = view.findViewById(R.id.game_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Αρχικοποίηση του Game_Adapter
        adapter = new Game_Adapter(getActivity(), list);
        recyclerView.setAdapter(adapter);

        // Αν κάνω κλικ στο backButton του toolbar κάνω reset
        backButton_toolbar.setOnClickListener(v -> {
            clearActionMode();
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshots : queryDocumentSnapshots) {
                            Games game = documentSnapshots.toObject(Games.class);
                            game.setId(documentSnapshots.getId());
                            id = game.getId();

                            list.add(game);
                        }

                        // Αρχικοποίηση του Game_Adapter
                        adapter = new Game_Adapter(getActivity(), list);
                        recyclerView.setAdapter(adapter);

                        // Αν η λίστα είναι κενή εμφάνιση κατάλληλου μηνύματος
                        if (list.size() == 0)
                            empty_list_textView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete: // Αν κάνω κλικ στο deleteButton καλώ την μέθοδο deleteButton και διαγράφω αγώνες
                deleteButton();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Reset όλων των στοιχείων
    private void clearActionMode() {
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

    public void check(View v, int index, Game_Adapter.MyHolder holder) {
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

    // Διαγραφή αγώνα
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
                    // Διατρέχω την λίστα με τους επιλεγμένους αγώνες
                    for (Games games : selectionList) {
                        list.remove(games); // Αφαιρώ το επιλεγμέν άθλημα από την αρχική λίστα
                    } // End of for loop

                    // Αφαιρώ τους επιλεγμένους αγλωνες από την απομακρυσμένη βάση [Πίνακα Games]
                    for (int i = 0; i < selectionList.size(); i++) {
                        firebaseFirestore.collection("Games").document(selectionList.get(i).getId())
                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    // Αν η λίστα είναι πλέον κενή εμφάνιση κατάλληλου μηνύματος
                                    if (list.size() == 0)
                                        empty_list_textView.setVisibility(View.VISIBLE);

                                    updateToolbarText(0); // Ενημέρωση του counter στο Toolbar
                                    clearActionMode(); // Καλώ την μέθοδο clearActionMode για να κάνω reset
                                }else{
                                    Toast.makeText(getActivity(), "Oops something went wrong..", Toast.LENGTH_LONG).show();
                                } // End of isSuccessful
                            } // End of onComplete
                        });
                    } // End of for loop
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
}