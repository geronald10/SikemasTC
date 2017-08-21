package id.ac.its.sikemastc.activity.dosen;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.KehadiranPerkuliahanAdapter;
import id.ac.its.sikemastc.model.PesertaPerkuliahan;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class DetailPertemuanKelas extends AppCompatActivity implements
        KehadiranPerkuliahanAdapter.StatusKehadiranAdapterOnClickHandler {

    private final String TAG = DetailPertemuanKelas.class.getSimpleName();

    private Toolbar toolbar;
    private Context mContext;
    private RecyclerView mRecyclerView;
    private List<PesertaPerkuliahan> pesertaPerkuliahanList;
    private KehadiranPerkuliahanAdapter mKehadiranPerkuliahanAdapter;

    private String idPertemuan;
    private String idKelas;
    private String pertemuanKe;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pertemuan);
        mContext = this;

        Intent intentExtra = getIntent();
        idPertemuan = intentExtra.getStringExtra("id_pertemuan");
        idKelas = intentExtra.getStringExtra("id_kelas");
        pertemuanKe = intentExtra.getStringExtra("pertemuan_ke");

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_detail_pertemuan);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Detail Pertemuan " + pertemuanKe);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.setElevation(10f);
        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        pesertaPerkuliahanList = new ArrayList<>();
        getPesertaKehadiranList(idPertemuan);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mKehadiranPerkuliahanAdapter = new KehadiranPerkuliahanAdapter(this, pesertaPerkuliahanList);
        mRecyclerView.setAdapter(mKehadiranPerkuliahanAdapter);
    }

    public void getPesertaKehadiranList(final String kodePerkuliahan) {
        Log.d("kodePerkuliahanMasuk", kodePerkuliahan);
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.GET_PESERTA_PERKULIAHAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            mKehadiranPerkuliahanAdapter.clear();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray perkuliahan = jsonObject.getJSONArray("perkuliahan");
                            for (int i = 0; i < perkuliahan.length(); i++) {
                                JSONObject kehadiran = perkuliahan.getJSONObject(i);
                                String idPerkuliahan = kehadiran.getString("id");
                                JSONArray kehadiranList = kehadiran.getJSONArray("kehadiran");
                                for (int j = 0; j < kehadiranList.length(); j++) {
                                    JSONObject detailKehadiran = kehadiranList.getJSONObject(j);
                                    String namaPeserta = detailKehadiran.getString("nama");
                                    String nrpPeserta = detailKehadiran.getString("nrp");
                                    JSONObject pivot = detailKehadiran.getJSONObject("pivot");
                                    String idPeserta = pivot.getString("id_mahasiswa");
                                    String ketKehadiran = pivot.getString("ket_kehadiran");

                                    PesertaPerkuliahan pesertaPerkuliahan = new PesertaPerkuliahan(
                                            idPerkuliahan, idPeserta, nrpPeserta, namaPeserta, ketKehadiran);
                                    pesertaPerkuliahanList.add(pesertaPerkuliahan);
                                }
                            }

                            // Sorting
                            Collections.sort(pesertaPerkuliahanList, new Comparator<PesertaPerkuliahan>() {
                                @Override
                                public int compare(PesertaPerkuliahan peserta1, PesertaPerkuliahan peserta2) {
                                    return peserta1.getNrpMahasiswa().compareTo(peserta2.getNrpMahasiswa());
                                }
                            });

                            mKehadiranPerkuliahanAdapter.notifyDataSetChanged();
                            showPesertaPerkuliahanDataView();
                            Toast.makeText(mContext, "Berhasil Memuat Peserta Perkuliahan", Toast.LENGTH_LONG).show();
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
                params.put("id_perkuliahan", kodePerkuliahan);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void changeStatusKehadiran(final String idPerkuliahan, final String idPeserta, final String ketKehadiran) {
        Log.d("idPerkuliahan", idPerkuliahan);
        Log.d("idMahasiswa", idPeserta);
        Log.d("kodeKehadiran", ketKehadiran);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.KIRIM_STATUS_KEHADIRAN_MAHASISWA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            mKehadiranPerkuliahanAdapter.notifyDataSetChanged();
                            Toast.makeText(mContext, "Berhasil Memuat Peserta Perkuliahan", Toast.LENGTH_LONG).show();
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
                params.put("id_perkuliahan", idPerkuliahan);
                params.put("id_mahasiswa", idPeserta);
                params.put("ket_kehadiran", ketKehadiran);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showPesertaPerkuliahanDataView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showAlertDialog(final String idPerkuliahan, final String idMahasiswa,
                                 final String nrpPeserta, final String namaPeserta) {

        final String[] statusKehadiran = new String[] {
                "Hadir", "Ijin", "Absen"
        };

        final String[] selectedItem = new String[1];
        final String[] kodeKehadiran = new String[1];

        final AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Ubah Status Kehadiran dari peserta " + nrpPeserta + " - " + namaPeserta + " ?");

        builder.setSingleChoiceItems(
                statusKehadiran,
                -1,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        selectedItem[0] = Arrays.asList(statusKehadiran).get(i);
                        switch (selectedItem[0]) {
                            case "Hadir":
                                kodeKehadiran[0] = "H";
                                break;
                            case "Ijin":
                                kodeKehadiran[0] = "I";
                                break;
                            case "Absen":
                                kodeKehadiran[0] = "A";
                                break;
                        }
                    }
                }
        );

        builder.setPositiveButton(R.string.pilih, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                changeStatusKehadiran(idPerkuliahan, nrpPeserta, kodeKehadiran[0]);
            }
        });

        builder.setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(int itemId, String idPerkuliahan, String idMahasiswa,
                        String nrpPeserta, String namaPeserta) {
        switch (itemId) {
            case R.id.ib_change_status:
                showAlertDialog(idPerkuliahan, idMahasiswa, nrpPeserta, namaPeserta);
                break;
            default:
                break;
        }
    }
}
