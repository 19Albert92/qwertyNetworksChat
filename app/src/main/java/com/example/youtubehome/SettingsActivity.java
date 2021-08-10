package com.example.youtubehome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button buttonSaveInformation;
    private EditText userNameEdT, userNickEdT, userStatusEdT;
    private CircleImageView imageViewNew;

    private String currentUserId;
    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;

    private StorageReference UserProfileImageRef;
    private ProgressDialog loadBar;

    public static final int REQUEST_CODE_FOR_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        init();
    }

    private void init() {
        buttonSaveInformation = (Button) findViewById(R.id.button_save_for_settings);
        clickSaveInformation();

        userNameEdT = (EditText) findViewById(R.id.set_user_name);
        userNickEdT = (EditText) findViewById(R.id.set_user_nick);
        userStatusEdT = (EditText) findViewById(R.id.save_user_information);

        imageViewNew = (CircleImageView) findViewById(R.id.profile_image);
        setImage_user();

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        rootRef = FirebaseDatabase.getInstance().getReference();

        userNameEdT.setVisibility(View.INVISIBLE);

        retrieveUserInformation();

        UserProfileImageRef = FirebaseStorage.getInstance().getReference().child("ProfileImages");
        loadBar = new ProgressDialog(this);
    }

    private void clickSaveInformation() {
        buttonSaveInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String setName = userNameEdT.getText().toString();
                String setNick = userNickEdT.getText().toString();
                String setStatus = userStatusEdT.getText().toString();

                if (TextUtils.isEmpty(setName) || TextUtils.isEmpty(setNick)) {
                    Toast.makeText(SettingsActivity.this, "Заполните пустые поля", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(setStatus)) {
                    Toast.makeText(SettingsActivity.this, "Введите статус", Toast.LENGTH_SHORT).show();
                } else {
                    HashMap<String, Object> profileMap = new HashMap<>();
                    profileMap.put("uid", currentUserId);
                    profileMap.put("name", setName);
                    profileMap.put("nick", setNick);
                    profileMap.put("status", setStatus);

                    rootRef.child("Users").child(currentUserId).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(SettingsActivity.this, "Toast информация обнавлена", Toast.LENGTH_LONG).show();
                                retrieveUserInformation();
                                Intent main_intent = new Intent(SettingsActivity.this, MainActivity.class);
                                startActivity(main_intent);
                            } else {
                                String message = task.getException().toString();
                                Log.d("myLog", "Toast Произошла ошибка: " + message);
                            }
                        }
                    });
                }
            }
        });
    }

    private void retrieveUserInformation() {
        rootRef.child("Users").child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("name") && snapshot.hasChild("image")) {
                    String retrieveImage = snapshot.child("image").getValue().toString();
                    String retrieveUserName = snapshot.child("name").getValue().toString();
                    String retrieveUserNick = snapshot.child("nick").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();

                    userNameEdT.setText(retrieveUserName);
                    userNickEdT.setText(retrieveUserNick);
                    userStatusEdT.setText(retrieveUserStatus);
                    Picasso.get().load(retrieveImage).into(imageViewNew);

                } else if (snapshot.exists() && snapshot.hasChild("name")) {
                    String retrieveUserName = snapshot.child("name").getValue().toString();
                    String retrieveUserNick = snapshot.child("nick").getValue().toString();
                    String retrieveUserStatus = snapshot.child("status").getValue().toString();

                    userNameEdT.setText(retrieveUserName);
                    userNickEdT.setText(retrieveUserNick);
                    userStatusEdT.setText(retrieveUserStatus);

                } else {
                    userNameEdT.setVisibility(View.VISIBLE);
                    Toast.makeText(SettingsActivity.this, "Введите свое имя", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void setImage_user() {
        imageViewNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent(Intent.ACTION_GET_CONTENT);
                gallery_intent.setType("image/*");
                startActivityForResult(gallery_intent, REQUEST_CODE_FOR_IMAGE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FOR_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                showProgressDialog("Загрузка фотографий", "Пожалуйста подождите", false);

                Uri resultUri = result.getUriContent();

                StorageReference filePath = UserProfileImageRef.child(currentUserId + ".jpg");

                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                final String downLoadUrl = uri.toString();
                                rootRef.child("Users").child(currentUserId).child("imageUser").setValue(downLoadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(SettingsActivity.this, "Фотография успешно загруженна", Toast.LENGTH_SHORT).show();
                                            loadBar.dismiss();
                                        } else {
                                            String message = task.getException().toString();
                                            Toast.makeText(SettingsActivity.this, "Произошла ошибка " + message, Toast.LENGTH_LONG).show();
                                            loadBar.dismiss();
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        }
    }

    private void showProgressDialog(String title, String message, boolean setCancel) {
        loadBar.setTitle(title);
        loadBar.setMessage(message);
        loadBar.setCanceledOnTouchOutside(setCancel);
        loadBar.show();
    }
}