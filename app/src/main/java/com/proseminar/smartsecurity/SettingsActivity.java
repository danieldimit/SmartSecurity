package com.proseminar.smartsecurity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SettingsActivity extends AppCompatActivity {


    ArrayList<Contact> arrayOfUsers;
    ImageButton addPeople;
    //make dynamic array if i give a fuck
    ImageButton[] updaterButton = new ImageButton[100];
    TextView[] myAwesomeNameView = new TextView[100];
    TextView[] myAwesomePhoneView = new TextView[100];
    ImageButton[] destoyerButton = new ImageButton[100];
    final Context context = this;

    //database
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts.db";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NAME = "name";

    DbHandler myHandle;

    //Sensor variables:
    ImageButton addSensors;
    ImageButton[] updaterButtonSensors = new ImageButton[100];
    TextView[] myAwesomeNameViewSensors = new TextView[100];
    ImageButton[] destoyerButtonSensors = new ImageButton[100];
    SensorDbHandler mySensorHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addPeople = (ImageButton)  findViewById(R.id.button_button_add_new_contact);
        myHandle = new DbHandler(this, DATABASE_NAME, null, 1);

        addPeople.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // get prompts.xml view
                LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
                View promptView = layoutInflater.inflate(R.layout.input_dialog_add_contact, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
                alertDialogBuilder.setView(promptView);

                final EditText input2 = (EditText) promptView.findViewById(R.id.edittext);
                final EditText input1 = (EditText) promptView.findViewById(R.id.textView);

                // setup a dialog window
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if ((input1.getText().toString().trim().length() == 0) || (input2.getText().toString().trim().length() == 0)) {
                                    emptyFields();
                                } else {
                                    Contact contact = new Contact(input1.getEditableText().toString(), input2.getEditableText().toString());
                                    //contact.setName(input1.getEditableText().toString());
                                    //contact.setNumber(input2.getEditableText().toString());
                                    myHandle.addContact(contact);
                                    Intent intent = getIntent();
                                    finish();
                                    startActivity(intent);
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
        });


        LinearLayout contactslist = (LinearLayout) findViewById(R.id.list_contacts);
        ArrayList<LinearLayout> contactsReadFromFile = new ArrayList<>();
        Contact[] contacts = myHandle.databaseToString();

        if (contacts != null) {
                for (int i = 0; i < contacts.length; i++) {
                    contactslist.addView((LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item_contacts, null));
                    myAwesomeNameView[i] = (TextView) findViewById(R.id.tvName);
                    if ((contacts[i].getName()) != null) {
                        myAwesomeNameView[i].setText(contacts[i].getName());
                    }
                    myAwesomeNameView[i].setId(i + 100);
                    myAwesomePhoneView[i] = (TextView) findViewById(R.id.tvNumber);
                    if (contacts[i].getNumber() != null) {
                        myAwesomePhoneView[i].setText(contacts[i].getNumber());
                    }
                    myAwesomePhoneView[i].setId(i + 200);
                    destoyerButton[i] = (ImageButton) findViewById(R.id.button_delete);
                    destoyerButton[i].setId(i + 300);
                    destoyerButton[i].setOnClickListener(clickDestroyer);
                    updaterButton[i] = (ImageButton) findViewById(R.id.button_edit);
                    updaterButton[i].setId(i);
                    updaterButton[i].setOnClickListener(clickListener);
                }
        }

        addSensors = (ImageButton) findViewById(R.id.button_add_new_sensor);
        addSensors.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(SettingsActivity.this, SensorListActivity.class);
                startActivity(i);
            }
        });
        mySensorHandler = new SensorDbHandler(this, "doesn't matter", null ,1);
        LinearLayout sensorslist = (LinearLayout) findViewById(R.id.list_sensors);
        ArrayList<LinearLayout> sensorsReadFromFile = new ArrayList<>();
        Sensor[] sensors = mySensorHandler.databaseToString();
        if (sensors != null) {
            for (int i = 0; i < sensors.length; i++){
                sensorslist.addView((LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item_paired_sensor, null));
                myAwesomeNameViewSensors[i] = (TextView) findViewById(R.id.tvNameSensor);
                if (sensors[i].getName() != null) {
                    myAwesomeNameViewSensors[i].setText(sensors[i].getName());
                }
                myAwesomeNameViewSensors[i].setId(i+500);
                updaterButtonSensors[i] = (ImageButton) findViewById(R.id.button_edit_sensors);
                updaterButtonSensors[i].setId(i+600);
                updaterButtonSensors[i].setOnClickListener(sensorUpdater);
                destoyerButtonSensors[i] = (ImageButton) findViewById(R.id.button_delete_sensors);
                destoyerButtonSensors[i].setId(i+700);
                destoyerButtonSensors[i].setOnClickListener(sensorDestroyer);
            }
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int number = 0;
            for (int i = 0; i<100; i++){
                if (v == updaterButton[i]) {
                    number = i;
                    break;
                }
            }
            final int number1 = number;
            // Alert Dialog Code Start
            LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
            View promptView = layoutInflater.inflate(R.layout.input_dialog_update_contact, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            alertDialogBuilder.setView(promptView);

            final EditText name = (EditText) promptView.findViewById(R.id.textName);
            name.setText(myAwesomeNameView[number1].getText().toString());
            final EditText phone = (EditText) promptView.findViewById(R.id.textPhone);
            phone.setText(myAwesomePhoneView[number1].getText().toString());

            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if ((name.getEditableText().toString().trim().length() == 0) || (phone.getEditableText().toString().trim().length() == 0)) {
                               emptyFields();
                            } else {
                                Contact contactHolderOld = new Contact(myAwesomeNameView[number1].getText().toString(), myAwesomePhoneView[number1].getText().toString());
                                Contact contactHolderNew = new Contact(name.getEditableText().toString(), phone.getEditableText().toString());
                                //contact.setName(input1.getEditableText().toString());
                                //contact.setNumber(input2.getEditableText().toString());
                                myHandle.updateRow(contactHolderOld, contactHolderNew);
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
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
    };

    //method to delete entry
    View.OnClickListener clickDestroyer = new View.OnClickListener() {
        public void onClick(View v) {
            int position =0;
            for (int i = 0; i<100; i++){
                if (v == destoyerButton[i]) {
                    position = i;
                    break;
                }
            }
            final int i = position;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            alertDialogBuilder.setTitle("Delete Contact");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Click yes to delete!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Contact contactHolder = new Contact((String)myAwesomeNameView[i].getText(), (String)myAwesomePhoneView[i].getText());
                            myHandle.deleteContact(contactHolder);
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    };

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

    View.OnClickListener sensorUpdater = new View.OnClickListener() {
        public void onClick(View v) {
            int number = 0;
            for (int i = 0; i<100; i++){
                if (v == updaterButtonSensors[i]) {
                    number = i;
                    break;
                }
            }
            final int number1 = number;
            // Alert Dialog Code Start
            LayoutInflater layoutInflater = LayoutInflater.from(SettingsActivity.this);
            View promptView = layoutInflater.inflate(R.layout.input_dialog_update_sensors, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingsActivity.this);
            alertDialogBuilder.setView(promptView);

            final EditText name = (EditText) promptView.findViewById(R.id.pimpMySensorName);
            name.setText(myAwesomeNameViewSensors[number1].getText().toString());

            // setup a dialog window
            alertDialogBuilder.setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if ((name.getEditableText().toString().trim().length() == 0)) {
                                emptyFields();
                            } else {
                                Sensor sensorHolderOld = new Sensor(myAwesomeNameViewSensors[number1].getText().toString(), null);
                                Sensor sensorHolderNew = new Sensor(name.getEditableText().toString(), null);
                                mySensorHandler.updateRow(sensorHolderOld, sensorHolderNew);
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);
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
    };

    //method to delete entry
    View.OnClickListener sensorDestroyer = new View.OnClickListener() {
        public void onClick(View v) {
            int position =0;
            for (int i = 0; i<100; i++){
                if (v == destoyerButtonSensors[i]) {
                    position = i;
                    break;
                }
            }
            final int i = position;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    context);
            // set title
            alertDialogBuilder.setTitle("Delete Contact");
            // set dialog message
            alertDialogBuilder
                    .setMessage("Click yes to delete!")
                    .setCancelable(false)
                    .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            Sensor sensorHolder = new Sensor(myAwesomeNameViewSensors[i].getText().toString(), null);
                            mySensorHandler.deleteSensor(sensorHolder);
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            // if this button is clicked, just close
                            // the dialog box and do nothing
                            dialog.cancel();
                        }
                    });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();
        }
    };


    public void alarmOnOnClick(View v) {
        Intent i = new Intent(this, AlarmOnActivity.class);
        startActivity(i);
    }

    public void infoOnClick(View v) {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }


}
