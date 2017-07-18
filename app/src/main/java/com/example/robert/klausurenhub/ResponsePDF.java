package com.example.robert.klausurenhub;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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

import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Rober on 04.07.2017.
 */

public class ResponsePDF {


    private String schoolVal;
    private String courseVal;
    private String degreeVal;
    private String semesterVal;
    private String subjectVal;
    private String teacherVal;
    private String yearVal;


    private Boolean schoolexist;
    private Boolean teacherexist;
    private Boolean courseexist;
    private Boolean subjectexist;
    private Boolean yearexist;


    private String clauseName;
    private String uploaderName;
    private final String updateTabledatabaseURL = "http://klausurenhub.bplaced.net/androidapp/updatetable.php";
    private final String insertClauseIntoDatabaseURL = "http://klausurenhub.bplaced.net/androidapp/insertclause.php";

    private int clauseSchoolID;
    private int clauseTeacherID;
    private int clauseCourseID;
    private int clauseSubjectID;
    private int clauseYearID;
    private int clauseDegreeID;
    private int clauseSemesterID;

    //Wichtige Variablen für Asynchronität
    private int numberOfQueryExecutions = 0;
    private int numberOfExecutedQueries = 0;

    private Activity responsePDF;






    public ResponsePDF(String clauseName, String schoolVal, String courseVal, String degreeVal, String semesterVal, String subjectVal, String teacherVal, String yearVal, Activity responsePDF) throws JSONException {

        this.clauseName = clauseName;
        this.schoolVal = schoolVal;
        this.courseVal = courseVal;
        this.degreeVal = degreeVal;
        this.semesterVal = semesterVal;
        this.subjectVal = subjectVal;
        this.teacherVal = teacherVal;
        this.yearVal = yearVal;

        this.responsePDF = responsePDF;

        if (AvailableAttributes.username != null) {
            this.uploaderName = AvailableAttributes.username;
        } else if (AvailableAttributes.username == null) {
            this.uploaderName = "tempuser";
        }


        if (this.executeExistingcheck()) {

            this.clauseName += ".pdf";

            this.distinguishBetweenDatalogic();

            this.uploadPDFHandler();

        } else {
            Toast.makeText(getApplicationContext(), "Fehler beim setzten der Attribute", Toast.LENGTH_SHORT).show();
        }


    }

    private void uploadPDFHandler() {
        if (checkPDFAttributesAreSet()) {
            this.insertClauseIntoDatabase();
        } else {
            Toast.makeText(getApplicationContext(), "Fehler beim übertragen der Attribute", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadPDF() throws IOException {

        new UploadTask(this.responsePDF).execute();
    }






    private void insertClauseIntoDatabase() {


        if(AvailableAttributes.renameInternalPDF(this.clauseName)){
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

            JSONObject paramobject = new JSONObject();
            try {

                paramobject.put("school", clauseSchoolID);
                paramobject.put("course", clauseCourseID);
                paramobject.put("degree", clauseDegreeID);
                paramobject.put("semester", clauseSemesterID);
                paramobject.put("subject", clauseSubjectID);
                paramobject.put("teacher", clauseTeacherID);
                paramobject.put("year", clauseYearID);
                paramobject.put("name", clauseName);
                paramobject.put("uploader", uploaderName);


            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.e("paramobject", paramobject.toString());

            JsonObjectRequest insertClauseIntoDatabaseRequest = new JsonObjectRequest(com.android.volley.Request.Method.POST, this.insertClauseIntoDatabaseURL, paramobject, new com.android.volley.Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        if (response.getBoolean("answer")) {
                            uploadPDF();
                        } else {
                            Toast.makeText(getApplicationContext(), "Fehler bei Datenübertragung zur Datenbank", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new com.android.volley.Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Fehler", error.toString());
                    Toast.makeText(getApplicationContext(), "Fehler bei Datenübertragung zur Datenbank", Toast.LENGTH_SHORT).show();
                }
            }) {

                /**
                 * Passing some request headers
                 * */
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    return headers;
                }


            };
            requestQueue.add(insertClauseIntoDatabaseRequest);
        }else{
            Toast.makeText(getApplicationContext(), "Fehler beim setzen des Namens der PDF", Toast.LENGTH_SHORT).show();
        }


    }

    private void handleAsyncFunctionallity() {
        this.numberOfExecutedQueries++;

        if (this.numberOfQueryExecutions == this.numberOfExecutedQueries) {
            this.uploadPDFHandler();
            this.numberOfQueryExecutions = 0;
        }
    }

    public Boolean checkPDFAttributesAreSet() {
        if (!(this.clauseSchoolID == 0) &&
                !(this.clauseTeacherID == 0) &&
                !(this.clauseCourseID == 0) &&
                !(this.clauseSubjectID == 0) &&
                !(this.clauseYearID == 0) &&
                !(this.clauseDegreeID == 0) &&
                !(this.clauseSemesterID == 0) &&
                (!(this.clauseName.isEmpty()) && !(this.clauseName == null)) &&
                (!(this.uploaderName.isEmpty() && !(this.uploaderName == null))) &&
                (!(AvailableAttributes.internalPdfPath.isEmpty()) && !(AvailableAttributes.internalPdfPath == null))
                ) {
            return true;
        } else {
            return false;
        }
    }

    private void distinguishBetweenDatalogic() throws JSONException {
        /*Log.e("school",this.schoolexist.toString() );
        Log.e("teacher", this.teacherexist.toString());
        Log.e("course", this.courseexist.toString());
        Log.e("subject", this.subjectexist.toString());
        Log.e("year", this.yearexist.toString());
        Log.e("clausename", this.clauseName);
        Log.e("username", this.uploaderName);*/

        //Schoolcheck
        if (!this.schoolexist) {
            this.numberOfQueryExecutions += 1;
            updateDatabaseTable(this.schoolVal, "schools", "schoolName", "schoolID", new VolleyCallback() {
                @Override
                public void getNewID(JSONObject ids) throws JSONException {

                    AvailableAttributes.availableschools.add(schoolVal);
                    JSONObject jsonObjectschool = new JSONObject();
                    jsonObjectschool.put("schoolID", ids.getString("schoolID"));
                    jsonObjectschool.put("schoolName", schoolVal);
                    AvailableAttributes.schools.put(jsonObjectschool);
                    clauseSchoolID = Integer.parseInt(ids.getString("schoolID"));
                    handleAsyncFunctionallity();

                }
            });
        } else if (this.schoolexist) {
            for (int i = 0; i < AvailableAttributes.availableschools.size(); i++) {
                if (this.schoolVal.equals(AvailableAttributes.schools.getJSONObject(i).getString("schoolName").toString())) {
                    this.clauseSchoolID = Integer.parseInt(AvailableAttributes.schools.getJSONObject(i).getString("schoolID").toString());
                }
            }
        }

        //Teachercheck
        if (!this.teacherexist) {
            this.numberOfQueryExecutions += 1;
            updateDatabaseTable(this.teacherVal, "teachers", "teacherName", "teacherID", new VolleyCallback() {
                @Override
                public void getNewID(JSONObject ids) throws JSONException {

                    AvailableAttributes.availableteachers.add(teacherVal);
                    JSONObject jsonObjectteacher = new JSONObject();
                    jsonObjectteacher.put("teacherID", ids.getString("teacherID"));
                    jsonObjectteacher.put("teacherName", teacherVal);
                    AvailableAttributes.teachers.put(jsonObjectteacher);
                    clauseTeacherID = Integer.parseInt(ids.getString("teacherID"));
                    handleAsyncFunctionallity();

                }
            });
        } else if (this.teacherexist) {
            for (int i = 0; i < AvailableAttributes.availableteachers.size(); i++) {
                if (this.teacherVal.equals(AvailableAttributes.teachers.getJSONObject(i).getString("teacherName").toString())) {
                    this.clauseTeacherID = Integer.parseInt(AvailableAttributes.teachers.getJSONObject(i).getString("teacherID").toString());
                }
            }
        }

        //Coursecheck
        if (!this.courseexist) {
            this.numberOfQueryExecutions += 1;
            updateDatabaseTable(this.courseVal, "courses", "courseName", "courseID", new VolleyCallback() {
                @Override
                public void getNewID(JSONObject ids) throws JSONException {

                    AvailableAttributes.availablecourses.add(courseVal);
                    JSONObject jsonObjectcourse = new JSONObject();
                    jsonObjectcourse.put("courseID", ids.getString("courseID"));
                    jsonObjectcourse.put("courseName", courseVal);
                    AvailableAttributes.courses.put(jsonObjectcourse);
                    clauseCourseID = Integer.parseInt(ids.getString("courseID"));
                    handleAsyncFunctionallity();


                }
            });
        } else if (this.courseexist) {
            for (int i = 0; i < AvailableAttributes.availablecourses.size(); i++) {
                if (this.courseVal.equals(AvailableAttributes.courses.getJSONObject(i).getString("courseName").toString())) {
                    this.clauseCourseID = Integer.parseInt(AvailableAttributes.courses.getJSONObject(i).getString("courseID").toString());
                }
            }
        }

        //Subjectcheck
        if (!this.subjectexist) {
            this.numberOfQueryExecutions += 1;
            updateDatabaseTable(this.subjectVal, "subjects", "subjectName", "subjectID", new VolleyCallback() {
                @Override
                public void getNewID(JSONObject ids) throws JSONException {

                    AvailableAttributes.availablesubjects.add(subjectVal);
                    JSONObject jsonObjectsubject = new JSONObject();
                    jsonObjectsubject.put("subjectID", ids.getString("subjectID"));
                    jsonObjectsubject.put("subjectName", subjectVal);
                    AvailableAttributes.subjects.put(jsonObjectsubject);
                    clauseSubjectID = Integer.parseInt(ids.getString("subjectID"));
                    handleAsyncFunctionallity();


                }
            });
        } else if (this.subjectexist) {
            for (int i = 0; i < AvailableAttributes.availablesubjects.size(); i++) {
                if (this.subjectVal.equals(AvailableAttributes.subjects.getJSONObject(i).getString("subjectName").toString())) {
                    this.clauseSubjectID = Integer.parseInt(AvailableAttributes.subjects.getJSONObject(i).getString("subjectID").toString());
                }
            }
        }

        //Yearcheck
        if (!this.yearexist) {
            this.numberOfQueryExecutions += 1;
            updateDatabaseTable(this.yearVal, "years", "yearName", "yearID", new VolleyCallback() {
                @Override
                public void getNewID(JSONObject ids) throws JSONException {

                    AvailableAttributes.availableyears.add(yearVal);
                    JSONObject jsonObjectyear = new JSONObject();
                    jsonObjectyear.put("yearID", ids.getString("yearID"));
                    jsonObjectyear.put("yearName", yearVal);
                    AvailableAttributes.years.put(jsonObjectyear);
                    clauseYearID = Integer.parseInt(ids.getString("yearID"));
                    handleAsyncFunctionallity();


                }
            });
        } else if (this.yearexist) {
            for (int i = 0; i < AvailableAttributes.availableyears.size(); i++) {
                if (this.yearVal.equals(AvailableAttributes.years.getJSONObject(i).getString("yearName").toString())) {
                    this.clauseYearID = Integer.parseInt(AvailableAttributes.years.getJSONObject(i).getString("yearID").toString());
                }
            }
        }

        //get DegreeID
        for (int i = 0; i < AvailableAttributes.availabledegrees.size(); i++) {
            if (this.degreeVal.equals(AvailableAttributes.degrees.getJSONObject(i).getString("degreeName").toString())) {
                this.clauseDegreeID = Integer.parseInt(AvailableAttributes.degrees.getJSONObject(i).getString("degreeID").toString());
            }
        }

        //get SemesterID
        for (int i = 0; i < AvailableAttributes.availablesemesters.size(); i++) {
            if (this.semesterVal.equals(AvailableAttributes.semesters.getJSONObject(i).getString("semesterName").toString())) {
                this.clauseSemesterID = Integer.parseInt(AvailableAttributes.semesters.getJSONObject(i).getString("semesterID").toString());
            }
        }


    }

    private void updateDatabaseTable(final String val, final String tablename, final String columname, final String idname, final VolleyCallback callback) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, this.updateTabledatabaseURL, new Response.Listener<String>() {
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
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("rowvalue", val);
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
        if (this.checkOnValErrors()) {
            this.schoolexist = this.checkIfValExist(this.schoolVal, "school");
            this.teacherexist = this.checkIfValExist(this.teacherVal, "teacher");
            this.courseexist = this.checkIfValExist(this.courseVal, "course");
            this.subjectexist = this.checkIfValExist(this.subjectVal, "subject");
            this.yearexist = this.checkIfValExist(this.yearVal, "year");
            checkError = true;
        } else if (!this.checkOnValErrors()) {
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
        } else {
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


class UploadTask extends AsyncTask<String, Void, Boolean>{

    Activity responsePDF;

    public UploadTask(Activity responsePDF){
        this.responsePDF = responsePDF;
    }


    @Override
    protected void onPostExecute(Boolean result){

        if(result){
            Intent intent = new Intent(this.responsePDF, UploadComplete_.class);
            this.responsePDF.startActivity(intent);
        }else{
            Toast.makeText(this.responsePDF.getApplicationContext(), "Fehler beim Hochladen", Toast.LENGTH_SHORT).show();
        }


    }




    @Override
    protected Boolean doInBackground(String... params) {

        Boolean resultofupload = false;
        try  {
            FTPClient ftpClient = new FTPClient();


            ftpClient.connect(InetAddress.getByName("klausurenhub.bplaced.net"));
            ftpClient.login("klausurenhub", "kgbhui1992!");
            ftpClient.changeWorkingDirectory("altklausuren/");

            File file = new File(AvailableAttributes.internalPdfPath);

            if (ftpClient.getReplyString().contains("250")) {
                ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
                BufferedInputStream buffIn = null;
                buffIn = new BufferedInputStream(new FileInputStream(AvailableAttributes.internalPdfPath));
                ftpClient.enterLocalPassiveMode();



                boolean result = ftpClient.storeFile(file.getName(), buffIn);
                resultofupload = result;
                buffIn.close();
                ftpClient.logout();
                ftpClient.disconnect();






            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultofupload;
    }

}



