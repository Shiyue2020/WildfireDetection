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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnRegister;
    Button btnLogin;
    TwitterLoginButton btnTwitter;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance(); //Initialize
        emailId = findViewById(R.id.editText6);
        password = findViewById(R.id.editText8);
        btnRegister = findViewById(R.id.button);
        btnLogin = findViewById(R.id.button2);
        btnTwitter = findViewById(R.id.button3);

        btnRegister.setOnClickListener(new View.OnClickListener() {

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
                else if (pwd.length() < 10) {
                    Toast.makeText(MainActivity.this, "The minimum password length is 10", Toast.LENGTH_SHORT).show();

                }
                else if (email.isEmpty() && pwd.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Cannot leave blank fields!", Toast.LENGTH_SHORT).show();

                }
                else if (!(email.isEmpty() && pwd.isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(MainActivity.this,
                            new OnCompleteListener<com.google.firebase.auth.AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<com.google.firebase.auth.AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent mapPg = new Intent(MainActivity.this, MapActivity.class);
                                        startActivity(mapPg);

                                        //startActivity(new Intent(MainActivity.this, MapActivity.class));

                                    }
                                    else {
                                        Toast.makeText(MainActivity.this, "Sign up failed, please try again",
                                                Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(MainActivity.this, "Check internet connection and try again", Toast.LENGTH_SHORT).show();

                }
            }

        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent toLogin = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(toLogin);
            }


        });

        btnTwitter.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Intent twitter = new Intent(MainActivity.this, TwitterActivity.class);
                startActivity(twitter);
            }

        });
    }
}