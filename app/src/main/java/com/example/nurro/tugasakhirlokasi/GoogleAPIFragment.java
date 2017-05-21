package com.example.nurro.tugasakhirlokasi;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzip.runOnUiThread;

/**
 * Created by nurro on 3/4/2017.
 */

public class GoogleAPIFragment extends Fragment {
    private View myView;
    private ListView myList;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> data;
    private Button searchAPIButton;
    private GoogleAPITracker googleAPI;
    private TextView textView;
    private Spinner methodList;
    private String method = null;
    private boolean enableButton = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        this.myView = inflater.inflate(R.layout.google_api_layout, container, false);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        this.textView = (TextView)myView.findViewById(R.id.resultAPI);

        this.methodList = (Spinner)myView.findViewById(R.id.methodList);
        final String[] items = new String[]{"KNN", "XY", "Regresi Linier"};
        this.methodList.setAdapter(new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_dropdown_item, items));
        this.methodList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                method = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        this.myList = (ListView)myView.findViewById(R.id.listGPSAPISearch);
        this.data = new ArrayList<String>();
        this.adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, data);
        this.myList.setAdapter(this.adapter);

        this.searchAPIButton = (Button)myView.findViewById(R.id.buttonSearchAPILocation);
        this.searchAPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if(googleAPI == null && method != null) {
                    googleAPI = new GoogleAPITracker(myView.getContext(), textView, myList, method, data);
                }
                else {
                    if (!googleAPI.isGoogleApiClientConnected()) {
                        googleAPI.getLocation();
                    }
                    else if (method == null) {
                        Toast.makeText(myView.getContext(), "Pilih Metode", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Log.d("Finish", "Finish API");
                        googleAPI.stopUsingAPI();
                    }
                }
            }
        });


        return myView;
    }

    public void onBackPressed() {
        googleAPI.stopUsingAPI();
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    public void disableGPS() {
        if(googleAPI != null) {
            googleAPI.stopUsingAPI();
        }
    }


}