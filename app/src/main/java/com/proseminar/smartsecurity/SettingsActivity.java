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
                View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
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


        LinearLayout sensorslist = (LinearLayout) findViewById(R.id.list_sensors);
        ArrayList<LinearLayout> sensorsReadFromFile = new ArrayList<>();
        for (int i=0; i<9;i++){
            sensorsReadFromFile.add((LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_item_sensors, null));
        }

        for (int i=0; i<9;i++){
            sensorslist.addView(sensorsReadFromFile.get(i));
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
            View promptView = layoutInflater.inflate(R.layout.input_dialog_update, null);
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

    public void alarmOnOnClick(View v) {
        Intent i = new Intent(this, AlarmOnActivity.class);
        startActivity(i);
    }

    public void infoOnClick(View v) {
        Intent i = new Intent(this, InfoActivity.class);
        startActivity(i);
    }


}
