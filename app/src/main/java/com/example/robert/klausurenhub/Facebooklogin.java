package com.example.robert.klausurenhub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

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

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {

                Facebooklogin.this.requestUserProfile(loginResult);

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

    public void requestUserProfile(LoginResult loginResult){
        GraphRequest.newMeRequest(
                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject me, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                        } else {
                            try {
                                String email = response.getJSONObject().get("email").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String id = me.optString("id");


                           // Log.e("Result", me.toString());
                            SharedPreferences prefs = getSharedPreferences("Facebookdata", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("facebookname", me.optString("name"));
                            editor.commit();
                        }
                    }
                }).executeAsync();
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
