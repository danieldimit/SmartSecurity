package com.proseminar.smartsecurity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.UUID;

/**
 * Created by christian on 28.01.16.
 */
public class BluetoothLeConnector extends Observable {

    private BluetoothGatt bluetoothGatt;
    private Context context;
    private BluetoothAdapter adapter;
    private List<BluetoothGattService> services = new LinkedList<>();
    private LinkedList<Integer> messageQueue = new LinkedList<>(Arrays.asList(10, 11, 20, 21));


    private final UUID UUID_HUM_SERV = UUID.fromString("f000aa20-0451-4000-b000-000000000000");
    private final UUID UUID_HUM_CONF = UUID.fromString("f000aa22-0451-4000-b000-000000000000");// 0: disable, 1: enable
    private final UUID UUID_HUM_DATA = UUID.fromString("f000aa21-0451-4000-b000-000000000000");
    private final UUID UUID_HUM_PERI = UUID.fromString("f000aa23-0451-4000-b000-000000000000"); // Period in tens of milliseconds
    private final UUID UUID_ACC_SERV = UUID.fromString("f000aa10-0451-4000-b000-000000000000");
    private final UUID UUID_ACC_DATA = UUID.fromString("f000aa11-0451-4000-b000-000000000000");
    private final UUID UUID_ACC_CONF = UUID.fromString("f000aa12-0451-4000-b000-000000000000"); // 0: disable, 1: enable
    private final UUID UUID_ACC_PERI = UUID.fromString("f000aa13-0451-4000-b000-000000000000"); // Period in tens of ms


    private double humidity = 0;
    private double temperature = 0;
    private double accX = 0;
    private double accY = 0;
    private double accZ = 0;
    private double accMax = 0;


    private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            if (characteristic.getUuid().equals(UUID_HUM_DATA)) {
                calculateHumidity(characteristic);
            }
            if (characteristic.getUuid().equals(UUID_ACC_DATA)) {
                calculateAcceleration(characteristic);
            }
            if (messageQueue.size() > 0) {
                int id = messageQueue.pop();
                callFunctionByID(id);
                System.out.println("Activate Function: " + id);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {

            System.out.println("Gedämpfte Huscheln!");
            if (messageQueue.size() > 0) {
                int id = messageQueue.pop();
                callFunctionByID(id);
                System.out.println("Activate Function: " + id);
            }
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                System.out.println("Fick ja!");
                messageQueue = new LinkedList<>(Arrays.asList(10, 11, 20, 21));
                gatt.discoverServices();
            } else {
                System.out.println("No Conenction:" + status);
                connectTo(gatt.getDevice());

            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a            BluetoothGatt.discoverServices() call
            System.out.println("Services Discovered!");
            services = bluetoothGatt.getServices();
            System.out.println("Anzahl Services: " + services.size());
            /**
             * This is a self-contained function for turning on the magnetometer
             * sensor. It must be called AFTER the onServicesDiscovered callback
             * is received.
             */
            if (messageQueue.size() > 0) {
                int id = messageQueue.pop();
                callFunctionByID(id);
            }
        }
    };


    BluetoothLeConnector(Context context, BluetoothAdapter btAdapter) {
        this.context = context;
        this.adapter = btAdapter;
        //startScan();
    }

    public void disconnect() {
        bluetoothGatt.disconnect();
        bluetoothGatt.close();
    }


    public void connectTo(BluetoothDevice device) {
        if (device != null) {
            bluetoothGatt = device.connectGatt(context, false, btleGattCallback);
        } else {
            System.out.println("null device!");
        }
    }

    public void connectToString(String str) {
        BluetoothDevice device = adapter.getRemoteDevice(str);
        connectTo(device);
    }

    private void callFunctionByID(int id) {
        switch (id) {
            case 10:
                turnOnService(bluetoothGatt, UUID_HUM_SERV, UUID_HUM_CONF);
                break;
            case 11:
                System.out.println("Activate ACC");
                turnOnService(bluetoothGatt, UUID_ACC_SERV, UUID_ACC_CONF);
                break;
            case 20:
                enableNotifications(bluetoothGatt, UUID_HUM_SERV, UUID_HUM_DATA);
                break;
            case 21:
                enableNotifications(bluetoothGatt, UUID_ACC_SERV, UUID_ACC_DATA);
                break;
            default:
                break;
        }
    }

    private void turnOnService(BluetoothGatt gatt, UUID serviceUUID, UUID configUUID) {


        BluetoothGattService humService = gatt.getService(serviceUUID);
        BluetoothGattCharacteristic config = humService.getCharacteristic(configUUID);
        config.setValue(new byte[]{1}); // 01 for Humidity; 01 for +-2g deviation//NB: the config value is different for the Gyroscope
        gatt.writeCharacteristic(config);
    }

    private void enableNotifications(BluetoothGatt gatt, UUID serviceUuid, UUID dataUuid) {
        UUID CCC = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

        BluetoothGattService service = gatt.getService(serviceUuid);
        BluetoothGattCharacteristic dataCharacteristic = service.getCharacteristic(dataUuid);
        gatt.setCharacteristicNotification(dataCharacteristic, true); //Enabled locally

        BluetoothGattDescriptor config = dataCharacteristic.getDescriptor(CCC);
        config.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        gatt.writeDescriptor(config); //Enabled remotely
    }

    private void calculateHumidity(BluetoothGattCharacteristic characteristic) {

    /*calculate humididty*/
        Integer lowerTempByte = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
        Integer upperTempByte = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
        Integer lowerHumByte = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
        Integer upperHumByte = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3); // Note: interpret MSB as unsigned.
        // Humidity
        int hum = (upperHumByte << 8) + lowerHumByte;
        hum=hum-(hum%4);
        humidity = (-6f) + 125f * (hum / 65535f);
        // Temperature
        int temp = (upperTempByte << 8) + lowerTempByte;
        temperature = -46.85f + (175.72f/65536f) * (float)temp;
        // System.out.println("Humidity: " + humidity);
        // System.out.println("Temperature: " + temperature);
    }

    private void calculateAcceleration(BluetoothGattCharacteristic characteristic) {
            /*
     * The accelerometer has the range [-2g, 2g] with unit (1/64)g.
     *
     * To convert from unit (1/64)g to unit g we divide by 64.
     *
     * (g = 9.81 m/s^2)
     *
     * The z value is multiplied with -1 to coincide
     * with how we have arbitrarily defined the positive y direction.
     * (illustrated by the apps accelerometer image)
     *
     * -32 = -2g
     * -16 = -1g
     *  0 = 0g
     *  16 = 1g
     *  32 = 2g
     * */

        accX = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 0);
        accY = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 1);
        accZ = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT8, 2) * -1;
        accMax = Math.max(Math.max(Math.abs(accX), Math.abs(accY)), Math.abs(accZ));
        // System.out.println("Acc X: " + accX + " Y: " + accY + " Z: " + accZ);
        setChanged();
        notifyObservers(getSensorData());
    }

    public SensorData getSensorData() {
        BluetoothDevice dev = bluetoothGatt.getDevice();
        SensorData sens = new SensorData(dev.getName(), dev.getAddress(), temperature, humidity, accX, accY, accZ);
        return sens;
    }


}
