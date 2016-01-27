package com.proseminar.smartsecurity;

import java.util.ArrayList;

/**
 * Created by Joachim on 26.01.2016.
 */
public class SensorCollection {

    private ArrayList<Sensor> sensors;

    public SensorCollection() { sensors = new ArrayList<Sensor>();}

    public void update(SensorDataUpdateResult newVals, String telNumber) {
        for (SensorData sd : newVals.getSensorData())
            for (Sensor sensor : sensors)
                if (sensor.idFits(sd)) {
                    if (sensor.updateSensorData(sd))
                        if (sensor.calcRobberyConfidence() >= 0.5)
                            Sms.sendSMS(telNumber, "SmartSecurity detected a possible threat ");
                    break;
                }


    }

    public void addSensor(Sensor sensor) {
        sensors.add(sensor);
    }

}


