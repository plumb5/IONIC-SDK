package com.plumb5.plugin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.InputStream;


public class P5PictureNotification  {

    protected static final String TAG = "p5 - P5PictureNoti";
    private final int pushId;
    private Context mContext;
    private String ticker, title, message, nSubtext, imageUrl;
    private PendingIntent pIntent;
    private int sIcon;
    NotificationCompat.Builder builder;

    public P5PictureNotification(NotificationCompat.Builder builder, Context context, String ticker, String title, PendingIntent pIntent, String message, String nSubtext, String imageUrl, int sIcon, int pushId) {
        super();
        this.mContext = context;
        this.ticker = ticker;
        this.title = title;
        this.message = message;
        this.imageUrl = imageUrl;
        this.pIntent = pIntent;
        this.sIcon = sIcon;
        this.nSubtext = nSubtext;
        this.builder = builder;
        this.pushId = pushId;
        P5ConnectionDetector cd = new P5ConnectionDetector(mContext.getApplicationContext());
        boolean isInternetPresent = cd.isConnectingToInternet();
        if (isInternetPresent) {
            Glide.with(context).asBitmap().load(imageUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                    try {

                        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        builder.setSmallIcon(sIcon);
                        builder.setDefaults(Notification.DEFAULT_ALL);
                        builder.setWhen(System.currentTimeMillis());
                        builder.setTicker(ticker);
                        builder.setContentTitle(title);
                        builder.setContentText(message);
                        builder.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), sIcon));
                        if (nSubtext.length() > 0) {
                            builder.setSubText(nSubtext);
                        }
                        builder.setContentIntent(pIntent);
                        builder.setAutoCancel(true);
                        builder.setSound(alarmSound);
                        NotificationCompat.BigPictureStyle picStyle = new NotificationCompat.BigPictureStyle(builder);
                        picStyle.setBigContentTitle(title);
                        picStyle.bigPicture(bitmap);
                        if (nSubtext.length() > 0) {
                            picStyle.setSummaryText(nSubtext);
                        }
                        ((NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE)).notify(pushId, builder.build());
                    } catch (Throwable e) {
                        Log.d(TAG, "something goes wrong.");
                    }

                }

                @Override
                public void onLoadCleared(Drawable placeholder) {
                }
            });
        }
    }



}