package com.proseminar.smartsecurity;

import android.util.Log;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Joachim on 26.01.2016.
 */
public class Sensor {
	private static int numberOfPastValues = 60;
	private static int numberOfPredictedValues = 5;
	private static int weightNewerValues = 3;
	private static double halfConfCap = 0.2;

	private static ArrayList<String> sensorNames = new ArrayList<String>();

	private String macAddress;
	private String name;
	private double[] temperature;



	private double[] accelerometer;
	private int valIndex;
	private int dataCounter;
	
	private double[] newtemps[];
	private double x;
	private double y;
	private double z;
	private double xold;
	private double yold;
	private double zold;
	private static newCap = 0.1;

	public Sensor(String name, String macAddress) {
		for (String str : sensorNames) {
			if (str.equals(macAddress)) {
				throw new IllegalArgumentException("The device with MAC-Address:  "
						+ macAddress + ", is already paired.");
			}
		}

		this.macAddress = macAddress;
		this.name = name;
		temperature = new double[numberOfPastValues];
		valIndex = 0;
		dataCounter = 0;
		
		newtemps = new double[6];
	}

	public boolean idFits(SensorData sd) {
		return macAddress.equals(sd.getMacAddress());
	}

	public void resetData() {
		temperature = new double[numberOfPastValues];
		accelerometer = new double[numberOfPastValues];
		valIndex = 0;
		dataCounter = 0;
	}

	// adds the newest value to the array and returns true if there's enough data collected to start identifying possible threats
	public boolean updateSensorData(SensorData sd) {
		double newTemp = sd.getTemp();
		
		newtemps[5] = newtemps[4];
		newtemps[4] = newtemps[3];
		newtemps[3] = newtemps[2];
		newtemps[2] = newtemps[1];
		newtemps[1] = newtemps[0];
		newtemps[0] = newTemp;
		
		if (dataCounter < 30)
			dataCounter++;
		
		xold = x;
		yold = y;
		zold = z;
		x = sd.getAccX();
		y = sd.getAccY();
		z = sd.getAccZ();
		
		if (dataCounter < 30)
			return false;
		else
			return true;
		
		//temperature[valIndex] = newTemp;
		//valIndex++;
		//if (valIndex == numberOfPastValues)
		//	valIndex = 0;
s
		//if (dataCounter == numberOfPastValues) {
		//	return true;
		//} else {
		//	dataCounter++;
		//	return false;
		//}
	}

	// returns how confident the sensor is that the newest values indicate a threat
	public double calcRobberyConfidence() {
		if (dataCounter < 30)
			return 0.0d;
		
		double sum = newtemps[]
		
		for (int i = 5; i > 0; i--)
		{
			sum += Math.pow(Math.abs(newtemps[i] - newtemps[i - 1]), (4 / 3));
		}
		
		double dx = Math.Abs(x - xold);
		double dy = Math.Abs(y - yold);
		double dz = Math.Abs(z - zold);
		
		If (dx >= 2.0d || dy >= 2.0d || dz >= 2.0d)
			return 1.0d;
		
		
		return Math.min(sum / (newCap * 2), 1);
		

		// -------------- Regression
		/*
		double sum_w = 0;
		double sum_wxy = 0;
		double sum_wx = 0;
		double sum_wy = 0;
		double sum_wx2 = 0;

		int totalValues = numberOfPastValues - numberOfPredictedValues;
		for (int x = 0; x < totalValues; x++) {
			int pastI = valIndex + x;
			if (pastI >= numberOfPastValues)
				pastI -= numberOfPastValues;

			double w = ((x + 1) * 3 / totalValues);

			sum_w += w;
			sum_wxy += w * x * temperature[pastI];
			sum_wx += w * x;
			sum_wy += w * temperature[pastI];
			sum_wx2 += w * x * x;
		}

		double delta = sum_w * sum_wx2 - sum_wx * sum_wx;

		double m = (sum_w * sum_wxy - sum_wx * sum_wy) / delta;
		double b = (sum_wx2 * sum_wy - sum_wx * sum_wxy) / delta;

		// --------------- Interpolation

		double[] predictedValues = new double[numberOfPredictedValues];
		for (int x = numberOfPastValues - numberOfPredictedValues; x < numberOfPastValues; x++) {
			predictedValues[x - numberOfPastValues + numberOfPredictedValues] = m * x + b;
		}

		// ---------------- Comparison

		double sum = 0;

		for (int predI = 0; predI < numberOfPredictedValues; predI++) {
			int pastI = valIndex - numberOfPredictedValues + predI;
			if (pastI < 0)
				pastI = numberOfPastValues + pastI;
			if (pastI == numberOfPastValues)
				pastI = 0;

			double diff = predictedValues[predI] - temperature[pastI];
			if (diff > 0)
				sum += diff * diff;
		}

		// ----------------- Confidence

		double confidence = Math.min(sum / (halfConfCap * 2), 1);
		*/
		
		return confidence;
	}

	public String getSensorId() { return macAddress; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public double[] getTemperature() {
		return temperature;
	}

	public double[] getAccelerometer() {
		return accelerometer;
	}

}