package com.example.project2021.welcome_page_files;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.project2021.LoginActivity;
import com.example.project2021.R;
import com.example.project2021.SignUpActivity;
import com.example.project2021.sharedPreferenceConfig;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {
    // Initialize values
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private PagerAdapter pagerAdapter;
    private TextView[] mDots; // dots when swiping left or right

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        List<Fragment> list = new ArrayList<>();
        list.add(new WelcomeFragment());
        //list.add(new LoginFragment());
        list.add(new WelcomeRightFragment());

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        pagerAdapter = new SlidePageAdapter(getSupportFragmentManager(), list);
        mSlideViewPager.setAdapter(pagerAdapter);
        mSlideViewPager.setCurrentItem(0); // Αρχική σελίδα

        addDotsIndicator(0); // Αρχική θέση του dot
        mSlideViewPager.addOnPageChangeListener(viewListener);
    }

    public void doAnimation(View view) {
        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Prevent activity from opening multiple times
        startActivity(intent);

        overridePendingTransition(R.anim.slide_up_in, R.anim.nothing);
    }

    public void doAnimation2(View view) {
        Intent intent = new Intent(OnboardingActivity.this, SignUpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); // Prevent activity from opening multiple times
        startActivity(intent);

        overridePendingTransition(R.anim.slide_up_in, R.anim.nothing);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1926){
            if (resultCode == 1926) {
                this.finish();
            }
        }
    }

    public void addDotsIndicator(int position) {
        mDots = new TextView[2];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));

            mDotLayout.addView(mDots[i]);
        }

        // Κάνε white το ενεργό dot
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.white));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int position) {

        }
    };

}