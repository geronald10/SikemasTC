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

    public static final String KIRIM_LOKASI =
            "http://absensi.if.its.ac.id/mhs/kirim_lokasi";

    // Token FCM
    public static final String KIRIM_TOKEN =
            "http://absensi.if.its.ac.id/kirim_token";

    // Login API
    public static final String LOGIN_SIKEMAS =
            "http://absensi.if.its.ac.id/loginmobile";

    // Upload Dataset TTD API
    public static final String UPLOAD_DATASET_TANDATANGAN_SIKEMAS =
            "http://absensi.if.its.ac.id/mhs/uploadsignature";

    // Upload Dataset Wajah API
    public static final String UPLOAD_DATASET_WAJAH_SIKEMAS =
            "http://absensi.if.its.ac.id/mhs/uploadfoto";
    // Kirim status kehadiran ke server
    public static final String KIRIM_STATUS_KEHADIRAN_MAHASISWA =
            "http://absensi.if.its.ac.id/mhs/ubahstatuskehadiran";


    // Sinkronisasi Dataset Tanda Tangan
    public static final String SINKRONISASI_DATASET_TANDATANGAN_SIKEMAS=
            "http://absensi.if.its.ac.id/mhs/retrievesignature";

    // List Kelas Diampu oleh Dosen API
    public static final String LIST_KELAS_DOSEN_SIKEMAS =
            "http://absensi.if.its.ac.id/kelasdiampu";
    // List Jadwal Kelas Hari ini
    public static final String LIST_JADWAL_KELAS_DOSEN_HARI_INI =
            "http://absensi.if.its.ac.id/kelashariini";
    // Change Status kelas
    public static final String CHANGE_STATUS_KELAS =
            "http://absensi.if.its.ac.id/ubahstatuskelas";
    // Detail peserta kelas yang dipilih
    public static final String GET_PESERTA_PERKULIAHAN =
            "http://absensi.if.its.ac.id/peserta_perkuliahan_ini";
    // Penjadwalan ulang kirim tanggal
    public static final String REQUEST_PENJADWALAN_BY_TANGGAL =
            "http://absensi.if.its.ac.id/jadwal_ulang_tanggal";
    // Penjadwalan ulang kirim tanggal, kirim waktu
    public static final String REQUEST_PENJADWALAN_BY_TANGGAL_WAKTU =
            "http://absensi.if.its.ac.id/jadwal_ulang_waktu";
    // Penjadwalan ulang kirim tanggal, kirim waktu
    public static final String CONFIRM_PENJADWALAN_SEMENTARA =
            "http://absensi.if.its.ac.id/konfirm_ganti_jadwal";
    // Penjadwalan sementara request perkuliahan terpilih
    public static final String REQUEST_JADWAL_PERKULIAHAN =
            "http://absensi.if.its.ac.id/req_perkuliahan";
    // Refresh Kehadiran
    public static final String REFRESH_KEHADIRAN =
            "http://absensi.if.its.ac.id/refresh_kehadiran";

    // List perkuliahan Mahasiswa saat ini
    public static final String LIST_PERKULIAHAN_MAHASISWA_SAAT_INI =
            "http://absensi.if.its.ac.id/mhs/kelashariini";
    // List kelas Mahasiswa
    public static final String LIST_KELAS_MAHASISWA =
            "http://absensi.if.its.ac.id/mhs/listkelas";
    // List kelas aktif Mahasiswa
    public static final String LIST_KELAS_MAHASISWA_AKTIF =
            "http://absensi.if.its.ac.id/mhs/kelasaktif";
    // List rekap kehadiran mahasiswa kelas
    public static final String LIST_REKAP_KEHADIRAN_MAHASISWA_KELAS =
            "http://absensi.if.its.ac.id/mhs/rekapdarikelas";
    // List rekap kehadiran mahasiswa perkuliahan
    public static final String LIST_REKAP_KEHADIRAN_MAHASISWA_PERKULIAHAN =
            "http://absensi.if.its.ac.id/mhs/rekapdariperkuliahan";
    // List url for downloading image
    public static final String LIST_FOTO_MAHASISWA_IMAGE_URL =
            "http://absensi.if.its.ac.id/mhs/retrievewajah";

    public static final String LIST_RUANG =
            "http://absensi.if.its.ac.id/get_ruang";

    private static final String KODE_DOSEN_PARAM = "kode_dosen";
    private static final String NRP_MAHASISWA_PARAM = "nrp";
    private static final String ID_KELAS = "id_kelas";

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

    public static String getResponseRekapKehadiranMahasiswa(Context context, final String nrpMahasiswa, final String idKelas) {
        int REQUEST_TIMEOUT = 5;
        JSONObject response = null;
        HashMap<String, String> params = new HashMap<>();
        params.put(NRP_MAHASISWA_PARAM, nrpMahasiswa);
        params.put(ID_KELAS, idKelas);

        RequestFuture<JSONObject> requestFuture = RequestFuture.newFuture();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                LIST_REKAP_KEHADIRAN_MAHASISWA_KELAS, new JSONObject(params), requestFuture, requestFuture);
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
