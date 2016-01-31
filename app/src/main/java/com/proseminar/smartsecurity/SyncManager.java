package com.proseminar.smartsecurity;

import android.app.Application;

import java.util.Observable;
import java.util.Observer;

/**
 * Created by christian on 31.01.16.
 */
public class SyncManager {

    private static SyncManager instance = new SyncManager();
    private static Observable observed;
    private static volatile BluetoothLeConnector bluetoothLeConnector;

    public static SyncManager getInstance() {
        return instance;
    }
    private SyncManager() {

    }

    public void registerObservable(Observable o) {
        observed = o;
    }

    public static BluetoothLeConnector getConnector() {
        return bluetoothLeConnector;
    }

    public static void setBluetoothLeConnector(BluetoothLeConnector connector) {
        bluetoothLeConnector = connector;
    }

    public static void setObserver(Observer obs) {
        bluetoothLeConnector.addObserver(obs);
    }
}