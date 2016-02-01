package com.proseminar.smartsecurity;

import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by christian on 31.01.16.
 */
public class SyncManager {

    private static SyncManager instance = new SyncManager();
    private static Observable observed;
    private static volatile BluetoothLeConnector bluetoothLeConnector;
    private static volatile LinkedList<Observer> observer = new LinkedList<Observer>();

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
        if (observer.size() > 0) {
            for (int i = 0; i < observer.size(); i++) {
                bluetoothLeConnector.addObserver(observer.get(i));
                System.out.println("added Observer later!");
            }
            observer = new LinkedList<Observer>();
        }
    }

    public static void setObserver(Observer obs) {
        if (bluetoothLeConnector == null) {
            observer.add(obs);
        } else {
            bluetoothLeConnector.addObserver(obs);
            System.out.println("added Observer!");
        }
    }
}