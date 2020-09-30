package com.example.wildfiredetection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Fire> {

    //The fire list that will be displayed
    private List<Fire> fireList;

    //context object
    private Context context;

    //here I am getting the fireList and context
    //so while creating the object of this adapter class I need to give fireList and context
    public ListViewAdapter(List<Fire> fireList, Context context) {
        super(context, R.layout.list_item, fireList);
        this.fireList = fireList;
        this.context = context;
    }

    //Get list item
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get layoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);

        //Create a view and inflate it from list_item.xml
        View listViewItem = inflater.inflate(R.layout.list_item, null, true);

        //Get text view
        TextView acq_date = listViewItem.findViewById(R.id.acq_date);
        TextView acq_time = listViewItem.findViewById(R.id.acq_time);
        TextView location = listViewItem.findViewById(R.id.location);
        TextView latitude = listViewItem.findViewById(R.id.latitude);
        TextView longitude = listViewItem.findViewById(R.id.longitude);

        //Get fires data to display at specified position in the adapter's dataset
        Fire fire = fireList.get(position);

        //Set values to fire elements
        acq_date.setText(fire.getAcq_date());
        acq_time.setText(fire.getAcq_time());
        location.setText(fire.getLocation());
        latitude.setText(fire.getLatitude().toString());
        longitude.setText(fire.getLongitude().toString());

        return listViewItem;
    }
}

