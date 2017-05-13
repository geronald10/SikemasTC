package id.ac.its.sikemastc.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.model.PerkuliahanMahasiswa;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasJsonUtils;

public class SikemasSyncTask {

    synchronized public static void syncPerkuliahan(Context context) {
        try {
            SikemasSessionManager session = new SikemasSessionManager(context);
            HashMap<String, String> dosen = session.getUserDetails();
            Log.d("SyncTask perkuliahan", dosen.get(SikemasSessionManager.KEY_USER_CODE));

            String jsonPerkuliahan = NetworkUtils.getResponseListPerkuliahanDosen(context,
                    dosen.get(SikemasSessionManager.KEY_USER_CODE));
            Log.d("jsonPerkuliahan", jsonPerkuliahan);

            ContentValues[] perkuliahanDosenValues = SikemasJsonUtils
                    .getListPerkuliahanDosenContentValuesFromJson(context, jsonPerkuliahan);
            ContentValues[] kehadiranPesertaValues = SikemasJsonUtils
                    .getListKehadiranPesertaContentValuesFromJson(context, jsonPerkuliahan);

            if (perkuliahanDosenValues != null && perkuliahanDosenValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.PerkuliahanEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.PerkuliahanEntry.CONTENT_URI,
                        perkuliahanDosenValues
                );
            }
            if (kehadiranPesertaValues != null && kehadiranPesertaValues.length != 0) {
                Log.d("kehadiranLength", String.valueOf(kehadiranPesertaValues.length));
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.KehadiranEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.KehadiranEntry.CONTENT_URI,
                        kehadiranPesertaValues
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public static void syncKelasDosen(Context context) {
        try {
            SikemasSessionManager session = new SikemasSessionManager(context);
            HashMap<String, String> dosen = session.getUserDetails();
            Log.d("SyncTask kelas", dosen.get(SikemasSessionManager.KEY_USER_CODE));

            String jsonKelasDosenResponse = NetworkUtils.getResponseListKelasDosen(context,
                    dosen.get(SikemasSessionManager.KEY_USER_CODE));
            Log.d("jsonPerkuliahan", jsonKelasDosenResponse);

            ContentValues[] kelasDosenValues = SikemasJsonUtils
                    .getListKelasDosenContentValuesFromJson(context, jsonKelasDosenResponse);
            ContentValues[] kelasPertemuanDosenValues = SikemasJsonUtils
                    .getListKelasPertemuanDosenContentValuesFromJson(context, jsonKelasDosenResponse);
            ContentValues[] kelasPesertaDosenValues = SikemasJsonUtils
                    .getListPesertaDosenContentValuesFromJson(context, jsonKelasDosenResponse);

            if (kelasDosenValues != null && kelasDosenValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.KelasEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.KelasEntry.CONTENT_URI,
                        kelasDosenValues
                );
            }
            if (kelasPertemuanDosenValues != null && kelasPertemuanDosenValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.PertemuanEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.PertemuanEntry.CONTENT_URI,
                        kelasPertemuanDosenValues
                );
            }
            if (kelasPesertaDosenValues != null && kelasPesertaDosenValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.PesertaEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.PesertaEntry.CONTENT_URI,
                        kelasPesertaDosenValues
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public static void syncPerkuliahanMahasiswa(Context context) {
        try {
            SikemasSessionManager session = new SikemasSessionManager(context);
            HashMap<String, String> mahasiswa = session.getUserDetails();
            Log.d("SyncTask perkuliahan", mahasiswa.get(SikemasSessionManager.KEY_USER_CODE));

//            String jsonPerkuliahanMahasiswa = NetworkUtils.getResponseListPerkuliahanMahasiswa(context,
//                    mahasiswa.get(SikemasSessionManager.KEY_USER_CODE));
            String jsonPerkuliahanMahasiswa = NetworkUtils.getResponseListPerkuliahanMahasiswa(context, "5113100050");
            Log.d("jsonPerkuliahan", jsonPerkuliahanMahasiswa);

            ContentValues[] perkuliahanMahasiswaValues = SikemasJsonUtils
                    .getListPerkuliahanMahasiswaContentValuesFromJson(context, jsonPerkuliahanMahasiswa);

            if (perkuliahanMahasiswaValues != null && perkuliahanMahasiswaValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.PerkuliahanEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.PerkuliahanEntry.CONTENT_URI,
                        perkuliahanMahasiswaValues
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized public static void syncKelasMahasiswa(Context context) {
        try {
            SikemasSessionManager session = new SikemasSessionManager(context);
            HashMap<String, String> mahasiswa = session.getUserDetails();
            Log.d("SyncTask kelas", mahasiswa.get(SikemasSessionManager.KEY_USER_ID));

            String jsonKelasMahasiswa = NetworkUtils.getResponseListKelasMahasiswa(context,
                    mahasiswa.get(SikemasSessionManager.KEY_USER_ID));

            ContentValues[] kelasMahasiswaValues = SikemasJsonUtils
                    .getListKelasMahasiswaContentValuesFromJson(context, jsonKelasMahasiswa);
            ContentValues[] kelasMahasiswaDosenValues = SikemasJsonUtils
                    .getListKelasMahasiswaDosenContentValuesFromJson(context, jsonKelasMahasiswa);

            if (kelasMahasiswaValues != null && kelasMahasiswaValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.KelasEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.KelasEntry.CONTENT_URI,
                        kelasMahasiswaValues
                );
            }
            if (kelasMahasiswaDosenValues != null && kelasMahasiswaDosenValues.length != 0) {
                ContentResolver sikemasContentResolver = context.getContentResolver();
                sikemasContentResolver.delete(
                        SikemasContract.DosenEntry.CONTENT_URI,
                        null,
                        null
                );
                sikemasContentResolver.bulkInsert(
                        SikemasContract.DosenEntry.CONTENT_URI,
                        kelasMahasiswaDosenValues
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void syncLogoutSession(Context context) {
        ContentResolver sikemasContentResolver = context.getContentResolver();
        sikemasContentResolver.delete(SikemasContract.BASE_CONTENT_URI, null, null);
    }
}
