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
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Map;

@EActivity(R.layout.activity_facebooklogin)
public class Facebooklogin extends AppCompatActivity {


    @ViewById
    Button fb_login_btn;

    @ViewById
    TextView facebook_loginstatus;

    @ViewById
    TextView facebook_name;

    @ViewById
    TextView facebook_email;

    @ViewById
    ImageView facebook_image;



    AccessToken accessToken;

    private CallbackManager callbackManager;

    private boolean isLoggedin = false;

    @AfterViews
    public void afterViews() {


        FacebookSdk.sdkInitialize(getApplicationContext());
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);


        callbackManager = CallbackManager.Factory.create();
        this.processFacebookLogin();


        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                    isLoggedin = false;
                    facebook_loginstatus.setText("ID");
                    facebook_email.setText("Name");
                    facebook_name.setText("Email");
                    facebook_image.setImageResource(R.color.com_facebook_blue);
                }
            }
        };

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {


                        if(jsonObject != null){
                            try {
                                String facebookId = jsonObject.getString("id");
                                String email = jsonObject.getString("email");
                                String name = jsonObject.getString("name");
                                String facebookImage = "https://graph.facebook.com/" + facebookId + "/picture?type=large";

                                facebook_loginstatus.setText(facebookId);
                                facebook_email.setText(email);
                                facebook_name.setText(name);

                                Glide.with(getApplicationContext()).load(facebookImage).into(facebook_image);

                                fb_login_btn.setText("Ausloggen");
                                isLoggedin = true;

                                AvailableAttributes.username = name;


                                startMainActivity();
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,cover,email");
        request.setParameters(parameters);
        request.executeAsync();


    }


    private void processFacebookLogin() {




        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        String userDatil = response.getRawResponse();
                        try {
                            JSONObject jsonObject = new JSONObject(userDatil);
                            Log.e("JsonObject: ", jsonObject.toString());


                            String facebookId = jsonObject.getString("id");
                            String email = jsonObject.getString("email");
                            String name = jsonObject.getString("name");
                            String facebookImage = "https://graph.facebook.com/" + facebookId + "/picture?type=large";

                            facebook_loginstatus.setText(facebookId);
                            facebook_email.setText(email);
                            facebook_name.setText(name);

                            Glide.with(getApplicationContext()).load(facebookImage).into(facebook_image);



                            AvailableAttributes.username = name;

                            startMainActivity();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name, email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();

                isLoggedin = true;



            }

            @Override
            public void onCancel() {
                Log.e("Login: ", "Canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("Login: ", "Networkerror");
            }
        });




    }

    @Click
    public void fb_login_btnClicked() {
        this.handleButtonText();
        if (!(this.isLoggedin)) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

        } else if (this.isLoggedin) {
            LoginManager.getInstance().logOut();
        }

    }

    private void handleButtonText() {
        if (this.isLoggedin) {
            this.fb_login_btn.setText("Weiter mit Facebook");
        } else if (!(this.isLoggedin)) {
            this.fb_login_btn.setText("Ausloggen");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    public void startMainActivity() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                startActivity(new Intent(Facebooklogin.this, MainActivity_.class));
            }
        }, 2000);

    }

    private String searchFacebookVal(SharedPreferences sharedPreferences, String value) {

        Map<String, ?> keys = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            return entry.getValue().toString();
        }
        return null;

    }

    private Boolean checkPrefs() {
        SharedPreferences prefs = getSharedPreferences("Facebookdata", Context.MODE_PRIVATE);
        String facebookval = searchFacebookVal(prefs, "facebookname");

        if (facebookval != null) {
            return true;
        } else {
            return false;
        }
    }


}
