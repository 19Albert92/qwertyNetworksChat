package com.example.youtubehome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    private Button loginButton, registerButton;
    private EditText emailEdit, passwordEdit;
    private TextView changeTv;

    private FirebaseAuth mAuth;

    private ProgressDialog loadBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initView();
        mAuth = FirebaseAuth.getInstance();
    }

    private void initView() {
        loginButton = (Button) findViewById(R.id.login_button);
        listenerForLoginButton();
        registerButton = (Button) findViewById(R.id.login_register);
        listenerForRegister();

        emailEdit = (EditText) findViewById(R.id.editEmailInput);
        passwordEdit = (EditText) findViewById(R.id.editPasswordInput);

        changeTv = (TextView) findViewById(R.id.change_textView);
        listenerForChangeTv();

        loadBar = new ProgressDialog(this);
    }

    private void listenerForChangeTv() {
        changeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTv.setVisibility(View.INVISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                registerButton.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void listenerForRegister() {
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "заролните поля", Toast.LENGTH_SHORT).show();
        } else {

            showProgressDialog("Создание аккаунта", "Пожалуйста подождите");

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent register_intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(register_intent);

                        Toast.makeText(RegisterActivity.this, "успешная регистрация", Toast.LENGTH_SHORT).show();
                        loadBar.dismiss();
                        finish();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "ошибка" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void listenerForLoginButton() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signAccount();
            }
        });
    }

    private void signAccount() {
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "заролните поля", Toast.LENGTH_SHORT).show();
        } else {
            showProgressDialog("Создание аккаунта", "Пожалуйста подождите");
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Intent register_intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(register_intent);

                        loadBar.dismiss();
                        Toast.makeText(RegisterActivity.this, "успешный вход", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "ошибка" + message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showProgressDialog(String title, String message) {
        loadBar.setTitle(title);
        loadBar.setMessage(message);
        loadBar.setCanceledOnTouchOutside(true);
        loadBar.show();
    }
}