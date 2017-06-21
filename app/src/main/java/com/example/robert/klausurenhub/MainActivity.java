package com.example.robert.klausurenhub;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
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
import org.androidannotations.annotations.res.BooleanRes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

    //@ViewById
    //ImageView camerabutton;


    @ViewById
    FloatingActionButton cameraButton;

    @ViewById
    RecyclerView recyclerView;


//    @ViewById
//    ImageView previewImage;

    @AfterViews
    public void afterViews() {


        // Define a layout for RecyclerView
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new GridLayoutManager(this, getResources().getInteger(R.integer.grid_rows));
        recyclerView.setLayoutManager(mLayoutManager);

        // Initialize a new instance of RecyclerView Adapter instance
        mAdapter = new ImageAdapter(this, this.documentImageArray, this);

        // Set the adapter for RecyclerView
        recyclerView.setAdapter(mAdapter);
    }


    @Click
    public void cameraButtonClicked() {
        dispatchTakePictureIntent();

    }

    private File createFile() throws IOException {


        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
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


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_convert:
                try {
                    if(this.convertToPDF()){
                        Toast.makeText(getApplication().getApplicationContext(), "PDF Erstellt!", Toast.LENGTH_SHORT).show();
                        this.viewPdf();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DocumentException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public Boolean convertToPDF() throws IOException, DocumentException {


         File pdfFolder = new File(getExternalCacheDir(), "KlausurenhubPDFs");


        if (!pdfFolder.exists()) {
            Boolean test = pdfFolder.mkdirs();
            Log.i("PDF: ", "Pdf Directory created: " + pdfFolder.getAbsolutePath());
        }


        Date date = new Date();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + "/" + timeStamp + ".pdf");
        this.tempPDF = myFile;
        OutputStream output = new FileOutputStream(myFile);

        //Rectangle pagesize = new Rectangle(216f, 720f);
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        PdfWriter.getInstance(document, output);
        document.open();

        for (int i = 0; i < this.getImagePaths().size(); i++) {
            Image image = Image.getInstance(this.getImagePaths().get(i));
            document.add(image);
        }

        document.close();

        if(document != null){
            return true;
        }else{
            return false;
        }

    }

    private void viewPdf(){
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
