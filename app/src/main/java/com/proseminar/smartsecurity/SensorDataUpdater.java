package com.proseminar.smartsecurity;

import android.content.Context;
import android.util.Log;
import java.util.Observable;
import java.util.Observer;

public final class SensorDataUpdater implements Observer {

	private static final String TAG = SensorDataUpdater.class.getSimpleName();

	private SensorDbHandler mySensorHandler;
	private SensorData sensorData;

	public SensorDataUpdater () {
		SyncManager manager = SyncManager.getInstance();
		manager.setObserver(this);
		sensorData = new SensorData("","",0,0,0,0,0);
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
				if(s.getSensorId().equals(sensorData.getMacAddress())) {
					sensorData.setName(s.getName());
					break;
				}
			}
			result.addSensorData(sensorData);
			Log.e(TAG, "Updated with " + String.format("%.2f", sensorData.getTemp()) +  "  " +
					sensorData.getMacAddress() + "  " + sensorData.getName());
		}
		return result;
	}

	@Override
	public void update(Observable observable, Object data) {
		sensorData = (SensorData) data;
	}
}
