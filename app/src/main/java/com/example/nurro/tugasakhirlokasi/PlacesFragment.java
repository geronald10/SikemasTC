package com.example.nurro.tugasakhirlokasi;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by nurro on 3/15/2017.
 */

public class PlacesFragment extends Fragment{
    View myView;
    ListView myList;
    ArrayAdapter<String> adapter;
    ArrayList<String> data;
    TextView textView;
    String link = "http://alnurrohman27.000webhostapp.com/TA/select.php?command=location";
    InputStream inputStream;
    String result;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        myView = inflater.inflate(R.layout.places, container, false);
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        myList = (ListView)myView.findViewById(R.id.listPlaces);
        data = new ArrayList<String>();
        getData();
        adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, data);
        myList.setAdapter(adapter);

        return myView;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_places, menu);
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle item selection
        FragmentManager fragmentManager = getFragmentManager();
        switch (item.getItemId()) {
            case R.id.action_add_place:
                fragmentManager.beginTransaction()
                        .replace(R.id.places, new AddPlaceFragment())
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if(getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
    }

    private void getData() {
        String line = null;
        try {
            URLConnection url = new URL(link).openConnection();

            HttpURLConnection con = (HttpURLConnection)url;
            //con.setRequestMethod("GET");

            InputStream inputStream = new BufferedInputStream(con.getInputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();

            while((line = br.readLine()) != null){
                sb.append(line+"\n");
            }
            inputStream.close();
            result = sb.toString();

        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(result);
            int dataLength = jsonArray.length();
            String dataTemp;
            for(int i = 0; i < dataLength; i++){
                dataTemp = null;
                dataTemp = "ID: " + jsonArray.getJSONObject(i).getString("id");
                dataTemp += "\n\tNama: " + jsonArray.getJSONObject(i).getString("name");
                dataTemp += "\n\tLatitude: " + jsonArray.getJSONObject(i).getString("latitude");
                dataTemp += "\n\tLongitude: " + jsonArray.getJSONObject(i).getString("longitude");
                dataTemp += "\n\tJangkauan: " + jsonArray.getJSONObject(i).getString("range");
                dataTemp += "\n\tAltitude: " + jsonArray.getJSONObject(i).getString("altitude");
                this.data.add(dataTemp);
            }

        }

        catch (Exception ex){
            ex.printStackTrace();
        }
    }



}
