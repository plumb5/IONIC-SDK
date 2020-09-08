package com.plumb5.plugin;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.cordova.plumb.demo.BuildConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.NOTIFICATION_SERVICE;

public class P5LifeCycle implements Application.ActivityLifecycleCallbacks {
    protected static final String TAG = "p5 - LifeCycle";
    public static CordovaInterface cordovaActivity;
    public static CordovaWebView cordovaWebView;
    static String p5Session = "";
    static Activity getactivity;
    private int screenCount = 0, CampaignId = 0, Offline = 1;
    private static String screenName = "", p5pushOlddate = "", p5getVisible = "";
    private static long preTime = 0;
    public static String Locality = "", City = "", State = "", Country = "", CountryCode = "";
    public static double Latitude = 00.0, Longitude = 00.0;
    static String pushmsg = "";
    P5Location p5location;
    Plumb5 P5 = new Plumb5();
    ServiceGenerator.API api;



    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {


        Log.d("Plumb5Cycle", "Created");
        try {
            p5getVisible = "";
            screenCount = 1;
            getactivity = activity;
            String pkg = activity.getPackageName();
            activity.getApplicationContext().registerReceiver(pushbroadcastReceiver, new IntentFilter(pkg + ".chatmessage"));
            IntentFilter afilter = new IntentFilter();
            afilter.addAction(pkg + ".0");
            afilter.addAction(pkg + ".1");
            afilter.addAction(pkg + ".2");
            afilter.addAction(pkg + ".3");
            afilter.addAction(pkg + ".4");
            afilter.addAction(pkg + ".5");
            afilter.addAction(pkg + ".6");
            afilter.addAction(pkg + ".7");
            afilter.addAction(pkg + ".8");
            afilter.addAction(pkg + ".9");
            afilter.addAction(pkg + ".10");
            activity.getApplicationContext().registerReceiver(MyActionReceiver, afilter);
            activity.getApplicationContext().registerReceiver(dMyAlarmReceiver, new IntentFilter(pkg + ".alarm"));
            String accountId = P5.getMetadata(getactivity, P5Constants.PLUMB5_ACCOUNT_ID);
            String serviceURL = P5.getMetadata(getactivity, P5Constants.PLUMB5_BASE_URL);
            String appKey = P5.getMetadata(getactivity, P5Constants.PLUMB5_API_KEY);
            api = ServiceGenerator.createService(ServiceGenerator.API.class, appKey, accountId,serviceURL);

        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {

        p5location = new P5Location(activity);
        getP5locationCity(activity);

        Log.d(TAG, "onActivityStarted");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        long diffInMs = date.getTime() - preTime;
        if (diffInMs > 300000 || preTime == 0) {
            preTime = date.getTime();
        } else {
            preTime = date.getTime();
        }
        //tracking(getactivity);
    }

    @Override
    public void onActivityResumed(Activity activity) {
        Log.d(TAG, "onActivityResumed");
        preTime = new Date().getTime();
    }

    @Override
    public void onActivityPaused(Activity activity) {
        Log.d(TAG, "onActivityPaused");
        preTime = new Date().getTime();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        Log.d(TAG, "onActivityStopped");
        preTime = new Date().getTime();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        Log.d(TAG, "onActivitySaveInstanceState");
        preTime = new Date().getTime();
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        Log.d(TAG, "onActivityDestroyed");

        preTime = new Date().getTime();
    }

    public static String getP5Session() {
        Date date = new Date();
        //Log.d("Plumb5post", date.getTime() - preTime +"chk"+p5Session);


        if (p5Session.equals("") || date.getTime() - preTime > 300000) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
            Random r = new Random();
            long randomNumber = r.nextLong();
            if (randomNumber < 0) {
                randomNumber = Math.abs(randomNumber);
            }
            p5Session = dateFormat.format(date) + randomNumber;
            p5Session = p5Session.replace("-", "");
            p5Session = p5Session.replace(" ", "");
            p5Session = p5Session.replace(":", "");

            preTime = date.getTime();
        }

        //Log.d("Plumb5post", "chk"+p5Session);
        return p5Session;
    }

    public static boolean isExpired() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        long diffInMs = date.getTime() - preTime;
        return (diffInMs <= 300000 && preTime != 0);
    }

    public void getP5locationCity(Activity activity) {
        try {
            Locality = "";
            City = "";
            State = "";
            Country = "";
            CountryCode = "";

            //Log.d("Plumb5post", Build.VERSION.SDK_INT+"chk"+Build.VERSION_CODES.M);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android M Permission check
                if (activity.checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    //get location from network or GPs
                    Location location = p5location.P5CurrentLocation(activity);
                    if (location != null) {
                        Latitude = location.getLatitude();
                        Longitude = location.getLongitude();
                        Address addresse = null;
                        try {
                            Geocoder gcd = new Geocoder(activity, Locale.getDefault());
                            List<Address> addresses = gcd.getFromLocation(Latitude, Longitude, 1);
                            if (addresses.size() > 0)
                                addresse = addresses.get(0);
                            Locality = addresse.getSubLocality();
                            City = addresse.getLocality();
                            State = addresse.getAdminArea();
                            Country = addresse.getCountryName();
                            CountryCode = addresse.getCountryCode();

                        } catch (IOException e) {
                            // Handle IOException
                        } catch (NullPointerException e) {
                            // Handle NullPointerException
                        }
                    }

                } else {
                    //activity.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    Log.d("P5 LifeCycle", "location provider requires ACCESS_FINE_LOCATION permission.");
                }

                //Log.d("p5", "Location" + Locality + "/" + City + "/" + State + "/" + Country + "/" +CountryCode);
            } else {
                //get location from network or GPs
                Location location = p5location.P5CurrentLocation(activity);
                if (location != null) {
                    Latitude = location.getLatitude();
                    Longitude = location.getLongitude();
                    Address addresse = null;
                    try {
                        Geocoder gcd = new Geocoder(activity, Locale.getDefault());
                        List<Address> addresses = gcd.getFromLocation(Latitude, Longitude, 1);
                        if (addresses.size() > 0)
                            addresse = addresses.get(0);
                        Locality = addresse.getSubLocality();
                        City = addresse.getLocality();
                        State = addresse.getAdminArea();
                        Country = addresse.getCountryName();
                        CountryCode = addresse.getCountryCode();

                    } catch (IOException e) {
                        // Handle IOException
                    } catch (NullPointerException e) {
                        // Handle NullPointerException
                    }
                }
            }
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    public static String p5GetScreenName(Activity activity) {
        String ScreenName = activity.getLocalClassName();
        if (ScreenName.indexOf(".") == -1) {
            ScreenName = activity.getPackageName() + "." + ScreenName;
        }
        return ScreenName;
    }

    private void tracking(Activity context) {

        SharedPreferences pref = context.getSharedPreferences(P5Constants.P5_INIT_KEY, 0);
        String gcmRegistrationId = pref.getString(P5Constants.PROPERTY_REG_ID, "");
        Map<String, Object> tracking = new HashMap<>();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();
        try {

            tracking.put("SessionId", P5LifeCycle.getP5Session());
            tracking.put("GcmRegId", gcmRegistrationId);
            tracking.put("ScreenName", p5GetScreenName(context));
            tracking.put("CampaignId", 0);
            tracking.put("WorkFlowDataId", "0");
            tracking.put("IsNewSession", P5LifeCycle.isExpired());
            tracking.put("DeviceId", P5.getDeviceId(context));
            tracking.put("Offline", 0);
            tracking.put("TrackDate", dateFormat.format(date));
            tracking.put("GeofenceId", "0");
            tracking.put("Locality", P5LifeCycle.Locality);
            tracking.put("City", P5LifeCycle.City);
            tracking.put("State", P5LifeCycle.State);
            tracking.put("Country", P5LifeCycle.Country);
            tracking.put("CountryCode", P5LifeCycle.CountryCode);
            tracking.put("Latitude", String.valueOf(P5LifeCycle.Latitude));
            tracking.put("Longitude", String.valueOf(P5LifeCycle.Longitude));
            tracking.put("PageParameter", "");
            tracking.put("CarrierName", Plumb5.getCarrierName(context));

        } catch (Exception e) {
            Log.v(TAG, "Please check the parameters \n error -");
            e.printStackTrace();

        }

        Call<String> responseBodyCall = api.Tracking(tracking);
        responseBodyCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {


                    Log.v(TAG, "Tracking details sent successful");

                } else {

                    Log.e(TAG, "Tracking details  failed");

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Tracking details failed");
                Log.e(TAG, t.getMessage());
                t.printStackTrace();
            }
        });
    }

    public BroadcastReceiver pushbroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String ticker = intent.getStringExtra("ticker"), name = intent.getStringExtra("name"), msg = intent.getStringExtra("message"), time = intent.getStringExtra("date"), redirect = intent.getStringExtra("intent");
                if (!p5pushOlddate.equals(msg + time)) {
                    if (!screenName.toLowerCase().contains(redirect.toLowerCase()) || p5getVisible.contains("pausestop")) {
                        p5pushOlddate = msg + time;
                        pushmsg = pushmsg + "^" + msg;
                        //Log.d("Plumb5Cycle", screenTrackerName.toLowerCase()+"Started" + redirect);
                        OfflineNotification(ticker, name, pushmsg.substring(1, pushmsg.length()), redirect);
                    }
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
    };

    private void OfflineNotification(String ticker, String title, String message, String redirect) {
        try {
            int sIcon = getactivity.getResources().getIdentifier("ic_p5_logo", "drawable", getactivity.getPackageName());
            if (sIcon == 0) {
                sIcon = getactivity.getApplicationInfo().icon;
            }

            Intent intent = new Intent();
            intent.setClassName(getactivity, redirect);

            PendingIntent pIntent = PendingIntent.getActivity(getactivity, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            Notification.Builder builder = new Notification.Builder(getactivity);
            builder.setSmallIcon(getactivity.getApplicationInfo().icon);
            builder.setTicker(ticker);
            builder.setContentTitle(title);

            String[] amessage1 = message.split("\\^");
            builder.setSubText(amessage1.length + " new message(s)");
            builder.setContentText(amessage1[0].toString());

            builder.setContentIntent(pIntent);
            builder.setAutoCancel(true);
            builder.setSound(alarmSound);

            Notification.InboxStyle inboxStyle = new Notification.InboxStyle(builder);
            inboxStyle.setBigContentTitle(title);

            for (int i = 0; i < amessage1.length; i++) {
                inboxStyle.addLine(amessage1[i].toString());
            }
            inboxStyle.setSummaryText(amessage1.length + " new message(s)");
            ((NotificationManager) getactivity.getSystemService(Context.NOTIFICATION_SERVICE)).notify(06, inboxStyle.build());
        } catch (Exception ex) {
            Log.d(TAG, ex.getMessage());
        }
    }

    public BroadcastReceiver dMyAlarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                Intent intent1 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(intent1);

                Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                if (alarmUri == null) {
                    alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                }
                final Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
                ringtone.play();
                Bundle bdl = intent.getExtras();
                String getValue = bdl.getString("alarmtxt");
                Toast.makeText(context, "ALARM : " + getValue, Toast.LENGTH_LONG).show();

                final Timer t = new Timer();
                t.schedule(new TimerTask() {
                    public void run() {
                        ringtone.stop();
                        t.cancel();
                    }
                }, 10000);

            } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }
        }
    };

    //Alarm receiver.........
    public BroadcastReceiver MyActionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                String AppKey = P5.getMetadata(context, P5Constants.PLUMB5_API_KEY);
                String PackAge = BuildConfig.APPLICATION_ID;
                if (context.getPackageName().toLowerCase().contains(PackAge.toLowerCase()) && AppKey.length() > 0) {
                    Bundle bdl = intent.getExtras();
                    String getName = bdl.getString("btnname");
                    String getValue = bdl.getString("ebtnpram");
                    String getExtraValue = bdl.getString("ebtnpramvalue");
                    String screen = bdl.getString("screenName");
                    int getPushId = bdl.getInt("PushId");
                    String workflowid = bdl.getString("workflowid");
                    String P5UniqueId = "";
                    if (bdl.containsKey("P5UniqueId")) {
                        if (bdl.get("P5UniqueId") != null) {
                            P5UniqueId = bdl.get("P5UniqueId").toString();
                        } else {
                            P5UniqueId = "0";
                        }
                    } else {
                        P5UniqueId = "0";
                    }

                    String getAction = intent.getAction();
                    //Log.i("HI", context+"/"+getValue + "/" + getExtraValue+"/"+getAction);

                    String pkg = context.getPackageName();
                    int click = 1, close = 0;
                    if (getAction.equals(pkg + ".0")) {
                        click = 0;
                        close = 1;
                    } else if (getAction.equals(pkg + ".1") || getAction.equals(pkg + ".2")) {
                        Intent sIntent = new Intent();
                        String nIntent = getValue, Parameter = getExtraValue;


                        if (Parameter.contains(",")) {
                            String[] paText = Parameter.split("\\,");
                            for (int i = 0; i < paText.length; i++) {
                                String[] paTextValue = paText[i].split("\\=");
                                sIntent.putExtra(paTextValue[0], paTextValue[1]);
                            }
                        } else if (Parameter.indexOf('=') > 0 && Parameter.length() > 1) {
                            String[] paTextValue = Parameter.split("\\=");
                            sIntent.putExtra(paTextValue[0], paTextValue[1]);
                        }


                        if (getAction.equals(pkg + ".1")) {
                            sIntent.setClassName(context, nIntent);
                            sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (P5.isP5IntentAvailable(context, sIntent)) {
                                context.startActivity(sIntent);
                            } else {
                                Log.d(TAG, "wrong intent.");
                            }
                        } else {
                            int lene = nIntent.lastIndexOf('.');
                            sIntent.setComponent(new ComponentName(nIntent.substring(0, lene), nIntent));
                            sIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(sIntent);
                        }

                    } else if (getAction.equals(pkg + ".3") && getValue.contains("http")) {
                        Uri uri = Uri.parse(getValue);
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } else if (getAction.equals(pkg + ".4")) {
                        Toast.makeText(context, "Copied successfully.", Toast.LENGTH_SHORT).show();
                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("text label", getValue);
                        clipboardManager.setPrimaryClip(clipData);
                    } else if (getAction.equals(pkg + ".5")) {
                        if (checkCallPermission(context)) {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + getValue));
                            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(callIntent);
                        }
                    } else if (getAction.equals(pkg + ".6")) {

                        String app_name = context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("text/plain");
                        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, getValue.toString());
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, getExtraValue.toString());

                        Intent iShare = Intent.createChooser(sharingIntent, "Share via " + app_name);
                        iShare.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(iShare);

                    } else if (getAction.equals(pkg + ".7")) {

                        Intent intent1 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent1);

                        Intent aintent = new Intent(pkg + ".alarm");
                        aintent.putExtra("alarmtxt", getExtraValue.toString());
                        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 0, aintent, 0);
                        AlarmManager alarmManager = (AlarmManager) (context.getSystemService(Context.ALARM_SERVICE));
                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 60000 * Integer.parseInt(getValue.toString()), pendingAlarmIntent);

                        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingAlarmIntent);
                    } else if (getAction.equals(pkg + ".8")) {

                        Offline = 1;
                        P5ConnectionDetector cd = new P5ConnectionDetector(context.getApplicationContext());
                        Boolean isInternetPresent = cd.isConnectingToInternet();
                        if (isInternetPresent) {
                            Offline = 0;
                        }
                        JSONArray jsonEventDate = new JSONArray();
                        JSONObject json = new JSONObject();
                        try {

                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            Date date = new Date();
                            json.put(P5Constants.SESSION_ID, P5LifeCycle.getP5Session());
                            json.put(P5Constants.DEVICE_ID, P5.getDeviceId(context));
                            json.put(P5Constants.TYPE, "Custom-Push-Event");
                            json.put(P5Constants.NAME, getValue);
                            json.put(P5Constants.VALUE, getExtraValue);
                            json.put(P5Constants.OFFLINE, Offline);
                            json.put(P5Constants.TRACK_DATE, dateFormat.format(date));
                            jsonEventDate.put(json);

                        } catch (JSONException ignored) {
                        }
                        if (Offline == 0) {

                            JSONObject finaljson = new JSONObject();
                            try {
                                finaljson.put(P5Constants.APP_KEY_POST, P5.p5GetAppKey());
                                finaljson.put(P5Constants.EVENT_DATA_KEY, jsonEventDate);
                                //eng.p5SetEventJsonData();

                            } catch (JSONException ignored) {
                            }
                            String result = finaljson.toString();
                            //Log.d("Plumb52event", result);
                            //  new P5HttpRequest(context, eng.p5GetServiceUrl(getactivity) + getactivity.getResources().getString(R.string.EVENT), result).execute();
                            callEventSend(result, context);
                        }
                    } else if (getAction.equals(pkg + ".9")) {

                        String sms = "";
                        if (getExtraValue.length() > 0) {
                            sms = getExtraValue.toString();
                        }
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("smsto:" + getValue.toString()));
                        smsIntent.putExtra("sms_body", sms.toString());
                        smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(smsIntent);

                    }else if (getAction.equals(pkg + ".10")) {

                        Intent intent1 = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        context.startActivity(intent1);
                        P5.navigateScreen(screen,cordovaActivity,cordovaWebView);


                    }

                    //Insert loading................
                    JSONObject json = new JSONObject();
                    try {

                        json.put("AppKey", AppKey);
                        json.put("MobileFormId", getPushId);
                        json.put("DeviceId", P5.getDeviceId(context));
                        json.put("SessionId", P5LifeCycle.getP5Session());
                        json.put("FormResponses", "Push");
                        json.put("BannerView", 0);
                        json.put("BannerClick", click);
                        json.put("BannerClose", close);
                        json.put("ButtonName", getName);
                        json.put("WorkFlowDataId", workflowid);
                        json.put("SendReport", 0);
                        json.put("P5UniqueId", P5UniqueId);
                        json.put("ButtonName", "");
                        json.put("WidgetName", "");
                        String getresult = json.toString().replace("\\", "").replace("\"{", "{").replace("}\"", "}");
                        //Log.d(TAG, getresult);
                        // new P5HttpRequest(context, eng.p5GetServiceUrl(getactivity) +getactivity.getResources().getString(R.string.FORM_RESPONSES), getresult).execute();
                        callPushSend(context,new ObjectMapper().readValue(getresult, HashMap.class));
                    } catch (JSONException ignored) {
                    }

                    if (getAction.equals(pkg + ".0")||getAction.equals(pkg + ".3")) {
                        NotificationManager notificationManager = (NotificationManager) getactivity.getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.cancel(getPushId);
                    }
                    //Toast.makeText(context, getValue + " Action: " + intent.getAction(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Log.d(TAG, ex.getMessage());
            }

        }
    };

    private boolean checkCallPermission(Context activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ;
            if (activity.checkSelfPermission(android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                //ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CALL_PHONE}, 1);
                Log.d(TAG, "location provider requires CALL_PHONE permission.");
                return false;
            }

        }
        return true;
    }


    void callEventSend(String json, Context context) {
        String accountId = P5.getMetadata(context, P5Constants.PLUMB5_ACCOUNT_ID);
        String serviceURL = P5.getMetadata(context, P5Constants.PLUMB5_BASE_URL);
        String appKey = P5.getMetadata(context, P5Constants.PLUMB5_API_KEY);

        Map<String, Object> eventDetails = new HashMap<>();

        try {
            eventDetails = new ObjectMapper().readValue(json, HashMap.class);


        } catch (Exception e) {
            Log.e(TAG, "Event details failed");
            e.printStackTrace();
            Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
        }
        Call<String> responseBodyCall =  Objects.requireNonNull(ServiceGenerator.createService(ServiceGenerator.API.class, appKey, accountId, serviceURL)).EventResponses(eventDetails);
        responseBodyCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {


                    Log.v(TAG, "Event details sent successful");

                } else {

                    Log.e(TAG, "Event details  failed");

                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Event details failed");
                Log.e(TAG, t.getMessage());

                t.printStackTrace();
            }
        });
    }

    void callPushSend(Context context,    Map<String, Object> pushResponse) {
        String accountId = P5.getMetadata(context, P5Constants.PLUMB5_ACCOUNT_ID);
        String serviceURL = P5.getMetadata(context, P5Constants.PLUMB5_BASE_URL);
        String appKey = P5.getMetadata(context, P5Constants.PLUMB5_API_KEY);


        try {
            if(!pushResponse.containsKey("ScreenName"))
                pushResponse.put("ScreenName",screenName);




            Call<ResponseBody> responseBodyCall = Objects.requireNonNull(ServiceGenerator.createService(ServiceGenerator.API.class, appKey, accountId, serviceURL)).PushResponse(pushResponse);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {


                        Log.v(TAG, "Push details sent successful");

                    } else {

                        Log.e(TAG, "Push details  failed");

                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "Push details failed");
                    Log.e(TAG, t.getMessage());

                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Push details failed");
            e.printStackTrace();
            Log.e(TAG, "Please check the parameters \n error -" + e.getLocalizedMessage());
        }
    }
}




