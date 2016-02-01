package com.proseminar.smartsecurity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Daniel on 27/01/2016.
 */
public class SensorListActivity extends AppCompatActivity {

    private static final String TAG = SensorListActivity.class.getSimpleName();

    SensorDbHandler mySensorHandle;
    final Context context = this;
    private Handler mHandler = new Handler();
    private BluetoothLeConnector mBLEConnector;
    private BluetoothAdapter btAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    private int MY_PERMISSIONS_REQUEST_ACCESS_LOCATION = 2;
    //private LinkedList<BluetoothDevice> deviceList = new LinkedList<>();
    //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
    ArrayList<String> listItems = new ArrayList<String>();

    //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW
    ArrayAdapter<String> adapter;
    private HashMap<String, BluetoothDevice> deviceList = new HashMap<String, BluetoothDevice>();
    private SyncManager manager = SyncManager.getInstance();

    private SensorDataCollectorApi api;

    private SensorDataCollectorListener.Stub collectorListener = new SensorDataCollectorListener.Stub() {
        @Override
        public void handleSensorDataUpdated() throws RemoteException {
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service connection established");

            // that's how we get the client side of the IPC connection
            api = SensorDataCollectorApi.Stub.asInterface(service);
            try {
                api.addListener(collectorListener);
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to add listener", e);
            }
            // updateTweetView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service connection closed");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensors_in_range);
        mySensorHandle = new SensorDbHandler(this, "doesn't matter", null ,1);
        System.out.println("Sensoren");



        final ListView listView = (ListView) findViewById(R.id.list_sensors);
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        listView.setAdapter(adapter);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (String) (listView.getItemAtPosition(position));
                BluetoothDevice device = deviceList.get(selectedFromList);
                String sensor_mac = device.getAddress();
                setSensorName(sensor_mac);
                stopScan();
                // mBLEConnector.connectTo(device);
            }

        });

        //---
        System.out.println("Created!");
        checkPermissions();
        initializeBT();
        // mBLEConnector = manager.getConnector();
        //manager.setBluetoothLeConnector(mBLEConnector);
        startScan(10000);
    }

    private void checkPermissions() {
        // Use this check to determine whether BLE is supported on the device. Then
        // you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_LOCATION);

        }
        if (btAdapter != null && !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
    }
    private void initializeBT() {
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = bluetoothManager.getAdapter();
        if (btAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }


    private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            // your implementation here
            newDeviceFound(device);
        }
    };

    public void startScan(int period) {
        btAdapter.startLeScan(leScanCallback);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                btAdapter.stopLeScan(leScanCallback);
            }
        }, period);
    }

    public void stopScan() {
        btAdapter.stopLeScan(leScanCallback);
    }

    private void newDeviceFound(BluetoothDevice device) {
        if (device != null) {
            if (!deviceList.containsValue(device)) {
                String name = device.getName();
                String address = device.getAddress();
                String s = name + " : " + address;
                deviceList.put(s, device);
                listItems.add(s);
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void setSensorName(final String macAdress) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(SensorListActivity.this);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_add_sensor, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SensorListActivity.this);
        alertDialogBuilder.setView(promptView);

        final EditText name = (EditText) promptView.findViewById(R.id.textView);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (name.getText().toString().trim().length() == 0) {
                            emptyFields();
                        } else {
                            Sensor sensor = new Sensor(name.getText().toString(), macAdress);
                            mySensorHandle.addSensors(sensor);
                            try {
                                api.addBluetoothConnection(macAdress);
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            Intent i = new Intent(SensorListActivity.this, SettingsActivity.class);
                            SensorCollection.addSensor(sensor);
                            finish();
                            startActivity(i);
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public void emptyFields(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);

        // set title
        alertDialogBuilder.setTitle("Field is empty!");

        // set dialog message
        alertDialogBuilder
                .setMessage("You must fill all fields!")
                .setCancelable(false)
                .setPositiveButton("Got It", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent;
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
            // Do something for lollipop and above versions

            intent = new Intent(SensorDataCollectorService.class.getCanonicalName());
            // This is the key line that fixed everything for me
            intent.setPackage("com.proseminar.smartsecurity");
        } else{
            // do something for phones running an SDK before lollipop

            intent = new Intent(SensorDataCollectorService.class.getName());
        }
        // start the service explicitly.
        // otherwise it will only run while the IPC connection is up.
        this.startService(intent);
        this.bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }


    @Override
    protected void onPause() {
        super.onPause();
        try {
            api.removeListener(collectorListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        unbindService(serviceConnection);
    }
}
