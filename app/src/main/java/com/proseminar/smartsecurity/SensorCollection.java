package com.proseminar.smartsecurity;

import android.content.Context;

import java.util.ArrayList;


/**
 * Created by Joachim on 26.01.2016.
 */
public class SensorCollection {

    private ArrayList<Sensor> sensors;
    Context c;
    private Contact[] contact;
    private static final String DATABASE_NAME = "contacts.db";
    private DbHandler mySensorHandle = new DbHandler(c, DATABASE_NAME, null, 1);

    public SensorCollection() {
        sensors = new ArrayList<Sensor>();
    }

    public void update(SensorDataUpdateResult newVals, boolean alarmIsOn) {
        for (SensorData sd : newVals.getSensorData())
            for (Sensor sensor : sensors)
                if (sensor.idFits(sd)) {
                    if (sensor.updateSensorData(sd))
                        if (alarmIsOn && sensor.calcRobberyConfidence() >= 0.5)
                            notifySMS();
                    break;
                }


    }

    public void notifySMS() {
        contact = mySensorHandle.databaseToString();
        if (!(contact == null)) {
            for (Contact cont: contact) {
                Sms.sendSMS(cont.getNumber(), "SmartSecurity detected a possible threat ");
            }
        }

    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

    public void removeSensor(String sensorId) {
        //
    }
}