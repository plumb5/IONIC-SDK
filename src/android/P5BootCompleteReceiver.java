package com.plumb5.plugin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;



public class P5BootCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        String pkg = context.getPackageName();
        Log.d("which packageis====", pkg);
        P5LifeCycle plyf = new P5LifeCycle();
        context.getApplicationContext().registerReceiver(plyf.pushbroadcastReceiver, new IntentFilter(pkg + ".chatmessage"));
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
        context.getApplicationContext().registerReceiver(plyf.MyActionReceiver, afilter);
        context.getApplicationContext().registerReceiver(plyf.dMyAlarmReceiver, new IntentFilter(pkg + ".alarm"));


        Log.d("plumb5", "Start");
    }
}