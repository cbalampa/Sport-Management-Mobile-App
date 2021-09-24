package com.example.project2021.athlete_actions;

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
import com.example.project2021.database.Athletes;

import java.util.ArrayList;
import java.util.List;

public class Athlete_Adapter extends RecyclerView.Adapter<Athlete_Adapter.MyHolder> {
    // Μεταβλητές
    private Context context;
    private ArrayList<Athletes> myModel;
    private MainActivity mainActivity;
    private Athlete_QueryFragment fragment;

    // PopUp Window μεταβλητές
    private Dialog myDialog;
    private TextView tv_first_name, tv_last_name, tv_age, tv_country, tv_gender, tv_sport;
    private Button update_btn;

    public Athlete_Adapter(Context context, ArrayList<Athletes> myModel) {
        this.context = context;
        this.mainActivity = (MainActivity) context;
        this.fragment = (Athlete_QueryFragment) MainActivity.fragmentManager.findFragmentById(R.id.fragment_container);
        this.myModel = myModel;
    }

    @NonNull
    @Override
    public Athlete_Adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.athlete_row, parent, false);

        // Αρχικοποίηση του PopUp Window
        context = parent.getContext();
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.athlete_popup_window);

        return new MyHolder(v, mainActivity);
    } // End of onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull Athlete_Adapter.MyHolder holder, int position) {
        Athletes currentAthlete = myModel.get(position);
        holder.athlete_sport_id = currentAthlete.getSport_id(); // Παίρνω το sport_id (ξένο κλειδί) από τον πίνακα Athletes
        // Περνώ σαν παράμετρο το sport_id για να μου επιστραφεί το sport_name από τον πίνακα Sports
        String sports_name = MainActivity.myDatabase.myDaoTemp().getSportName(holder.athlete_sport_id);
        // Παίρνω όνομα αθλητή και αθλήματος και τα τοποθετώ στα TextViews του athlete_row.xml
        holder.fullName.setText(currentAthlete.getFname() + " " + currentAthlete.getLname());
        holder.sport.setText("Sport: " + sports_name);

        // Εμφάνιση PopUp Window όταν κάνω κλικ πάνω σε αντικείμενο του RecyclerView [δηλαδή σε αθλητή - athlete_row.xml]
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    init(); // Καλώ την μέθοδο init
                    showPopUpWindow(holder.getAdapterPosition(), myModel);
                } // End of if position
            } // End of if
        }); // End of setOnClickListener

        if(fragment.position == position){
            holder.checkBox.setChecked(true);
            holder.itemView.animate().translationX(100f); // Slide to right
            fragment.position = -1;
        } // End of if

        // Αν έχει πραγματοποιηθεί onLongClick [isActionMode == true] εμφανίζω τα checkBox με animation
        if (fragment.isActionMode) {
            Anim anim = new Anim(100,holder.linearLayout);
            anim.setDuration(300);
            holder.linearLayout.setAnimation(anim);
        } else {
            Anim anim = new Anim(0,holder.linearLayout);
            anim.setDuration(300);
            holder.linearLayout.setAnimation(anim);
            holder.checkBox.setChecked(false);
            holder.itemView.animate().translationX(0f); // Επιστροφή στην αρχική του θέση [athlete_row.xml]
        } // End of if

        // Ξεκινά η διαδικασία του multiple selection
        holder.cardView.setOnLongClickListener(v -> {
            fragment.startSelection(position); // Εκτελείται η μέθοδος που υπάρχει στο Athlete_QueryFragment
            return true;
        });

        // Προσθέτω ή αφαιρώ αθλητές από την selectionList ανάλογα με την τιμή που έχει το checkBox του cardView [true-false]
        holder.checkBox.setOnClickListener(v -> {
            fragment.check(v, position, holder); // Εκτελείται η μέθοδος που υπάρχει στο Athlete_QueryFragment
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

        public TextView fullName, sport;
        public int athlete_sport_id;

        public MyHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            // Αρχικοποίηση μεταβλητών
            textView = itemView.findViewById(R.id.tv_athlete_empty_list);
            cardView = itemView.findViewById(R.id.athlete_row_cv);
            checkBox = itemView.findViewById(R.id.ath_row_checkbox);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            this.mainActivity = mainActivity;
            fullName = itemView.findViewById(R.id.ath_row_fullname);
            sport = itemView.findViewById(R.id.aht_row_sport);

            // Αν η λίστα είναι κενή εμφάνιση κατάλληλου μηνύματος
            if(myModel.size() == 0)
                textView.setVisibility(View.VISIBLE);
        } // End of MyHolder constructor
    } // End of MyHolder class

    // Κλάση για animation - εμφάνιση του checkBox
    class  Anim extends Animation {
        private int width, startWidth;
        private View view;

        public Anim(int width, View view){
            this.width = width;
            this.view = view;
            this.startWidth = view.getWidth();
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            int newWidth = startWidth + (int) ((width-startWidth) * interpolatedTime);
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
    public void showPopUpWindow(int position, List<Athletes> athletes_list) {
        String sport_name = "";
        // Παίρνω τις τιμές που έχει το συγκεκριμένο cardView στο πίνακα Athletes
        int athlete_id = athletes_list.get(position).getId();
        int sport_id = athletes_list.get(position).getSport_id();
        int yob = athletes_list.get(position).getAge();
        // και τα θέτω στα κατάλληλα TextViews
        tv_first_name.setText(athletes_list.get(position).getFname());
        tv_last_name.setText(athletes_list.get(position).getLname());
        // Παίρνω το όνομα του αθλήματος μέσω της ath_getSportsNames
        try {
            sport_name = MainActivity.myDatabase.myDaoTemp().getSportName(sport_id);
        } catch (Exception e) {
            System.out.println("DEBUG ERROR MESSAGE: " + e.getMessage());
        } // End of try - catch
        tv_sport.setText("Sport: " + sport_name);
        tv_age.setText(String.valueOf(yob));
        tv_gender.setText(athletes_list.get(position).getGender());
        tv_country.setText(athletes_list.get(position).getCountry());

        myDialog.show(); // Εμφανίζω στο athlete_popup_window.xml

        // Αλλάζω fragment όταν πατάω το κουμπί Update Athlete
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Στέλνω τα στοιχεία του Popup Window στο Athlete_UpdateFragment
                Bundle bundle = new Bundle();
                bundle.putInt("ID", athlete_id);
                bundle.putString("FirstName", tv_first_name.getText().toString());
                bundle.putString("LastName", tv_last_name.getText().toString());
                bundle.putString("Gender", tv_gender.getText().toString());
                bundle.putInt("SportID", sport_id);
                bundle.putInt("Age", yob);

                Athlete_UpdateFragment fragment = new Athlete_UpdateFragment();
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                myDialog.dismiss(); // Κλείνω το myDialog
            } // End of onClick [update button]
        }); // End of setOnClickListener
    } // End of showPopUpWindow

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο athlete_popup_window.xml
    public void init() {
        tv_first_name = myDialog.findViewById(R.id.team_popup_name);
        tv_last_name = myDialog.findViewById(R.id.team_popup_stadium);
        tv_sport = myDialog.findViewById(R.id.team_popup_location);
        tv_age = myDialog.findViewById(R.id.ath_popup_age);
        tv_gender = myDialog.findViewById(R.id.team_popup_city);
        tv_country = myDialog.findViewById(R.id.team_popup_country);
        update_btn = myDialog.findViewById(R.id.team_popup_window_update_btn);
    } // End of init
} // End of Athlete_Adapter


