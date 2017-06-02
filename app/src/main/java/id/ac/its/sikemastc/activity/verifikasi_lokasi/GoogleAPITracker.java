package id.ac.its.sikemastc.activity.verifikasi_lokasi;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.opencsv.CSVWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import id.ac.its.sikemastc.activity.mahasiswa.MainPerkuliahanFragment;
import id.ac.its.sikemastc.activity.verifikasi_tandatangan.KelolaDataSetTandaTangan;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;


/**
 * Created by nurro on 3/3/2017.
 */

public class GoogleAPITracker extends Service implements LocationListener {
    //Variable for Context
    private Context context;

    //GoogleAPI Client
    private GoogleApiClient googleApiClient;

    //Variable for Attribute GPS
    private String providerName;
    private LocationRequest locationRequest;
    private double latitude;
    private double longitude;
    private double accuracy;
    private double altitude;

    //Variable for Method Result
    private ArrayList<String[]> resultKNN;
    private ArrayList<String[]> resultMean;
    private ArrayList<String[]> resultRegression;
    public static String[] finalResult = new String[]{"0", "0", "0", "0"};

    // The minimum distance to change Updates in meters
    private static final long minDistance = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long minTime = 1000 * 1 * 1; // 1 menit

    // Max iteration
    private int iteration = 0;

    // Connection to SERVER
    private String link = "http://10.151.31.201/sikemas/public/get_ruang";
    private String[][] dataPlaces;
    private InputStream inputStream = null;
    private String line;
    private String result;
    private int dataLength;
    private ArrayList<String[]> dataIteration;
    private String NRP;

    private void setContext(Context context) {
        this.context = context;
    }

    public GoogleAPITracker(final Context context2, String NRP) {
        setContext(context2);
        this.NRP = NRP;
    }

    private boolean checkPlayServices() {
        int result = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.context);
        if (result != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(result)) {
                Toast.makeText(this.context,
                        "This device is supported. Please download Google Play Services", Toast.LENGTH_LONG)
                        .show();
            }
            else {
                Toast.makeText(this.context,
                        "This device is not supported", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

    public boolean isGoogleApiClientConnected() {
        return googleApiClient != null && googleApiClient.isConnected();
    }

    private void startAPI() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this.context).addApi(LocationServices.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks(){
                        @Override
                        public void onConnectionSuspended(int cause) {

                        }

                        @Override
                        public void onConnected(Bundle connectionHint) {

                        }

                    }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                        }

                    }).build();
            googleApiClient.connect();
        }
        else {
            googleApiClient.connect();
        }
    }

    private void requestUpdateLocation(final LocationListener listener) {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(minTime);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, listener);
                }
                catch (SecurityException se) {
                    Log.d("Security Exception", se.toString());
                    se.printStackTrace();
                }
                catch (Exception ex){
                    Log.d("Ex Request", ex.toString());
                    ex.printStackTrace();
                }
            }
        }, minTime);
    }


    public void getLocation() {
        try {
            if (checkPlayServices()) {
                Toast.makeText(this.context, "Pencarian Dimulai", Toast.LENGTH_SHORT).show();
                Log.d("Pencarian Lokasi", "Pencarian Lokasi Dimulai");
                this.iteration = 0;
                this.dataIteration = new ArrayList<>();
                getPlaces();
                startAPI();
                requestUpdateLocation(this);
            }
        }
        catch (Exception ex) {
            Log.d("Error Google API", ex.toString());
        }
    }


    //Get Latitude Value
    public double getLatitude() {
        return latitude;
    }

    //Get Longitude Value
    public double getLongitude() {
        return longitude;
    }

    //Get Altitude Value
    public double getAltitude() {
        return altitude;
    }

    //Get Accuracy Value
    public double getAccuracy() {
        return accuracy;
    }

    public void stopUsingAPI() {
        Toast.makeText(this.context, "Pencarian Selesai", Toast.LENGTH_SHORT).show();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    public String[] getFinalResult() { return this.finalResult; }

    private void sendFinalResult() {
        if (this.finalResult[0] != "0") {
            try {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, NetworkUtils.KIRIM_LOKASI,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.d("Response Send Location", response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {

                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("user_id", NRP);
                        params.put("id_lokasi", finalResult[0]);
                        return params;
                    }
                };
                VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
            }
            catch (Exception ex) {
                Log.d("Error Validation Data", ex.toString());
            }
        }
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
            dataPlaces = new String[jsonArray.length()][7];
            int j=0;
            for(int i = 0; i < this.dataLength; i++){
                if (jsonArray.getJSONObject(i).getString("longitude") != "0" || jsonArray.getJSONObject(i).getString("latitude") != "0") {
                    dataPlaces[j][0] = jsonArray.getJSONObject(i).getString("id");
                    dataPlaces[j][1] = jsonArray.getJSONObject(i).getString("nama");
                    dataPlaces[j][2] = jsonArray.getJSONObject(i).getString("latitude");
                    dataPlaces[j][3] = jsonArray.getJSONObject(i).getString("longitude");
                    dataPlaces[j][4] = jsonArray.getJSONObject(i).getString("altitude");
                    j++;
                }
            }
            this.dataLength = j;

        }

        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void KNN() {
        double tempDistance, tempAltitude;
        String result[][] = new String[this.dataLength][4];
        this.resultKNN = new ArrayList<>();
        int counter = 0;
        String[] tempData = new String[3];
        Location loc1, loc2;

        for (int i = 0; i < this.iteration; i++) {
            loc1 = new Location("");
            loc1.setLongitude(Double.parseDouble(this.dataIteration.get(i)[1]));
            loc1.setLatitude(Double.parseDouble(this.dataIteration.get(i)[2]));
            tempAltitude = Double.parseDouble(this.dataIteration.get(i)[3]);
            for (int j = 0; j < this.dataLength; j++) {
                loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(this.dataPlaces[j][2]));
                loc2.setLongitude(Double.parseDouble(this.dataPlaces[j][3]));
                tempDistance = loc1.distanceTo(loc2);
                if (tempData[0] == null && tempAltitude != 0) {
                    tempData[0] = Double.toString(tempDistance);
                    tempData[1] = this.dataPlaces[j][0];
                    tempData[2] = this.dataPlaces[j][1];
                }
                else if (tempData[0] != null && tempAltitude != 0) {
                    if (tempDistance < Double.parseDouble(tempData[0])) {
                        tempData[0] = Double.toString(tempDistance);
                        tempData[1] = this.dataPlaces[j][0];
                        tempData[2] = this.dataPlaces[j][1];
                    }
                }
                else {
                    tempData[0] = Integer.toString(0);
                    tempData[1] = Integer.toString(0);
                    tempData[2] = Integer.toString(0);
                }
            }

            boolean available = false;
            for (int j = 0; j < counter; j++) {
                if (result[j][0].equals(tempData[1])) {
                    int tempCounter = Integer.parseInt(result[j][3])+1;
                    result[j][3] = Integer.toString(tempCounter);
                    available = true;
                    Log.d("Result " + Integer.toString(i + 1), result[j][1] + " n: " + result[j][3]);
                    Log.d("Data Iteration " + Integer.toString(i + 1), tempData[1]);
                    break;
                }
            }
            if (!available) {
                result[counter][0] = tempData[1];
                result[counter][1] = tempData[2];
                result[counter][2] = tempData[0];
                result[counter][3] = Integer.toString(1);
                Log.d("Result " + Integer.toString(i + 1), result[counter][1] + " n: " + result[counter][3]);
                Log.d("Data Iteration " + Integer.toString(i + 1), tempData[1]);
                counter++;
            }
        }
        for (int i = 0; i < counter; i++) {
            this.resultKNN.add(result[i]);
        }
        int temp1 = 0, temp2 = 0;
        int idResult = 0;
        double distance = Double.parseDouble(result[0][2]);
        temp1 = Integer.parseInt(result[0][3]);
        idResult = Integer.parseInt(result[0][0]);
        String placeResult = result[0][1];
        for (int i = 1; i < counter; i++) {
            temp2 = Integer.parseInt(result[i][3]);
            if (temp2 > temp1) {
                temp1 = temp2;
                distance = Double.parseDouble(result[i][2]);
                idResult = Integer.parseInt(result[i][0]);
                placeResult = result[i][1];
            }
        }
        Log.d("ID: " + idResult, placeResult);
        this.resultKNN.add(new String[]{Integer.toString(idResult), placeResult, Double.toString(distance), "Final"});
    }

    private void xyMethod() {
        double meanLongitude = 0, meanLatitude = 0, meanAltitude = 0, tempDistance = 0;
        Location loc1, loc2;
        this.resultMean = new ArrayList<>();
        String[][] result = new String[1][3];

        for (int i = 0; i < this.iteration; i++) {
            meanLongitude += Double.parseDouble(this.dataIteration.get(i)[1]);
            meanLatitude += Double.parseDouble(this.dataIteration.get(i)[2]);
            meanAltitude += Double.parseDouble(this.dataIteration.get(i)[3]);
        }
        meanLongitude /= this.iteration;
        meanLatitude /= this.iteration;
        meanAltitude /= this.iteration;

        this.resultMean.add(new String[]{Double.toString(meanLongitude), Double.toString(meanLatitude), Double.toString(meanAltitude)});

        Log.d("Mean XY", "Long: " + meanLongitude + "\nLat: " + meanLatitude + "\nAlt: " + meanAltitude);
        loc1 = new Location("");
        loc1.setLatitude(meanLatitude);
        loc1.setLongitude(meanLongitude);
        loc1.setAltitude(meanAltitude);
        for (int i = 0; i < this.dataLength; i++) {
            loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(this.dataPlaces[i][2]));
            loc2.setLongitude(Double.parseDouble(this.dataPlaces[i][3]));
            loc2.setAltitude((Double.parseDouble(this.dataPlaces[i][4])));

            Log.d("Check " + i, "ID: " + this.dataPlaces[i][0] + "\nPlace: " + this.dataPlaces[i][1] + "\nDistance: " +
                    loc1.distanceTo(loc2));

            this.resultMean.add(new String[]{this.dataPlaces[i][0], this.dataPlaces[i][1], Double.toString(tempDistance)});

            if (tempDistance == 0 || tempDistance > loc1.distanceTo(loc2)) {
                tempDistance = loc1.distanceTo(loc2);
                result[0][0] = this.dataPlaces[i][0];
                result[0][1] = this.dataPlaces[i][1];
                result[0][2] = Double.toString(tempDistance);
            }
        }
        this.resultMean.add(new String[]{result[0][0], result[0][1],result[0][2]});

        Log.d("ID: " + result[0][0], result[0][1]);
    }

    private void linearRegression() {
        Location loc1, loc2;
        double sumX = 0, sumY = 0, sumX2 = 0, sumY2 = 0, sumXY = 0, medianX, medianY, a, b, resultX, resultY, tempDistance = 0,
                meanAltitude = 0;
        this.resultRegression = new ArrayList<>();
        String[][] result = new String[1][3];

        for (int i = 0; i < this.iteration; i++) {
            sumX += Double.parseDouble(this.dataIteration.get(i)[1]);
            sumY += Double.parseDouble(this.dataIteration.get(i)[2]);
            sumX2 += (Double.parseDouble(this.dataIteration.get(i)[1])*Double.parseDouble(this.dataIteration.get(i)[1]));
            sumY2 += (Double.parseDouble(this.dataIteration.get(i)[2])*Double.parseDouble(this.dataIteration.get(i)[2]));
            sumXY += (Double.parseDouble(this.dataIteration.get(i)[1])*Double.parseDouble(this.dataIteration.get(i)[2]));
            meanAltitude += Double.parseDouble(this.dataIteration.get(i)[3]);
        }
        meanAltitude /= this.iteration;
        int half = this.iteration/2;
        if(this.iteration % 2 == 0) {
            medianX = (Double.parseDouble(this.dataIteration.get(half-1)[1]) + Double.parseDouble(this.dataIteration.get(half)[1]))/2;
            medianY = (Double.parseDouble(this.dataIteration.get(half-1)[2]) + Double.parseDouble(this.dataIteration.get(half)[2]))/2;
        }
        else {
            medianX = Double.parseDouble(this.dataIteration.get(half)[1]);
            medianY = Double.parseDouble(this.dataIteration.get(half)[2]);
        }
        b = ((this.iteration*sumXY) - (sumX*sumY))/((this.iteration*sumX2) - (sumX*sumX));
        a = (sumY/this.iteration) - (b*(sumX/this.iteration));
        resultX = (medianY-a)/b;
        resultY = a + (b*medianX);

        this.resultRegression.add(new String[]{Double.toString(sumX), Double.toString(sumY), Double.toString(sumX2), Double.toString(sumY2),
                Double.toString(sumXY), Double.toString(medianX), Double.toString(medianY), Double.toString(a),
                Double.toString(b), Double.toString(resultX), Double.toString(resultY), Double.toString(meanAltitude)});

        Log.d("Regresi Linier", "Sum X: " + sumX + "\nSum Y: " + sumY + "\nSum X2: " + sumX2 + "\nSum Y2: " + sumY2 +
                "\nSum XY: " + sumXY + "\nMedian X: " + medianX + "\nMedian Y: " + medianY + "\nA: " + a + "\nB: " + b +
                "\nLong: " + resultX + "\nLat: " + resultY);

        loc1 = new Location("");
        loc1.setLongitude(resultX);
        loc1.setLatitude(resultY);

        for (int i = 0; i < this.dataLength; i++) {
            loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(this.dataPlaces[i][2]));
            loc2.setLongitude(Double.parseDouble(this.dataPlaces[i][3]));
            loc2.setAltitude((Double.parseDouble(this.dataPlaces[i][4])));
            Log.d("Check " + i, "ID: " + this.dataPlaces[i][0] + "\nPlace: " + this.dataPlaces[i][1] + "\nDistance: " +
                    loc1.distanceTo(loc2));

            this.resultRegression.add(new String[]{this.dataPlaces[i][0], this.dataPlaces[i][1], Double.toString(tempDistance)});

            if (tempDistance == 0 || tempDistance > loc1.distanceTo(loc2)) {
                tempDistance = loc1.distanceTo(loc2);
                result[0][0] = this.dataPlaces[i][0];
                result[0][1] = this.dataPlaces[i][1];
                result[0][2] = Double.toString(tempDistance);
            }
        }
        this.resultRegression.add(new String[]{result[0][0], result[0][1],result[0][2]});
        Log.d("ID: " + result[0][0], result[0][1]);
    }

    private void createCSVFile() {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        Long ts = System.currentTimeMillis()/1000;
        String fileName = NRP + "-" + ts.toString() + ".csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer;
        try {
            if (this.iteration == 10) {
                writer = new CSVWriter(new FileWriter(filePath));
                // Write Data Iteration
                writer.writeNext(new String[]{"Data Iterasi"});
                writer.writeNext(new String[]{"No", "Longitude", "Latitude", "Altitude", "Accuracy"});
                for (int i = 0; i < this.iteration; i++) {
                    writer.writeNext(new String[]{Integer.toString(i+1), this.dataIteration.get(i)[1], this.dataIteration.get(i)[2],
                            this.dataIteration.get(i)[3], this.dataIteration.get(i)[4]});
                }
                writer.writeNext(new String[]{""});
                writer.writeNext(new String[]{""});

                // Write KNN Result
                writer.writeNext(new String[]{"KNN"});
                writer.writeNext(new String[]{"No", "ID", "Name", "Distance", "Counter"});
                for (int i = 0; i < this.resultKNN.size(); i++) {
                    writer.writeNext(new String[]{Integer.toString(i+1), this.resultKNN.get(i)[0], this.resultKNN.get(i)[1],
                            this.resultKNN.get(i)[2], this.resultKNN.get(i)[3]});
                }

                writer.writeNext(new String[]{""});
                writer.writeNext(new String[]{""});

                // Write Mean Result
                writer.writeNext(new String[]{"Mean"});
                writer.writeNext(new String[]{"No", "ID", "Name", "Distance", "", "MeanLongitude", "MeanLatitude", "MeanAltitude"});
                for (int i = 1; i < this.resultMean.size(); i++) {
                    writer.writeNext(new String[]{Integer.toString(i), this.resultMean.get(i)[0], this.resultMean.get(i)[1],
                            this.resultMean.get(i)[2], "", this.resultMean.get(0)[0], this.resultMean.get(0)[1],
                            this.resultMean.get(0)[2]});
                }
                writer.writeNext(new String[]{""});
                writer.writeNext(new String[]{""});

                // Write Regression
                writer.writeNext(new String[]{"Regression"});
                writer.writeNext(new String[]{"No", "ID", "Name", "Distance", "", "SumX", "SumY", "SumX2", "SumY2", "SumXY",
                        "MedianX", "MedianY", "A", "B", "ResultX", "ResultY", "MeanAltitude"});
                for (int i = 1; i < this.resultRegression.size(); i++) {
                    writer.writeNext(new String[]{Integer.toString(i), this.resultRegression.get(i)[0], this.resultRegression.get(i)[1],
                            this.resultRegression.get(i)[2], "", this.resultRegression.get(0)[0], this.resultRegression.get(0)[1],
                            this.resultRegression.get(0)[2], this.resultRegression.get(0)[3], this.resultRegression.get(0)[4],
                            this.resultRegression.get(0)[5], this.resultRegression.get(0)[6], this.resultRegression.get(0)[7],
                            this.resultRegression.get(0)[8], this.resultRegression.get(0)[9], this.resultRegression.get(0)[10],
                            this.resultRegression.get(0)[11]});
                }
                writer.writeNext(new String[]{""});
                writer.writeNext(new String[]{""});

                writer.close();
            }

            String id = "0", place = "0";
            double distance = 1000;

            if (distance > Double.parseDouble(this.resultKNN.get(this.resultKNN.size()-1)[2])) {
                if (Double.parseDouble(this.resultKNN.get(this.resultKNN.size()-1)[2]) > 0) {
                    id = this.resultKNN.get(this.resultKNN.size()-1)[0];
                    place = this.resultKNN.get(this.resultKNN.size()-1)[1];
                    distance = Double.parseDouble(this.resultKNN.get(this.resultKNN.size()-1)[2]);
                }
            }
            if (distance > Double.parseDouble(this.resultMean.get(this.resultMean.size()-1)[2])) {
                if (Double.parseDouble(this.resultMean.get(this.resultMean.size()-1)[2]) > 0) {
                    id = this.resultMean.get(this.resultMean.size()-1)[0];
                    place = this.resultMean.get(this.resultMean.size()-1)[1];
                    distance = Double.parseDouble(this.resultMean.get(this.resultMean.size()-1)[2]);
                }
            }
            if (distance > Double.parseDouble(this.resultRegression.get(this.resultRegression.size()-1)[2])) {
                if (Double.parseDouble(this.resultRegression.get(this.resultRegression.size()-1)[2]) > 0) {
                    id = this.resultRegression.get(this.resultRegression.size()-1)[0];
                    place = this.resultRegression.get(this.resultRegression.size()-1)[1];
                    distance = Double.parseDouble(this.resultRegression.get(this.resultRegression.size()-1)[2]);
                }
            }
            Log.d("Final Result", place);
            finalResult[0] = id;
            finalResult[1] = place;
            finalResult[2] = Double.toString(distance);
            MainPerkuliahanFragment.location.setText(finalResult[1]);
            //sendFinalResult();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if(this.accuracy <= 15) {
            if (this.iteration < 10) {
                Log.d("Data", "Longitude: " + getLongitude() + "\nLatitude: " +
                        getLatitude() + "\nAccuracy: " + getAccuracy() + "\nAltitude: " + getAltitude());
                this.dataIteration.add(new String[]{Integer.toString(iteration + 1), Double.toString(getLongitude()),
                        Double.toString(getLatitude()), Double.toString(getAltitude()), Double.toString(getAccuracy())});
                this.iteration++;
            }
            else {
                stopUsingAPI();
            }
            if (this.iteration > 0) {
                KNN();
                xyMethod();
                linearRegression();
                createCSVFile();
            }
        }
    }

}
