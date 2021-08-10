package com.example.youtubehome;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.Toolbar;

import static com.example.youtubehome.fragments.GroupsFragment.KEY_GROUP_CHAT;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView sendMessage;
    private EditText inputMessageEdT;
    private ScrollView scrollView;
    private AppCompatTextView displayMessage;

    private String currentGroupChatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        initial();
    }

    private void initial() {
        currentGroupChatName = getIntent().getExtras().get(KEY_GROUP_CHAT).toString();
        initToolbar();

        sendMessage = (ImageView) findViewById(R.id.group_chat_send_message);

        inputMessageEdT = (EditText) findViewById(R.id.input_group_chat);

        scrollView = (ScrollView) findViewById(R.id.group_scroll_view);

        displayMessage = (AppCompatTextView) findViewById(R.id.group_chat_text_display);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(currentGroupChatName);
    }
}