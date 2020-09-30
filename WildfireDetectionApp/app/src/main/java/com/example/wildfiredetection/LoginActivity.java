package com.example.wildfiredetection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnLogin;
    Button btnRegister;
    TwitterLoginButton btnTwitter;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText6);
        password = findViewById(R.id.editText8);
        btnLogin = findViewById(R.id.button);
        btnRegister = findViewById(R.id.button2);
        btnTwitter = (TwitterLoginButton) findViewById(R.id.button3);

        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            //Gets user login details from FirebaseAuthentication database
            //and compare them to the user input
            public void onAuthStateChanged(@NonNull com.google.firebase.auth.FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //If successful
                    Toast.makeText(LoginActivity.this, "Logged in successfully",
                            Toast.LENGTH_SHORT).show();
                    Intent mapPg = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(mapPg);
                }
                else {
                    //Display text
                    Toast.makeText(LoginActivity.this, "Incorrect username or password",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if (email.isEmpty()) {
                    emailId.setError("Please enter your email address");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();

                }
                else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Cannot leave blank fields!",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!(email.isEmpty() && pwd.isEmpty())) {
                    //Checks if the login task is completed or not
                    firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(
                            LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Incorrect email or password",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Intent toMap = new Intent(LoginActivity.this, MapActivity.class);
                                        startActivity(toMap);
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(LoginActivity.this, "Check internet connection and try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(register);
            }
        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent twitter = new Intent(LoginActivity.this, TwitterActivity.class);
                startActivity(twitter);
            }
        });
    }
}