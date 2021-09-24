package com.example.project2021;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.project2021.athlete_actions.Athlete_InsertFragment;
import com.example.project2021.athlete_actions.Athlete_QueryFragment;
import com.example.project2021.game_actions.Game_InsertFragment;
import com.example.project2021.game_actions.Game_QueryFragment;
import com.example.project2021.sport_actions.Sport_InsertFragment;
import com.example.project2021.sport_actions.Sport_QueryFragment;
import com.example.project2021.team_actions.Team_InsertFragment;
import com.example.project2021.team_actions.Team_QueryFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class myBottomSheetDialog extends BottomSheetDialogFragment {
    private BottomSheetListener mListener;
    private String cardView_title;
    private Button insert_btn, delete_btn, update_btn, more_btn;
    private View view;

    // Constructor
    public myBottomSheetDialog(String string) {
        this.cardView_title = string;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Εμφάνιση bottom_sheet_layout ανάλογα με το cardView_title
        switch (cardView_title) {
            case "athlete_card":
                view = inflater.inflate(R.layout.athlete_bottom_sheet_layout, container, false);

                insert_btn = view.findViewById(R.id.bottom_sheet_insert_btn); // Αντιστοίχιση με το ADD button του athlete_bottom_sheet_layout.xml
                insert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του athlete_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Athlete_InsertFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [athlete_card - ADD]
                more_btn = view.findViewById(R.id.bottom_sheet_more_btn); // Αντιστοίχιση με το SHOW MORE button του athlete_bottom_sheet_layout.xml
                more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του athlete_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Athlete_QueryFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [athlete_card - SHOW MORE]
                break;
            case "sport_card":
                view = inflater.inflate(R.layout.sport_bottom_sheet_layout, container, false);

                insert_btn = view.findViewById(R.id.bottom_sheet_insert_btn); // Αντιστοίχιση με το ADD button του sport_bottom_sheet_layout.xml
                insert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του sport_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Sport_InsertFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [sport_card - ADD]
                more_btn = view.findViewById(R.id.bottom_sheet_more_btn); // Αντιστοίχιση με το SHOW MORE button του sport_bottom_sheet_layout.xml
                more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του sport_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Sport_QueryFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [sport_card - SHOW MORE]
                break;
            case "team_card":
                view = inflater.inflate(R.layout.team_bottom_sheet_layout, container, false);

                insert_btn = view.findViewById(R.id.bottom_sheet_insert_btn); // Αντιστοίχιση με το ADD button του team_bottom_sheet_layout.xml
                insert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του team_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Team_InsertFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [team_card - ADD]
                more_btn = view.findViewById(R.id.bottom_sheet_more_btn); // Αντιστοίχιση με το SHOW MORE button του sport_bottom_sheet_layout.xml
                more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του team_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Team_QueryFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [team_card - SHOW MORE]
                break;
            case "game_card":
                view = inflater.inflate(R.layout.game_bottom_sheet_layout, container, false);

                insert_btn = view.findViewById(R.id.bottom_sheet_insert_btn); // Αντιστοίχιση με το ADD button του game_bottom_sheet_layout.xml
                insert_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του game_fragment_insert.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Game_InsertFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [game_card - ADD]
                more_btn = view.findViewById(R.id.bottom_sheet_more_btn); // Αντιστοίχιση με το SHOW MORE button του game_bottom_sheet_layout.xml
                more_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Αλλάζω το περιεχόμενο του fragment_container [activity_main.xml] με αυτό του game_fragment_query.xml
                        MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, new Game_QueryFragment()).addToBackStack(null).commit();
                        dismiss(); // Κλείνω το BottomSheetDialog
                    } // End of onClick method
                }); // End of setOnClickListener [game_card - SHOW MORE]
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + cardView_title);
        }

        return view;
    } // End of onCreateView method

    public interface BottomSheetListener {
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement ButtomSheetListener");
        } // End of try - catch
    } // End of onAttach method
} // End of class
