package id.ac.its.sikemastc.activity.dosen;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.kofigyan.stateprogressbar.StateProgressBar;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.date.TextViewWithCircularIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.model.RuangKelas;
import id.ac.its.sikemastc.model.WaktuKelas;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class PenjadwalanUlangSementara extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    private String[] progressState = {"Tanggal", "Waktu", "Ruangan", "Konfirmasi"};

    private final String TAG = PenjadwalanUlangSementara.class.getSimpleName();

    public static final String[] MAIN_SELECTED_KELAS_DOSEN_PROJECTION = {
            SikemasContract.KelasEntry.KEY_ID_KELAS,
            SikemasContract.KelasEntry.KEY_KODE_KELAS,
            SikemasContract.KelasEntry.KEY_KODE_MK,
            SikemasContract.KelasEntry.KEY_NAMA_MK,
            SikemasContract.KelasEntry.KEY_NAMA_RUANGAN,
            SikemasContract.KelasEntry.KEY_HARI,
            SikemasContract.KelasEntry.KEY_MULAI,
            SikemasContract.KelasEntry.KEY_SELESAI
    };

    public static final int INDEX_ID_KELAS = 0;
    public static final int INDEX_KODE_KELAS = 1;
    public static final int INDEX_KELAS_KODE_MK = 3;
    public static final int INDEX_KELAS_NAMA_MK = 4;
    public static final int INDEX_KELAS_NAMA_RUANGAN = 5;
    public static final int INDEX_KELAS_HARI = 6;
    public static final int INDEX_KELAS_MULAI = 7;
    public static final int INDEX_KELAS_SELESAI = 8;

    private static final int ID_SELECTED_KELAS_LOADER = 46;

    private List<WaktuKelas> waktuKelasList;
    private List<RuangKelas> ruangKelasList;

    private ProgressBar mLoadingIndicator;
    private StateProgressBar stateProgressBar;

    private TextView tvSelectedDate;
    private TextView tvSelectedTime;
    private TextView tvSelectedRoom;
    private RadioGroup waktuRadioGroup;
    private RadioGroup ruangRadioGroup;
    private RadioButton pilihanWaktu;
    private RadioButton pilihanRuang;

    private ConstraintLayout clPilihTanggal;
    private ConstraintLayout clPilihWaktu;
    private ConstraintLayout clPilihRuangan;
    private CardView cvMataKuliahBaru;
    private CardView cvMataKuliah;

    private String idPerkuliahan;
    private String selectedDate;
    private String selectedTime;
    private String selectedRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjadwalan_ulang_sementara);

        idPerkuliahan = getIntent().getStringExtra("id_perkuliahan");

        waktuKelasList = new ArrayList<>();
        ruangKelasList = new ArrayList<>();

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Penjadwalan Ulang");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

        // Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(10f);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        stateProgressBar = (StateProgressBar) findViewById(R.id.state_reschedule_progress_bar);
        stateProgressBar.setStateDescriptionData(progressState);
        showPilihTanggalState();

//        getSupportLoaderManager().initLoader(ID_SELECTED_KELAS_LOADER, null, this);
        Button btnNextToWaktu = (Button) findViewById(R.id.btn_next_to_waktu);
        Button btnNextToRuangan = (Button) findViewById(R.id.btn_next_to_ruangan);
        Button btnNextToKonfirmasi = (Button) findViewById(R.id.btn_next_to_konfirmasi);

        Button btnPilihTanggal = (Button) findViewById(R.id.btn_pilih_tanggal);
        Button btnSimpanPerubahan = (Button) findViewById(R.id.btn_konfirmasi_perubahan);
        Button btnBatalkanPerubahan = (Button) findViewById(R.id.btn_batalkan_perubahan);

        tvSelectedDate = (TextView) findViewById(R.id.tv_tanggal_terpilih);
        tvSelectedTime = (TextView) findViewById(R.id.tv_waktu_terpilih);
        tvSelectedRoom = (TextView) findViewById(R.id.tv_ruangan_terpilih);

        clPilihTanggal = (ConstraintLayout) findViewById(R.id.cl_pilih_tanggal);
        clPilihWaktu = (ConstraintLayout) findViewById(R.id.cl_pilih_waktu);
        clPilihRuangan = (ConstraintLayout) findViewById(R.id.cl_pilih_ruangan);
        cvMataKuliahBaru = (CardView) findViewById(R.id.cv_mata_kuliah_baru);
        cvMataKuliah = (CardView) findViewById(R.id.cv_mata_kuliah);

        btnPilihTanggal.setOnClickListener(operate);
        btnNextToWaktu.setOnClickListener(operate);
        btnNextToRuangan.setOnClickListener(operate);
        btnNextToKonfirmasi.setOnClickListener(operate);
        btnSimpanPerubahan.setOnClickListener(operate);
        btnBatalkanPerubahan.setOnClickListener(operate);
    }

    View.OnClickListener operate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_pilih_tanggal:
                    Calendar now = Calendar.getInstance();
                    DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                            PenjadwalanUlangSementara.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    datePickerDialog.setAccentColor(ContextCompat.getColor(getApplicationContext()
                            , R.color.colorPrimaryDark));
                    datePickerDialog.setVersion(DatePickerDialog.Version.VERSION_2);
                    datePickerDialog.show(getFragmentManager(), "DatePickerdialog");
                    break;
                case R.id.btn_next_to_waktu:
                    if (selectedDate != null)
                        getWaktuTersedia(selectedDate);
                    else
                        Toast.makeText(getApplicationContext(), "Anda belum memilih tanggal perkuliahan pengganti",
                                Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_next_to_ruangan:
                    if (selectedDate != null && selectedTime != null)
                        getRuanganTersedia(selectedDate, selectedTime);
                    else
                        Toast.makeText(getApplicationContext(), "Anda belum memilih waktu perkuliahan pengganti",
                                Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_next_to_konfirmasi:
                    if (selectedDate != null && selectedTime != null && selectedRoom != null
                            && idPerkuliahan != null)
                        showKonfirmasiJadwalState();
                    else
                        Toast.makeText(getApplicationContext(), "Anda belum memilih ruang kelas perkuliahan pengganti",
                                Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_simpan_perubahan:
                    postJadwalBaru(selectedDate, selectedTime, selectedRoom, idPerkuliahan);
                    break;
            }
        }
    };

//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        return null;
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//
//    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        tvSelectedDate.setText(SikemasDateUtils.formatDate(selectedDate));
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showPilihTanggalState() {
        mLoadingIndicator.setVisibility(View.GONE);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.ONE);
    }

    private void showPilihWaktuState() {
        mLoadingIndicator.setVisibility(View.GONE);
        clPilihTanggal.setVisibility(View.GONE);
        clPilihWaktu.setVisibility(View.VISIBLE);
        stateProgressBar.enableAnimationToCurrentState(true);
        stateProgressBar.setAnimationStartDelay(200000);
        stateProgressBar.setAnimationDuration(5000000);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.TWO);
        waktuRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                selectedTime = String.valueOf(checkedId);
                pilihanWaktu = (RadioButton) findViewById(checkedId);
                tvSelectedTime.setText(pilihanWaktu.getText());
            }
        });
    }

    private void showPilihRuanganState() {
        mLoadingIndicator.setVisibility(View.GONE);
        clPilihTanggal.setVisibility(View.GONE);
        clPilihWaktu.setVisibility(View.GONE);
        clPilihRuangan.setVisibility(View.VISIBLE);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.THREE);
        ruangRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                selectedRoom = String.valueOf(checkedId);
                pilihanRuang = (RadioButton) findViewById(checkedId);
                tvSelectedRoom.setText(pilihanRuang.getText());
            }
        });
    }

    private void showKonfirmasiJadwalState() {
        mLoadingIndicator.setVisibility(View.GONE);
        clPilihTanggal.setVisibility(View.GONE);
        clPilihWaktu.setVisibility(View.GONE);
        clPilihRuangan.setVisibility(View.GONE);
        cvMataKuliah.setVisibility(View.GONE);
        cvMataKuliahBaru.setVisibility(View.VISIBLE);
        stateProgressBar.setCurrentStateNumber(StateProgressBar.StateNumber.FOUR);
    }

    private void getWaktuTersedia(final String tanggalTerpilih) {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.REQUEST_PENJADWALAN_BY_TANGGAL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listWaktu = jsonObject.getJSONArray("listwaktu");
                            for (int i = 0; i < listWaktu.length(); i++) {
                                JSONObject listWaktuKelas = listWaktu.getJSONObject(i);
                                String idWaktu = listWaktuKelas.getString("id");
                                String hari = listWaktuKelas.getString("hari");
                                String waktuMulai = SikemasDateUtils.formatTime(listWaktuKelas.getString("mulai"));
                                String waktuSelesai = SikemasDateUtils.formatTime(listWaktuKelas.getString("selesai"));

                                WaktuKelas waktuKelas = new WaktuKelas(
                                        idWaktu, hari, waktuMulai, waktuSelesai);
                                waktuKelasList.add(waktuKelas);
                                Log.d("waktu kelas", String.valueOf(waktuKelas));
                            }
                            addWaktuRadioButton();
                            showPilihWaktuState();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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
                params.put("tanggal", tanggalTerpilih);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void getRuanganTersedia(final String tanggalTerpilih, final String idWaktu) {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.REQUEST_PENJADWALAN_BY_TANGGAL_WAKTU,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listRuangan = jsonObject.getJSONArray("listruangan");
                            for (int i = 0; i < listRuangan.length(); i++) {
                                JSONObject listRuanganKelas = listRuangan.getJSONObject(i);
                                String idRuangan = listRuanganKelas.getString("id");
                                String namaRuangan = listRuanganKelas.getString("nama");

                                RuangKelas ruangKelas = new RuangKelas(idRuangan, namaRuangan);
                                ruangKelasList.add(ruangKelas);
                                Log.d("ruang kelas", String.valueOf(ruangKelas));
                            }
                            addRuangRadioButton();
                            showPilihRuanganState();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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
                params.put("tanggal", tanggalTerpilih);
                params.put("id_waktu", idWaktu);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void postJadwalBaru(final String tanggalTerpilih, final String idWaktu,
                                final String idRuangan, final String idPerkuliahan) {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.REQUEST_PENJADWALAN_BY_TANGGAL_WAKTU,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listRuangan = jsonObject.getJSONArray("listruangan");
                            for (int i = 0; i < listRuangan.length(); i++) {
                                JSONObject listRuanganKelas = listRuangan.getJSONObject(i);
                                String idRuangan = listRuanganKelas.getString("id");
                                String namaRuangan = listRuanganKelas.getString("nama");

                                RuangKelas ruangKelas = new RuangKelas(idRuangan, namaRuangan);
                                ruangKelasList.add(ruangKelas);
                                Log.d("waktu kelas", String.valueOf(ruangKelas));
                            }
                            showPilihWaktuState();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
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
                params.put("tanggal", tanggalTerpilih);
                params.put("id_waktu", idWaktu);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    private void addWaktuRadioButton() {
        for (int i = 0; i < 1; i++) {
            waktuRadioGroup = new RadioGroup(this);
            waktuRadioGroup.setOrientation(LinearLayout.VERTICAL);
            for (int j = 0; j < waktuKelasList.size(); j++) {
                pilihanWaktu = new RadioButton(this);
                pilihanWaktu.setId(Integer.parseInt(waktuKelasList.get(j).getIdWaktuKelas()));
                pilihanWaktu.setTextSize(16f);
                pilihanWaktu.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                pilihanWaktu.setText(waktuKelasList.get(j).getWaktuMulai() + " - " +
                        waktuKelasList.get(j).getWaktuSelesai());
                waktuRadioGroup.addView(pilihanWaktu);
            }
            ((ViewGroup) findViewById(R.id.pilihan_waktu_radio_group)).addView(waktuRadioGroup);
        }
    }

    private void addRuangRadioButton(){
        for (int i = 0; i < 1; i++) {
            ruangRadioGroup = new RadioGroup(this);
            ruangRadioGroup.setOrientation(LinearLayout.VERTICAL);
            for (int j = 0; j < ruangKelasList.size(); j++) {
                pilihanRuang = new RadioButton(this);
                pilihanRuang.setId(Integer.parseInt(ruangKelasList.get(j).getIdRuangan()));
                pilihanRuang.setTextSize(18f);
                pilihanRuang.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                pilihanRuang.setText(ruangKelasList.get(j).getNamaRuangan());
                ruangRadioGroup.addView(pilihanRuang);
            }
            ((ViewGroup) findViewById(R.id.pilihan_ruang_radio_group)).addView(ruangRadioGroup);
        }
    }
}