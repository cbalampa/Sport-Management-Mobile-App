
package com.example.project2021;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2021.database.Sports;

import java.util.ArrayList;

public class StatsFragment extends Fragment{
    private TextView n;

    private RecyclerView recyclerView;
    private ArrayList<Sports> list = new ArrayList<>();
    private Smartest adapter;
    private int number;

    private View view;

    public StatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.stats_fragment, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();

        n = view.findViewById(R.id.number_all);
        number = MainActivity.myDatabase.myDaoTemp().number_Of_Athletes();
        n.setText(Integer.toString(number));

        n = view.findViewById(R.id.number_avg);
        number = MainActivity.myDatabase.myDaoTemp().avg_yob_Of_Athletes();
        n.setText(Integer.toString(number));


        recyclerView = view.findViewById(R.id.sport_recyclerView1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = (ArrayList<Sports>) MainActivity.myDatabase.myDaoTemp().getSports(); // Γεμίζω την μεταβλητή list με όλα τα αθλήματα της τοπικής βάσης

        // Αρχικοποίηση του Athlete_Adapter
        adapter = new Smartest(getContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void init(){

    }
}