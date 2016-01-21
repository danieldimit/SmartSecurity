package com.proseminar.smartsecurity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

// Makes the Service start at boot-up (Currently is turned off from the android-manifest)
// Can make it to Send-SMS before turning off(if turned off by a thief for example), when alarm is turned on.
public class BootReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(SensorDataCollectorService.class.getName());
        context.startService(serviceIntent); 
	}
}
