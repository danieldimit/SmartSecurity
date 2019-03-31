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

	private String name;
	private String macAddress;
	private double temp;
	private double humidity;
	private double accX;
	private double accY;
	private double accZ;



	public SensorData(String name, String macAddress, double temp, double humidity, double accX, double accY, double accZ) {
		this.name = name;
		this.macAddress = macAddress;
		this.temp = temp;
		this.humidity = humidity;
		this.accX = accX;
		this.accY = accY;
		this.accZ = accZ;
	}
	
	private SensorData(Parcel source) {
		name = source.readString();
		macAddress = source.readString();
		temp = source.readDouble();
		humidity = source.readDouble();
		accX = source.readDouble();
		accY = source.readDouble();
		accZ = source.readDouble();
	}

	public String getName() {
		return name;
	}

	public void setSensorId(String sensorId) {
		this.macAddress = sensorId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMacAddress() {
		return macAddress;
	}

	public double getTemp() {
		return temp;
	}

	public double getHumidity() {
		return humidity;
	}

	public double getAccX() { return accX; }

	public double getAccY() { return accY; }

	public double getAccZ() { return accZ; }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(macAddress);
		dest.writeDouble(temp);
		dest.writeDouble(humidity);
		dest.writeDouble(accX);
		dest.writeDouble(accY);
		dest.writeDouble(accZ);

	}
}
