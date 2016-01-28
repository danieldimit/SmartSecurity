package com.proseminar.smartsecurity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SensorRUMEN extends AppCompatActivity {


    Button mButton;
    EditText macText;
    EditText nameText;
    SensorDbHandler mySensorHandle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_list);
        mButton = (Button)findViewById(R.id.click_me_baby);
        nameText = (EditText) findViewById(R.id.enter_me_name_sensor);
        macText = (EditText) findViewById(R.id.enter_me_mac_adress);
        mySensorHandle = new SensorDbHandler(this, "doesn't matter", null ,1);

        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SensorData sensor = new SensorData(nameText.getText().toString(), macText.getText().toString(), 0,0,0);
                //mySensorHandle.addSensors(sensor);
                Intent i = new Intent(SensorRUMEN.this, SettingsActivity.class);
                startActivity(i);
            }
        });
    }
}
