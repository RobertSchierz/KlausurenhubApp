package com.example.robert.klausurenhub;

import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Rober on 04.07.2017.
 */

public class ResponsePDF {

    private String clauseName;
    private String uploaderName;
    private String schoolVal;
    private String courseVal;
    private String degreeVal;
    private String semesterVal;
    private String subjectVal;
    private String teacherVal;
    private String yearVal;


    public final String databaseURL = "http://klausurenhub.bplaced.net/androidapp/updatetable.php";

    private Boolean schoolexist;
    private Boolean teacherexist;
    private Boolean courseexist;
    private Boolean subjectexist;
    private Boolean yearexist;

    private int clauseSchoolID;



    public ResponsePDF(String clauseName, String schoolVal, String courseVal, String degreeVal, String semesterVal, String subjectVal, String teacherVal, String yearVal) throws JSONException {

        this.clauseName = clauseName;
        this.schoolVal = schoolVal;
        this.courseVal = courseVal;
        this.degreeVal = degreeVal;
        this.semesterVal = semesterVal;
        this.subjectVal = subjectVal;
        this.teacherVal = teacherVal;
        this.yearVal = yearVal;

        if (AvailableAttributes.username != null) {
            this.uploaderName = AvailableAttributes.username;
        } else if (AvailableAttributes.username == null) {
            this.uploaderName = "tempuser";
        }


        if(this.executeExistingcheck()){

            this.clauseName += ".pdf";

            this.distinguishBetweenDatalogic();
        }else{
            Toast.makeText(getApplicationContext(), "Fehler beim setzten der Attribute", Toast.LENGTH_SHORT).show();
        }


    }

    private void distinguishBetweenDatalogic() throws JSONException {
        Log.e("school",this.schoolexist.toString() );
        Log.e("teacher", this.teacherexist.toString());
        Log.e("course", this.courseexist.toString());
        Log.e("subject", this.subjectexist.toString());
        Log.e("year", this.yearexist.toString());
        Log.e("clausename", this.clauseName);
        Log.e("username", this.uploaderName);


        if(!this.schoolexist){
            updateDatabaseTable(this.schoolVal, "schools", "schoolName", "schoolID", new VolleyCallback() {
                @Override
                public void getNewID(JSONObject ids) throws JSONException {

                    AvailableAttributes.availableschools.add(schoolVal);
                    JSONObject jsonObjectschool = new JSONObject();
                    jsonObjectschool.put("schoolID", ids.getString("schoolID"));
                    jsonObjectschool.put("schoolName", schoolVal);
                    AvailableAttributes.schools.put(jsonObjectschool);
                    clauseSchoolID = Integer.parseInt(ids.getString("schoolID"));

                }
            });
        }else if(this.schoolexist){
            for(int i = 0; i <AvailableAttributes.availableschools.size(); i++){
                if(this.schoolVal.equals(AvailableAttributes.schools.getJSONObject(i).getString("schoolName").toString())){
                    this.clauseSchoolID = Integer.parseInt(AvailableAttributes.schools.getJSONObject(i).getString("schoolID").toString());
                }
            }
        }

    }

    private void updateDatabaseTable(final String val, final String tablename, final String columname, final String idname, final VolleyCallback callback){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.databaseURL, new Response.Listener<String>(){
            @Override
            public void onResponse(String response) {
                try {
                    String newID;

                    JSONObject jsonObject = new JSONObject(response.toString());

                    callback.getNewID(jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError{
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("rowvalue", val );
                parameters.put("columnname", columname);
                parameters.put("tablename", tablename);
                parameters.put("id", idname);

                return parameters;
            }
        };
        requestQueue.add(stringRequest);

    }


    private Boolean executeExistingcheck() {

        Boolean checkError = false;
        if(this.checkOnValErrors()){
            this.schoolexist = this.checkIfValExist(this.schoolVal, "school");
            this.teacherexist = this.checkIfValExist(this.teacherVal, "teacher");
            this.courseexist = this.checkIfValExist(this.courseVal, "course");
            this.subjectexist = this.checkIfValExist(this.subjectVal, "subject");
            this.yearexist = this.checkIfValExist(this.yearVal, "year");
            checkError = true;
        }else if(!this.checkOnValErrors()){
            Log.e("ResponsePDF fail", "Ein PDF Attribut ist nicht gesetzt!");
            checkError = false;
        }

        return checkError;

    }

    private Boolean checkOnValErrors() {
        if (this.clauseName.equals("") || this.clauseName == null ||
                this.uploaderName.equals("") || this.uploaderName == null ||
                this.schoolVal.equals("") || this.schoolVal == null ||
                this.courseVal.equals("") || this.courseVal == null ||
                this.degreeVal.equals("") || this.degreeVal == null ||
                this.semesterVal.equals("") || this.semesterVal == null ||
                this.subjectVal.equals("") || this.subjectVal == null ||
                this.teacherVal.equals("") || this.teacherVal == null ||
                this.yearVal.equals("") || this.yearVal == null
                ) {
            return false;
        }else{
            return true;
        }
    }





    private boolean checkIfValExist(String inputval, String source) {

        Boolean responseBool = false;

        switch (source) {
            case "school":
                responseBool = checkIfValExistHelper(AvailableAttributes.availableschools, inputval);
                break;
            case "teacher":
                responseBool = checkIfValExistHelper(AvailableAttributes.availableteachers, inputval);
                break;
            case "course":
                responseBool = checkIfValExistHelper(AvailableAttributes.availablecourses, inputval);
                break;
            case "subject":
                responseBool = checkIfValExistHelper(AvailableAttributes.availablesubjects, inputval);
                break;
            case "year":
                responseBool = checkIfValExistHelper(AvailableAttributes.availableyears, inputval);
                break;
            default:
                responseBool = false;
                break;
        }

        return responseBool;
    }

    private boolean checkIfValExistHelper(ArrayList<String> source, String inputval) {
        Boolean responseBool = false;
        for (String element : source) {
            if (element.equals(inputval)) {
                responseBool = true;
            }
        }

        return responseBool;
    }


    public interface VolleyCallback {

        void getNewID(JSONObject ids) throws JSONException;

    }

}
