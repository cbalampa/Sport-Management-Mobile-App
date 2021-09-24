package com.example.project2021;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomePage_Fragment extends Fragment implements myBottomSheetDialog.BottomSheetListener {
    private myBottomSheetDialog bottomSheetDialog;
    private long mLastClickTime = 0;

    public HomePage_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.homepage_fragment, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().show(); // Εμφάνιση του Drawer menu toolbar

        // Εμφάνιση του BottomSheetDialog ανάλογα με ποιο card έγινε clicked
        CardView athleteCard = view.findViewById(R.id.athlete_card); // Αντιστοίχιση με το CardView athlete_card του homepage_fragment.xml
        athleteCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Αποτρέπω το BottomSheetDialog να εμφανιστεί πάνω από μία φορά [prevent spamming clicks]
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                bottomSheetDialog = new myBottomSheetDialog("athlete_card");
                bottomSheetDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.show(getFragmentManager(), "Athlete Card - Bottom Sheet Dialog");
            } // End of onClick method
        }); // End of setOnClickListener [athlete_card]

        CardView sportCard = view.findViewById(R.id.sport_card); // Αντιστοίχιση με το CardView sport_card του homepage_fragment.xml
        sportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Αποτρέπω το BottomSheetDialog να εμφανιστεί πάνω από μία φορά [prevent spamming clicks]
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                bottomSheetDialog = new myBottomSheetDialog("sport_card");
                bottomSheetDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.show(getFragmentManager(), "Sport Card - Bottom Sheet Dialog");
            } // End of onClick method
        }); // End of setOnClickListener [sport_card]

        CardView teamCard = view.findViewById(R.id.team_card); // Αντιστοίχιση με το CardView team_card του homepage_fragment.xml
        teamCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Αποτρέπω το BottomSheetDialog να εμφανιστεί πάνω από μία φορά [prevent spamming clicks]
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                bottomSheetDialog = new myBottomSheetDialog("team_card");
                bottomSheetDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.show(getFragmentManager(), "Team Card - Bottom Sheet Dialog");
            } // End of onClick method
        }); // End of setOnClickListener [team_card]

        CardView gameCard = view.findViewById(R.id.game_card); // Αντιστοίχιση με το CardView game_card του homepage_fragment.xml
        gameCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Αποτρέπω το BottomSheetDialog να εμφανιστεί πάνω από μία φορά [prevent spamming clicks]
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();

                bottomSheetDialog = new myBottomSheetDialog("game_card");
                bottomSheetDialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BottomSheetDialogTheme);
                bottomSheetDialog.show(getFragmentManager(), "Game Card - Bottom Sheet Dialog");
            } // End of onClick method
        }); // End of setOnClickListener [game_card]

        return view;
    } // End of onCreateView method
} // End of class