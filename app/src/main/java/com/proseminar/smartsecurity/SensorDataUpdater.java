package com.proseminar.smartsecurity;

import android.util.Log;

public final class SensorDataUpdater {

	private static final String TAG = SensorDataUpdater.class.getSimpleName();


	/**
	 * For Christian
	 * Gathers data from all sensors and returns a SensorDataUpdateResult object.
	 * Idea: Run the connection to every sensor in separate thread, gather the data here
	 * and pack it in a SensorDataUpdateResult object, which would be than returned in
	 * this method (update())
	 * @return SensorDataUpdateResult - the data from all sensors
	 */
	public SensorDataUpdateResult update() {

		// Example for Data from one sensor
		SensorDataUpdateResult result = new SensorDataUpdateResult();
		String id = "asdf";
		double temp = 24.1;
		double hum = 29.4;
		double acc = 1.2;
		SensorData exampleSD = new SensorData(id, temp, hum, acc);
		result.addSensorData(exampleSD);
		// End of example

		return result;
	}
}
