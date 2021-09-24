package com.example.project2021.game_actions;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project2021.MainActivity;
import com.example.project2021.R;
import com.example.project2021.database.Games;
import com.example.project2021.database.Teams;
import com.example.project2021.team_actions.Team_UpdateFragment;

import java.util.ArrayList;
import java.util.List;

public class Game_Adapter extends RecyclerView.Adapter<Game_Adapter.MyHolder> {
    // Μεταβλητές
    private Context context;
    private ArrayList<Games> myModel;
    private MainActivity mainActivity;
    private Game_QueryFragment fragment;

    // PopUp Window μεταβλητές
    private Dialog myDialog;
    private TextView tv_game_row_home_team, tv_game_row_away_team, tv_game_row_home_score, tv_game_row_away_score;
    private TextView tv_game_row_sport, tv_game_row_date, tv_game_row_city, tv_game_row_country;
    private Button update_btn;

    public Game_Adapter(Context context, ArrayList<Games> myModel) {
        this.context = context;
        this.mainActivity = (MainActivity) context;
        this.fragment = (Game_QueryFragment) MainActivity.fragmentManager.findFragmentById(R.id.fragment_container);
        this.myModel = myModel;
    }

    @NonNull
    @Override
    public Game_Adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_row, parent, false);

        // Αρχικοποίηση του PopUp Window
        context = parent.getContext();
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.game_popup_window);

        return new MyHolder(v, mainActivity);
    } // End of onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull Game_Adapter.MyHolder holder, int position) {
        Games currentGame = myModel.get(position);
        //holder.id = currentGame.getId(); // Παίρνω το id από τον πίνακα Games
        // Παίρνω τα ονόματα των ομάδων και τα score, τα τοποθετώ στα TextViews του game_row.xml
        holder.homeTeam.setText(currentGame.getTeamHome());
        holder.awayTeam.setText(currentGame.getTeamAway());
        holder.teamHomeScore.setText(String.valueOf(currentGame.getTeamHomeScore()));
        holder.awayHomeScore.setText(String.valueOf(currentGame.getTeamAwayScore()));

        // Εμφάνιση PopUp Window όταν κάνω κλικ πάνω σε αντικείμενο του RecyclerView [δηλαδή σε αγώνα - game_row.xml]
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    init(); // Καλώ την μέθοδο init
                    showPopUpWindow(holder.getAdapterPosition(), myModel);
                } // End of if position
            } // End of if
        }); // End of setOnClickListener

        if (fragment.position == position) {
            holder.checkBox.setChecked(true);
            holder.itemView.animate().translationX(100f); // Slide to right
            fragment.position = -1;
        } // End of if

        // Αν έχει πραγματοποιηθεί onLongClick [isActionMode == true] εμφανίζω τα checkBox με animation
        if (fragment.isActionMode) {
            Anim anim = new Anim(100, holder.linearLayout);
            anim.setDuration(300);
            holder.linearLayout.setAnimation(anim);
        } else {
            Anim anim = new Anim(0, holder.linearLayout);
            anim.setDuration(300);
            holder.linearLayout.setAnimation(anim);
            holder.checkBox.setChecked(false);
            holder.itemView.animate().translationX(0f); // Επιστροφή στην αρχική του θέση [game_row.xml]
        } // End of if

        // Ξεκινά η διαδικασία του multiple selection
        holder.cardView.setOnLongClickListener(v -> {
            fragment.startSelection(position); // Εκτελείται η μέθοδος που υπάρχει στο Game_QueryFragment
            return true;
        });

        // Προσθέτω ή αφαιρώ ομάδες από την selectionList ανάλογα με την τιμή που έχει το checkBox του cardView [true-false]
        holder.checkBox.setOnClickListener(v -> {
            fragment.check(v, position, holder); // Εκτελείται η μέθοδος που υπάρχει στο Game_QueryFragment
        });
    }

    @Override
    public int getItemCount() {
        return myModel.size();
    }

    class MyHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private TextView textView;
        private MainActivity mainActivity;
        private LinearLayout linearLayout;
        private CheckBox checkBox;

        public TextView homeTeam, teamHomeScore, awayTeam, awayHomeScore;
        public String id;

        public MyHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            // Αρχικοποίηση μεταβλητών
            textView = itemView.findViewById(R.id.tv_game_empty_list);
            cardView = itemView.findViewById(R.id.game_row_cv);
            checkBox = itemView.findViewById(R.id.game_row_checkbox);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            this.mainActivity = mainActivity;
            homeTeam = itemView.findViewById(R.id.game_row_home_team);
            awayTeam = itemView.findViewById(R.id.game_row_away_team);
            teamHomeScore = itemView.findViewById(R.id.game_row_home_score);
            awayHomeScore = itemView.findViewById(R.id.game_row_away_score);

            // Αν η λίστα είναι κενή εμφάνιση κατάλληλου μηνύματος
            if (myModel.size() == 0)
                textView.setVisibility(View.VISIBLE);
        } // End of MyHolder constructor
    } // End of MyHolder class

    // Κλάση για animation - εμφάνιση του checkBox
    class Anim extends Animation {
        private int width, startWidth;
        private View view;

        public Anim(int width, View view) {
            this.width = width;
            this.view = view;
            this.startWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = startWidth + (int) ((width - startWidth) * interpolatedTime);
            view.getLayoutParams().width = newWidth;
            view.requestLayout();

            super.applyTransformation(interpolatedTime, t);
        }

        @Override
        public boolean willChangeBounds() {
            return true;
        }
    } // End of Anim

    // PopUp Window όταν κάνω κλικ σε cardView
    public void showPopUpWindow(int position, List<Games> games_list) {
        String sport_name = "";
        // Παίρνω τις τιμές που έχει το συγκεκριμένο cardView στο πίνακα Games
        //String gameID = games_list.get(position).getId();
        String gameSport = games_list.get(position).getGameSport();
        String teamHome = games_list.get(position).getTeamHome();
        String teamAway = games_list.get(position).getTeamAway();
        int teamHomeScore = games_list.get(position).getTeamHomeScore();
        int teamAwayScore = games_list.get(position).getTeamAwayScore();
        String gameDate = games_list.get(position).getGameDate();
        String gameCity = games_list.get(position).getGameCity();
        String gameCountry = games_list.get(position).getGameCountry();

        try{
            tv_game_row_home_team.setText(teamHome);
            tv_game_row_away_team.setText(teamAway);
            tv_game_row_home_score.setText(String.valueOf(teamHomeScore));
            tv_game_row_away_score.setText(String.valueOf(teamAwayScore));
            tv_game_row_sport.setText(gameSport);
            tv_game_row_date.setText(String.valueOf(gameDate));
            tv_game_row_city.setText(gameCity);
            tv_game_row_country.setText(gameCountry);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        myDialog.show(); // Εμφανίζω στο athlete_popup_window.xml

        // Αλλάζω fragment όταν πατάω το κουμπί Update Athlete
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Στέλνω τα στοιχεία του Popup Window στο Team_UpdateFragment
                Bundle bundle = new Bundle();
                bundle.putString("gameSport", gameSport);
                bundle.putString("teamHome", teamHome);
                bundle.putString("teamAway", teamAway);
                bundle.putInt("teamHomeScore", teamHomeScore);
                bundle.putInt("teamAwayScore", teamAwayScore);
                bundle.putString("gameDate", gameDate);
                bundle.putString("gameCity", gameCity);
                bundle.putString("gameCountry", gameCountry);

                Game_UpdateFragment fragment = new Game_UpdateFragment();
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction().
                        replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                myDialog.dismiss(); // Κλείνω το myDialog
            } // End of onClick [update button]
        }); // End of setOnClickListener
    } // End of showPopUpWindow

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο game_popup_window.xml
    public void init() {
        tv_game_row_home_team = myDialog.findViewById(R.id.game_row_home_team);
        tv_game_row_away_team = myDialog.findViewById(R.id.game_row_away_team);
        tv_game_row_home_score = myDialog.findViewById(R.id.game_row_home_score);
        tv_game_row_away_score = myDialog.findViewById(R.id.game_row_away_score);
        tv_game_row_sport = myDialog.findViewById(R.id.game_popup_sport);
        tv_game_row_date = myDialog.findViewById(R.id.game_popup_date);
        tv_game_row_city = myDialog.findViewById(R.id.game_popup_city);
        tv_game_row_country = myDialog.findViewById(R.id.game_popup_country);
        update_btn = myDialog.findViewById(R.id.game_popup_window_update_btn);
    } // End of init
} // End of Athlete_Adapter


