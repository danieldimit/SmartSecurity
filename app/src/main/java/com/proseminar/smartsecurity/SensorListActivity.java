package com.proseminar.smartsecurity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Daniel on 27/01/2016.
 */
public class SensorListActivity extends AppCompatActivity {

    SensorDbHandler mySensorHandle;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_sensors_in_range);
        mySensorHandle = new SensorDbHandler(this, "doesn't matter", null ,1);

        final ListView listView = (ListView) findViewById(R.id.list_sensors);
        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item_add_sensor, R.id.sensorName, values);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                int itemPosition = position;
                String sensor_name = (String) listView.getItemAtPosition(position);
                setSensorName(sensor_name);
            }

        });
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
                            Intent i = new Intent(SensorListActivity.this, SettingsActivity.class);
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
}
