package com.lethalsys.mimix;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
     
    @Override
    public void onReceive(Context context, Intent intent) {

        // Explicitly specify that Notify_Handler will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(),
        		Notify_Handler.class.getName());
         
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
} 