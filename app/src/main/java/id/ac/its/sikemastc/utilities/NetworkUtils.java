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
    // Change Status kelas
    public static final String CHANGE_STATUS_KELAS =
            "http://10.151.31.201/sikemas/public/ubahstatuskelas";
    // Change Status kehadiran mahasiswa
    public static final String CHANGE_STATUS_KEHADIRAN =
            "http://10.151.31.201/sikemas/public/ubah_ket_kehadiran_mahasiswa";
    // Detail peserta kelas yang dipilih
    public static final String GET_PESERTA_PERKULIAHAN =
            "http://10.151.31.201/sikemas/public/peserta_perkuliahan_ini";

    // List perkuliahan Mahasiswa saat ini
    public static final String LIST_PERKULIAHAN_MAHASISWA_SAAT_INI =
            "http://10.151.31.201/sikemas/public/mhs/kelashariini";
    // List kelas Mahasiswa
    public static final String LIST_KELAS_MAHASISWA =
            "http://10.151.31.201/sikemas/public/mhs/listkelas";
    // List kelas aktif Mahasiswa
    public static final String LIST_KELAS_MAHASISWA_AKTIF =
            "http://10.151.31.201/sikemas/public/mhs/kelasaktif";
    // Kirim status kehadiran ke server
    public static final String KIRIM_STATUS_KEHADIRAN_MAHASISWA =
            "http://10.151.31.201/sikemas/public/mhs/";

    private static final String KODE_DOSEN_PARAM = "kode_dosen";
    private static final String NRP_MAHASISWA_PARAM = "nrp";

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

    public static String getResponseListPerkuliahanMahasiswa(Context context, final String nrpMahasiswa) {
        int REQUEST_TIMEOUT = 5;
        JSONObject response = null;
        HashMap<String, String> params = new HashMap<>();
        params.put(NRP_MAHASISWA_PARAM, nrpMahasiswa);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                LIST_PERKULIAHAN_MAHASISWA_SAAT_INI, new JSONObject(params), requestFuture, requestFuture);
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

    public static String getResponseListKelasMahasiswa(Context context, final String nrpMahasiswa) {
        int REQUEST_TIMEOUT = 5;
        JSONObject response = null;
        HashMap<String, String> params = new HashMap<>();
        params.put(NRP_MAHASISWA_PARAM, nrpMahasiswa);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                LIST_KELAS_MAHASISWA, new JSONObject(params), requestFuture, requestFuture);
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

    public static String getResponseListKelasAktifMahasiswa(Context context, final String nrpMahasiswa) {
        int REQUEST_TIMEOUT = 5;
        JSONObject response = null;
        HashMap<String, String> params = new HashMap<>();
        params.put(NRP_MAHASISWA_PARAM, nrpMahasiswa);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                LIST_KELAS_MAHASISWA_AKTIF, new JSONObject(params), requestFuture, requestFuture);
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
