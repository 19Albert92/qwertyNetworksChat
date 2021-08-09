package com.example.youtubehome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private Button nextButton, verificationButton, registerButton;
    private TextView textHint;
    private EditText phoneInput, verificationCodeInput;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    public static final String TAG = "myLog";
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);

        nextButton = (Button) findViewById(R.id.login_next_button);
        verificationButton = (Button) findViewById(R.id.buttonToVerification);
        registerButton = (Button) findViewById(R.id.login_register_button);

        phoneInput = (EditText) findViewById(R.id.login_phone_input);
        verificationCodeInput = (EditText) findViewById(R.id.login_verification_input);

        textHint = (TextView) findViewById(R.id.hint);

        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = phoneInput.getText().toString();

                if (TextUtils.isEmpty(phoneNumber)) {
                    Toast.makeText(LoginActivity.this, "Заполните номер", Toast.LENGTH_SHORT).show();

                } else {
                    loadingBar.setTitle("Проверка номера");
                    loadingBar.setMessage("Пожалуйста подождите");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();


                    PhoneAuthOptions options =
                            PhoneAuthOptions.newBuilder(mAuth)
                                    .setPhoneNumber(phoneNumber)       // Phone number to verify
                                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                    .setActivity(LoginActivity.this)                 // Activity (for callback binding)
                                    .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                    .build();
                    PhoneAuthProvider.verifyPhoneNumber(options);
                }
            }
        });

        verificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String verificationCode = verificationCodeInput.getText().toString();
                if (TextUtils.isEmpty(verificationCode)) {
                    Toast.makeText(LoginActivity.this, "Введите код", Toast.LENGTH_SHORT).show();
                } else {
                    loadingBar.setTitle("Проверка кода");
                    loadingBar.setMessage("Пожалуйста подождите");
                    loadingBar.setCanceledOnTouchOutside(false);
                    loadingBar.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register_intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(register_intent);
            }
        });

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                loadingBar.dismiss();

                Toast.makeText(LoginActivity.this, "ошибка номера", Toast.LENGTH_SHORT).show();

                nextButton.setVisibility(View.VISIBLE);
                verificationButton.setVisibility(View.INVISIBLE);
                verificationCodeInput.setVisibility(View.INVISIBLE);
                phoneInput.setVisibility(View.VISIBLE);
                textHint.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                loadingBar.dismiss();

                nextButton.setVisibility(View.INVISIBLE);
                verificationButton.setVisibility(View.VISIBLE);
                verificationCodeInput.setVisibility(View.VISIBLE);
                phoneInput.setVisibility(View.INVISIBLE);
                textHint.setVisibility(View.INVISIBLE);

                Toast.makeText(LoginActivity.this, "код отправлен", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            // Update UI
                            loadingBar.dismiss();
                            Toast.makeText(LoginActivity.this, "Проверка прошла успешно", Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Ошибка проверки номера", Toast.LENGTH_SHORT).show();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            //}
                        }
                    }
                });
    }
}