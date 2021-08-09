package com.example.youtubehome;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonSaveInformation;
    private EditText userNameEdT, userNickEdT, userStatusEdT;
    private CircleImageView imageViewNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        buttonSaveInformation = (Button) findViewById(R.id.button_save_for_settings);

        userNameEdT = (EditText) findViewById(R.id.set_user_name);
        userNickEdT = (EditText) findViewById(R.id.set_user_nick);
        userStatusEdT = (EditText) findViewById(R.id.save_user_information);

        imageViewNew = (CircleImageView) findViewById(R.id.profile_image);
    }
}