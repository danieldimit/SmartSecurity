package com.proseminar.smartsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Daniel on 24/01/2016.
 */
public class InfoActivity extends AppCompatActivity {

    GridView gridView;

    static final String[] numbers = new String[] {
            "A", "B", "C", "D", "E",
            "F", "G", "H", "I", "J"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_info_activity);

        gridView = (GridView) findViewById(R.id.gridView1);

        // Construct the data source
        ArrayList<SensorData> arrayOfUsers = new ArrayList<SensorData>();
        // Create the adapter to convert the array to views
        SensorDataAdapter adapter = new SensorDataAdapter(this, arrayOfUsers);
        // Attach the adapter to a ListView
        gridView.setAdapter(adapter);

        for (int i=0; i<25; i++) {
            SensorData newUser = new SensorData("Nathan", 4.2, 3.2, 4.2);
            adapter.add(newUser);
        }
    }
















    public void alarmOnOnClick(View v) {
        Intent i = new Intent(this, AlarmOnActivity.class);
        startActivity(i);
    }

    public void settingsOnClick(View v) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
}
