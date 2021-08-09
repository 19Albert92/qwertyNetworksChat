package com.example.youtubehome;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends AppCompatActivity {

    private Button agreeButton;
    private TextView textPrivacy;

    private FirebaseAuth mAuth;
    private FirebaseApp firebaseApp;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        FirebaseApp.initializeApp(this);
        initViewView();
    }

    private void initViewView() {
        agreeButton = (Button) findViewById(R.id.agree_button);
        clickListenerForButton();

        textPrivacy = (TextView) findViewById(R.id.privacy);
        clickListenerForTextPrivacy();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    private void clickListenerForButton() {
        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login_intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(login_intent);
            }
        });
    }

    private void clickListenerForTextPrivacy() {
        textPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacy_intent = new Intent(WelcomeActivity.this, PrivacyActivity.class);
                startActivity(privacy_intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
        }
    }
}