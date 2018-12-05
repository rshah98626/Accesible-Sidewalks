package com.cs465.team_award.accessiblesidewalks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.cs465.team_award.accessiblesidewalks.MapsActivity.PREF_USER_FIRST_TIME;

public class WelcomeActivity extends AppCompatActivity {

    boolean isUserFirstTime;
    private Button startActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        this.startActivity = findViewById(R.id.goForward);

        this.startActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isUserFirstTime = Boolean.valueOf(Utils.readSharedSetting(WelcomeActivity.this, PREF_USER_FIRST_TIME, "true"));
                Intent introIntent = new Intent(WelcomeActivity.this, OnboardingActivity.class);
                introIntent.putExtra(PREF_USER_FIRST_TIME, isUserFirstTime);
                finish();
                startActivity(introIntent);
            }
        });
    }
}
