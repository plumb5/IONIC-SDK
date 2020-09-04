package com.plumb5.plugin;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class P5SendNotification {

    protected static final String TAG = "p5 - P5SendNotification";
    public static final String MyPREFERENCES = "MyPrefs";


    static void Send(Context context, int PushId, int Action, String notificationTicker, String notificationTitle, String nMessage, String nSubtext, String nImage, int clkAction, String nIntent, String Parameter, String ExtraAction, String GeofenceName, String BeaconName, String workflowId) {

        try {
            SharedPreferences pref = context.getApplicationContext().getSharedPreferences("p5Init", 0);
            String AppKey = pref.getString("appKey", null);

            Plumb5 eng = new Plumb5();
            Intent intent = new Intent();

            if (clkAction == 2) {
                int len1 = eng.getMetadata(context, P5Constants.PLUMB5_BASE_URL).lastIndexOf('/');
                String getnewurl = eng.getMetadata(context, P5Constants.PLUMB5_BASE_URL).substring(0, len1 - 1);
                String geturl = getnewurl.substring(0, getnewurl.lastIndexOf('/'));
                Uri uri = Uri.parse(geturl + "/Redirect.aspx?AppKey=" + AppKey + "&PushId=" + PushId + "&SessionId=" + P5LifeCycle.getP5Session() + "&DeviceId=" + eng.getDeviceId(context) + "&RedirectUrl=" + nIntent);
                intent = new Intent(Intent.ACTION_VIEW, uri);
            } else if (clkAction == 0 || clkAction == 1) {

                if (Parameter.indexOf(',') > 0 && Parameter.length() > 1) {
                    String[] paText = Parameter.split("\\,");
                    for (int i = 0; i < paText.length; i++) {
                        String[] paTextValue = paText[i].split("\\=");
                        intent.putExtra(paTextValue[0], paTextValue[1]);
                    }
                } else if (Parameter.indexOf('=') > 0 && Parameter.length() > 1) {
                    String[] paTextValue = Parameter.split("\\=");
                    intent.putExtra(paTextValue[0], paTextValue[1]);
                }

                if (clkAction == 1) {
                    int lene = nIntent.lastIndexOf('.');
                    intent.setComponent(new ComponentName(nIntent.substring(0, lene), nIntent));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                } else {
                    intent.putExtra("PushId", PushId);
                    intent.putExtra("workflowdataId", workflowId);
                    intent.putExtra("sentFromNotiFication", true);
                    intent.setClassName(context, nIntent);
                }
            }
            //Log.d("p5", nIntent + "nghjhgjn" + Parameter);

            //Insert loading................
            JSONObject json = new JSONObject();
            try {

                json.put("AppKey", AppKey);
                json.put("MobileFormId", PushId);
                json.put("DeviceId", eng.getDeviceId(context));
                json.put("SessionId", P5LifeCycle.getP5Session());
                json.put("FormResponses", "Push");
                json.put("BannerView", 1);
                json.put("BannerClick", 0);
                json.put("BannerClose", 0);
                json.put("GeofenceName", GeofenceName);
                json.put("BeaconName", BeaconName);
                json.put("WorkFlowDataId", workflowId);
                json.put("SendReport", 0);
                json.put("P5UniqueId", "");
                json.put("ButtonName", "");
                json.put("WidgetName", "");
                String getresult = json.toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
                // new P5HttpRequest(context, eng.p5GetServiceUrl(context) + context.getResources().getString(R.string.FORM_RESPONSES), getresult).execute();
                new P5LifeCycle().callPushSend(context, new ObjectMapper().readValue(getresult, HashMap.class));
            } catch (JSONException ignored) {
            }

            //Log.d("p5", nIntent + "nn" + Parameter);


            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            int sIcon = context.getResources().getIdentifier("ic_p5_logo", "drawable", context.getPackageName());
            if (sIcon == 0) {
                sIcon = context.getApplicationInfo().icon;
            }


//
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String NOTIFICATION_CHANNEL_ID = "plumb5";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Notifications", NotificationManager.IMPORTANCE_DEFAULT);

                // Configure the notification channel.
//            notificationChannel.setDescription("p");
//            notificationChannel.enableLights(true);
//            notificationChannel.setLightColor(Color.RED);
//            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
//            notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);


            //Attach Extra button with notification....
            if (ExtraAction.length() > 0) {
                try {
                    String[] btnText = ExtraAction.split("\\|");
                    if (btnText.length > 0) {
                        for (int i = 0; i < btnText.length; i++) {
                            String[] btnValue = btnText[i].split("\\^");
                            String btnname = btnValue[0];
                            int icochk = Integer.parseInt(btnValue[1]);
                            int actionno = Integer.parseInt(btnValue[2]);

                            Intent btnReceive = new Intent();
                            btnReceive.putExtra("btnname", btnname);
                            if (actionno != 0) {
                                btnReceive.putExtra("ebtnpram", btnValue[3]);
                            } else {
                                btnReceive.putExtra("ebtnpram", String.valueOf(Action));
                            }
                            if (btnValue.length == 5) {
                                btnReceive.putExtra("ebtnpramvalue", btnValue[4]);
                            } else {
                                btnReceive.putExtra("ebtnpramvalue", "");
                            }
                            btnReceive.putExtra("PushId", PushId);
                            btnReceive.putExtra("workflowid", workflowId);
                            btnReceive.setAction(context.getPackageName() + "." + actionno);
                            PendingIntent pendingIntentYes = PendingIntent.getBroadcast(context, 0, btnReceive, PendingIntent.FLAG_UPDATE_CURRENT);

                            //Log.i("HI all", "plumb5." + actionno + "/" + btnText[i]);

                            int btnIcon = 0;
                            if (icochk != 0) {
                                if (actionno == 0) {
                                    btnIcon = android.R.drawable.ic_menu_delete;
                                } else if (actionno == 1 || actionno == 2 || actionno == 3) {
                                    btnIcon = android.R.drawable.ic_menu_more;
                                } else if (actionno == 4) {
                                    btnIcon = android.R.drawable.ic_menu_save;
                                } else if (actionno == 5) {
                                    btnIcon = android.R.drawable.ic_menu_call;
                                } else if (actionno == 6) {
                                    btnIcon = android.R.drawable.ic_menu_share;
                                } else if (actionno == 7) {
                                    btnIcon = android.R.drawable.ic_menu_my_calendar;
                                } else if (actionno == 8) {
                                    btnIcon = android.R.drawable.ic_menu_send;
                                } else if (actionno == 9) {
                                    btnIcon = android.R.drawable.ic_menu_edit;
                                }
                            }
                            builder.addAction(btnIcon, btnname, pendingIntentYes);

                        }
                    }
                } catch (Throwable e) {
                    Log.d(TAG, "something goes wrong." + e.getMessage());
                }
            }

            //For BigTextStyle.........................................................................

            if (Action == 1) {

                builder.setSmallIcon(sIcon);
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setWhen(System.currentTimeMillis());
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), sIcon));
                builder.setTicker(notificationTicker);
                builder.setContentTitle(notificationTitle);
                builder.setContentText(nMessage);
                if (nSubtext.length() > 0) {
                    builder.setSubText(nSubtext);
                }
                builder.setContentIntent(pIntent);
                builder.setAutoCancel(true);
                builder.setSound(alarmSound);
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(nMessage));

                NotificationCompat.BigTextStyle bigtextStyle = new NotificationCompat.BigTextStyle(builder);
                bigtextStyle.setBigContentTitle(notificationTitle);
                bigtextStyle.bigText(nMessage);
                if (nSubtext.length() > 0) {
                    bigtextStyle.setSummaryText(nSubtext);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(PushId, bigtextStyle.build());

            }

            //For inboxStyle.........................................................................

            else if (Action == 2) {

                builder.setSmallIcon(sIcon);
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setWhen(System.currentTimeMillis());
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), sIcon));
                builder.setTicker(notificationTicker);
                builder.setContentTitle(notificationTitle);
                builder.setContentText(nMessage);
                if (nSubtext.length() > 0) {
                    builder.setSubText(nSubtext);
                }
                builder.setContentIntent(pIntent);
                builder.setAutoCancel(true);
                builder.setSound(alarmSound);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                inboxStyle.setBigContentTitle(notificationTitle);
                inboxStyle.addLine(nMessage);
                if (nSubtext.length() > 0) {
                    inboxStyle.setSummaryText(nSubtext);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(PushId, inboxStyle.build());
            }

            //For BigPictureStyle.........................................................................
            else if (Action == 3) {
                new P5PictureNotification(builder, context, notificationTicker, notificationTitle, pIntent, nMessage, nSubtext, nImage, sIcon, PushId);
            }
            //For multilineStyle.........................................................................

            else if (Action == 4) {
                builder.setSmallIcon(sIcon);
                builder.setDefaults(Notification.DEFAULT_ALL);
                builder.setWhen(System.currentTimeMillis());
                builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), sIcon));
                builder.setTicker(notificationTicker);
                builder.setContentTitle(notificationTitle);
                builder.setContentText(nMessage);
                if (nSubtext.length() > 0) {
                    builder.setSubText(nSubtext);
                }
                builder.setContentIntent(pIntent);
                builder.setAutoCancel(true);
                builder.setSound(alarmSound);

                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                inboxStyle.setBigContentTitle(notificationTitle);

                String[] amessage1 = nMessage.split("\\,");
                for (int i = 0; i < amessage1.length; i++) {
                    inboxStyle.addLine(amessage1[i].toString());
                }
                if (nSubtext.length() > 0) {
                    inboxStyle.setSummaryText(nSubtext);
                }
                ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(PushId, inboxStyle.build());
            }

        } catch (Throwable e) {
            Log.d(TAG, "something goes wrong.." + e.getMessage());
        }
    }


}




