package id.ac.its.sikemastc.utilities;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();
    // Login API
    public static final String LOGIN_SIKEMAS =
            "http://10.151.31.201/sikemas/public/loginmobile";
    // Upload Dataset Wajah API
    public static final String UPLOAD_DATASET_WAJAH_SIKEMAS =
            "http://10.151.31.201/sikemas/public/mhs/uploadfoto";
    // List Kelas Diampu oleh Dosen API
    public static final String LIST_KELAS_DOSEN_SIKEMAS =
            "http://10.151.31.201/sikemas/public/kelasdiampu";
    // List Jadwal Kelas Hari ini
    public static final String LIST_JADWAL_KELAS_DOSEN_HARI_INI =
            "http://10.151.31.201/sikemas/public/kelashariini";

    private static final String KODE_DOSEN_PARAM = "kode_dosen";

    public static String getResponseListKelasDosen(Context context, final String kodeDosen) {
        int REQUEST_TIMEOUT = 5;
        JSONObject response = null;
        HashMap<String, String> params = new HashMap<>();
        params.put(KODE_DOSEN_PARAM, kodeDosen);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                LIST_KELAS_DOSEN_SIKEMAS, new JSONObject(params), requestFuture, requestFuture);
        VolleySingleton.getmInstance(context).addToRequestQueue(jsonRequest);
        try {
            response = requestFuture.get(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            Log.d("response kelas", String.valueOf(response));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response.toString();
    }

    public static String getResponseListPerkuliahanDosen(Context context, final String kodeDosen) {
        int REQUEST_TIMEOUT = 5;
        JSONObject response = null;
        HashMap<String, String> params = new HashMap<>();
        params.put(KODE_DOSEN_PARAM, kodeDosen);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                LIST_JADWAL_KELAS_DOSEN_HARI_INI, new JSONObject(params), requestFuture, requestFuture);
        VolleySingleton.getmInstance(context).addToRequestQueue(jsonRequest);
        try {
            response = requestFuture.get(REQUEST_TIMEOUT, TimeUnit.SECONDS);
            Log.d("response perkuliahan", String.valueOf(response));
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
