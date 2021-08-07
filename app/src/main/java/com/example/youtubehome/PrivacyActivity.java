package com.example.youtubehome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class PrivacyActivity extends AppCompatActivity {

    private WebView privacyWebView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        initView();
    }

    private void initView() {
        privacyWebView = (WebView) findViewById(R.id.webView);
        backButton = (Button) findViewById(R.id.button_for_exit);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        goWebViewToDocs();
    }

    private void goWebViewToDocs() {
        privacyWebView.loadUrl("https://docs.google.com/document/d/1dHhoNBivxOaNm9aXTA-Tvg3-f1z6lU924W32Hm98nao/edit");
    }

}