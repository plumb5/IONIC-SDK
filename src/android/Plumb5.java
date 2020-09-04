package com.plumb5.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.cordova.plumb.demo.BuildConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Plumb5 extends CordovaPlugin {
    protected static final String TAG = "p5 - Engine";
    private static String gcmRegistrationId = "";
    static String projectNumber = null;
    static String packageName = null;
    static String accountId = null;
    static String serviceURL = null;
    static String appKey = null;
    static JSONArray jsonEventDate = new JSONArray();
    static JSONArray jsonFormData = new JSONArray();
    static JSONArray jsonTransData = new JSONArray();
    static int StaticFormId;
    private String pageParms = "null";
    private String screenParms = "null";
    private static String uniqueID = null;
    static boolean isAuthenticate = false;
    public ServiceGenerator.API api;


    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Log.d(TAG, "inside native code : " + action);
        switch (action) {
            case "setup":
                callP5Init(callbackContext);
                return true;
            case "deviceRegistration":
                this.deviceRegistration(callbackContext);
                return true;
            // nav test
            case "navTest":

                return false;
            case "setUserDetails":
                this.setUserDetails(args, callbackContext);
                return true;
            case "tracking":
                 this.tracking(args, callbackContext);
                return true;
            case "notificationSubscribe":

                return false;
            case "eventPost":
                this.eventPost(args, callbackContext);
                return true;
            case "pushResponsePost":
                this.pushResponsePost(args.getJSONObject(0), callbackContext);
                return true;
            default:
                callbackContext.error("fail");
                return false;
        }


    }

    private void callP5Init(CallbackContext callbackContext) {
        if (appKey != null && appKey.length() > 0) {


            SharedPreferences pref = this.cordova.getActivity().getSharedPreferences(P5Constants.P5_INIT_KEY, 0);
            String value = pref.getString(P5Constants.PLUMB5_API_KEY, null);
            String servicevalue = pref.getString(P5Constants.PLUMB5_BASE_URL, null);
            if ((value == null || !value.equals(appKey)) && (servicevalue == null || !servicevalue.equals(serviceURL))) {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(P5Constants.PLUMB5_API_KEY, appKey);
                editor.putString(P5Constants.PLUMB5_BASE_URL, serviceURL);
                editor.putString(P5Constants.PLUMB5_ACCOUNT_ID, accountId);
                editor.putString("packageName", "");
                editor.apply();
            } else {
                Log.v(TAG, "Please check apiKey or service url");
                callbackContext.error("Please check apiKey or service url");
            }
            Call<ResponseBody> call = api.PackageInfo();
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        JSONObject jsonArr = null;
                        try {
                            String array = response.body().string();


                            jsonArr = new JSONArray(array).getJSONObject(0);

                            checkUser(callbackContext, jsonArr);


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // user object available
//                        if (pref.getBoolean("isNew", true)) {
//                            Log.v(TAG, "New user");
//
////                                deviceRegistration(callbackContext);
////
//
//                        } else {
//                            Log.v(TAG, "Existing user");
//
//                        }

                    } else {
                        Log.v(TAG, "Network call fail error ");
                        callbackContext.error("Network call fail error ");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "PackageInfo failed");
                    Log.e(TAG, t.getMessage());
                    callbackContext.error("Network call fail error - " + t.getMessage() + "\n stack trace - ");
                    t.printStackTrace();

                }
            });


        } else {
            Log.d(TAG, "Please provide apiKey");
            callbackContext.error("Expected valid apiKey");


        }
    }

    private void checkUser(CallbackContext callbackContext, JSONObject jsonObject) {
        if (null != jsonObject && jsonObject.length() > 0) {

            try {

                projectNumber = jsonObject.getString("GcmProjectNo");

                packageName = jsonObject.getString("PackageName");
                if (packageName.equals(BuildConfig.APPLICATION_ID)) {
                    deviceRegistration(callbackContext);
                } else {
                    callbackContext.error("Package name does't match with apiKey package name");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void deviceRegistration(CallbackContext callbackContext) {

        SharedPreferences pref = this.cordova.getActivity().getSharedPreferences(P5Constants.P5_INIT_KEY, 0);
        gcmRegistrationId = pref.getString(P5Constants.PROPERTY_REG_ID, "");
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(P5Constants.PACKAGE_NAME, packageName);
        editor.apply();
        if (checkPlayServices(callbackContext)) {
            Map<String, Object> json = new HashMap<>();
            try {
                json.put("DeviceId", getDeviceId(this.cordova.getActivity()));
                json.put("Manufacturer", Build.MANUFACTURER);
                json.put("DeviceName", Build.MODEL);
                json.put("OS", "Android");
                json.put("AppVersion", BuildConfig.VERSION_NAME);
                json.put("CarrierName", getCarrierName(this.cordova.getActivity()));
                json.put("DeviceDate", new Date().toString());
                json.put("GcmRegId", gcmRegistrationId);
                json.put("Resolution", getScreenResolution(this.cordova.getActivity()));
                json.put("InstalledStatus", true);
                json.put("IsInstalledStatusDate", new Date().toString());

            } catch (Exception e) {
                Log.v(TAG, "Please check the parameters \n error -");
                Log.d(TAG, "Please check json" + e.getLocalizedMessage());
                callbackContext.error("Please check the parameters \n error -" + e.getLocalizedMessage());
            }

            Call<String> responseBodyCall = api.DeviceRegistration(json);
            responseBodyCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    if (response.isSuccessful()) {

                        editor.putBoolean(P5Constants.IS_NEW, true);
                        Log.v(TAG, "Device registration successful");
                        callbackContext.success("Device registration successful");
                    } else {
                        editor.putBoolean(P5Constants.IS_NEW, false);
                        Log.e(TAG, "Device registration failed");
                        callbackContext.error("Device registration failed");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "Device registration failed");
                    Log.e(TAG, t.getMessage());
                    callbackContext.error("Network call fail error - " + t.getMessage() + "\n stack trace - ");
                    t.printStackTrace();
                }
            });
        }


    }

    private void setUserDetails(JSONArray args, CallbackContext callbackContext) {
        Map<String, Object> userDetails = new HashMap<>();

        try {

            userDetails = new ObjectMapper().readValue(args.getJSONObject(0).toString(), HashMap.class);
            userDetails.put(P5Constants.DEVICE_ID, getDeviceId(this.cordova.getActivity()));

        } catch (Exception e) {
            Log.v(TAG, "Please check the parameters \n error -");
            e.printStackTrace();
            callbackContext.error("Please check the parameters \n error -" + e.getLocalizedMessage());
        }
        if (userDetails != null) {
            Call<ResponseBody> responseBodyCall = api.ContactDetails(userDetails);
            responseBodyCall.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    if (response.isSuccessful()) {


                        Log.v(TAG, "User details sent successful");
                        callbackContext.success("User details sent successful");
                    } else {

                        Log.e(TAG, "User details  failed");
                        callbackContext.error("User details  failed");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.e(TAG, "User details failed");
                    Log.e(TAG, t.getMessage());
                    callbackContext.error("Network call fail error - " + t.getMessage() + "\n stack trace - ");
                    t.printStackTrace();
                }
            });
        }


    }

    private void navTest(String navTestVF, CallbackContext callbackContext) {
        callbackContext.success(navTestVF);
    }

    private void tracking(JSONArray args, CallbackContext callbackContext) {
        Map<String, Object> tracking = null;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date date = new Date();

        try {
            tracking = new ObjectMapper().readValue(args.getJSONObject(0).toString(), HashMap.class);
            tracking.put("SessionId", P5LifeCycle.getP5Session());
            tracking.put("CarrierName", getCarrierName(this.cordova.getActivity()));
            tracking.put("ScreenName", screenParms);
            tracking.put("CampaignId", 0);
            tracking.put("WorkFlowDataId", 0);
            tracking.put("IsNewSession", P5LifeCycle.isExpired());
            tracking.put("DeviceId", getDeviceId(this.cordova.getActivity()));
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
            tracking.put("PageParameter", pageParms);


        } catch (Exception e) {
            Log.v(TAG, "Please check the parameters \n error -");
            e.printStackTrace();
            callbackContext.error("Please check the parameters \n error -" + e.getLocalizedMessage());
        }
        if (tracking != null) {
            Call<String> responseBodyCall = api.Tracking(tracking);
            responseBodyCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    if (response.isSuccessful()) {


                        Log.v(TAG, "Tracking details sent successful");
                        callbackContext.success("Tracking details sent successful");
                    } else {

                        Log.e(TAG, "Tracking details  failed");
                        callbackContext.error("Tracking details  failed");
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "Tracking details failed");
                    Log.e(TAG, t.getMessage());
                    callbackContext.error("Network call fail error - " + t.getMessage() + "\n stack trace - ");
                    t.printStackTrace();
                }
            });
        }
    }

    private void notificationSubscribe(String notificationSubscribeVF, CallbackContext callbackContext) {
        callbackContext.success(notificationSubscribeVF);
    }

    private void eventPost(JSONArray args, CallbackContext callbackContext) {


        Map<String, Object> eventDetails = new HashMap<>();

        try {
            eventDetails = new ObjectMapper().readValue(args.getJSONObject(0).toString(), HashMap.class);
            Date date = new Date();
            DateFormat dateFormat = new SimpleDateFormat(P5Constants.SAMPLE_DATE, Locale.ENGLISH);
            eventDetails.put(P5Constants.SESSION_ID, P5LifeCycle.getP5Session());
            eventDetails.put(P5Constants.DEVICE_ID, getDeviceId(this.cordova.getActivity()));
            eventDetails.put(P5Constants.OFFLINE, 0);
            eventDetails.put(P5Constants.TRACK_DATE, dateFormat.format(date));

        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("Please check the parameters \n error -" + e.getLocalizedMessage());
        }


        Call<String> responseBodyCall = api.EventResponses(eventDetails);
        responseBodyCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                if (response.isSuccessful()) {

                    //  new P5GetDialogHttpRequest(activity, eng.p5GetServiceUrl(activity), Name, Value, "").execute();
                    Log.v(TAG, "Event details sent successful");
                    callbackContext.success("Event details sent successful");
                } else {

                    Log.e(TAG, "Event details  failed");
                    callbackContext.error("Event details  failed");
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e(TAG, "Event details failed");
                Log.e(TAG, t.getMessage());
                callbackContext.error("Network call fail error - " + t.getMessage() + "\n stack trace - ");
                t.printStackTrace();
            }
        });
    }


    private void pushResponsePost(JSONObject json, CallbackContext callbackContext) {
        Map<String, Object> inAppDetails = new HashMap<>();

        try {
           // inAppDetails = new ObjectMapper().readValue(json.toString(), HashMap.class);
            inAppDetails.put(P5Constants.DEVICE_ID, getDeviceId(this.cordova.getActivity()));
            inAppDetails.put(P5Constants.SESSION_ID, P5LifeCycle.getP5Session());
              inAppDetails.put(P5Constants.SCREEN_NAME, "");
             inAppDetails.put(P5Constants.EVENT_ID, "");
           inAppDetails.put(P5Constants.EVENT_VALUE, "");
            inAppDetails.put(P5Constants.PAGE_PARAMETER, "");

        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error("Please check the parameters \n error -" + e.getLocalizedMessage());
        }


        Call<ResponseBody> responseBodyCall = api.InAppDetails(inAppDetails);
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        final String getContent = jsonObject.get("FormContent").toString();
                        String WidgetName = jsonObject.get("WidgetName").toString();
                        int MobileFormId = Integer.parseInt(jsonObject.get("MobileFormId").toString());
                        Log.v(TAG, "InApp details sent successful");
                        if (!getContent.equals("")) {
                            P5DialogBox v = new P5DialogBox();
                            v.dialogBox(cordova.getActivity(), getContent, serviceURL, MobileFormId, WidgetName);
                            callbackContext.success("InApp details sent successful");
                        } else {
                            Log.d(TAG, "There is no data or status is inactive for in-app campaign");
                            callbackContext.success("There is no data or status is inactive for in-app campaign");
                        }


                    } catch (Exception ex) {
                        Log.e(TAG, "InApp details json failed");
                        Log.e(TAG, ex.getMessage());
                        callbackContext.error("JSON failed - " + ex.getMessage() + "\n stack trace - ");
                        ex.printStackTrace();
                    }
                } else {

                    Log.e(TAG, "InApp details  failed");
                    Log.e(TAG, response.errorBody().toString());
                    callbackContext.error("InApp details  failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "InApp details failed");
                Log.e(TAG, t.getMessage());
                callbackContext.error("Network call fail error - " + t.getMessage() + "\n stack trace - ");
                t.printStackTrace();
            }
        });

    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        SharedPreferences.Editor sharedPreferences = this.cordova.getActivity()
                .getSharedPreferences(P5Constants.P5_INIT_KEY, 0)
                .edit();
        // your init code here

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this.cordova.getActivity(), new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String token = instanceIdResult.getToken();

                sharedPreferences
                        .putString(P5Constants.PROPERTY_REG_ID, token)
                        .apply();

            }
        });

        accountId = getMetadata(this.cordova.getActivity(), P5Constants.PLUMB5_ACCOUNT_ID);
        serviceURL = getMetadata(this.cordova.getActivity(), P5Constants.PLUMB5_BASE_URL);
        appKey = getMetadata(this.cordova.getActivity(), P5Constants.PLUMB5_API_KEY);
        api = ServiceGenerator.createService(ServiceGenerator.API.class, appKey, accountId, serviceURL);
    }


    String getMetadata(Context context, String key) {


        try {
            return Objects.requireNonNull(context.getPackageManager().getApplicationInfo(
                    context.getPackageName(), PackageManager.GET_META_DATA).metaData.get(key)).toString();

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG,
                    "Failed to load meta-data, NameNotFound: " + e.getMessage());
            return null;
        } catch (NullPointerException e) {
            Log.e(TAG,
                    "Failed to load meta-data, NullPointer: " + e.getMessage());
            return null;
        }
    }

    private boolean checkPlayServices(CallbackContext callbackContext) {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this.cordova.getActivity());
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                callbackContext.error(googleAPI.getErrorString(result));
                Log.i(TAG, "Google Play Services are not available.");
            }

            return false;
        }

        return true;
    }

    public synchronized String getDeviceId(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    P5Constants.PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(P5Constants.PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(P5Constants.PREF_UNIQUE_ID, uniqueID);
                editor.commit();
            }
        }
        return uniqueID;
    }

    private static String getScreenResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        return width + "*" + height;
    }

    private static String getCarrierName(Context context) {
        if (Build.VERSION.SDK_INT > 22) {
            //for dual sim mobile
            SubscriptionManager localSubscriptionManager = SubscriptionManager.from(context);

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                TelephonyManager tManager = (TelephonyManager) context
                        .getSystemService(Context.TELEPHONY_SERVICE);


                return tManager.getNetworkOperatorName();

            } else {
                return "";
            }

        } else {
            //below android version 22
            TelephonyManager tManager = (TelephonyManager) context
                    .getSystemService(Context.TELEPHONY_SERVICE);

            return tManager.getNetworkOperatorName();
        }
    }

    public String p5GetScreenName(Activity activity) {
        return "";
    }

    public String p5GetAppKey() {


        if (appKey != null) {
            return appKey;
        } else {
            Log.d(TAG, "Please provide appkey.");
            return "";
        }
    }

    public boolean isP5IntentAvailable(Context ctx, Intent intent) {
        final PackageManager mgr = ctx.getPackageManager();
        List<ResolveInfo> list =
                mgr.queryIntentActivities(intent,
                        PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static void p5ChkPermission(Activity activity, String getPermission) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                String[] permission = getPermission.split(",");   //{"android.permission.CALL_PHONE", "android.permission.ACCESS_FINE_LOCATION"};
                if (permission.length > 0)
                    ActivityCompat.requestPermissions(activity, permission, 1);
            }
        } catch (Throwable e) {
            Log.d(TAG, "Permission error - " + e.getLocalizedMessage());
            e.printStackTrace();
        }

    }

}


