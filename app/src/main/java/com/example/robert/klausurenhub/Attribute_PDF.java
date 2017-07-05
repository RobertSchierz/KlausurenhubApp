package com.example.robert.klausurenhub;

import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Rober on 05.07.2017.
 */

@EActivity(R.layout.attribute_pdf)
public class Attribute_PDF extends AppCompatActivity {

    @ViewById
    AutoCompleteTextView autoCompleteTextView_schools;

    @AfterViews
    public void afterViews() {


        ArrayAdapter<String> autocompletetextAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, AvailableAttributes.availableschools);

        autoCompleteTextView_schools.setAdapter(autocompletetextAdapter);

    }

}
