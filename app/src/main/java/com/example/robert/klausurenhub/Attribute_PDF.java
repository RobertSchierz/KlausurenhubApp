package com.example.robert.klausurenhub;

import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;

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

    @ViewById
    EditText textinput_name;

    public Menu optionMenu;

    private ResponsePDF responsePDF;

    Animation slide_left;



    public boolean filled_name = false;
    public boolean filled_school = false;
    public boolean filled_teacher = false;
    public boolean filled_course = false;
    public boolean filled_subject = false;
    public boolean filled_year = false;

    public boolean allfilled = false;


    @AfterViews
    public void afterViews() {

        this.slide_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);

        autoCompleteTextView_schools.startAnimation(slide_left);
        autoCompleteTextView_teachers.startAnimation(slide_left);
        autoCompleteTextView_courses.startAnimation(slide_left);
        autoCompleteTextView_subjects.startAnimation(slide_left);
        autoCompleteTextView_years.startAnimation(slide_left);


        this.createArrayAdapter(AvailableAttributes.availableschools, this.autoCompleteTextView_schools);
        this.createArrayAdapter(AvailableAttributes.availableteachers, this.autoCompleteTextView_teachers);
        this.createArrayAdapter(AvailableAttributes.availablecourses, this.autoCompleteTextView_courses);
        this.createArrayAdapter(AvailableAttributes.availablesubjects, this.autoCompleteTextView_subjects);
        this.createArrayAdapter(AvailableAttributes.availableyears, this.autoCompleteTextView_years);

        this.createArrayAdapterForSpinner(AvailableAttributes.availabledegrees, this.spinner_degrees);
        this.createArrayAdapterForSpinner(AvailableAttributes.availablesemesters, this.spinner_semesters);

        setChangeListener(null, textinput_name, "name");
        setChangeListener(this.autoCompleteTextView_schools, null, "school");
        setChangeListener(this.autoCompleteTextView_teachers, null, "teacher");
        setChangeListener(this.autoCompleteTextView_courses, null, "course");
        setChangeListener(this.autoCompleteTextView_subjects, null, "subject");
        setChangeListener(this.autoCompleteTextView_years, null, "year");


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.optionMenu = menu;
        getMenuInflater().inflate(R.menu.titlebarattribute, menu);
        displayMenuItem(this.optionMenu, false, "uploadtoserver");
        return true;
    }

    public void displayMenuItem(Menu menu, boolean show, String itemtitle) {

        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getTitle().equals(itemtitle)) {
                menu.getItem(i).setVisible(show);

            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upload:

                Toast.makeText(getApplication().getApplicationContext(), "Upload zum Server", Toast.LENGTH_SHORT).show();
                try {
                    this.responsePDF = new ResponsePDF(
                            textinput_name.getText().toString(),
                            autoCompleteTextView_schools.getText().toString(),
                            autoCompleteTextView_courses.getText().toString(),
                            spinner_degrees.getSelectedItem().toString(),
                            spinner_semesters.getSelectedItem().toString(),
                            autoCompleteTextView_subjects.getText().toString(),
                            autoCompleteTextView_teachers.getText().toString(),
                            autoCompleteTextView_years.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public void setfilledBoolean(boolean filledBool, final String source) {

        switch (source) {
            case "name":
                this.filled_name = filledBool;
                break;
            case "school":
                this.filled_school = filledBool;
                break;
            case "teacher":
                this.filled_teacher = filledBool;
                break;
            case "course":
                this.filled_course = filledBool;
                break;
            case "subject":
                this.filled_subject = filledBool;
                break;
            case "year":
                this.filled_year = filledBool;
                break;
            default:
                Log.e("Error", "Fehler beim befüllen der Boolean!");
                break;
        }

    }



    public void checkAllFilled() {
        if (this.filled_name &&
                this.filled_school &&
                this.filled_teacher &&
                this.filled_course &&
                this.filled_subject &&
                this.filled_year) {

            this.allfilled = true;
            displayMenuItem(this.optionMenu, true, "uploadtoserver");
        } else {
            this.allfilled = false;
            displayMenuItem(this.optionMenu, false, "uploadtoserver");
        }

    }

    public void setChangeListener(AutoCompleteTextView autoCompletementElement, EditText pdfname, final String source) {

        if (autoCompletementElement != null) {
            autoCompletementElement.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        setfilledBoolean(true, source);
                        checkAllFilled();
                    } else if (s.length() == 0) {
                        setfilledBoolean(false, source);
                        checkAllFilled();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        } else if (pdfname != null) {
            pdfname.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() != 0) {
                        setfilledBoolean(true, source);
                        checkAllFilled();
                    } else if (s.length() == 0) {
                        setfilledBoolean(false, source);
                        checkAllFilled();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }


    public void createArrayAdapterForSpinner(ArrayList<String> arrayList, Spinner spinner) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, arrayList);

        spinner.setAdapter(spinnerAdapter);
    }

    public void createArrayAdapter(ArrayList<String> arrayList, AutoCompleteTextView autoCompleteTextView) {
        ArrayAdapter<String> autocompletetextAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, arrayList);

        autoCompleteTextView.setAdapter(autocompletetextAdapter);
    }


}
