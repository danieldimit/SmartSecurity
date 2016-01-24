package com.proseminar.smartsecurity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Daniel on 23/01/2016.
 */
public class AlarmOnActivity extends AppCompatActivity {

    TextView tvSeconds;
    TextView cancel;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_on);
        tvSeconds = (TextView) findViewById(R.id.seconds);
        cancel = (TextView) findViewById(R.id.cancel_button);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToInfoActivity();
                Log.e("STOP", "Cancel clicked");
                timer.cancel();
            }
        });

        timer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvSeconds.setText(Integer.toString((int) (millisUntilFinished / 1000)));
                Log.e("STOP", "click");
            }

            public void onFinish() {
                cancel.setText(R.string.turnOffButton);
                tvSeconds.setText("0");
            }

        }.start();
    }

    public void goToInfoActivity() {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }


}
