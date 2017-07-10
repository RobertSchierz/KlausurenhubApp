package com.example.robert.klausurenhub;

import android.util.Log;

import java.util.ArrayList;

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

    private Boolean schoolexist;
    private Boolean teacherexist;
    private Boolean courseexist;
    private Boolean subjectexist;
    private Boolean yearexist;

    public ResponsePDF(String clauseName, String schoolVal, String courseVal, String degreeVal, String semesterVal, String subjectVal, String teacherVal, String yearVal) {

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

        this.executeExistingcheck();
        this.distinguishBetweenDatalogic();

    }

    private void distinguishBetweenDatalogic(){
        Log.e("school",this.schoolexist.toString() );
        Log.e("teacher", this.teacherexist.toString());
        Log.e("course", this.courseexist.toString());
        Log.e("subject", this.subjectexist.toString());
        Log.e("year", this.yearexist.toString());

    }


    private void executeExistingcheck() {

        if(this.checkOnValErrors()){
            this.schoolexist = this.checkIfValExist(this.schoolVal, "school");
            this.teacherexist = this.checkIfValExist(this.teacherVal, "teacher");
            this.courseexist = this.checkIfValExist(this.courseVal, "course");
            this.subjectexist = this.checkIfValExist(this.subjectVal, "subject");
            this.yearexist = this.checkIfValExist(this.yearVal, "year");

        }else if(!this.checkOnValErrors()){
            Log.e("ResponsePDF fail", "Ein PDF Attribut ist nicht gesetzt!");
        }

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

}
