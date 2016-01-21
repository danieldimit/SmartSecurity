package com.proseminar.smartsecurity;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * A simple data class, that defines a list of SensorData objects. Implements Parcelable, so that it
 * can get sent through different processes(IPC).
 */
public final class SensorDataUpdateResult implements Parcelable {

	public static final Creator<SensorDataUpdateResult> CREATOR = new Creator<SensorDataUpdateResult>() {
		@Override
		public SensorDataUpdateResult[] newArray(int size) {
			return new SensorDataUpdateResult[size];
		}
		
		@Override
		public SensorDataUpdateResult createFromParcel(Parcel source) {
			return new SensorDataUpdateResult(source);
		}
	};
	
	private List<SensorData> sDataList;
	
	public SensorDataUpdateResult() {
		sDataList = new ArrayList<SensorData>();
	}
	
	@SuppressWarnings("unchecked")
	private SensorDataUpdateResult(Parcel source) {
		sDataList = source.readArrayList(SensorData.class.getClassLoader());
	}
	
	public void addSensorData(SensorData sData) {
		sDataList.add(sData);
	}
	
	public List<SensorData> getSensorData() {
		return sDataList;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(sDataList);
	}

}
