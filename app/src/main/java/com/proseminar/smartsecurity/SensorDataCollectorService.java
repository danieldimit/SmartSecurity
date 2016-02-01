package com.proseminar.smartsecurity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;


/**
 * A Service class, that runs in the background and implements the connection between the
 * service process(this process) and the activity process(the UI).
 */
public class SensorDataCollectorService extends Service {

	// Bluetooth Connector
	private BluetoothLeConnector mBLEConnector;
	private BluetoothAdapter btAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	private int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 2;
	private SyncManager manager = SyncManager.getInstance();

	// Constants
	private static final String TAG = SensorDataCollectorService.class.getSimpleName();
	static final boolean ON = true;
	static final boolean OFF = false;
	static final String KEY = "alarm_status";

	// Recalls the last state - ON or OFF
	private SharedPreferences mPrefs;
	private boolean currentStatus;

	private SensorCollection logic;
	SensorDataUpdater updater;

    Context context = this;
	
	private Timer timer;
	
	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			try {
				SensorDataUpdateResult newUpdateResult = updater.update(context);
				Log.d(TAG, "+++Retrieved Data from " + newUpdateResult.getSensorData().size() + " sensor(s)+++");
				logic.update(newUpdateResult, currentStatus);

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
			logic.reset();
			currentStatus = alarmStatus;
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
				Log.e(TAG, "+++++++++++++++++++++ ADDED LISTENER +++++++++++++++++++++");
				listeners.add(listener);
			}
		}

		@Override
		public void removeListener(SensorDataCollectorListener listener)
				throws RemoteException {
			
			synchronized (listeners) {
				Log.e(TAG, "+++++++++++++++++++++ REMOVED LISTENER +++++++++++++++++++++");
				listeners.remove(listener);
			}
		}

		@Override
		public void addBluetoothConnection (String s) throws RemoteException {
			mBLEConnector.connectToString(s);
		}

		@Override
		public void removeBluetoothConnection (String macAdress) throws RemoteException {
			// Remove sensor with macAdress
			System.out.println("-----------remove BLE: " + macAdress);
			mBLEConnector.disconnect(macAdress);
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
		logic = new SensorCollection(context);
		updater = new SensorDataUpdater();

		initializeBT();
		mBLEConnector = new BluetoothLeConnector(this, btAdapter);
		manager.setBluetoothLeConnector(mBLEConnector);

		SensorDbHandler mySensorHandler = new SensorDbHandler(this, "some", null, 1);
		Sensor sList[] = mySensorHandler.databaseToString();

		if (sList != null) {
			for (Sensor s: sList) {
				mBLEConnector.connectToString(s.getSensorId());
			}
		}

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

























	private void initializeBT() {
		// Initializes Bluetooth adapter.
		final BluetoothManager bluetoothManager =
				(BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		btAdapter = bluetoothManager.getAdapter();
		if (btAdapter == null) {
			Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
			return;
		}
	}

}
