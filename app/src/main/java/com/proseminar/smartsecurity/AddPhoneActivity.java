package com.proseminar.smartsecurity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


public class AddPhoneActivity extends AppCompatActivity {

    // Layout elements
    Button mButton;
    EditText phoneText;
    EditText nameText;
    EditText mName;
    EditText mPhone;

    FileOutputStream fos;
    String FILENAME = "eatShit.txt";
    FileInputStream fis;
    String[] mList;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone);
        mButton = (Button)findViewById(R.id.button3);
        nameText = (EditText) findViewById(R.id.editText);
        phoneText = (EditText) findViewById(R.id.editText2);

        mList = readFromFile();
        if (mList != null) {
            if (mList.length == 2) {
                nameText.setText(mList[0]);
                phoneText.setText(mList[1]);
            }
        }


        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mPhone   = (EditText)findViewById(R.id.editPhone);
                mName = (EditText)findViewById(R.id.editName);
                if ((mName.getText().toString().trim().length() == 0) || (mPhone.getText().toString().trim().length() == 0)) {
                    Log.w("myApp", "stupid bastard");
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
                } else {
                    String data = mName.getText().toString() + ':' + mPhone.getText().toString();
                    writeToFile(data);
                    mName.getText().clear();
                    mPhone.getText().clear();
                    mList = readFromFile();
                    if (mList.length == 2) {
                        nameText.setText(mList[0]);
                        phoneText.setText(mList[1]);
                    }
                }
            }
        });
    }

    private void writeToFile(String data) {
        try {
            fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String[] readFromFile() {
        String collected = null;
        try {
            fis = openFileInput(FILENAME);
            byte[] dataArray = new byte[fis.available()];
            while (fis.read(dataArray) != -1) {
                collected = new String(dataArray);
            }
            fis.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] result = null;
        try {
            result = collected.split(":");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return result;
    }

}
