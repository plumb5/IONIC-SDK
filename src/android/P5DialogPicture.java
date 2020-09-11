package com.plumb5.plugin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.net.MalformedURLException;
import java.net.URL;


public class P5DialogPicture {

    protected static final String TAG = "P5 - Dialog Picture";

    public P5DialogPicture(Context context, String imageUrl, ImageView imgNew, AlertDialog builder, int fWidth, RelativeLayout diagLayout, int fieldBottom, int flag) throws MalformedURLException {
        super();
        URL url;
        try {
            if (!imageUrl.contains("http://") && !imageUrl.contains("https://")) {
                url = new URL("https:" + imageUrl);
            } else {
                url = new URL(imageUrl);
            }

            if (new P5ConnectionDetector(context.getApplicationContext()).isConnectingToInternet()) {

                Glide.with(context).asBitmap().load(url).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, Transition<? super Bitmap> transition) {
                        try {
                            if (flag == 0 || flag == -1) {
                                int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.18);
                                int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.18);
                                imgNew.setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, true));
                                Drawable bgDrawable = imgNew.getDrawable();
                                diagLayout.setBackground(bgDrawable);
                                if (flag == 0) {
                                    builder.show();
                                }
                            } else {
                                float newHeight = (float) fWidth;
                                if (bitmap.getWidth() != 0 && bitmap.getHeight() != 0) {
                                    Log.d("widthred", String.valueOf(bitmap.getWidth()));
                                    Log.d("Heightred", String.valueOf(bitmap.getHeight()));
                                    newHeight = ((float) fWidth * bitmap.getHeight()) / bitmap.getWidth();
                                }

                                //Log.d("p5 - Dialog Picture", result+""+screenWidth+"/"+newHeight);
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, (int) (float) fWidth, (int) (newHeight), true);
                                imgNew.setImageBitmap(scaledBitmap);
                                builder.show();

                            }
                        } catch (Throwable e) {
                            Log.d(TAG, "something goes wrong.");
                        }

                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                    }
                });
            } else {
                Log.d(TAG, "Check internet connection.");
            }
        } catch (Exception e) {
            Log.i(TAG, "unknown error" + e.getMessage());
        }

    }


}