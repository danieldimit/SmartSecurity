package com.proseminar.smartsecurity;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

public final class SensorDataUpdater implements Observer {

	private static final String TAG = SensorDataUpdater.class.getSimpleName();

	private SensorDbHandler mySensorHandler;
	private SensorData sensorData;
	private HashMap<String, SensorData> sensorDataHashMap = new HashMap<>();

	public SensorDataUpdater () {
		SyncManager manager = SyncManager.getInstance();
		manager.setObserver(this);
		sensorData = new SensorData("Name","adress",0,0,0,0,0);
	}

	/**
	 * Gathers data from all sensors and returns a SensorDataUpdateResult object.
	 * Idea: Run the connection to every sensor in separate thread, gather the data here
	 * and pack it in a SensorDataUpdateResult object, which would be than returned in
	 * this method (update())
	 * @return SensorDataUpdateResult - the data from all sensors
	 */
	public SensorDataUpdateResult update(Context x) {

		SensorDataUpdateResult result;
		mySensorHandler = new SensorDbHandler(x, "some", null, 1);

		result = new SensorDataUpdateResult();
		Sensor sList[] = mySensorHandler.databaseToString();

		if (sList != null) {

			// Set the right name.
			for (Sensor s: mySensorHandler.databaseToString()) {
				SensorData sd = sensorDataHashMap.get(s.getSensorId());
				if (sd != null) {
					if (s.getSensorId().equals(sd.getMacAddress())) {
						sd.setName(s.getName());
						result.addSensorData(sd);
						Log.e(TAG, "Updated with " + sd.getTemp() + "  " + sd.getMacAddress() + "  " + sd.getName());
					}
				}
			}
		}
		return result;
	}

	@Override
	public void update(Observable observable, Object data) {
		SensorData sd = (SensorData) data;
		sensorDataHashMap.put(sd.getMacAddress(), sd);
	}
}
