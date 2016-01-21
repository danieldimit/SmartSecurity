package com.proseminar.smartsecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * A Service class, that runs in the background and implements the connection between the
 * service process(this process) and the activity process(the UI).
 */
public class SensorDataCollectorService extends Service {
	
	private static final String TAG = SensorDataCollectorService.class.getSimpleName();
	private static final int ALARM_MODE = 1;
	private static final int INFO_MODE = 0;
	
	private Timer timer;

	int counter;
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			try {
				SensorDataUpdater updater = new SensorDataUpdater();
				SensorDataUpdateResult newUpdateResult = updater.update();
				// Log.d(TAG, "Retrieved Data from " + newUpdateResult.getSensorData().size() + " sensor(s)");
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
		public void turnServiceOff() {
			stopSelf();
		}
		
		@Override
		public void addListener(SensorDataCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
				listeners.add(listener);
			}
		}

		@Override
		public void removeListener(SensorDataCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
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
			return null;
		}
	}


	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "Service created");
		counter = 0;
		/**
		 * TODO: read a file that remembers the last state of the program, because when
		 * the App gets killed the Service restarts and doesn't know if it must be in Alarm mode
		 * or Info mode.
		 * Alarm mode: Don't update info for the UI process and get updates from the sensors every
		 * 1 second.
		 * Info mode: only while UI process is bound to the Service, get updates from the sensors
		 * every 10 seconds.
		 */
		
		timer = new Timer("SensorDataCollectorTimer");
		// Update every 60 sec
		timer.schedule(updateTask, 1000L, 1000L);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "Service destroyed");
		
		timer.cancel();
		timer = null;
	}
}
