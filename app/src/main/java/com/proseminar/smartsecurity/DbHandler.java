package com.proseminar.smartsecurity;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.content.Context;
import android.content.ContentValues;

public class DbHandler extends SQLiteOpenHelper{

    //use if you have only one table
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contacts.db";
    public static final String TABLE_CONTACTS = "contacts";
    public static final String COLUMN_PHONE = "phone";
    public static final String COLUMN_NAME = "name";

    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_CONTACTS + "(" +
                COLUMN_PHONE + " TEXT," +
                COLUMN_NAME + " TEXT," +
                "PRIMARY KEY(" + COLUMN_PHONE + ")" +
                ");";
        db.execSQL(query);
    }

    //Add row to database
    public void addContact(Contact contact){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, contact.getName());
        values.put(COLUMN_PHONE, contact.getNumber());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_CONTACTS, null, values);
        db.close();
    }

    //Delete a contact from database
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CONTACTS + " WHERE " + COLUMN_PHONE + "=\"" + contact.getNumber() + "\"" + " AND " + COLUMN_NAME + "=\"" + contact.getName() + "\";");
        db.close();
    }

    public void updateRow(Contact contact, Contact newContact) {
        SQLiteDatabase db = getWritableDatabase();
        String strSQL = ("UPDATE " + TABLE_CONTACTS + " SET " +
                COLUMN_PHONE + " = " + "\"" + newContact.getNumber() + "\"" + ", " +
                COLUMN_NAME + " = " + "\"" + newContact.getName() + "\"" +
                " WHERE " +
                COLUMN_PHONE + " = " + "\"" + contact.getNumber() + "\"" + " AND " +
                COLUMN_NAME + " = " + "\"" + contact.getName() + "\"" + ";") ;
        db.execSQL(strSQL);
        db.close();
    }

    //get String values
    public Contact[] databaseToString() {
        SQLiteDatabase db = getWritableDatabase();
        //Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM contacts;", null);
            Contact[] contact = new Contact[db.rawQuery("SELECT " + COLUMN_PHONE + " FROM " + TABLE_CONTACTS + ";", null).getCount()];
            //SQLiteDatabase db = getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_CONTACTS + " WHERE 1";
            Cursor c = db.rawQuery(query, null);

            c.moveToFirst();
            int i = 0;
            while (!c.isAfterLast()) {
                if (c.getString(c.getColumnIndex("phone")) != null) {
                    Contact contactHolder = new Contact(
                            (c.getString(c.getColumnIndex("name"))),
                            (c.getString(c.getColumnIndex("phone")))
                    );
                    contact[i] = contactHolder;
                    c.moveToNext();
                    i++;
                }
            }
            db.close();
            return contact;
    }


}
