package com.example.nurro.tugasakhirlokasi;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by nurro on 1/27/2017.
 */

public class GPSTracker extends Service implements LocationListener {
    //Variable for Context
    private final Context context;

    //Variable for ListView
    private ListView myList;
    private ArrayList<String>data;
    private ArrayAdapter<String> arrayAdapter;

    //Variable for TextView
    private TextView textView;

    //Flag for GPS Status
    //private boolean isGPSOn = false;
    private boolean canGetLocation = false;

    //Flag for network status
    //private boolean isNetworkOn = false;

    //Flag for Provider status
    private boolean isOn = false;

    //Variable for Attribute GPS
    private String providerName;
    private Location location;
    private double latitude;
    private double longitude;
    private double accuracy;
    private double altitude;

    // The minimum distance to change Updates in meters
    private static final long minDistance = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long minTime = 1000 * 1 * 1; // 1 menit

    // Max iteration
    private int iteration = 0;

    //Declaring a Location Manager
    protected LocationManager locationManager;

    // Connection to SERVER
    private String link = "http://alnurrohman27.000webhostapp.com/TA/select.php?command=location";
    private String[][] dataPlaces;
    private InputStream inputStream = null;
    private String line;
    private String result;
    private int dataLength;
    private String[][] dataIteration;

    //Set Context from Constructor
    public GPSTracker(Context context, TextView textView, ListView myList, ArrayList<String> data) {
        this.context = context;
        this.myList = myList;
        this.arrayAdapter = (ArrayAdapter<String>)myList.getAdapter();
        this.textView = textView;
        this.data = data;
        location = null;
        getPlaces();
    }

    private void getCriteriaProvider() {
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(Criteria.POWER_HIGH); // Chose your desired power consumption level.
        criteria.setAccuracy(Criteria.ACCURACY_FINE); // Choose your accuracy requirement.
        criteria.setSpeedRequired(false); // Chose if speed for first location fix is required.
        criteria.setAltitudeRequired(true); // Choose if you use altitude.
        criteria.setBearingRequired(false); // Choose if you use bearing.
        criteria.setCostAllowed(true); // Choose if this provider can waste money :-)

        providerName = locationManager.getBestProvider(criteria, true);
    }

    public void getLocation() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        try {
            Toast.makeText(this.context, "Pencarian Dimulai", Toast.LENGTH_SHORT).show();
            clearListData();
            dataIteration = new String[1][2];
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
            getCriteriaProvider();

            //Getting Provider Status
            isOn = locationManager.isProviderEnabled(providerName);


            if (!isOn) {
                Toast.makeText(context, "Please Turn GPS or Network On", Toast.LENGTH_SHORT).show();
            } else {
                this.canGetLocation = true;
//                //Get Location from Network
//                if (isNetworkOn) {
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, this);
//                    Log.d("Network", "Network");
//                    if (locationManager != null) {
//                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//                        if (location != null) {
//                            latitude = location.getLatitude();
//                            longitude = location.getLongitude();
//                            accuracy = location.getAccuracy();
//                            altitude = location.getAltitude();
//                        }
//                    }
//                }

                //Get Location from GPS
                if (isOn) {
                    locationManager.requestLocationUpdates(providerName, minTime, 0, this);
                    Log.d("GPS", "GPS was running");
                    if (locationManager != null) {

                        location = locationManager.getLastKnownLocation(providerName);

                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                            accuracy = location.getAccuracy();
                            altitude = location.getAltitude();
                        }
                    }
                }
            }
        }
        catch (Exception ex) {
            Log.d("Error GPS Tracker", ex.toString());
        }


    }

    //Stop Using GPS
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
            locationManager = null;
            location = null;
        }
    }

    private void clearListData() {
        this.data.clear();
        arrayAdapter.notifyDataSetChanged();
    }

    //Get GPS and Network is On
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    //Get Latitude Value
    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }
        return latitude;
    }

    //Get Longitude Value
    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }
        return longitude;
    }

    //Get Altitude Value
    public double getAltitude() {
        if (location != null) {
            altitude = location.getAltitude();
        }
        return altitude;
    }

    //Get Accuracy Value
    public double getAccuracy() {
        if (location != null) {
            accuracy = location.getAccuracy();
        }
        return accuracy;
    }

    private void getPlaces() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        line = null;
        try {
            URLConnection url = new URL(link).openConnection();

            HttpURLConnection con = (HttpURLConnection)url;
            //con.setRequestMethod("GET");

            inputStream = new BufferedInputStream(con.getInputStream());

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
            dataLength = jsonArray.length();
            dataPlaces = new String[jsonArray.length()][6];
            for(int i = 0; i < dataLength; i++){
                int j=0;
                dataPlaces[i][j] = jsonArray.getJSONObject(i).getString("id");
                j++;
                dataPlaces[i][j] = jsonArray.getJSONObject(i).getString("name");
                j++;
                dataPlaces[i][j] = jsonArray.getJSONObject(i).getString("latitude");
                j++;
                dataPlaces[i][j] = jsonArray.getJSONObject(i).getString("longitude");
                j++;
                dataPlaces[i][j] = jsonArray.getJSONObject(i).getString("range");
                j++;
                dataPlaces[i][j] = jsonArray.getJSONObject(i).getString("altitude");

            }

        }

        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(this.context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            textView.setText(add);

            add = add + "\n" + obj.getCountryName();
            add = add + "\n" + obj.getCountryCode();
            add = add + "\n" + obj.getAdminArea();
            add = add + "\n" + obj.getPostalCode();
            add = add + "\n" + obj.getSubAdminArea();
            add = add + "\n" + obj.getLocality();
            add = add + "\n" + obj.getSubThoroughfare();

            Log.v("Address", add);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this.context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public String calculateAddress(double lat, double lng) {
        double[] distancesInMeters = new double[dataLength];
        String[] namePlace = new String[dataLength];
        Location loc1 = new Location("");
        loc1.setLatitude(lat);
        loc1.setLongitude(lng);
        Location loc2 = new Location("");
        for (int i = 0; i < dataLength; i++) {
            loc2.setLatitude(Double.parseDouble(dataPlaces[i][2]));
            loc2.setLongitude(Double.parseDouble(dataPlaces[i][3]));
            distancesInMeters[i] = loc1.distanceTo(loc2);
            namePlace[i] = dataPlaces[i][1];
        }
        for (int i = 0; i < dataLength-1; i++) {
            for (int j = 1; j < dataLength; j++) {
                if (distancesInMeters[j] < distancesInMeters[i]) {
                    double temp = distancesInMeters[i];
                    distancesInMeters[i] = distancesInMeters[j];
                    distancesInMeters[j] = temp;

                    String temp2 = namePlace[i];
                    namePlace[i] = namePlace[j];
                    namePlace[j] = temp2;
                }
            }
        }
        return namePlace[0];
    }

    public String calculateDistance(double lat, double lng) {
        double[] distancesInMeters = new double[dataLength];
        Location loc1 = new Location("");
        loc1.setLatitude(lat);
        loc1.setLongitude(lng);
        Location loc2 = new Location("");
        for (int i = 0; i < dataLength; i++) {
            loc2.setLatitude(Double.parseDouble(dataPlaces[i][2]));
            loc2.setLongitude(Double.parseDouble(dataPlaces[i][3]));
            distancesInMeters[i] = loc1.distanceTo(loc2);
        }
        for (int i = 0; i < dataLength-1; i++) {
            for (int j = 1; j < dataLength; j++) {
                if (distancesInMeters[j] < distancesInMeters[i]) {
                    double temp = distancesInMeters[i];
                    distancesInMeters[i] = distancesInMeters[j];
                    distancesInMeters[j] = temp;
                }
            }
        }
        return Double.toString(distancesInMeters[0]);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.accuracy = location.getAccuracy();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
        this.altitude = location.getAltitude();
        Log.d("Data", "Longitude: " + getLongitude() + "\nLatitude: " +
                getLatitude() + "\nAccuracy: " + getAccuracy() + "\nAltitude: " + getAltitude());
        if(this.accuracy > 0) {
            if (iteration < 5) {
                String tempAddress = calculateAddress(getLatitude(), getLongitude());
                double tempDistance = Double.parseDouble(calculateDistance(getLatitude(), getLongitude()));
                if (dataIteration[0][0] == null) {
                    dataIteration[0][0] = tempAddress;
                    dataIteration[0][1] = Double.toString(tempDistance);
                }
                else {
                    double temp2 = Double.parseDouble(dataIteration[0][1]);
                    if (tempDistance < temp2) {
                        dataIteration[0][0] = tempAddress;
                        dataIteration[0][1] = Double.toString(tempDistance);
                    }
                }

                if (tempAddress != null) {
                    data.add("Longitude: " + getLongitude() + "\nLatitude: " +
                            getLatitude() + "\nAccuracy: " + getAccuracy() + "\nAltitude: " + getAltitude() +
                            "\nPlace: " + tempAddress + "\nDistance: " + tempDistance);
                }
                else {
                    data.add("Longitude: " + getLongitude() + "\nLatitude: " +
                            getLatitude() + "\nAccuracy: " + getAccuracy() + "\nAltitude: " + getAltitude() +
                            calculateAddress(getLatitude(), getLongitude()));
                }
                arrayAdapter.notifyDataSetChanged();
                iteration++;
            }
            else {
                textView.setText(dataIteration[0][0]);
                Toast.makeText(this.context, "Pencarian Selesai", Toast.LENGTH_SHORT).show();
                stopUsingGPS();
            }
        }
        this.location = null;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
