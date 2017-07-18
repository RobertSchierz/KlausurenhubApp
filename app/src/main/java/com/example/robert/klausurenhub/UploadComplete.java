package com.example.robert.klausurenhub;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.VideoView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.io.File;

/**
 * Created by Rober on 17.07.2017.
 */

@EActivity(R.layout.uploadcomplete)
public class UploadComplete extends AppCompatActivity {

    public UploadComplete() {

    }


    @ViewById
    ImageView uploadcompleteimage;

    @ViewById
    ImageView viewpdf;

    @ViewById
    ImageView newpdf;

    @AfterViews
    public void afterViews() {


        final Animation fadeInUpload = new AlphaAnimation(0, 1);
        fadeInUpload.setInterpolator(new DecelerateInterpolator()); //add this
        fadeInUpload.setDuration(1000);

        Animation fadeOutUpload = new AlphaAnimation(1, 0);
        fadeOutUpload.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOutUpload.setStartOffset(2000);
        fadeOutUpload.setDuration(1000);

        AnimationSet animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeInUpload);
        animation.addAnimation(fadeOutUpload);
        uploadcompleteimage.setAnimation(animation);


        fadeOutUpload.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                uploadcompleteimage.setVisibility(View.GONE);

                viewpdf.setVisibility(View.VISIBLE);
                newpdf.setVisibility(View.VISIBLE);
            }


        });

    }

    @Click
    public void viewpdfClicked() {
        this.viewPdf();
    }

    @Click
    public void newpdfClicked() {
        startActivity(new Intent(UploadComplete.this, MainActivity_.class));
    }

    private void viewPdf() {

        File tempPDF = new File(AvailableAttributes.internalPdfPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        String authorities = getApplicationContext().getPackageName() + ".fileprovider";
        Uri pdfURI = FileProvider.getUriForFile(this, authorities, tempPDF);

        intent.setDataAndType(pdfURI, "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivity(intent);
    }


}
