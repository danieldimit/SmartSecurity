package com.proseminar.smartsecurity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SettingsActivity extends AppCompatActivity {



    ArrayList<Contact> arrayOfUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        // Dummy lists
        LinearLayout contactslist = (LinearLayout) findViewById(R.id.list_contacts);
        ArrayList<LinearLayout> contactsReadFromFile = new ArrayList<>();
        for (int i=0; i<6;i++){
            contactsReadFromFile.add((LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item_contacts, null));
        }

        for (int i=0; i<6;i++){
            contactslist.addView(contactsReadFromFile.get(i));
        }

        LinearLayout sensorslist = (LinearLayout) findViewById(R.id.list_sensors);
        ArrayList<LinearLayout> sensorsReadFromFile = new ArrayList<>();
        for (int i=0; i<9;i++){
            sensorsReadFromFile.add((LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item_sensors, null));
        }

        for (int i=0; i<9;i++){
            sensorslist.addView(sensorsReadFromFile.get(i));
        }


    }

    public void alarmOnOnClick(View v) {
        Intent i = new Intent(this, AlarmOnActivity.class);
        startActivity(i);
    }

    public void infoOnClick(View v) {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }



}
