package com.example.robert.klausurenhub;

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

    public ResponsePDF(String clauseName,  String schoolVal, String courseVal, String degreeVal, String semesterVal, String subjectVal, String teacherVal, String yearVal) {

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

    }

    public boolean checkIfValExist(String inputval, String source){
        return true;
    }

}
