package id.ac.its.sikemastc.activity.mahasiswa;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.KehadiranMahasiswaAdapter;
import id.ac.its.sikemastc.model.RekapKehadiran;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class DetailJadwalKelasMahasiswa extends AppCompatActivity {

    private final String TAG = DetailJadwalKelasMahasiswa.class.getSimpleName();

    private List<RekapKehadiran> rekapKehadiranMahasiswaList;
//    public static final String[] MAIN_LIST_RIWAYAT_KEHADIRAN_MAHASISWA_PROJECTION = {
//            SikemasContract.KehadiranEntry.KEY_PERTEMUAN_KE,
//            SikemasContract.KehadiranEntry.KEY_TANGGAL_PERTEMUAN,
//            SikemasContract.KehadiranEntry.KEY_WAKTU_CHEKIN,
//            SikemasContract.KehadiranEntry.KEY_TEMPAT_CHECKIN,
//            SikemasContract.KehadiranEntry.KEY_STATUS_KEHADIRAN,
//            SikemasContract.KehadiranEntry.KEY_KET_KEHADIRAN
//    };
//
//    public static final int INDEX_PERTEMUAN_KE = 0;
//    public static final int INDEX_TANGGAL_PERTEMUAN = 1;
//    public static final int INDEX_WAKTU_CHECKIN = 2;
//    public static final int INDEX_TEMPAT_CHECKIN = 3;
//    public static final int INDEX_STATUS_KEHADIRAN = 4;
//    public static final int INDEX_KET_KEHADIRAN = 5;
//
//    private static final int ID_LIST_RIWAYAT_KEHADIRAN_MAHASISWA_LOADER = 47;

    private RecyclerView mRecyclerView;
    private KehadiranMahasiswaAdapter mKehadiranMahasiswaAdapter;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_jadwal_kelas_mahasiswa);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_riwayat_pertemuan);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        Intent intent = getIntent();
        String idKelas = intent.getStringExtra("id_kelas");
        String idMahasiswa = intent.getStringExtra("id_mahasiswa");
        String infoKelas = intent.getStringExtra("info_kelas");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Riwayat Kehadiran");
        mToolbar.setSubtitle(infoKelas);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(10f);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        rekapKehadiranMahasiswaList = new ArrayList<>();
        getRekapKehadiranMahasiswa(idKelas, idMahasiswa);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mKehadiranMahasiswaAdapter = new KehadiranMahasiswaAdapter(this, rekapKehadiranMahasiswaList);
        mRecyclerView.setAdapter(mKehadiranMahasiswaAdapter);
    }

//    @Override
//    public Loader onCreateLoader(int loaderId, Bundle args) {
//        switch (loaderId) {
//            case ID_LIST_RIWAYAT_KEHADIRAN_MAHASISWA_LOADER:
//                Log.d("TAG", "masuk on create loader");
//                return new CursorLoader(this,
//                        mUri,
//                        MAIN_LIST_RIWAYAT_KEHADIRAN_MAHASISWA_PROJECTION,
//                        null,
//                        null,
//                        null);
//            default:
//                throw new RuntimeException("Loader Not Implemented: " + loaderId);
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mKehadiranMahasiswaAdapter.swapCursor(data);
//        if (mPosition == RecyclerView.NO_POSITION)
//            mPosition = 0;
//        mRecyclerView.smoothScrollToPosition(mPosition);
//        if (data.getCount() > 0)
//            showRiwayatKehadiranDataView();
//    }
//
//    @Override
//    public void onLoaderReset(Loader loader) {
//        mKehadiranMahasiswaAdapter.swapCursor(null);
//    }

    private void showRiwayatKehadiranDataView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void getRekapKehadiranMahasiswa(final String idKelas, final String idMahasiswa) {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.LIST_REKAP_KEHADIRAN_MAHASISWA_KELAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            mKehadiranMahasiswaAdapter.clear();

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray rekapAbsen = jsonObject.getJSONArray("rekapabsen");
                            Log.d("rekapAbsen", String.valueOf(rekapAbsen.length()));

                            for (int i = 0; i < rekapAbsen.length(); i++) {
                                JSONObject absenMahasiswa = rekapAbsen.getJSONObject(i);
                                String waktuTimeStamp = absenMahasiswa.getString("updated_at");
                                Log.d(i + " waktutimestamp", waktuTimeStamp);
                                String tanggal = SikemasDateUtils.formatDateFromTimestamp(waktuTimeStamp);
                                String waktu = SikemasDateUtils.formatTimeFromTimestamp(waktuTimeStamp);
                                String statusHadir = absenMahasiswa.getString("ket_kehadiran");
                                String pesanKehadiran = absenMahasiswa.getString("pesan");
                                JSONObject perkuliahanMahasiswa = absenMahasiswa.getJSONObject("perkuliahanmahasiswa");
                                String pertemuanKe = perkuliahanMahasiswa.getString("pertemuan");

                                RekapKehadiran rekapKehadiran = new RekapKehadiran(tanggal, waktu,
                                        null, statusHadir, pertemuanKe, pesanKehadiran);
                                rekapKehadiranMahasiswaList.add(rekapKehadiran);
                            }
                            mKehadiranMahasiswaAdapter.notifyDataSetChanged();
                            showRiwayatKehadiranDataView();
                            Toast.makeText(getApplicationContext(), "Berhasil Memuat Rekap Kehadiran",
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_kelas", idKelas);
                params.put("nrp", idMahasiswa);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
