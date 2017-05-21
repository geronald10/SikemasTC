package com.example.nurro.tugasakhirlokasi;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nurro on 3/4/2017.
 */

public class GPSFragment extends Fragment{
    View myView;
    ListView myList;
    ArrayAdapter<String> adapter;
    ArrayList<String> data;
    Button searchButton;
    GPSTracker gps;
    TextView textView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView = inflater.inflate(R.layout.gps_layout, container, false);

        textView = (TextView)myView.findViewById(R.id.result);
        myList = (ListView)myView.findViewById(R.id.listGPSSearch);
        data = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, data);
        myList.setAdapter(adapter);
        searchButton = (Button)myView.findViewById(R.id.buttonSearchLocation);
        searchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // create class object
                if(gps == null) {
                    // Create an ArrayAdapter from List
                    gps = new GPSTracker(myView.getContext(), textView, myList, data);
                    gps.getLocation();
                }
                else {
                    gps.stopUsingGPS();
                    gps = null;
                }
            }
        });

        return myView;
    }

    public void onBackPressed() {
        gps.stopUsingGPS();
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    public void disableGPS() {
        if(gps != null) {
            gps.stopUsingGPS();
        }
    }
}
