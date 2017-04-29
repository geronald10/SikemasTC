package id.ac.its.sikemastc.activity.dosen;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.JadwalMkAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.model.DosenKelas;
import id.ac.its.sikemastc.model.JadwalKelas;
import id.ac.its.sikemastc.model.PesertaKelas;
import id.ac.its.sikemastc.model.WaktuKelas;
import id.ac.its.sikemastc.utilities.NetworkUtils;

public class ListKelasDiampu extends BaseActivity implements
        JadwalMkAdapter.JadwalMKAdapterOnClickHandler {

    private final String TAG = ListKelasDiampu.class.getSimpleName();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private JadwalMkAdapter mAdapter;
    private List<JadwalKelas> jadwalKelasList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kelas_diampu);
        mContext = this;

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setCancelable(false);
        HashMap<String, String> dosen = session.getUserDetails();
        String kodeDosen = dosen.get(SikemasSessionManager.KEY_KODE_DOSEN);

        jadwalKelasList = new ArrayList<>();
//        jadwalMatakuliahList = JadwalKelas.getDummyDataList();
        getListKelasDiampu(kodeDosen);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_kelas);
        mAdapter = new JadwalMkAdapter(this, jadwalKelasList);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void getListKelasDiampu(final String kodeDosen) {
        String tag_string_req = "req_list_kelas_diampu";
        StringRequest stringRequest;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        progressDialog.setMessage("Fetch List Kelas Daimpu ...");
        showDialog();

        stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.LIST_KELAS_DOSEN_SIKEMAS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Jadwal MK List Response: " + response);
                try {
                    mAdapter.clear();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray listKelas = jsonObject.getJSONArray("listkelas");
                    for(int i=0; i < listKelas.length(); i++) {
                        JSONObject detailKelas = listKelas.getJSONObject(i);
                        String idListJadwal = detailKelas.getString("id");
                        String kodeMk = detailKelas.getString("kode_matakuliah");
                        String namaMk = detailKelas.getString("nama_kelas");
                        String kelasMk = detailKelas.getString("kode_kelas");
                        String ruangMK = detailKelas.getString("nama_ruangan");

                        // handle "peserta kelas"
                        JSONArray listPeserta = detailKelas.getJSONArray("peserta");
                        List<PesertaKelas> listPesertaKelas = new ArrayList<>();
                        for (int j=0; j<listPeserta.length(); j++) {
                            JSONObject detailPeserta = listPeserta.getJSONObject(j);
                            PesertaKelas pesertaKelas = new PesertaKelas(detailPeserta.getString("nrp"),
                                    detailPeserta.getString("nama"));
                            listPesertaKelas.add(pesertaKelas);
                        }

                        // handle "kelaswaktu"
                        JSONArray listWaktu = detailKelas.getJSONArray("kelaswaktu");
                        List<WaktuKelas> listWaktuKelas = new ArrayList<>();
                        for (int k=0; k < listWaktu.length(); k++) {
                            JSONObject detailWaktu = listWaktu.getJSONObject(k);
                            WaktuKelas waktuKelas = new WaktuKelas(detailWaktu.getString("id"),
                                    detailWaktu.getString("hari"), detailWaktu.getString("mulai"),
                                    detailWaktu.getString("selesai"));
                            listWaktuKelas.add(waktuKelas);
                        }

                        // handle "dosenkelas"
                        JSONArray listDosen = detailKelas.getJSONArray("dosenkelas");
                        List<DosenKelas> listDosenKelas = new ArrayList<>();
                        for (int l=0; l<listDosen.length(); l++) {
                            JSONObject detailDosen = listDosen.getJSONObject(l);
                            DosenKelas dosenKelas = new DosenKelas(detailDosen.getString("id"),
                                    detailDosen.getString("nip"), detailDosen.getString("nama"),
                                    detailDosen.getString("kode_dosen"));
                            listDosenKelas.add(dosenKelas);
                        }

                        // add to Jadwal Kelas Diampu List
                        JadwalKelas jadwalKelas = new JadwalKelas(idListJadwal, kodeMk, namaMk,
                                kelasMk, ruangMK, listPesertaKelas, listWaktuKelas, listDosenKelas);
                        jadwalKelasList.add(jadwalKelas);
                        Toast.makeText(mContext, "Berhasil Memuat List Kelas Diampu", Toast.LENGTH_LONG).show();
                    }
                    mAdapter.notifyDataSetChanged();
                    hideDialog();
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

        stringRequest.setTag(tag_string_req);
        requestQueue.add(stringRequest);
    }

    private void showDialog() {
        if(!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if(progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onClick(String idListKelas) {
        Intent intentToDetailKelas = new Intent(mContext, DetailKelas.class);
        startActivity(intentToDetailKelas);
    }
}
