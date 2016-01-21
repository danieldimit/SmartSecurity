// SensorDataCollectorApi.aidl
package com.proseminar.smartsecurity;

// Declare any non-default types here with import statements
import com.proseminar.smartsecurity.SensorDataUpdateResult;
import com.proseminar.smartsecurity.SensorDataCollectorListener;

interface SensorDataCollectorApi {

	SensorDataUpdateResult getLatestUpdateResult();

	void turnServiceOff();

	void addListener(SensorDataCollectorListener listener);

	void removeListener(SensorDataCollectorListener listener);
}