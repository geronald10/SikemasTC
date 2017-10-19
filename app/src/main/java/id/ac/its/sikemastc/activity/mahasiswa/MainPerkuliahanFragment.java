package id.ac.its.sikemastc.activity.mahasiswa;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import id.ac.its.sikemastc.activity.verifikasi_bluetooth.BluetoothVerification;
import id.ac.its.sikemastc.activity.verifikasi_tandatangan.MenuVerifikasiTandaTangan;
import id.ac.its.sikemastc.activity.verifikasi_wajah.VerifikasiWajahMenuActivity;
import id.ac.its.sikemastc.adapter.PerkuliahanAktifAdapter;
import id.ac.its.sikemastc.model.Perkuliahan;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class MainPerkuliahanFragment extends Fragment implements
        PerkuliahanAktifAdapter.PerkuliahanAktifOnClickHandler {

    private final String TAG = MainPerkuliahanFragment.class.getSimpleName();

    private TextView tvStatusKehadiran, tvJmlPerkuliahanAktif, tvJmlPerkuliahan;
    private ImageView ivStatusKehadiran;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private CardView emptyView;
    private PerkuliahanAktifAdapter mPerkuliahanAktifAdapter;
    private String bundleIdUser;
    private String bundleNamaUser;
    private List<Perkuliahan> perkuliahanAktifMahasiswaList;
    private BluetoothVerification bluetoothVerification;

    //Verifikasi Lokasi
    private ImageButton searchLocationButton;
    public static TextView location;

    //Verifikasi QR Code
    private Button buttonBluetooth;
    public static String resultBluetooth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_main_perkuliahan, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            bundleIdUser = bundle.getString("id_mahasiswa");
            bundleNamaUser = bundle.getString("nama_mahasiswa");
            Log.d("bundleIdUser", bundleIdUser);
            Log.d("bundleNamaUser", bundleNamaUser);
        }

        this.resultBluetooth = null;

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_kelas_aktif);
        emptyView = (CardView) view.findViewById(R.id.empty_view);
        location = (TextView) view.findViewById(R.id.tv_classroom_position);
        buttonBluetooth = (Button) view.findViewById(R.id.buttonBluetooth);

        TextView tvNamaPengguna = (TextView) view.findViewById(R.id.tv_perkuliahan_aktif_headline_2);
        TextView tvCurrentTgl = (TextView) view.findViewById(R.id.tv_calendar_tanggal);
        TextView tvCurrentBulan = (TextView) view.findViewById(R.id.tv_calendar_bulan);
        TextView tvCurrentHari = (TextView) view.findViewById(R.id.tv_calendar_hari);
        tvJmlPerkuliahanAktif = (TextView) view.findViewById(R.id.tv_jumlah_perkuliahan_aktif_hari_ini);
        tvJmlPerkuliahan = (TextView) view.findViewById(R.id.tv_jumlah_perkuliahan_hari_ini);

        String currentDate = SikemasDateUtils.getCurrentDate(getActivity());
        String[] splitCalendar = currentDate.split(" ");
        tvNamaPengguna.setText(bundleNamaUser);
        tvCurrentHari.setText(splitCalendar[0].substring(0, splitCalendar[0].length() - 1));
        tvCurrentTgl.setText(splitCalendar[1]);
        tvCurrentBulan.setText(splitCalendar[2]);

        tvStatusKehadiran = (TextView) view.findViewById(R.id.tv_status_kehadiran);
        ivStatusKehadiran = (ImageView) view.findViewById(R.id.iv_status_kehadiran);

        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.swipe_color_1),
                ContextCompat.getColor(getContext(), R.color.swipe_color_2),
                ContextCompat.getColor(getContext(), R.color.swipe_color_3),
                ContextCompat.getColor(getContext(), R.color.swipe_color_4));

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        bluetoothVerification = new BluetoothVerification(getActivity(), location, resultBluetooth);

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mSwipeRefreshLayout.isRefreshing()) {
            showLoading();
        }
        tvJmlPerkuliahan.setText(String.valueOf(ListPerkuliahanFragment.jmlPerkuliahan));
        perkuliahanAktifMahasiswaList = new ArrayList<>();
        getPerkuliahanAktifList(bundleIdUser);
        mPerkuliahanAktifAdapter = new PerkuliahanAktifAdapter(getActivity(), perkuliahanAktifMahasiswaList, this);
        mRecyclerView.setAdapter(mPerkuliahanAktifAdapter);

        if (perkuliahanAktifMahasiswaList != null) {
            showPerkuliahanAktifDataView();
        } else {
            showEmptyView();
        }

        buttonBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (perkuliahanAktifMahasiswaList.size() == 0)
                    Toast.makeText(getActivity(), "Tidak ada perkuliah yang aktif",
                            Toast.LENGTH_SHORT).show();
                else {
                    bluetoothVerification.startBluetoothDiscovery(perkuliahanAktifMahasiswaList);
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });
    }

    @Override
    public void onClick(int buttonId, String idPerkuliahan, String bluetoothAddr, String statusKehadiran) {
        switch (buttonId) {
            case R.id.btn_verifikasi_tandatangan:
                if (!statusKehadiran.equals("H")) {
                    if(bluetoothVerification.mLocationResult != null)
                        this.resultBluetooth = bluetoothVerification.mLocationResult;

                    if (bluetoothAddr.equals(this.resultBluetooth)) {
                        Intent intentToVerifikasiTandaTangan = new Intent(getActivity(), MenuVerifikasiTandaTangan.class);
                        intentToVerifikasiTandaTangan.putExtra("id_perkuliahan", idPerkuliahan);
                        intentToVerifikasiTandaTangan.putExtra("nrp_mahasiswa", bundleIdUser);
                        intentToVerifikasiTandaTangan.putExtra("nama_mahasiswa", bundleNamaUser);
                        startActivity(intentToVerifikasiTandaTangan);
                    }
                    else {
                        Toast.makeText(getContext(), "Pastikan Anda berada pada ruangan yang benar atau " +
                                "lakukan ulang pencarian lokasi", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                else
                    Toast.makeText(getContext(), "Anda sudah melakukan presensi pada perkuliahan ini",
                            Toast.LENGTH_SHORT).show();

            case R.id.btn_verifikasi_wajah:
                if (!statusKehadiran.equals("H")) {
                    if (bluetoothAddr.equals(this.resultBluetooth)) {
                        Intent intentToVerifikasiWajah = new Intent(getActivity(), VerifikasiWajahMenuActivity.class);
                        intentToVerifikasiWajah.putExtra("id_perkuliahan", idPerkuliahan);
                        intentToVerifikasiWajah.putExtra("nrp_mahasiswa", bundleIdUser);
                        intentToVerifikasiWajah.putExtra("nama_mahasiswa", bundleNamaUser);
                        startActivity(intentToVerifikasiWajah);
                    }
                    else {
                        Toast.makeText(getContext(), "Pastikan Anda berada pada ruangan yang benar atau " +
                                "lakukan pemindaian ulang QR Code", Toast.LENGTH_SHORT).show();
                    }
                    break;
                } else
                    Toast.makeText(getContext(), "Anda sudah melakukan presensi pada perkuliahan ini",
                            Toast.LENGTH_SHORT).show();
        }
    }

    private void showPerkuliahanAktifDataView() {
        emptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void initiateRefresh() {
        Log.d(TAG, "initiate Refresh");
        getPerkuliahanAktifList(bundleIdUser);
    }

    public void getPerkuliahanAktifList(final String idMahasiswa) {
        Log.d("idMahasiswa", idMahasiswa);
        if (!mSwipeRefreshLayout.isRefreshing())
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
                                JSONObject detailKehadiran = listKelasAktif.getJSONObject(i);
                                String statusKehadiran = detailKehadiran.getString("ket_kehadiran");

                                JSONObject perkuliahan = detailKehadiran.getJSONObject("perkuliahanmahasiswa");
                                String idPerkuliahan = perkuliahan.getString("id");
                                String pertemuanKe = perkuliahan.getString("pertemuan");
                                String statusPerkuliahan = perkuliahan.getString("status_perkuliahan");
                                String statusDosen = perkuliahan.getString("status_dosen");
                                String hari = perkuliahan.getString("hari");
                                String waktuMulai = SikemasDateUtils.formatTime(perkuliahan.getString("mulai"));
                                String waktuSelesai = SikemasDateUtils.formatTime(perkuliahan.getString("selesai"));
                                String decryptedQR = perkuliahan.getString("md5");

                                JSONObject ruangan = perkuliahan.getJSONObject("ruangan");
                                String kodeRuangan = ruangan.getString("kode_ruangan");
                                String ruangMK = ruangan.getString("nama");
                                String bluetoothAddr = ruangan.getString("bluetooth_address");

                                JSONObject kelas = perkuliahan.getJSONObject("kelas");
                                String kodeSemester = kelas.getString("kode_semester");
                                String kodeMk = kelas.getString("kode_matakuliah");
                                String kelasMk = kelas.getString("kode_kelas");
                                String namaMk = kelas.getString("nama_kelas");

                                Perkuliahan perkuliahanAktifMahasiswa = new Perkuliahan(
                                        idPerkuliahan, kodeRuangan, kodeMk, kodeSemester, namaMk,
                                        kelasMk, ruangMK, bluetoothAddr, pertemuanKe, hari, waktuMulai,
                                        waktuSelesai, statusDosen, statusPerkuliahan, statusKehadiran,
                                        decryptedQR);
                                perkuliahanAktifMahasiswaList.add(perkuliahanAktifMahasiswa);
                            }

                            tvJmlPerkuliahanAktif.setText(String.valueOf(perkuliahanAktifMahasiswaList.size()));
                            tvJmlPerkuliahan.setText(String.valueOf(ListPerkuliahanFragment.jmlPerkuliahan));

                            if (mSwipeRefreshLayout.isRefreshing()) {
                                mSwipeRefreshLayout.setRefreshing(false);
                            }

                            if (listKelasAktif.length() == 0) {
                                mPerkuliahanAktifAdapter.notifyDataSetChanged();
                                showEmptyView();
                                Toast.makeText(getActivity(), "Tidak ada Perkuliahan aktif yang dimuat",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                mPerkuliahanAktifAdapter.notifyDataSetChanged();
                                showPerkuliahanAktifDataView();
                                Toast.makeText(getActivity(), "Berhasil Memuat Perkuliahan Aktif",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("nrp", idMahasiswa);
                return params;
            }
        };
        VolleySingleton.getmInstance(getActivity()).addToRequestQueue(stringRequest);
    }
}
