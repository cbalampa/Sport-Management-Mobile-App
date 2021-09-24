package com.example.project2021.team_actions;

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
import com.example.project2021.athlete_actions.Athlete_UpdateFragment;
import com.example.project2021.database.Teams;

import java.util.ArrayList;
import java.util.List;

public class Team_Adapter extends RecyclerView.Adapter<Team_Adapter.MyHolder> {
    // Μεταβλητές
    private Context context;
    private ArrayList<Teams> myModel;
    private MainActivity mainActivity;
    private Team_QueryFragment fragment;

    // PopUp Window μεταβλητές
    private Dialog myDialog;
    private TextView tv_team_name, tv_stadium, tv_age, tv_country, tv_city, tv_sport_name;
    private Button update_btn;

    public Team_Adapter(Context context, ArrayList<Teams> myModel) {
        this.context = context;
        this.mainActivity = (MainActivity) context;
        this.fragment = (Team_QueryFragment) MainActivity.fragmentManager.findFragmentById(R.id.fragment_container);
        this.myModel = myModel;
    }

    @NonNull
    @Override
    public Team_Adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.team_row, parent, false);

        // Αρχικοποίηση του PopUp Window
        context = parent.getContext();
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.team_popup_window);

        return new MyHolder(v, mainActivity);
    } // End of onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull Team_Adapter.MyHolder holder, int position) {
        Teams currentTeam = myModel.get(position);
        holder.team_sport_id = currentTeam.getSport_id(); // Παίρνω το sport_id (ξένο κλειδί) από τον πίνακα Teams
        // Παίρνω όνομα της ομάδας και αθλήματος και τα τοποθετώ στα TextViews του team_row.xml
        // Περνώ σαν παράμετρο το sport_id για να μου επιστραφεί το sport_name από τον πίνακα Sports
        String sports_name = MainActivity.myDatabase.myDaoTemp().getSportName(currentTeam.getSport_id());
        holder.teamName.setText(currentTeam.getName() + " (" + sports_name + ")");
        holder.location.setText(currentTeam.getCity() + ", " + currentTeam.getCountry());

        // Εμφάνιση PopUp Window όταν κάνω κλικ πάνω σε αντικείμενο του RecyclerView [δηλαδή σε αθλητή - team_row.xml]
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
            holder.itemView.animate().translationX(0f); // Επιστροφή στην αρχική του θέση [team_row.xml]
        } // End of if

        // Ξεκινά η διαδικασία του multiple selection
        holder.cardView.setOnLongClickListener(v -> {
            fragment.startSelection(position); // Εκτελείται η μέθοδος που υπάρχει στο Team_QueryFragment
            return true;
        });

        // Προσθέτω ή αφαιρώ ομάδες από την selectionList ανάλογα με την τιμή που έχει το checkBox του cardView [true-false]
        holder.checkBox.setOnClickListener(v -> {
            fragment.check(v, position, holder); // Εκτελείται η μέθοδος που υπάρχει στο Team_QueryFragment
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

        public TextView teamName, location;
        public int team_sport_id;

        public MyHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            // Αρχικοποίηση μεταβλητών
            textView = itemView.findViewById(R.id.tv_team_empty_list);
            cardView = itemView.findViewById(R.id.team_row_cv);
            checkBox = itemView.findViewById(R.id.team_row_checkbox);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            this.mainActivity = mainActivity;
            teamName = itemView.findViewById(R.id.team_row_team_name);
            location = itemView.findViewById(R.id.team_row_location);

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
    public void showPopUpWindow(int position, List<Teams> team_list) {
        String sport_name = "";
        // Παίρνω τις τιμές που έχει το συγκεκριμένο cardView στο πίνακα Teams
        int team_id = team_list.get(position).getId();
        int sport_id = team_list.get(position).getSport_id();
        int yoe = team_list.get(position).getYoe();
        // και τα θέτω στα κατάλληλα TextViews
        tv_team_name.setText(team_list.get(position).getName());
        // Παίρνω το όνομα του αθλήματος μέσω της ath_getSportsNames
        try {
            sport_name = MainActivity.myDatabase.myDaoTemp().getSportName(sport_id);
        } catch (Exception e) {
            System.out.println("DEBUG ERROR MESSAGE: " + e.getMessage());
        } // End of try - catch
        tv_stadium.setText(team_list.get(position).getStadium());
        tv_sport_name.setText("(" + sport_name + ")");
        tv_age.setText(String.valueOf(yoe));
        tv_city.setText(team_list.get(position).getCity());
        tv_country.setText(team_list.get(position).getCountry());

        myDialog.show(); // Εμφανίζω στο athlete_popup_window.xml

        // Αλλάζω fragment όταν πατάω το κουμπί Update Athlete
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Στέλνω τα στοιχεία του Popup Window στο Team_UpdateFragment
                Bundle bundle = new Bundle();
                bundle.putInt("ID", team_id);
                bundle.putString("TeamName", tv_team_name.getText().toString());
                bundle.putString("Stadium", tv_stadium.getText().toString());
                bundle.putString("City", tv_city.getText().toString());
                bundle.putInt("SportID", sport_id);
                bundle.putString("SportName", tv_sport_name.getText().toString());
                bundle.putInt("Age", yoe);

                Team_UpdateFragment fragment = new Team_UpdateFragment();
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                myDialog.dismiss(); // Κλείνω το myDialog
            } // End of onClick [update button]
        }); // End of setOnClickListener
    } // End of showPopUpWindow

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο athlete_popup_window.xml
    public void init() {
        tv_team_name = myDialog.findViewById(R.id.team_popup_name);
        tv_stadium = myDialog.findViewById(R.id.team_popup_stadium);
        tv_sport_name = myDialog.findViewById(R.id.team_popup_sport);
        tv_age = myDialog.findViewById(R.id.ath_popup_age);
        tv_city = myDialog.findViewById(R.id.team_popup_city);
        tv_country = myDialog.findViewById(R.id.team_popup_country);
        update_btn = myDialog.findViewById(R.id.team_popup_window_update_btn);
    } // End of init
} // End of Athlete_Adapter


