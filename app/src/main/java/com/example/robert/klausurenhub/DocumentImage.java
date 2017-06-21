package com.example.robert.klausurenhub;

import android.graphics.Bitmap;

/**
 * Created by Rober on 12.06.2017.
 */

public class DocumentImage {

    public Bitmap bitmap;
    public String path;

    public DocumentImage(Bitmap bitmap, String path){
        this.bitmap = bitmap;
        this.path = path;
    }

    public Bitmap getBitmap(){
        return this.bitmap;
    }

    public String getPath(){
        return this.path;
    }


}
