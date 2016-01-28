package com.proseminar.smartsecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

/**
 * A Service class, that runs in the background and implements the connection between the
 * service process(this process) and the activity process(the UI).
 */
public class SensorDataCollectorService extends Service {

	// Constants
	private static final String TAG = SensorDataCollectorService.class.getSimpleName();
	static final boolean ON = true;
	static final boolean OFF = false;
	static final String KEY = "alarm_status";

	// Recalls the last state - ON or OFF
	private SharedPreferences mPrefs;
	private boolean currentStatus;


	private SensorCollection logic;

    Context context = this;
	
	private Timer timer;

	int counter;
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			try {
				SensorDataUpdater updater = new SensorDataUpdater();
				SensorDataUpdateResult newUpdateResult = updater.update(context);
				Log.d(TAG, "++++++++++++++Retrieved Data from " + newUpdateResult.getSensorData().size() + " sensor(s)");
				logic.update(newUpdateResult, currentStatus);
				counter++;
				Log.e(TAG, Integer.toString(counter));

				synchronized (latestUpdateResultLock) {
					latestUpdateResult = newUpdateResult;
				}
			
				synchronized (listeners) {
					for (SensorDataCollectorListener listener : listeners) {
						try {
							listener.handleSensorDataUpdated();
						} catch (RemoteException e) {
							Log.w(TAG, "Failed to notify listener " + listener, e);
						}
					}
				}
			} catch (Throwable t) { /* you should always ultimately catch 
									   all exceptions in timer tasks, or 
									   they will be sunk */
				Log.e(TAG, "Failed to retrieve the sensor results", t);
			}
		}
	};



	
	private final Object latestUpdateResultLock = new Object();
	
	private SensorDataUpdateResult latestUpdateResult = new SensorDataUpdateResult();

	private List<SensorDataCollectorListener> listeners = new ArrayList<SensorDataCollectorListener>();

	/**
	 * Implementation of the AIDL SensorDataCollectorApi, that is defined in the aidl folder.
	 */
	private SensorDataCollectorApi.Stub apiEndpoint = new SensorDataCollectorApi.Stub() {
		
		@Override
		public SensorDataUpdateResult getLatestUpdateResult() throws RemoteException {
			synchronized (latestUpdateResultLock) {
				return latestUpdateResult;
			}
		}

		@Override
		public void notifyAlarmStatusChanged(boolean alarmStatus) {
			if (alarmStatus == ON) {
                goInAlarmMode();
            } else {
                goInInfoMode();
            }
		}
		
		@Override
		public void addListener(SensorDataCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
				Log.e(TAG, "+++++++++++++++++++++ ADDED LISTENER");
				listeners.add(listener);
			}
		}

		@Override
		public void removeListener(SensorDataCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
				Log.e(TAG, "+++++++++++++++++++++ REMOVED LISTENER+++++++++++++++++++++");
				listeners.remove(listener);
			}
		}
		
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		if (SensorDataCollectorService.class.getName().equals(intent.getAction())) {
			Log.d(TAG, "Bound by intent " + intent);
			return apiEndpoint;
		} else {
			Log.d(TAG, "Denied bounding");
			return null;
		}
	}


    /**
     * Read a file that remembers the last state of the program, because when
     * the App gets killed the Service restarts and doesn't know if it must be in Alarm mode
     * or Info mode.
     * Alarm mode: Don't update info for the UI process and get updates from the sensors every
     * 1 second.
     * Info mode: only while UI process is bound to the Service, get updates from the sensors
     * every 10 seconds.
     */
	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service created");
		counter = 0;
		logic = new SensorCollection();

        timer = new Timer ("SensorDataCollectorTimer");
        timer.schedule(updateTask, 5000L, 1000L);

        mPrefs = getSharedPreferences("latest_alarm_status", Context.MODE_PRIVATE);
        currentStatus = mPrefs.getBoolean(KEY, ON);

        if (currentStatus) {
            goInAlarmMode();
        } else {
            goInInfoMode();
        }

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service destroyed");

        timer.cancel();
        timer = null;
    }

	private void goInAlarmMode() {

		Log.i(TAG, "++++++++++++++ alarm mode on");
	}

	private void goInInfoMode() {

		Log.i(TAG, "++++++++++++++ info mode on");
	}
}
