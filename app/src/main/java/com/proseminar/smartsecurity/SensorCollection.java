package com.proseminar.smartsecurity;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Joachim on 26.01.2016.
 */
public class SensorCollection {

    private static ArrayList<Sensor> sensors;
    Context c;
    private Contact[] contact;
    private final String DATABASE_NAME = "contacts.db";
    private ContactDbHandler myHandle;
    private SensorDbHandler mySensorHandle1;
    private boolean notified;
    private static final String TAG = SensorCollection.class.getSimpleName();

    public SensorCollection(Context c) {

        this.c = c;
        myHandle = new ContactDbHandler(c, DATABASE_NAME, null, 1);
        mySensorHandle1 = new SensorDbHandler (c, "sensors.db", null, 1);
        notified = false;
        Sensor[] sa = mySensorHandle1.databaseToString();
        if (sa != null) {
            List<Sensor> sList = Arrays.asList(sa);
            sensors = new ArrayList<Sensor>();
            for (Sensor s : sList) {
                sensors.add(s);
            }
        } else {
            sensors = new ArrayList<Sensor>();
        }

    }

    public void update(SensorDataUpdateResult newVals, boolean alarmIsOn) {
        for (SensorData sd : newVals.getSensorData()) {
            for (Sensor sensor : sensors) {
                if (sensor.idFits(sd)) {
                    if (sensor.updateSensorData(sd)) {
                        double conf = sensor.calcRobberyConfidence();
                        if (alarmIsOn && conf >= 0.8) {
                             if (!notified) {
                                 Log.e(TAG, "++++++++++++++++++++++++ SMS SENT ++++++++++++++++++++++++");
                                 // notifySMS(sensor.getName());
                                 notified = true;
                             }
                        }
                    }
                    break;
                }
            }
        }
    }

    private void notifySMS(String room) {
        contact = myHandle.databaseToString();
        if (!(contact == null)) {
            for (Contact cont: contact) {
                Sms.sendSMS(cont.getNumber(), "SmartSecurity detected a possible threat in the " + room + ".");
            }
        }

    }

    public static void addSensor(Sensor sensor) {
        if (sensors != null) {
            sensors.add(sensor);
        }
    }

    public static void removeSensor (String sensorId) {
        if (sensors != null) {
            for (Sensor s:sensors) {
                if (s.getSensorId().equals(sensorId)) {
                    sensors.remove(s);
                    break;
                }
            }
        }
    }

    public void reset() {
        notified = false;
    }
}