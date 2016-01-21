package com.proseminar.smartsecurity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple data class, that defines SensorData object. Implements Parcelable, so that it
 * can get sent through different processes(IPC).
 */
public final class SensorData implements Parcelable {
	
	public static final Creator<SensorData> CREATOR = new Creator<SensorData>() {
		@Override
		public SensorData createFromParcel(Parcel source) {
			return new SensorData(source);
		}

		@Override
		public SensorData[] newArray(int size) {
			return new SensorData[size];
		}
	};
	
	private String sensorId;
	private double temp;
	private double humidity;
	private double accelometer;

	public SensorData(String sensorId, double temp, double humidity, double accelometer) {
		this.sensorId = sensorId;
		this.temp = temp;
		this.humidity = humidity;
		this.accelometer = accelometer;
	}
	
	private SensorData(Parcel source) {
		sensorId = source.readString();
		temp = source.readDouble();
		humidity = source.readDouble();
		accelometer = source.readDouble();
	}

	public String getSensorId() {
		return sensorId;
	}

	public double getTemp() {
		return temp;
	}

	public double getHumidity() {
		return humidity;
	}

	public double getAccelometer() {
		return accelometer;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(sensorId);
		dest.writeDouble(temp);
		dest.writeDouble(humidity);
		dest.writeDouble(accelometer);
	}
}
