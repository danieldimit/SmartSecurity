package com.proseminar.smartsecurity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Daniel on 23/01/2016.
 */
public class AlarmOnActivity extends AppCompatActivity {

    // Constants
    static final boolean ON = true;
    static final boolean OFF = false;
    static final String KEY = "alarm_status";
    private static final String TAG = AlarmOnActivity.class.getSimpleName();

    // Recalls the last state - ON or OFF
    private SharedPreferences mPrefs;
    private static SharedPreferences.Editor updater;
    private boolean currentStatus;

    // UI items
    private TextView tvSeconds;
    private TextView btnOff;

    // Count down timers
    private CountDownTimer turnOnTimer;

    Resources res;

    private SensorDataCollectorApi api;

    private SensorDataCollectorListener.Stub collectorListener = new SensorDataCollectorListener.Stub() {
        @Override
        public void handleSensorDataUpdated() throws RemoteException {
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connection established");

            // that's how we get the client side of the IPC connection
            api = SensorDataCollectorApi.Stub.asInterface(service);
            try {
                api.addListener(collectorListener);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to add listener", e);
            }
            // updateTweetView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service connection closed");
        }
    };

    // BIND TO SERVICE TO TELL IT TURN ON AND OFF
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("latest_alarm_status", Context.MODE_PRIVATE);
        currentStatus = mPrefs.getBoolean(KEY, OFF);
        updater = mPrefs.edit();

        Log.e(TAG, "+++++++++++++++ ON CREATE");
        // Set different layouts depending on the status of the alarm.
        if (currentStatus) {
            setContentView(R.layout.activity_alarm_on);
        } else {
            setContentView(R.layout.activity_alarm_warning);
            tvSeconds = (TextView) findViewById(R.id.seconds);
        }
        initButton();

        res = getResources();
        final int countDownMiliSeconds = res.getInteger(R.integer.ui_alarm_on_countdown);

        if (!currentStatus) {
            // Timer counting down the time until the alarm gets turned on (30 000)
            turnOnTimer = new CountDownTimer(countDownMiliSeconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvSeconds.setText(Integer.toString((int) (millisUntilFinished / 1000)));
                    Log.e("STOP", "click");
                }

                // The timer is over the alarm turns on.
                public void onFinish() {
                    turnAlarmOn();
                    tvSeconds.setText("0");
                    setContentView(R.layout.activity_alarm_on);
                    initButton();
                }
            };
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "++++++++++++ON RESUME");

        // Start/Restart timer
        if (!currentStatus) {
            turnOnTimer.start();
        }

        Intent intent;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions

            intent = new Intent(this, SensorDataCollectorService.class);
        } else{
            // do something for phones running an SDK before lollipop

            intent = new Intent(SensorDataCollectorService.class.getName());
        }
        // start the service explicitly.
        // otherwise it will only run while the IPC connection is up.
        this.startService(intent);
        bindService(intent, serviceConnection, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!(turnOnTimer == null)) {
            turnOnTimer.cancel();
        }
        unbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "+++++++++++++++DSTRY");
        if (!(turnOnTimer == null)) {
            turnOnTimer.cancel();
        }
        turnAlarmOff();
        unbindService();
    }

    public void initButton() {
        btnOff = (TextView) findViewById(R.id.off_button);
        // Cancel or turn off the alarm.
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Button clicked");
                if (!currentStatus) {
                    turnOnTimer.cancel();
                }
                turnAlarmOff();
                goToInfoActivity();
            }
        });
    }

    public void goToInfoActivity() {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }

    private void turnAlarmOn() {
        updater.putBoolean(KEY, ON);
        updater.commit();
        currentStatus = mPrefs.getBoolean(KEY, OFF);
        if (currentStatus) {
            Log.e(TAG, "ON");
        }
        try {
            api.notifyAlarmStatusChanged(currentStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void turnAlarmOff() {
        updater.putBoolean(KEY, OFF);
        updater.commit();
        currentStatus = mPrefs.getBoolean(KEY, OFF);
        if (!currentStatus) {
            Log.e(TAG, "OFF");
        }
        try {
            api.notifyAlarmStatusChanged(currentStatus);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unbindService() {
        try {
            api.removeListener(collectorListener);
            unbindService(serviceConnection);
        } catch (Throwable t) {
            // catch any issues, typical for destroy routines
            // even if we failed to destroy something, we need to continue destroying
            Log.w(TAG, "Failed to unbind from the service", t);
        }
    }


}
