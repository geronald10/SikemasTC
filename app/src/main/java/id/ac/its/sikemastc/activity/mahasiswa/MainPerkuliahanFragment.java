package id.ac.its.sikemastc.activity.mahasiswa;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import id.ac.its.sikemastc.activity.verifikasi_wajah.MenuVerifikasiWajah;
import id.ac.its.sikemastc.adapter.PerkuliahanAktifMahasiswaAdapter;
import id.ac.its.sikemastc.model.PerkuliahanMahasiswa;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class MainPerkuliahanFragment extends Fragment implements
        PerkuliahanAktifMahasiswaAdapter.PerkuliahanAktifMahasiswaOnClickHandler {

    private final String TAG = MainPerkuliahanFragment.class.getSimpleName();

    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private PerkuliahanAktifMahasiswaAdapter mPerkuliahanAktifAdapter;
    private String bundleIdUser;
    private String bundleNamaUser;
    private List<PerkuliahanMahasiswa> perkuliahanAktifMahasiswaList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_perkuliahan, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            bundleIdUser = bundle.getString("id_mahasiswa");
            bundleNamaUser = bundle.getString("nama_mahasiswa");
            Log.d("bundleIdUser", bundleIdUser);
            Log.d("bundleNamaUser", bundleNamaUser);
        }
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_kelas_aktif);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        showLoading();

        perkuliahanAktifMahasiswaList = new ArrayList<>();
        getPerkuliahanAktifList(bundleIdUser);
        mPerkuliahanAktifAdapter = new PerkuliahanAktifMahasiswaAdapter(getActivity(), perkuliahanAktifMahasiswaList, this);
        mRecyclerView.setAdapter(mPerkuliahanAktifAdapter);

        if (perkuliahanAktifMahasiswaList != null) {
            showPerkuliahanAktifDataView();
        } else {
            Toast.makeText(getActivity(), "Tidak ada perkuliahan yang aktif", Toast.LENGTH_SHORT).show();
        }

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(int buttonId, String idPerkuliahan) {
        switch (buttonId) {
            case R.id.btn_verifikasi_tandatangan:
                break;
            case R.id.btn_verifikasi_wajah:
                Intent intentToVerifikasiWajah = new Intent(getActivity(), MenuVerifikasiWajah.class);
                intentToVerifikasiWajah.putExtra("id_perkuliaha", idPerkuliahan);
                intentToVerifikasiWajah.putExtra("nrp_mahasiswa", bundleIdUser);
                intentToVerifikasiWajah.putExtra("nama_mahasiswa", bundleNamaUser);
                startActivity(intentToVerifikasiWajah);
                break;
        }
    }

    private void showPerkuliahanAktifDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    public void getPerkuliahanAktifList(final String idMahasiswa) {
        Log.d("idMahasiswa", idMahasiswa);
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.LIST_KELAS_MAHASISWA_AKTIF,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            mPerkuliahanAktifAdapter.clear();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listKelasAktif = jsonObject.getJSONArray("listkelas");
                            Log.d("length listkelas", String.valueOf(listKelasAktif.length()));

                            for (int i = 0; i < listKelasAktif.length(); i++) {
                                JSONObject detailKelasAktif = listKelasAktif.getJSONObject(i);
                                String idPerkuliahan = detailKelasAktif.getString("id");
                                String pertemuanKe = detailKelasAktif.getString("pertemuan");
                                String statusPerkuliahan = detailKelasAktif.getString("status_perkuliahan");
                                String statusDosen = detailKelasAktif.getString("status_dosen");
                                String hari = detailKelasAktif.getString("hari");
                                String waktuMulai = SikemasDateUtils.formatDate(detailKelasAktif.getString("mulai"));
                                String waktuSelesai = SikemasDateUtils.formatDate(detailKelasAktif.getString("selesai"));

                                JSONObject kelas = detailKelasAktif.getJSONObject("kelas");
                                String kodeSemester = kelas.getString("kode_semester");
                                String kodeMk = kelas.getString("kode_matakuliah");
                                String kelasMk = kelas.getString("kode_kelas");
                                String namaMk = kelas.getString("nama_kelas");
                                String ruangMK = kelas.getString("nama_ruangan");

                                PerkuliahanMahasiswa perkuliahanAktifMahasiswa = new PerkuliahanMahasiswa(
                                        idPerkuliahan, kodeMk, kodeSemester, namaMk, kelasMk, ruangMK, pertemuanKe,
                                        hari, waktuMulai, waktuSelesai, statusDosen, statusPerkuliahan);
                                perkuliahanAktifMahasiswaList.add(perkuliahanAktifMahasiswa);
                            }
                            mPerkuliahanAktifAdapter.notifyDataSetChanged();
                            showPerkuliahanAktifDataView();
                            Toast.makeText(getActivity(), "Berhasil Memuat Perkuliahan Aktif", Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nrp", idMahasiswa);
                return params;
            }
        };
        VolleySingleton.getmInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
