package com.example.project2021;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.project2021.database.Sports;
import java.util.ArrayList;

public class Smartest extends RecyclerView.Adapter<Smartest.MyHolder> {
    // Μεταβλητές
    private Context context;
    private ArrayList<Sports> myModel;
    private MainActivity mainActivity;
    private StatsFragment fragment;

    // PopUp Window μεταβλητές
    private Dialog myDialog;
    private TextView tv_sport_name, tv_sport_gender, tv_sport_type;
    private Button update_btn;

    public Smartest(Context context, ArrayList<Sports> myModel) {
        this.context = context;
        this.mainActivity = (MainActivity) context;
        this.fragment = (StatsFragment) MainActivity.fragmentManager.findFragmentById(R.id.fragment_container);
        this.myModel = myModel;
    }

    @Override
    public int getItemCount() {
        return myModel.size();
    }

    @NonNull
    @Override
    public Smartest.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_row, parent, false);

        // Αρχικοποίηση του PopUp Window
        context = parent.getContext();
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.sport_popup_window);

        return new Smartest.MyHolder(v, mainActivity);
    } // End of onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull Smartest.MyHolder holder, int position) {
        Sports currentSport = myModel.get(position);

        holder.sport_id = currentSport.getId(); // Παίρνω το sport_id (κύριο κλειδί) από τον πίνακα Sports
        holder.gender.setText(currentSport.getGender());
        // Παίρνω όνομα της ομάδας και αθλήματος και τα τοποθετώ στα TextViews του team_row.xml
        //List<Sports> team_info = MainActivity.myDatabase.myDaoTemp().team_getTeamInfo(currentSport.getId(), currentSport.getSport_id());
        // Περνώ σαν παράμετρο το sport_id για να μου επιστραφεί το sport_name από τον πίνακα Sports
        //String sports_name = MainActivity.myDatabase.myDaoTemp().getSportName(team_info.get(position).getSport_id());
        holder.sportName.setText(currentSport.getName());
        holder.type.setText(currentSport.getType());
        holder.n.setText(String.valueOf(MainActivity.myDatabase.myDaoTemp().players(currentSport.getId())));
        System.out.println(MainActivity.myDatabase.myDaoTemp().players(currentSport.getId()));
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textView;
        private MainActivity mainActivity;
        private LinearLayout linearLayout;


        public TextView sportName, type, n, gender;
        public int sport_id;

        public MyHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            // Αρχικοποίηση μεταβλητών

            this.mainActivity = mainActivity;
            sportName = itemView.findViewById(R.id.sport_row_name1);
            n = itemView.findViewById(R.id.number_players);
            type = itemView.findViewById(R.id.sport_row_type);
            gender = itemView.findViewById(R.id.gend);
        } // End of MyHolder constructor
    } // End of MyHolder class


} //

