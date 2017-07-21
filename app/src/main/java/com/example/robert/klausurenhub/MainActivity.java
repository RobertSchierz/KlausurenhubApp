package com.example.robert.klausurenhub;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import static android.support.v4.content.ContextCompat.startActivity;

@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {


    private Context mContext;

    RelativeLayout mRelativeLayout;
    private RecyclerView mRecyclerView;

    private ImageAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public Menu optionMenu;

    public String mCurrentPhotoPath;

    public File tempPDF;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;

    public ArrayList<DocumentImage> documentImageArray = new ArrayList<>();

    public AvailableAttributes availableoptions;

    @ViewById
    FloatingActionButton cameraButton;

    @ViewById
    TextView newimages_text;

    @ViewById
    ImageView newimages_arrow;

    @ViewById
    RecyclerView recyclerView;

    //Wichtige Variablen für Asynchronität
    private int numberOfQueryExecutions = 7;
    private int numberOfExecutedQueries = 0;
    private Boolean allQueriesExecuted = false;



    Animation slide_left;

    @AfterViews
    public void afterViews() {



        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_rows));
        recyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new ImageAdapter(this, this.documentImageArray, this);


        recyclerView.setAdapter(mAdapter);

        this.setAvailableOptions();




        Toast.makeText(getApplication().getApplicationContext(), "Eingeloggt als: " + AvailableAttributes.username, Toast.LENGTH_SHORT).show();

        this.slide_left = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_left);
        this.newimages_text.setAnimation(slide_left);
        this.newimages_arrow.setAnimation(slide_left);

    }

    /*
    private String searchFacebookVal(SharedPreferences sharedPreferences, String value) {

        Map<String, ?> keys = sharedPreferences.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            return entry.getValue().toString();
        }
        return null;

    }*/


    private void asynchandler(){
        this.numberOfExecutedQueries++;
        if(this.numberOfExecutedQueries == this.numberOfQueryExecutions){
            this.allQueriesExecuted = true;
            this.numberOfExecutedQueries = 0;
        }
        else{
            this.allQueriesExecuted = false;
        }
    }



    public void setAvailableOptions() {
        this.availableoptions = new AvailableAttributes(getApplicationContext());

        this.availableoptions.getAvailableOptions(new AvailableAttributes.VolleyCallback() {

            @Override
            public void getSchools(JSONArray schools) throws JSONException {
                AvailableAttributes.schools = schools;
                AvailableAttributes.availableschools = extractAvailableOptions(schools, "schoolName") ;
                asynchandler();
            }

            @Override
            public void getCourses(JSONArray courses) throws JSONException {
                AvailableAttributes.courses = courses;
                AvailableAttributes.availablecourses = extractAvailableOptions(courses, "courseName");
                asynchandler();
            }

            @Override
            public void getDegrees(JSONArray degrees) throws JSONException {
                AvailableAttributes.degrees = degrees;
                AvailableAttributes.availabledegrees = extractAvailableOptions(degrees, "degreeName");
                asynchandler();
            }

            @Override
            public void getSemesters(JSONArray semesters) throws JSONException {
                AvailableAttributes.semesters = semesters;
                AvailableAttributes.availablesemesters = extractAvailableOptions(semesters, "semesterName");
                asynchandler();
            }

            @Override
            public void getSubjects(JSONArray subjects) throws JSONException {
                AvailableAttributes.subjects = subjects;
                AvailableAttributes.availablesubjects = extractAvailableOptions(subjects, "subjectName");
                asynchandler();
            }

            @Override
            public void getTeachers(JSONArray teachers) throws JSONException {
                AvailableAttributes.teachers = teachers;
                AvailableAttributes.availableteachers = extractAvailableOptions(teachers, "teacherName");
                asynchandler();
            }

            @Override
            public void getYears(JSONArray years) throws JSONException {
                AvailableAttributes.years = years;
                AvailableAttributes.availableyears = extractAvailableOptions(years, "yearName");
                asynchandler();
            }

            public ArrayList<String> extractAvailableOptions(JSONArray sourcearray, String columnName) throws JSONException {
                ArrayList<String> tempArraylist = new ArrayList<String>();
                for(int i = 0; i < sourcearray.length(); i++){
                    JSONObject tempJSONObject = sourcearray.getJSONObject(i);
                    tempArraylist.add(tempJSONObject.getString(columnName));
                }
                return tempArraylist;

            }

        });
    }


    @Click
    public void cameraButtonClicked() {
        dispatchTakePictureIntent();

    }

    private File createFile() throws IOException {



        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );


        mCurrentPhotoPath = image.getAbsolutePath();
        return image;

    }


    private void dispatchTakePictureIntent() {


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            File photoFile = null;
            try {
                photoFile = createFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }

            if (photoFile != null) {

                String authorities = getApplicationContext().getPackageName() + ".fileprovider";
                Uri photoURI = FileProvider.getUriForFile(this, authorities, photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                setResult(RESULT_OK, takePictureIntent);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                galleryAddPic();
            }

        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(this.mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {

            Matrix matrix = new Matrix();

            matrix.postRotate(90);

            Bitmap imageBitmap = BitmapFactory.decodeFile(this.mCurrentPhotoPath);
            Bitmap rotatedBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
            generateNewBitmapImage(rotatedBitmap);


        }
    }

    private void generateNewBitmapImage(Bitmap imagemap) {

        this.documentImageArray.add(new DocumentImage(imagemap, this.mCurrentPhotoPath));
        this.mAdapter.notifyDataSetChanged();
        this.checkImagelenthForToolbar();

    }

    public void checkImagelenthForToolbar() {
        if (this.mAdapter.getItemCount() > 0) {
            displayMenuItem(this.optionMenu, true, "convertImages");
            this.newimages_text.setVisibility(View.INVISIBLE);
            this.newimages_arrow.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        this.optionMenu = menu;
        getMenuInflater().inflate(R.menu.titlebarpreviewimages, menu);
        displayMenuItem(this.optionMenu, false, "convertImages");
        return true;
    }

    public void displayMenuItem(Menu menu, boolean show, String itemtitle) {

        for (int i = 0; i < menu.size(); i++) {
            if (menu.getItem(i).getTitle().equals(itemtitle)) {
                menu.getItem(i).setVisible(show);

            }

        }
    }

    public void startAttributeActivity(){
        startActivity(new Intent(MainActivity.this, Attribute_PDF_.class));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_convert:

                if(this.allQueriesExecuted){
                    Log.v("Queriesexecuted: ", this.allQueriesExecuted.toString());
                    try {
                        if (this.convertToPDF()) {
                            Toast.makeText(getApplication().getApplicationContext(), "PDF Erstellt!", Toast.LENGTH_SHORT).show();
                            this.startAttributeActivity();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (DocumentException e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.v("Queriesexecuted: ", this.allQueriesExecuted.toString());
                    Toast.makeText(getApplication().getApplicationContext(), "Fehler beim Laden der Availableoptions!", Toast.LENGTH_SHORT).show();
                    this.setAvailableOptions();
                    Toast.makeText(getApplication().getApplicationContext(), "Availableoptions werden neu geladen", Toast.LENGTH_SHORT).show();
                }



                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public Boolean convertToPDF() throws IOException, DocumentException {


        File pdfFolder = new File(getExternalCacheDir(), "KlausurenhubPDFs");


        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
            Log.i("PDF: ", "Pdf Directory created: " + pdfFolder.getAbsolutePath());
        }


        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + "/" + timeStamp + ".pdf");
        this.tempPDF = myFile;
        OutputStream output = new FileOutputStream(myFile);


        Document document = new Document(/*PageSize.A4*/);

        PdfWriter.getInstance(document, output);
        document.open();

        for (int i = 0; i < this.getImagePaths().size(); i++) {

            Image image = Image.getInstance(this.getImagePaths().get(i));
            image.setRotationDegrees(-90);

            document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
            document.newPage();

            document.add(image);

        }

        document.close();

        AvailableAttributes.internalPdfPath =  myFile.getPath();


        return (document!= null);


    }

    private void viewPdf() {
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String authorities = getApplicationContext().getPackageName() + ".fileprovider";
        Uri pdfURI = FileProvider.getUriForFile(this, authorities, this.tempPDF);

        intent.setDataAndType(pdfURI, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }





    public ArrayList<String> getImagePaths() {
        ArrayList<String> tempPathList = new ArrayList<String>();
        for (int i = 0; i < this.documentImageArray.size(); i++) {
            tempPathList.add(this.documentImageArray.get(i).getPath());
        }
        return tempPathList;


    }
}
