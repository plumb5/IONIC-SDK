package com.plumb5.plugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class P5Location {

    private Activity context;

    public P5Location(Activity context) {
        this.context = context;
    }

    protected LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    boolean canGetLocation = false;

    private static Location getlocation; // location
    double latitude; // latitude
    double longitude; // longitude

    private static Activity activity;

    public Location P5CurrentLocation(Activity getactivity) {
        Log.d("Network", "Connect");

        try {
            activity = getactivity;

            locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

                Log.d("Network", "Both are false");
            } else if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                this.canGetLocation = true;

                if (isGPSEnabled) {
                    if (getlocation == null) {
                        //Log.d("Network", "GPS");
                        if (locationManager != null) {

                            getlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (getlocation != null) {
                                latitude = getlocation.getLatitude();
                                longitude = getlocation.getLongitude();
                            }
                        }
                    }
                }
                if (isNetworkEnabled) {
                    //Log.d("Network", "Network");
                    if (locationManager != null) {

                        getlocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (getlocation != null) {
                            latitude = getlocation.getLatitude();
                            longitude = getlocation.getLongitude();
                        }
                    }
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        return getlocation;
    }


}
