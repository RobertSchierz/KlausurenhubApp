package com.example.robert.klausurenhub;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


/**
 * Created by Rober on 12.06.2017.
 */

class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

    public ArrayList<DocumentImage> imageDataSet;
    public Context mContext;
    public MainActivity activity;

    public ImageAdapter(Context context,ArrayList<DocumentImage> imageDataSet, MainActivity activity ) {
        this.mContext = context;
        this.imageDataSet = imageDataSet;
        this.activity = activity;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView itemImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            itemImageView = (ImageView) itemView.findViewById(R.id.previewImage);
        }

        public void bind(final DocumentImage image, final int position, final ArrayList<DocumentImage> imageDataSet, final ImageAdapter adapter, final MainActivity activity){
            itemImageView.setImageBitmap(image.getBitmap());
            itemImageView.setScaleType(ImageView.ScaleType.FIT_XY);
            itemImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(R.string.dialog_delete_image)
                            .setPositiveButton(R.string.delete_image, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                    try {
                                        File file = new File(imageDataSet.get(position).getPath());
                                        boolean deleted = file.delete();
                                        if(deleted){
                                            imageDataSet.remove(position);
                                            adapter.notifyDataSetChanged();
                                            if(adapter.getItemCount() == 0){
                                                activity.displayMenuItem(activity.optionMenu, false, "convertImages");
                                                activity.newimages_text.setVisibility(View.VISIBLE);
                                                activity.newimages_arrow.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Log.e("Error:", "Error " + e.getMessage());
                                    }


                                }
                            })
                            .setNegativeButton(R.string.delete_image_cancel, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    builder.create();
                    builder.show();
                    return false;
                }
            });
        }

    }


    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(mContext).inflate(R.layout.imagecustomview, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(this.imageDataSet.get(position), position, this.imageDataSet, this, this.activity );
    }

    @Override
    public int getItemCount() {
        //Toast.makeText(this.mContext, "Anzahl der Bilder: " + this.imageDataSet.size(), Toast.LENGTH_SHORT).show();
        return this.imageDataSet.size();
    }

}
