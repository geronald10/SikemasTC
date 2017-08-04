package id.ac.its.sikemastc.activity.mahasiswa;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.verifikasi_lokasi.GoogleAPITracker;
import id.ac.its.sikemastc.activity.verifikasi_lokasi.LocationService;
import id.ac.its.sikemastc.activity.verifikasi_qr_code.BarcodeCaptureActivity;
import id.ac.its.sikemastc.activity.verifikasi_tandatangan.MenuVerifikasiTandaTangan;
import id.ac.its.sikemastc.activity.verifikasi_wajah.VerifikasiWajahMenuActivity;
import id.ac.its.sikemastc.adapter.PerkuliahanAktifAdapter;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.model.Perkuliahan;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class MainPerkuliahanFragment extends Fragment implements
        PerkuliahanAktifAdapter.PerkuliahanAktifOnClickHandler {

    private final String TAG = MainPerkuliahanFragment.class.getSimpleName();

    private TextView currentDate;
    private TextView tvStatusKehadiran;
    private ImageView ivStatusKehadiran;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private ConstraintLayout emptyView;
    private PerkuliahanAktifAdapter mPerkuliahanAktifAdapter;
    private String bundleIdUser;
    private String bundleNamaUser;
    private List<Perkuliahan> perkuliahanAktifMahasiswaList;

    //Verifikasi Lokasi
    private ImageButton searchLocationButton;
    public static TextView location;
    public static ProgressBar mSearchingIndicator;
    public static TextView searchLoading;

    //Verifikasi QR Code
    private Button buttonQR;
    private String resultQR;

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

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_kelas_aktif);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
        mSearchingIndicator = (ProgressBar) view.findViewById(R.id.pb_searching_location);
        emptyView = (ConstraintLayout) view.findViewById(R.id.empty_view);
        currentDate = (TextView) view.findViewById(R.id.tv_tanggal_hari_ini);
        searchLocationButton = (ImageButton) view.findViewById(R.id.ib_refresh_location);
        searchLoading = (TextView) view.findViewById(R.id.tv_mencari_lokasi);
        location = (TextView) view.findViewById(R.id.tv_classroom_position);
        buttonQR = (Button) view.findViewById(R.id.buttonQR);

        currentDate = (TextView) view.findViewById(R.id.tv_tanggal_hari_ini);
        currentDate.setText(SikemasDateUtils.getCurrentDate(getActivity()));

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

        return view;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (!mSwipeRefreshLayout.isRefreshing()) {
            showLoading();
        }

        if (GoogleAPITracker.finalResult[0] != "0") {
            showLocationFounded();
        }

        perkuliahanAktifMahasiswaList = new ArrayList<>();
        getPerkuliahanAktifList(bundleIdUser);
        mPerkuliahanAktifAdapter = new PerkuliahanAktifAdapter(getActivity(), perkuliahanAktifMahasiswaList, this);
        mRecyclerView.setAdapter(mPerkuliahanAktifAdapter);

        // Verifikasi Lokasi
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());
        final Intent intent = new Intent(view.getContext(), LocationService.class);
        intent.putExtra("NRP", this.bundleIdUser);

        if (perkuliahanAktifMahasiswaList != null) {
            showPerkuliahanAktifDataView();
        } else {
            showEmptyView();
        }

        searchLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            getContext().startService(intent);
            showSearchingLocation();
            }
        });

        buttonQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
            Intent intent = new Intent(getContext(), BarcodeCaptureActivity.class);
            startActivityForResult(intent, 1);
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
    public void onClick(int buttonId, String idPerkuliahan, String kodeRuangan) {
        switch (buttonId) {
            case R.id.btn_verifikasi_tandatangan:
                //if (kodeRuangan.equals(this.resultQR)) {
                ////    Toast.makeText(getContext(), "ini nrp ->" +bundleIdUser, Toast.LENGTH_SHORT).show();
                    Intent intentToVerifikasiTandaTangan = new Intent(getActivity(), MenuVerifikasiTandaTangan.class);
                    intentToVerifikasiTandaTangan.putExtra("id_perkuliahan", idPerkuliahan);
                    intentToVerifikasiTandaTangan.putExtra("nrp_mahasiswa", bundleIdUser);
                    intentToVerifikasiTandaTangan.putExtra("nama_mahasiswa", bundleNamaUser);
                    startActivity(intentToVerifikasiTandaTangan);
//             //   } else {
//                    Toast.makeText(getContext(), "Pastikan Anda berada pada ruangan yang benar atau " +
//                            "lakukan pemindaian ulang QR Code", Toast.LENGTH_SHORT).show();
//                    Log.d("Cek", this.resultQR + " " + kodeRuangan);
//                }
                break;

            case R.id.btn_verifikasi_wajah:
            //    if (kodeRuangan.equals(this.resultQR)) {
                    Intent intentToVerifikasiWajah = new Intent(getActivity(), VerifikasiWajahMenuActivity.class);
                    intentToVerifikasiWajah.putExtra("id_perkuliahan", idPerkuliahan);
                    intentToVerifikasiWajah.putExtra("nrp_mahasiswa", bundleIdUser);
                    intentToVerifikasiWajah.putExtra("nama_mahasiswa", bundleNamaUser);
                    startActivity(intentToVerifikasiWajah);
//                } else {
//                    Toast.makeText(getContext(), "Pastikan Anda berada pada ruangan yang benar atau " +
//                            "lakukan pemindaian ulang QR Code", Toast.LENGTH_SHORT).show();
//                }
                break;
        }
    }

    private void showSearchingLocation() {
        location.setVisibility(View.GONE);
        mSearchingIndicator.setVisibility(View.VISIBLE);
        searchLoading.setVisibility(View.VISIBLE);
    }

    public static void showLocationFounded() {
        mSearchingIndicator.setVisibility(View.GONE);
        searchLoading.setVisibility(View.GONE);
        location.setVisibility(View.VISIBLE);
    }

    private void showPerkuliahanAktifDataView() {
        mLoadingIndicator.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
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

                                JSONObject ruangan = perkuliahan.getJSONObject("ruangan");
                                String kodeRuangan = ruangan.getString("kode_ruangan");

                                JSONObject kelas = perkuliahan.getJSONObject("kelas");
                                String kodeSemester = kelas.getString("kode_semester");
                                String kodeMk = kelas.getString("kode_matakuliah");
                                String kelasMk = kelas.getString("kode_kelas");
                                String namaMk = kelas.getString("nama_kelas");
                                String ruangMK = kelas.getString("nama_ruangan");

                                Perkuliahan perkuliahanAktifMahasiswa = new Perkuliahan(
                                        idPerkuliahan, kodeRuangan, kodeMk, kodeSemester, namaMk,
                                        kelasMk, ruangMK, pertemuanKe, hari, waktuMulai,
                                        waktuSelesai, statusDosen, statusPerkuliahan, statusKehadiran);
                                perkuliahanAktifMahasiswaList.add(perkuliahanAktifMahasiswa);
                            }

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
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("nrp", idMahasiswa);
                return params;
            }
        };
        VolleySingleton.getmInstance(getActivity()).addToRequestQueue(stringRequest);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    String md5 = null;

                    if (barcode.displayValue.replace(" ","").length() % 4 == 0) {
                        try {
                            byte[] digest = Base64.decode(barcode.displayValue, Base64.DEFAULT);
                            String text = new String(digest, "UTF-8");

                            if (text.length() > 8) {
                                String decrypt = "Error QR Code";
                                if (text.contains("IF-101 Informatika Sikemas")) {
                                    decrypt = "IF-101";
                                }
                                else if (text.contains("IF-102 Informatika Sikemas")) {
                                    decrypt = "IF-102";
                                }
                                else if (text.contains("IF-103 Informatika Sikemas")) {
                                    decrypt = "IF-103";
                                }
                                else if (text.contains("IF-104 Informatika Sikemas")) {
                                    decrypt = "IF-104";
                                }
                                else if (text.contains("IF-105a Informatika Sikemas")) {
                                    decrypt = "IF-105a";
                                }
                                else if (text.contains("IF-105b Informatika Sikemas")) {
                                    decrypt = "IF-105b";
                                }
                                else if (text.contains("IF-106 Informatika Sikemas")) {
                                    decrypt = "IF-106";
                                }
                                else if (text.contains("IF-108 Informatika Sikemas")) {
                                    decrypt = "IF-108";
                                }
                                location.setText(decrypt);
                                this.resultQR = decrypt;
                            }
                            else {
                                location.setText("Error QR Code");
                                this.resultQR = "Error QR Code";
                            }

                        }
                        catch (Exception ex) {

                        }
                    }
                    if (barcode.displayValue.matches("[a-fA-F0-9]{32}")) {
                        try {
                            String decrypt = "Error QR Code";
                            Log.d("MD5 Hash", barcode.displayValue);
                            if (barcode.displayValue.equals(md5Encode("IF-101 Informatika Sikemas"))) {
                                decrypt = "IF-101";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-102 Informatika Sikemas"))) {
                                decrypt = "IF-102";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-103 Informatika Sikemas"))) {
                                decrypt = "IF-103";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-104 Informatika Sikemas"))) {
                                decrypt = "IF-104";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-105a Informatika Sikemas"))) {
                                decrypt = "IF-105a";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-105b Informatika Sikemas"))) {
                                decrypt = "IF-105b";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-106 Informatika Sikemas"))) {
                                decrypt = "IF-106";
                            }
                            else if (barcode.displayValue.equals(md5Encode("IF-108 Informatika Sikemas"))) {
                                decrypt = "IF-108";
                            }
                            location.setText(decrypt);
                            this.resultQR = decrypt;
                        }
                        catch (Exception ex) {

                        }
                    }


                }
                else {
                    location.setText(R.string.no_barcode_captured);
                    this.resultQR = null;
                }
            }
            else Log.e("Pemindaian Error", String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        }
        else super.onActivityResult(requestCode, resultCode, data);
    }

    private Boolean isBase64Encoded(String value) {
        try {
            byte[] decodedString = Base64.decode(value, Base64.DEFAULT);
            BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private String md5Encode(String value) {
        try {
            // Create MD5 Hash
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(value.getBytes());
            byte messageDigest[] = md.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
