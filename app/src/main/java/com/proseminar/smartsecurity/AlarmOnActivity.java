package com.proseminar.smartsecurity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Daniel on 23/01/2016.
 */
public class AlarmOnActivity extends AppCompatActivity {

    // Constants
    static final boolean ON = true;
    static final boolean OFF = false;
    static final String KEY = "alarm_status";
    private static final String TAG = AlarmOnActivity.class.getSimpleName();

    // Recalls the last state - ON or OFF
    private SharedPreferences mPrefs;
    private static SharedPreferences.Editor updater;
    private boolean currentStatus;

    // UI items
    private TextView tvSeconds;
    private TextView btnOff;

    // Count down timers
    private CountDownTimer turnOnTimer;

    // BIND TO SERVICE TO TELL IT TURN ON AND OFF
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPrefs = getSharedPreferences("azis", Context.MODE_PRIVATE);
        currentStatus = mPrefs.getBoolean(KEY, OFF);
        updater = mPrefs.edit();

        // Set different layouts depending on the status of the alarm.
        if (currentStatus) {
            setContentView(R.layout.activity_alarm_on);
        } else {
            setContentView(R.layout.activity_alarm_warning);
            tvSeconds = (TextView) findViewById(R.id.seconds);
        }
        initButton();
        // TODO: Should have all the connection stuff that the InfoActivity and the MainActivity have.




        if (!currentStatus) {
            // Timer counting down the time until the alarm gets turned on
            turnOnTimer = new CountDownTimer(30000, 1000) {

                public void onTick(long millisUntilFinished) {
                    tvSeconds.setText(Integer.toString((int) (millisUntilFinished / 1000)));
                    Log.e("STOP", "click");
                }

                // The timer is over the alarm turns on.
                public void onFinish() {
                    turnAlarmOn();
                    tvSeconds.setText("0");
                    setContentView(R.layout.activity_alarm_on);
                    initButton();
                }
            }.start();
        }
    }

    public void initButton() {
        btnOff = (TextView) findViewById(R.id.off_button);
        // Cancel or turn off the alarm.
        btnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "Button clicked");
                if (!currentStatus) {
                    turnOnTimer.cancel();
                }
                turnAlarmOff();
                goToInfoActivity();
            }
        });
    }

    public void goToInfoActivity() {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }

    private void turnAlarmOn() {
        updater.putBoolean(KEY, ON);
        updater.commit();
        if (mPrefs.getBoolean(KEY, ON)) {
            Log.e(TAG, "ON");
        }
    }

    private void turnAlarmOff() {
        updater.putBoolean(KEY, OFF);
        updater.commit();
        if (!mPrefs.getBoolean(KEY, ON)) {
            Log.e(TAG, "OFF");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!(turnOnTimer == null)) {
            turnOnTimer.cancel();
        }
        turnAlarmOff();
    }
}
