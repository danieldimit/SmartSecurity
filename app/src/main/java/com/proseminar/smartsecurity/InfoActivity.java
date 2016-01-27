package com.proseminar.smartsecurity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Daniel on 24/01/2016.
 */
public class InfoActivity extends AppCompatActivity {

    // Constants
    static final boolean ON = true;
    static final boolean OFF = false;
    static final String KEY = "alarm_status";
    private static final String TAG = InfoActivity.class.getSimpleName();

    // Recalls the last state - ON or OFF
    private SharedPreferences mPrefs;
    private boolean currentStatus;

    // UI Elements
    GridView gridView;

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

    private SensorDataCollectorApi api;

    //private Handler handler;

    private SensorDataCollectorListener.Stub collectorListener = new SensorDataCollectorListener.Stub() {
        @Override
        public void handleSensorDataUpdated() throws RemoteException {
            // updateTweetView();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("azis" ,Context.MODE_PRIVATE);
        currentStatus = mPrefs.getBoolean(KEY, OFF);

        Log.e(TAG, Boolean.toString(currentStatus));
        Log.e(TAG, "v on create sum pi4");
        if (currentStatus) {
            Intent i = new Intent(this, AlarmOnActivity.class);
            startActivity(i);
        } else {
            initializeInfoUI();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            api.removeListener(collectorListener);
            unbindService(serviceConnection);
        } catch (Throwable t) {
            // catch any issues, typical for destroy routines
            // even if we failed to destroy something, we need to continue destroying
            Log.w(TAG, "Failed to unbind from the service", t);
        }

        Log.i(TAG, "Activity destroyed");
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeInfoUI();
    }

    private void initializeInfoUI() {
        setContentView(R.layout.activity_info_activity);

        //Intent intent = new Intent(SensorDataCollectorService.class.getName());
        Intent intent = new Intent(this, SensorDataCollectorService.class);

        // start the service explicitly.
        // otherwise it will only run while the IPC connection is up.
        //startService(intent);
        this.startService(intent);

        //bindService(intent, serviceConnection, 0);

        Log.i(TAG, "Activity created");

        gridView = (GridView) findViewById(R.id.gridView1);
        // Construct the data source
        ArrayList<SensorData> arrayOfUsers = new ArrayList<SensorData>();
        // Create the adapter to convert the array to views
        SensorDataAdapter adapter = new SensorDataAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        gridView.setAdapter(adapter);

        // Fill with Dummy items
        for (int i=0; i<25; i++) {
            SensorData newUser = new SensorData("Nathan", 4.2, 3.2, 4.2);
            adapter.add(newUser);
        }
    }

    public void alarmOnOnClick(View v) {
        Intent i = new Intent(this, AlarmOnActivity.class);
        startActivity(i);
    }

    public void settingsOnClick(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
