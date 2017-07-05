package com.example.robert.klausurenhub;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

/**
 * Created by Rober on 05.07.2017.
 */

@EActivity(R.layout.attribute_pdf)
public class Attribute_PDF extends AppCompatActivity {

    @ViewById
    AutoCompleteTextView autoCompleteTextView_schools;

    @ViewById
    AutoCompleteTextView autoCompleteTextView_teachers;

    @ViewById
    AutoCompleteTextView autoCompleteTextView_courses;

    @ViewById
    AutoCompleteTextView autoCompleteTextView_subjects;

    @ViewById
    Spinner spinner_degrees;

    @ViewById
    Spinner spinner_semesters;

    @ViewById
    AutoCompleteTextView autoCompleteTextView_years;


    @AfterViews
    public void afterViews() {


        this.createArrayAdapter(AvailableAttributes.availableschools, this.autoCompleteTextView_schools);
        this.createArrayAdapter(AvailableAttributes.availableteachers, this.autoCompleteTextView_teachers);
        this.createArrayAdapter(AvailableAttributes.availablecourses, this.autoCompleteTextView_courses);
        this.createArrayAdapter(AvailableAttributes.availablesubjects, this.autoCompleteTextView_subjects);
        this.createArrayAdapter(AvailableAttributes.availableyears, this.autoCompleteTextView_years);

        this.createArrayAdapterForSpinner(AvailableAttributes.availabledegrees, this.spinner_degrees);
        this.createArrayAdapterForSpinner(AvailableAttributes.availablesemesters, this.spinner_semesters);





    }

    /*public void spinnerAdapter(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item) {

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }

                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you dont display last item. It is used as hint.
            }

        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.add("Item 1");
        adapter.add("Item 2");
        adapter.add("Hint to be displayed");

        spinner_degrees.setAdapter(adapter);
        spinner_degrees.setSelection(adapter.getCount()); //display hint
    }

    */

    public void createArrayAdapterForSpinner(ArrayList<String> arrayList, Spinner spinner){
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner.setAdapter(spinnerAdapter);
    }

    public void createArrayAdapter(ArrayList<String> arrayList, AutoCompleteTextView autoCompleteTextView){
        ArrayAdapter<String> autocompletetextAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, arrayList);

        autoCompleteTextView.setAdapter(autocompletetextAdapter);
    }


}
