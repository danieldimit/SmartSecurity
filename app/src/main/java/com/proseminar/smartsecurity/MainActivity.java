package com.proseminar.smartsecurity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

// A mess. Would be reimplemened.
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    boolean mBound;

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

    private TextView tweetView;

    //private Handler handler;

    private SensorDataCollectorListener.Stub collectorListener = new SensorDataCollectorListener.Stub() {
        @Override
        public void handleSensorDataUpdated() throws RemoteException {
            // updateTweetView();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // handler = new Handler(); // handler will be bound to the current thread (UI)

        // Don't delete. Will be used later for the overview activity, when the alarm is off.
        // tweetView = (TextView) findViewById(R.id.tweet_view);

        Intent intent = new Intent(SensorDataCollectorService.class.getName());

        // start the service explicitly.
        // otherwise it will only run while the IPC connection is up.
        startService(intent);

        bindService(intent, serviceConnection, 0);

        mBound = true;

        Log.i(TAG, "Activity created");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            api.removeListener(collectorListener);
            unbindService(serviceConnection);
            mBound = false;
        } catch (Throwable t) {
            // catch any issues, typical for destroy routines
            // even if we failed to destroy something, we need to continue destroying
            Log.w(TAG, "Failed to unbind from the service", t);
        }

        Log.i(TAG, "Activity destroyed");
    }

    public void onToggle(View view) throws RemoteException {
        Intent intent = new Intent(SensorDataCollectorService.class.getName());
        if (!mBound) {
            Log.w(TAG, "++ START & BIND ++");
            startService(intent);
            bindService(intent, serviceConnection, 0);
            mBound = true;
        } else {
            Log.w(TAG, "++ UNBIND & STOP ++");
            unbindService(serviceConnection);
            api.turnServiceOff();
            mBound = false;
        }
    }

    public void onPhoneClick(View view) {
        Intent i = new Intent(this, AddPhoneActivity.class);
        startActivity(i);
    }

    public void onScanClick(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }



    // Don't delete. Will be used later for the overview activity, when the alarm is off.
    /**
    private void updateTweetView() {
        // doing this in a Handler allows to call this method safely from any thread
        // see Handler docs for more info
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    SensorDataUpdateResult result = api.getLatestUpdateResult();

                    if (result.getSensorData().isEmpty()) {
                        tweetView.setText("Sorry, no tweets yet");
                    } else {
                        StringBuilder builder = new StringBuilder();
                        for (SensorData sensorData : result.getSensorData()) {
                            builder.append(String.format("<br><b>%s</b>: %f %f %f<br>",
                                    sensorData.getSensorId(),
                                    sensorData.getTemp(),
                                    sensorData.getHumidity(),
                                    sensorData.getAccelometer()));
                        }

                        tweetView.setText(Html.fromHtml(builder.toString()));
                    }
                } catch (Throwable t) {
                    Log.e(TAG, "Error while updating the UI with tweets", t);
                }
            }
        });
    }
     */
}
