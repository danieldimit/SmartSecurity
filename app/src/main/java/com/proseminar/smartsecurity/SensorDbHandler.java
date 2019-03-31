package com.proseminar.smartsecurity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Sensor database handler class.
 */
public class SensorDbHandler extends SQLiteOpenHelper {

    //use if you have only one table
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "sensors.db";
    public static final String TABLE_SENSORS = "sensors";
    public static final String COLUMN_MAC = "mac";
    public static final String COLUMN_NAME = "name";


    public SensorDbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSORS);
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_SENSORS + "(" +
                COLUMN_NAME + " TEXT," +
                COLUMN_MAC + " TEXT," +
                "PRIMARY KEY(" + COLUMN_MAC + ")" +
                ");";
        db.execSQL(query);
    }

    //Add row to database
    public void addSensors(Sensor sensor){
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, sensor.getName());
        values.put(COLUMN_MAC, sensor.getSensorId());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_SENSORS, null, values);
        db.close();
    }

    public void deleteSensor(Sensor sensor) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_SENSORS + " WHERE " + COLUMN_NAME + "=\"" + sensor.getName() + "\"" + ";");
        db.close();
    }

    public void updateRow(Sensor sensor, Sensor newSensor) {
        SQLiteDatabase db = getWritableDatabase();
        String strSQL = ("UPDATE " + TABLE_SENSORS + " SET " +
                COLUMN_NAME + " = " + "\"" + newSensor.getName() + "\"" +
                " WHERE " +
                COLUMN_NAME + " = " + "\"" + sensor.getName() + "\"" + ";") ;
        db.execSQL(strSQL);
        db.close();
    }

    //get String values
    public Sensor[] databaseToString() {
        SQLiteDatabase db = getWritableDatabase();
        Sensor[] sensor = new Sensor[db.rawQuery("SELECT " + COLUMN_NAME + " FROM " + TABLE_SENSORS + ";", null).getCount()];
        String query = "SELECT * FROM " + TABLE_SENSORS + " WHERE 1";
        Cursor c = db.rawQuery(query, null);

        c.moveToFirst();
        int i = 0;
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex("name")) != null) {
                Sensor sensorHolder = new Sensor(
                        (c.getString(c.getColumnIndex("name"))),
                        (c.getString(c.getColumnIndex("mac")))
                );
                sensor[i] = sensorHolder;
                c.moveToNext();
                i++;
            }
        }
        db.close();
        return sensor;
    }

}
