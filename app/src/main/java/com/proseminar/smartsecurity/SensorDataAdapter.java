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
        TextView tvHumid = (TextView) convertView.findViewById(R.id.tvHumid);

        // Populate the data into the template view using the data object
        tvRoom.setText(sensorData.getName());
        tvTemp.setText(String.format("%.1f", sensorData.getTemp()) + "°C");
        tvHumid.setText(String.format("%.1f", sensorData.getHumidity()) + "%");

        // Return the completed view to render on screen
        return convertView;
    }
}
