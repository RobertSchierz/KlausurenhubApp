package com.example.robert.klausurenhub;

import android.content.Context;
import android.util.Log;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Rober on 04.07.2017.
 */

public class AvailableAttributes {

    public static JSONArray schools;
    public static JSONArray courses;
    public static JSONArray degrees;
    public static JSONArray semesters;
    public static JSONArray subjects;
    public static JSONArray teachers;
    public static JSONArray years;

    public static ArrayList<String> availableschools;
    public static ArrayList<String> availablecourses;
    public static ArrayList<String> availabledegrees;
    public static ArrayList<String> availablesemesters;
    public static ArrayList<String> availablesubjects;
    public static ArrayList<String> availableteachers;
    public static ArrayList<String> availableyears;


    public final String databaseURL = "http://klausurenhub.bplaced.net/androidapp/getavailableoptions.php";

    public RequestQueue requestQueue;


    public AvailableAttributes(Context mainContext) {

        requestQueue = Volley.newRequestQueue(mainContext);

    }


    public void getAvailableOptions(final VolleyCallback callback) {


        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.databaseURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject jsonObject = new JSONObject(response.toString());

                    callback.getSchools(jsonObject.getJSONArray("schools"));
                    callback.getCourses(jsonObject.getJSONArray("courses"));
                    callback.getDegrees(jsonObject.getJSONArray("degrees"));
                    callback.getSemesters(jsonObject.getJSONArray("semesters"));
                    callback.getSubjects(jsonObject.getJSONArray("subjects"));
                    callback.getTeachers(jsonObject.getJSONArray("teachers"));
                    callback.getYears(jsonObject.getJSONArray("years"));


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                String param = "";
                parameters.put("parameter", param);

                return parameters;
            }

        };

        requestQueue.add(stringRequest);
    }

    public interface VolleyCallback {
        void getSchools(JSONArray schools) throws JSONException;

        void getCourses(JSONArray courses) throws JSONException;

        void getDegrees(JSONArray degrees) throws JSONException;

        void getSemesters(JSONArray semesters) throws JSONException;

        void getSubjects(JSONArray subjects) throws JSONException;

        void getTeachers(JSONArray teachers) throws JSONException;

        void getYears(JSONArray years) throws JSONException;


    }

}


