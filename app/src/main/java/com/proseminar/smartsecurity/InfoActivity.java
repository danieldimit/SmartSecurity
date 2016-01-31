package com.proseminar.smartsecurity;

import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * Created by Daniel on 24/01/2016.
 */
public class InfoActivity extends AppCompatActivity {

    // Constants
    static final boolean ON = true;
    static final boolean OFF = false;
    static final String KEY = "alarm_status";
    private static final String TAG = InfoActivity.class.getSimpleName();

    Resources res;

    // Recalls the last state - ON or OFF
    private SharedPreferences mPrefs;
    private boolean currentStatus;

    boolean connected;

    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    private final static int REQUEST_ENABLE_BT=1;

    // UI Elements
    GridView gridView;

    List<SensorData> sDataList;

    Context context = this;

    Thread t;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connection established");

            // that's how we get the client side of the IPC connection
            api = SensorDataCollectorApi.Stub.asInterface(service);
            try {
                api.addListener(collectorListener);
                connected = true;
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to add listener", e);
            }
            updateGridView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service connection closed");
            connected = false;
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

    private Timer timer;
    private TimerTask updateInfo = new TimerTask() {
        @Override
        public void run() {
            updateGridView();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("latest_alarm_status", Context.MODE_PRIVATE);
        currentStatus = mPrefs.getBoolean(KEY, OFF);

        Log.e(TAG, Boolean.toString(currentStatus));
        if (currentStatus) {
            Intent i = new Intent(this, AlarmOnActivity.class);
            startActivity(i);
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
        connected = false;
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        initializeInfoUI();

        res = getResources();
        final int updateSeconds = res.getInteger(R.integer.ui_update_info_page);

        t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(updateSeconds);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateGridView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };
        t.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        t.interrupt();
    }

    private void initializeInfoUI() {
        setContentView(R.layout.activity_info_activity);

        //Intent intent = new Intent(SensorDataCollectorService.class.getName());
        Intent intent;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions

            //intent = new Intent(this, SensorDataCollectorService.class);

            intent = new Intent(SensorDataCollectorService.class.getCanonicalName());
            // This is the key line that fixed everything for me
            intent.setPackage("com.proseminar.smartsecurity");
        } else{
            // do something for phones running an SDK before lollipop

            intent = new Intent(SensorDataCollectorService.class.getName());
        }
        this.startService(intent);

        this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        Log.i(TAG, "Activity created");

        gridView = (GridView) findViewById(R.id.gridView1);

    }

    public void alarmOnOnClick(View v) {
        Intent i = new Intent(this, AlarmOnActivity.class);
        startActivity(i);
    }

    public void settingsOnClick(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void overviewOnClick(View v) {
        updateGridView();
    }


    private void updateGridView() {
        // Construct the data source
        ArrayList<SensorData> arrayOfUsers = new ArrayList<SensorData>();
        // Create the adapter to convert the array to views
        SensorDataAdapter adapter = new SensorDataAdapter(context, arrayOfUsers);
        // Attach the adapter to a ListView
        gridView.setAdapter(adapter);
        if (api != null) {
            try {
                SensorDataUpdateResult sdur = api.getLatestUpdateResult();
                sDataList = sdur.getSensorData();
                // Fill with Dummy items
                if (!(sDataList == null)) {
                    for (SensorData sd : sDataList) {
                        adapter.add(sd);
                    }
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }


}
