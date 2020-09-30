package com.example.wildfiredetection;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class TwitterActivity extends AppCompatActivity {

    TwitterLoginButton btnLogin;
    FirebaseAuth firebaseAuth;
    FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Configure Twitter SDK
        TwitterAuthConfig authConfig = new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        TwitterConfig twitterConfig = new TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig).build();

        //Initialize Twitter
        Twitter.initialize(twitterConfig);
        setContentView(R.layout.activity_twitter);

        //Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.button);

        authStateListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Toast.makeText(TwitterActivity.this, "You're logged in", Toast.LENGTH_LONG).show();
                    Intent mapPg = new Intent(TwitterActivity.this, MapActivity.class);
                    startActivity(mapPg);

                    // startActivity(new Intent(TwitterActivity.this, MapActivity.class));
                }
            }
        };

        btnLogin.setCallback(new Callback<TwitterSession>(){
            @Override
            //If successful
            public void success(Result<TwitterSession> result){
                //TwitterSession for making API call
                TwitterSession session = TwitterCore.getInstance().getSessionManager()
                        .getActiveSession();

                //User will be prompted to authorize access to the app
                AuthCredential credential = TwitterAuthProvider.getCredential(session.getAuthToken().token,
                        session.getAuthToken().secret);

                //firebaseAuth listens if the user signed in with Twitter
                firebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(TwitterActivity.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(TwitterActivity.this, "Signed in Twitter successfully", Toast.LENGTH_LONG).show();
                                if (!task.isSuccessful()){
                                    Toast.makeText(TwitterActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }

            @Override
            public void failure(TwitterException exception){
                //Failure request
                Toast.makeText(TwitterActivity.this, "Authentication Failed",
                        Toast.LENGTH_LONG).show();
                if (TwitterCore.getInstance().getSessionManager().getActiveSession() == null){
                    btnLogin.setVisibility(View.VISIBLE);
                }
                else{
                    btnLogin.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Pass ActivityResult to btnLogin.
        btnLogin.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}

