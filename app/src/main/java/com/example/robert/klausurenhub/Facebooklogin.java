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

    //CallbackManager callbackManager;


    AccessToken accessToken;

    private CallbackManager callbackManager;

    private boolean isLoggedin = false;

    @AfterViews
    public void afterViews() {


        FacebookSdk.sdkInitialize(getApplicationContext());


        callbackManager = CallbackManager.Factory.create();
        processFacebookLogin();


        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                if (currentAccessToken == null) {
                   Log.v("AMK", "logout");
                }
            }
        };

        /*
        AccessToken accessToken = AccessToken.getCurrentAccessToken();

        if (accessToken != null) {
            startMainActivity();
        } else {

            FacebookSdk.sdkInitialize(getApplicationContext());
            callbackManager = CallbackManager.Factory.create();

            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));

            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    SharedPreferences prefs = getSharedPreferences("Facebookdata", Context.MODE_PRIVATE);
                    String facebookval = searchFacebookVal(prefs, "facebookname");

                    if ((facebookval.isEmpty())) {
                        Facebooklogin.this.requestUserProfile(loginResult);
                    }


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

            //  }

        }
*/
    }



    private void processFacebookLogin() {

        if (accessToken != null) {
            accessToken = com.facebook.AccessToken.getCurrentAccessToken();

            //LoginManager.getInstance().logOut();
        }
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name, email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();


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

        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email"));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    /*

    private String searchFacebookVal(SharedPreferences sharedPreferences, String value) {

        Map<String, ?> keys = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            return entry.getValue().toString();
        }
        return null;

    }

    public void requestUserProfile(LoginResult loginResult) {
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

    public void startMainActivity() {
        startActivity(new Intent(Facebooklogin.this, MainActivity_.class));
        this.facebook_loginstatus.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

*/
}
