package id.ac.its.sikemastc.activity.dosen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.adapter.JadwalUtamaMkAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.model.JadwalKelas;
import id.ac.its.sikemastc.model.WaktuKelas;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class HalamanUtamaDosen extends BaseActivity implements
        JadwalUtamaMkAdapter.JadwalUtamaMkAdapterOnClickHandler {

    private final String TAG = HalamanUtamaDosen.class.getSimpleName();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private View emptyView;
    private Button btnTestNotifikasi;
    private JadwalUtamaMkAdapter mAdapter;
    private List<JadwalKelas> jadwalKelasList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_dosen);
        mContext = this;

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        HashMap<String, String> dosen = session.getUserDetails();
        String kodeDosen = dosen.get(SikemasSessionManager.KEY_KODE_DOSEN);

        jadwalKelasList = new ArrayList<>();
        getListJadwalUtama(kodeDosen);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_jadwal_utama_dosen);
        emptyView = findViewById(R.id.empty_view);

        mAdapter = new JadwalUtamaMkAdapter(mContext, this, jadwalKelasList);
        mRecyclerView.setAdapter(mAdapter);

        btnTestNotifikasi = (Button) findViewById(R.id.btn_test_notifikasi);
        btnTestNotifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        SikemasSyncUtils.startImmediateSync(this);
    }

    public void getListJadwalUtama(final String kodeDosen) {
        progressDialog.setMessage("Fetch List Jadwal Kelas Hari ini ...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.LIST_JADWAL_KELAS_DOSEN_HARI_INI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Jadwal Kelas Hari ini Response: " + response);
                        try {
                            mAdapter.clear();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listKelas = jsonObject.getJSONArray("listkelas");
                            for (int i = 0; i < listKelas.length(); i++) {
                                JSONObject detailKelas = listKelas.getJSONObject(i);
                                String idListJadwal = detailKelas.getString("id");
                                String hariPerkuliahan = detailKelas.getString("hari");
                                String tanggalPerkuliahan = detailKelas.getString("tanggal");
                                String perkuliahanKe = detailKelas.getString("pertemuan");
                                String statusPerkuliahan = detailKelas.getString("status_perkuliahan");
                                String waktuMulai = detailKelas.getString("mulai");
                                String waktuSelesai = detailKelas.getString("selesai");

                                JSONObject kelas = detailKelas.getJSONObject("kelas");
                                String kelasMk = kelas.getString("kode_kelas");
                                String namaMk = kelas.getString("nama_kelas");
                                String ruangMK = kelas.getString("nama_ruangan");
                                Log.d(TAG, kelasMk);

                                List<WaktuKelas> listWaktuKelas = new ArrayList<>();
                                WaktuKelas waktuKelas = new WaktuKelas(idListJadwal, hariPerkuliahan,
                                        waktuMulai, waktuSelesai);
                                listWaktuKelas.add(waktuKelas);

                                // add to Jadwal Kelas Hari ini List
                                JadwalKelas jadwalKelas = new JadwalKelas(idListJadwal, namaMk, kelasMk,
                                        ruangMK, tanggalPerkuliahan, perkuliahanKe, listWaktuKelas,
                                        statusPerkuliahan);
                                jadwalKelasList.add(jadwalKelas);
                            }
                            Toast.makeText(mContext, "Berhasil Memuat List Kelas Hari ini", Toast.LENGTH_LONG).show();
                            mAdapter.notifyDataSetChanged();
                            hideDialog();
                            if (jadwalKelasList.isEmpty()) {
                                mRecyclerView.setVisibility(View.GONE);
                                emptyView.setVisibility(View.VISIBLE);
                            } else {
                                mRecyclerView.setVisibility(View.VISIBLE);
                                emptyView.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put(SikemasContract.UserEntry.KEY_KODE_DOSEN, kodeDosen);

                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onClick(String idListKelas) {
        Intent intentToDetailKelas = new Intent(mContext, DetailKelas.class);
        startActivity(intentToDetailKelas);
    }
}
