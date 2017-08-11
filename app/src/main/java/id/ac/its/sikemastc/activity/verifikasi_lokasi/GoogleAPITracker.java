package id.ac.its.sikemastc.activity.verifikasi_lokasi;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.opencsv.CSVWriter;
import org.json.JSONArray;
import id.ac.its.sikemastc.activity.mahasiswa.MainPerkuliahanFragment;
import id.ac.its.sikemastc.utilities.NetworkUtils;

import static java.lang.Math.abs;


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
    private String link;
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
        this.link = NetworkUtils.LIST_RUANG;
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

    private void getPlaces() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        line = null;
        try {
            URLConnection url = new URL(link).openConnection();

            HttpURLConnection con = (HttpURLConnection)url;

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
            for (int j = 0; j < this.dataLength; j++) {
                loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(this.dataPlaces[j][2]));
                loc2.setLongitude(Double.parseDouble(this.dataPlaces[j][3]));
                tempDistance = loc1.distanceTo(loc2);
                if (tempData[0] == null) {
                    tempData[0] = Double.toString(tempDistance);
                    tempData[1] = this.dataPlaces[j][0];
                    tempData[2] = this.dataPlaces[j][1];
                }
                else if (tempData[0] != null) {
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
        for (int i = 0; i < this.dataLength; i++) {
            loc2 = new Location("");
            loc2.setLatitude(Double.parseDouble(this.dataPlaces[i][2]));
            loc2.setLongitude(Double.parseDouble(this.dataPlaces[i][3]));
            Log.d("Check " + i, "ID: " + this.dataPlaces[i][0] + "\nPlace: " + this.dataPlaces[i][1] + "\nDistance: " +
                    loc1.distanceTo(loc2));

            this.resultMean.add(new String[]{this.dataPlaces[i][0], this.dataPlaces[i][1], Double.toString(loc1.distanceTo(loc2))});

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
            Log.d("Check " + i, "ID: " + this.dataPlaces[i][0] + "\nPlace: " + this.dataPlaces[i][1] + "\nDistance: " +
                    loc1.distanceTo(loc2));

            this.resultRegression.add(new String[]{this.dataPlaces[i][0], this.dataPlaces[i][1], Double.toString(loc1.distanceTo(loc2))});

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
        String baseDir = android.os.Environment.getExternalStorageDirectory() + "/Sikemas/lokasi/";
        Long ts = System.currentTimeMillis()/1000;
        String fileName = NRP + "-" + ts.toString() + ".csv";
        String filePath = baseDir + fileName;
        File f = new File(baseDir);
        if(!f.exists())
        {
            f.mkdirs();
        }
        CSVWriter writer;
        try {
            if (this.resultKNN.get(this.resultKNN.size()-1)[0].equals(this.resultMean.get(this.resultMean.size()-1)[0])) {
                if (this.resultKNN.get(this.resultKNN.size()-1)[0].equals(this.resultRegression.get(this.resultRegression.size()-1)[0])) {
                    if (abs(Double.parseDouble(this.resultKNN.get(this.resultKNN.size()-1)[2]) - Double.parseDouble(this.resultMean.get(this.resultMean.size()-1)[2])) <= 1) {
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

                        stopUsingAPI();
                    }
                }
            }

            String id = "0", place = "0";
            double distance = 100000000;

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

            if (finalResult[1].contains("IF")) {
                MainPerkuliahanFragment.location.setText(finalResult[1]);
                MainPerkuliahanFragment.showLocationFounded();
            }
            else {
                MainPerkuliahanFragment.location.setText("Tidak Terdeteksi");
                MainPerkuliahanFragment.showLocationFounded();
            }

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
        if(this.accuracy <= 20) {
            if (this.iteration > 0 && this.longitude != Double.parseDouble(this.dataIteration.get(this.iteration-1)[1]) &&
                    this.latitude != Double.parseDouble(this.dataIteration.get(this.iteration-1)[2])) {
                Log.d("Data", "Longitude: " + getLongitude() + "\nLatitude: " +
                        getLatitude() + "\nAccuracy: " + getAccuracy() + "\nAltitude: " + getAltitude());
                this.dataIteration.add(new String[]{Integer.toString(iteration + 1), Double.toString(getLongitude()),
                        Double.toString(getLatitude()), Double.toString(getAltitude()), Double.toString(getAccuracy())});
                this.iteration++;
            }
            else if (this.iteration == 0) {
                Log.d("Data", "Longitude: " + getLongitude() + "\nLatitude: " +
                        getLatitude() + "\nAccuracy: " + getAccuracy() + "\nAltitude: " + getAltitude());
                this.dataIteration.add(new String[]{Integer.toString(iteration + 1), Double.toString(getLongitude()),
                        Double.toString(getLatitude()), Double.toString(getAltitude()), Double.toString(getAccuracy())});
                this.iteration++;
            }
            if (this.iteration > 10) {
                try {
                    KNN();
                    xyMethod();
                    linearRegression();
                    createCSVFile();
                    this.iteration = 0;
                    this.dataIteration = new ArrayList<>();
                }
                catch (Exception ex) {

                }
            }
        }
    }

}
