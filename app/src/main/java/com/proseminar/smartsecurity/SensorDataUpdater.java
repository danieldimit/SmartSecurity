package com.proseminar.smartsecurity;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public final class SensorDataUpdater {

	private static final String TAG = SensorDataUpdater.class.getSimpleName();

	SensorDbHandler mySensorHandler;

	/**
	 * For Christian
	 * Gathers data from all sensors and returns a SensorDataUpdateResult object.
	 * Idea: Run the connection to every sensor in separate thread, gather the data here
	 * and pack it in a SensorDataUpdateResult object, which would be than returned in
	 * this method (update())
	 * @return SensorDataUpdateResult - the data from all sensors
	 */
	public SensorDataUpdateResult update(Context x) {


		SensorDataUpdateResult result = new SensorDataUpdateResult();
		SensorData sd;
		Sensor[] sList;
		ArrayList<SensorData> sdList = new ArrayList<SensorData>();
		mySensorHandler = new SensorDbHandler(x, "doesn't matter", null ,1);

		sList = mySensorHandler.databaseToString();

		// Convert to Sensor Data
		if (sList != null) {
			for (Sensor s: sList) {
				sd = new SensorData(s.getName(), s.getSensorId(), genTemp(), 0 ,0);
				result.addSensorData(sd);
			}
		}


		// Example for Data from one sensor

		String id = "asdf";
		String name = "ivan";
		double temp = 24.1;
		double hum = 29.4;
		double acc = 1.2;
		SensorData exampleSD = new SensorData(name, id, temp, hum, acc);

		// End of example

		return result;
	}

	private int genTemp() {
		int min = 15;
		int max = 20;
		int value = max - min;

		Random r = new Random();
		int i1 = r.nextInt(value) + min;
		return i1;
	}
}
