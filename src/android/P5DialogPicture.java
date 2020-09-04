package com.plumb5.plugin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.SSLHandshakeException;


public class P5DialogPicture extends AsyncTask<String, Void, Bitmap> {

    protected static final String TAG = "P5 - Dialog Picture";
    private ImageView imgNew;
    private String imageUrl;
    private AlertDialog builder;
    private Context context;
    private int fWidth;
    private RelativeLayout diagLayout;
    private int fieldBottom;
    private int flag;

    public P5DialogPicture(Context context, String imageUrl, ImageView imgNew, AlertDialog builder, int fWidth, RelativeLayout diagLayout, int fieldBottom, int flag) {
        super();
        this.imageUrl = imageUrl;
        this.imgNew = imgNew;
        this.builder = builder;
        this.context = context;
        this.fWidth = fWidth;
        this.diagLayout = diagLayout;
        this.fieldBottom = fieldBottom;
        this.flag = flag;
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        InputStream in;


        P5ConnectionDetector cd = new P5ConnectionDetector(context.getApplicationContext());
        Boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            try {
                URL url = null;
                //Log.d("p5", fWidth+"something."+this.imageUrl);
                if (!imageUrl.contains("http://")&&!imageUrl.contains("https://")) {
                    StringBuilder SB = new StringBuilder();
                    SB.append("https:").append(imageUrl);
                    url = new URL(SB.toString());
                } else {
                    url = new URL(imageUrl);
                }
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (UnknownHostException e) {
                Log.i(TAG, "unknown host."+e.getMessage());
            } catch (SSLHandshakeException e) {
                Log.i(TAG, "ssl handshake exception"+e.getMessage());
            } catch (Exception e) {
                Log.i(TAG, "unknown error"+e.getMessage());
            }
        } else {
            Log.d(TAG, "Check internet connection.");
        }


        return null;
    }

    //@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPostExecute(Bitmap result) {
        Handler handler = new Handler();
        try {
            super.onPostExecute(result);

            // final P5DialogBox p5box = new P5DialogBox();
            //  p5box.getP5dialog().getWindow().setLayout(P5Engine.getEightyOfDisplayWidth(),P5Engine.getSeventyOfDisplayHeight());
            if (flag == 0 || flag == -1) {
                // if (result.getHeight() >= P5Engine.getSeventyOfDisplayHeight()) {
                float newHeight = (fWidth * result.getHeight()) / result.getWidth();
                int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.18);
                int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.18);
                imgNew.setImageBitmap(Bitmap.createScaledBitmap(result, width, height, true));

                // imgNew.setImageBitmap(Bitmap.createScaledBitmap(result, (int)(result.getWidth()*0.3f),(int) ( result.getHeight()*0.3f), true));
//                } else {
//                    imgNew.setImageBitmap(result);
//                }
                Drawable bgDrawable = imgNew.getDrawable();
                diagLayout.setBackground(bgDrawable);
                if (flag == 0){
                    builder.show();}
//                if (flag == 0) {
//                }
//                float screenWidth = fWidth;
//                float newHeight = screenWidth;
//                if (result.getWidth() != 0 && result.getHeight() != 0) {
//                    Log.d("width", String.valueOf(result.getWidth()));
//                    Log.d("Height", String.valueOf(result.getHeight()));
//                    newHeight = (screenWidth * result.getHeight()) / result.getWidth();
//                }
//                imgNew.setImageBitmap(Bitmap.createScaledBitmap(result, (int)screenWidth, (int) (newHeight), true));
//                Drawable bgDrawable = imgNew.getDrawable();
//                diagLayout.setBackground(bgDrawable);
//                if (flag == 0) {
//                    p5box.getP5dialog().show();
//                }
            } else {
                float screenWidth = fWidth;
                float newHeight = screenWidth;
                if (result.getWidth() != 0 && result.getHeight() != 0) {
                    Log.d("widthred", String.valueOf(result.getWidth()));
                    Log.d("Heightred", String.valueOf(result.getHeight()));
                    newHeight = (screenWidth * result.getHeight()) / result.getWidth();
                }

                //Log.d("p5 - Dialog Picture", result+""+screenWidth+"/"+newHeight);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(result, (int) screenWidth, (int) (newHeight), true);
                imgNew.setImageBitmap(scaledBitmap);
                builder.show();

            }

        } catch (Throwable e) {
            //Log.d("p5 - Dialog Picture", "something goes wrong.."+e.getMessage());
        }
    }

}