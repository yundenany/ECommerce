package com.example.ecommerce.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecommerce.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, email, phone, password;
    FirebaseAuth auth;
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String role="user";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //getSupportActionBar().hide();
        auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
            finish();
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phonenumber);

        sharedPreferences = getSharedPreferences("onBoardingScreen", MODE_PRIVATE);

        boolean isFirstTime = sharedPreferences.getBoolean("firstTime", true);

        if(isFirstTime){
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();

            Intent intent = new Intent(RegistrationActivity.this, OnBoardingActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void signup(View view){

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPassword = password.getText().toString();
        String userPhone = phone.getText().toString();

        if(TextUtils.isEmpty(userName)){

            Toast.makeText(this, "Enter Name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(userEmail)){

            Toast.makeText(this, "Enter Email Address!", Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(userPhone)){

            Toast.makeText(this, "Enter Phone Number!", Toast.LENGTH_SHORT).show();
            return;

        }

        if(TextUtils.isEmpty(userPassword)){

            Toast.makeText(this, "Enter Password!", Toast.LENGTH_SHORT).show();
            return;

        }


        if(userPassword.length() < 8){
            Toast.makeText(this, "Password is too short. Enter minimum 8 characters!", Toast.LENGTH_SHORT).show();
            return;

        }

//        auth.createUserWithEmailAndPassword(userEmail, userPassword)
//                        .addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
//                            @Override
//                            public void onComplete(@NonNull Task<AuthResult> task) {
//                                 if(task.isSuccessful()){
//
//                                     //Phan Bai Da Lam
//                                     Toast.makeText(RegistrationActivity.this, "Successfully Register ", Toast.LENGTH_SHORT).show();
//                                     startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
//                                 }else{
//                                     Toast.makeText(RegistrationActivity.this, "Registration Faile"+ task.getException(), Toast.LENGTH_SHORT).show();
//                                 }
//                            }
//                        });

        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String uid = authResult.getUser().getUid();

                        // Create a document for the user in Firestore
                        Map<String, Object> userMap = new HashMap<>();
                        userMap.put("email", userEmail);
                        userMap.put("name", userName);
                        userMap.put("phonenumber", userPhone);
                        userMap.put("password", userPassword);
                        // Add other user data here...

                        db.collection("Users").document(uid).set(userMap)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // User registered successfully and data saved
                                        // Navigate to your app or show success message

                                        Toast.makeText(RegistrationActivity.this, "Successfully Register ", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Handle error saving user data


                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle registration error
                    }
                });

    }

    public void signin(View view){
        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));

    }
}