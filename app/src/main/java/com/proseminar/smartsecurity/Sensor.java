package com.proseminar.smartsecurity;

import android.os.Parcel;
import android.os.Parcelable;


public final class Sensor {

	private String sensorId;
	private double temp;
	private double humidity;
	private double accelometer;
	private String name;

	public Sensor(String name, String sensorId) {
		this.name = name;
		this.sensorId = sensorId;
	}

	public String getName() { return name; }

	public String getSensorId() {
		return name;
	}

	public void setSensorId(String sensorId) {
		this.sensorId = sensorId;
	}

	public void setName(String name) {
		this.name = name;
	}
}
