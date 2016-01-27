package com.proseminar.smartsecurity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Daniel on 24/01/2016.
 */
public class SensorDataAdapter extends ArrayAdapter<SensorData> {
    public SensorDataAdapter(Context context, ArrayList<SensorData> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        SensorData sensorData = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_info, parent, false);
        }
        // Lookup view for data population
        TextView tvRoom = (TextView) convertView.findViewById(R.id.tvRoom);
        TextView tvTemp = (TextView) convertView.findViewById(R.id.tvTemp);
        // TODO: add sensor name to SensorData.
        // Populate the data into the template view using the data object
        tvRoom.setText(sensorData.getSensorId());
        tvTemp.setText(Double.toString(sensorData.getTemp()) + " °C");
        // Return the completed view to render on screen
        return convertView;
    }
}
