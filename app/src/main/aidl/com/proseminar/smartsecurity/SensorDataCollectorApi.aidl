// SensorDataCollectorApi.aidl
package com.proseminar.smartsecurity;

// Declare any non-default types here with import statements
import com.proseminar.smartsecurity.SensorDataUpdateResult;
import com.proseminar.smartsecurity.SensorDataCollectorListener;
import android.bluetooth.BluetoothDevice;

interface SensorDataCollectorApi {

	SensorDataUpdateResult getLatestUpdateResult();

	void notifyAlarmStatusChanged(boolean alarmStatus);

	void addListener(SensorDataCollectorListener listener);

	void removeListener(SensorDataCollectorListener listener);

	void addBluetoothConnection(String s);

	void removeBluetoothConnection(String s);
}