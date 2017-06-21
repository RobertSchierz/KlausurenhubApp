package com.example.robert.klausurenhub;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_facebooklogin)
public class Facebooklogin extends AppCompatActivity {


    @ViewById
    Button fb_login_btn;

    @ViewById
    TextView facebook_loginstatus;

    CallbackManager callbackManager;

    @AfterViews
    public void afterViews() {

        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if(accessToken != null){
            startMainActivity();
        }else{

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                facebook_loginstatus.setText("Login erfolgreich " + loginResult.getAccessToken().getUserId());
                facebook_loginstatus.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.facebooklogin_success));
                // 2 Sekundenbreak
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                       startMainActivity();
                    }
                }, 2000);
                ;
            }

            @Override
            public void onCancel() {
                facebook_loginstatus.setText("Login abgebrochen");
            }

            @Override
            public void onError(FacebookException error) {

            }


        });

        }

    }

    public void startMainActivity(){
        startActivity(new Intent(Facebooklogin.this, MainActivity_.class));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }




}
