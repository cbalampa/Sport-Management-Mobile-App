package com.example.project2021.sport_actions;

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
import com.example.project2021.database.Sports;

import java.util.ArrayList;
import java.util.List;

public class Sport_Adapter extends RecyclerView.Adapter<Sport_Adapter.MyHolder> {
    // Μεταβλητές
    private Context context;
    private ArrayList<Sports> myModel;
    private MainActivity mainActivity;
    private Sport_QueryFragment fragment;

    // PopUp Window μεταβλητές
    private Dialog myDialog;
    private TextView tv_sport_name, tv_sport_gender, tv_sport_type;
    private Button update_btn;

    public Sport_Adapter(Context context, ArrayList<Sports> myModel) {
        this.context = context;
        this.mainActivity = (MainActivity) context;
        this.fragment = (Sport_QueryFragment) MainActivity.fragmentManager.findFragmentById(R.id.fragment_container);
        this.myModel = myModel;
    }

    @NonNull
    @Override
    public Sport_Adapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_row, parent, false);

        // Αρχικοποίηση του PopUp Window
        context = parent.getContext();
        myDialog = new Dialog(context);
        myDialog.setContentView(R.layout.sport_popup_window);

        return new MyHolder(v, mainActivity);
    } // End of onCreateViewHolder

    @Override
    public void onBindViewHolder(@NonNull Sport_Adapter.MyHolder holder, int position) {
        Sports currentSport = myModel.get(position);
        holder.sport_id = currentSport.getId(); // Παίρνω το sport_id (κύριο κλειδί) από τον πίνακα Sports
        // Παίρνω όνομα της ομάδας και αθλήματος και τα τοποθετώ στα TextViews του team_row.xml
        //List<Sports> team_info = MainActivity.myDatabase.myDaoTemp().team_getTeamInfo(currentSport.getId(), currentSport.getSport_id());
        // Περνώ σαν παράμετρο το sport_id για να μου επιστραφεί το sport_name από τον πίνακα Sports
        //String sports_name = MainActivity.myDatabase.myDaoTemp().getSportName(team_info.get(position).getSport_id());
        holder.sportName.setText(currentSport.getName() + " (" +currentSport.getGender()+")");
        holder.type.setText(currentSport.getType());

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
            holder.itemView.animate().translationX(0f); // Επιστροφή στην αρχική του θέση [sport_row.xml]
        } // End of if

        // Ξεκινά η διαδικασία του multiple selection
        holder.cardView.setOnLongClickListener(v -> {
            fragment.startSelection(position); // Εκτελείται η μέθοδος που υπάρχει στο Sport_QueryFragment
            return true;
        });

        // Προσθέτω ή αφαιρώ αθλήματα από την selectionList ανάλογα με την τιμή που έχει το checkBox του cardView [true-false]
        holder.checkBox.setOnClickListener(v -> {
            fragment.check(v, position, holder); // Εκτελείται η μέθοδος που υπάρχει στο Sport_QueryFragment
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

        public TextView sportName, type;
        public int sport_id;

        public MyHolder(@NonNull View itemView, MainActivity mainActivity) {
            super(itemView);

            // Αρχικοποίηση μεταβλητών
            textView = itemView.findViewById(R.id.tv_sport_empty_list);
            cardView = itemView.findViewById(R.id.sport_row_cv);
            checkBox = itemView.findViewById(R.id.sport_row_checkbox);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            this.mainActivity = mainActivity;
            sportName = itemView.findViewById(R.id.sport_row_name);
            type = itemView.findViewById(R.id.sport_row_type);

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
    public void showPopUpWindow(int position, List<Sports> sport_list) {
        String sport_name = "";
        // Παίρνω τις τιμές που έχει το συγκεκριμένο cardView στο πίνακα Sports
        int sport_id = sport_list.get(position).getId();
        // και τα θέτω στα κατάλληλα TextViews
        tv_sport_name.setText(sport_list.get(position).getName());
        tv_sport_type.setText(sport_list.get(position).getType());
        tv_sport_gender.setText(sport_list.get(position).getGender());

        myDialog.show(); // Εμφανίζω στο athlete_popup_window.xml

        // Αλλάζω fragment όταν πατάω το κουμπί Update Sport
        update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Στέλνω τα στοιχεία του Popup Window στο Sport_UpdateFragment
                Bundle bundle = new Bundle();
                bundle.putInt("ID", sport_id);
                bundle.putString("SportName", tv_sport_name.getText().toString());
                bundle.putString("SportType", tv_sport_type.getText().toString());
                bundle.putString("SportGender", tv_sport_gender.getText().toString());

                Sport_UpdateFragment fragment = new Sport_UpdateFragment();
                fragment.setArguments(bundle);
                MainActivity.fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack(null).commit();

                myDialog.dismiss(); // Κλείνω το myDialog
            } // End of onClick [update button]
        }); // End of setOnClickListener
    } // End of showPopUpWindow

    // Αντιστοίχιση μεταβλητών με τα resources που βρίσκονται στο sport_popup_window.xml
    public void init() {
        tv_sport_name = myDialog.findViewById(R.id.sport_popup_name);
        tv_sport_type = myDialog.findViewById(R.id.sport_popup_type);
        tv_sport_gender = myDialog.findViewById(R.id.sport_popup_gender);
        update_btn = myDialog.findViewById(R.id.sport_popup_window_update_btn);
    } // End of init
} // End of Sport_Adapter


