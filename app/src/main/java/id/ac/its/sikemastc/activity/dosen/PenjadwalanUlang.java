package id.ac.its.sikemastc.activity.dosen;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.Perkuliahan;
import id.ac.its.sikemastc.model.RuangKelas;
import id.ac.its.sikemastc.model.WaktuKelas;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class PenjadwalanUlang extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener {

    private String[] progressState = {"Tanggal", "Waktu", "Ruangan", "Konfirmasi"};
    private final String TAG = PenjadwalanUlang.class.getSimpleName();

    private Perkuliahan perkuliahanTerpilih;
    private List<WaktuKelas> waktuKelasList;
    private List<RuangKelas> ruangKelasList;

    private ProgressBar mLoadingIndicator;
    private ProgressDialog mProgressDialog;
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
    private String selectedRoomName;
    private String selectedTimeStart;
    private String selectedTimeEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_penjadwalan_ulang_sementara);

        idPerkuliahan = getIntent().getStringExtra("id_perkuliahan");
        waktuKelasList = new ArrayList<>();
        ruangKelasList = new ArrayList<>();
        mProgressDialog = new ProgressDialog(this);
        getJadwalTerpilih(idPerkuliahan);

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
                            PenjadwalanUlang.this,
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
                case R.id.btn_konfirmasi_perubahan:
                    showAlertDialog(R.id.btn_konfirmasi_perubahan);
                    break;
                case R.id.btn_batalkan_perubahan:
                    showAlertDialog(R.id.btn_batalkan_perubahan);
                    break;
            }
        }
    };

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        selectedDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        tvSelectedDate.setText(SikemasDateUtils.formatDate(selectedDate));
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showJadwalTerpilih() {
        TextView tvPertemuanKe = (TextView) findViewById(R.id.tv_pertemuan_ke);
        TextView tvKodePerkuliahan = (TextView) findViewById(R.id.tv_kode_matakuliah);
        TextView tvNamaMk = (TextView) findViewById(R.id.tv_matakuliah);
        TextView tvRuangan = (TextView) findViewById(R.id.tv_ruangan);
        TextView tvKelas = (TextView) findViewById(R.id.tv_kelas);
        TextView tvTanggal = (TextView) findViewById(R.id.tv_date);
        TextView tvWaktuMulai = (TextView) findViewById(R.id.tv_waktu_mulai);
        TextView tvWaktuSelesai = (TextView) findViewById(R.id.tv_waktu_selesai);

        tvPertemuanKe.setText(perkuliahanTerpilih.getPertemuanKe());
        tvKodePerkuliahan.setText(perkuliahanTerpilih.getKodeSemester());
        tvNamaMk.setText(perkuliahanTerpilih.getNamaMk());
        tvRuangan.setText(perkuliahanTerpilih.getRuangMk());
        tvKelas.setText(perkuliahanTerpilih.getKelasMk());
        tvTanggal.setText(perkuliahanTerpilih.getTanggal());
        tvWaktuMulai.setText(perkuliahanTerpilih.getWaktuMulai());
        tvWaktuSelesai.setText(perkuliahanTerpilih.getWaktuSelesai());
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
                pilihanWaktu = (RadioButton) group.findViewById(checkedId);
                tvSelectedTime.setText(pilihanWaktu.getText());
                String selectedTimeFull = (String) pilihanWaktu.getText();
                String[] splittedTime = selectedTimeFull.split(" - ");
                selectedTimeStart = splittedTime[0];
                selectedTimeEnd = splittedTime[1];
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
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedRuangId) {
                pilihanRuang = (RadioButton) group.findViewById(checkedRuangId);
                selectedRoom = String.valueOf(checkedRuangId);
                selectedRoomName = (String) pilihanRuang.getText();
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
        showPerubahanJadwal();
    }

    private void showPerubahanJadwal() {
        TextView tvPertemuanKe = (TextView) findViewById(R.id.tv_pertemuan_ke_baru);
        TextView tvKodePerkuliahan = (TextView) findViewById(R.id.tv_kode_matakuliah);
        TextView tvNamaMk = (TextView) findViewById(R.id.tv_matakuliah_baru);
        TextView tvKelas = (TextView) findViewById(R.id.tv_kelas_lama);

        TextView tvRuanganLama = (TextView) findViewById(R.id.tv_ruangan_lama);
        TextView tvRuanganBaru = (TextView) findViewById(R.id.tv_ruangan_baru);
        TextView tvTanggalLama = (TextView) findViewById(R.id.tv_date_lama);
        TextView tvTanggalBaru = (TextView) findViewById(R.id.tv_date_baru);
        TextView tvWaktuMulaiLama = (TextView) findViewById(R.id.tv_waktu_mulai_lama);
        TextView tvWaktuSelesaiLama = (TextView) findViewById(R.id.tv_waktu_selesai_lama);
        TextView tvWaktuMulaiBaru = (TextView) findViewById(R.id.tv_waktu_mulai_baru);
        TextView tvWaktuSelesaiBaru = (TextView) findViewById(R.id.tv_waktu_selesai_baru);

        tvPertemuanKe.setText(perkuliahanTerpilih.getPertemuanKe());
        tvKodePerkuliahan.setText(perkuliahanTerpilih.getKodeSemester());
        tvNamaMk.setText(perkuliahanTerpilih.getNamaMk());
        tvKelas.setText(perkuliahanTerpilih.getKelasMk());

        tvRuanganLama.setText(perkuliahanTerpilih.getRuangMk());
        tvTanggalLama.setText(perkuliahanTerpilih.getTanggal());
        tvWaktuMulaiLama.setText(perkuliahanTerpilih.getWaktuMulai());
        tvWaktuSelesaiLama.setText(perkuliahanTerpilih.getWaktuSelesai());

        tvRuanganBaru.setText(selectedRoomName);
        tvTanggalBaru.setText(SikemasDateUtils.formatDate(selectedDate));
        tvWaktuMulaiBaru.setText(selectedTimeStart);
        tvWaktuSelesaiBaru.setText(selectedTimeEnd);
    }

    private void getJadwalTerpilih(final String idPerkuliahan) {
        mProgressDialog.setMessage("Memuat Jadwal Perkuliahan Terpilih ...");
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.REQUEST_JADWAL_PERKULIAHAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Perkuliahan terpilih response: " + response);
                        try {
                            JSONArray jsonObject = new JSONArray(response);
                            JSONObject list = jsonObject.getJSONObject(0);
                            String pertemuanKe = list.getString("pertemuan");
                            String tanggal = SikemasDateUtils.formatDate(list.getString("tanggal"));
                            String waktuMulai = SikemasDateUtils.formatTime(list.getString("mulai"));
                            String waktuSelesai = SikemasDateUtils.formatTime(list.getString("selesai"));
                            JSONObject kelas = list.getJSONObject("kelas");
                            String kodeKelas = kelas.getString("kode_kelas");
                            String kodeMk = kelas.getString("kode_matakuliah");
                            String namaMk = kelas.getString("nama_kelas");
                            Log.d(TAG, namaMk);
                            JSONObject ruangan = list.getJSONObject("ruangan");
                            String namaRuangan = ruangan.getString("nama");
                            perkuliahanTerpilih = new Perkuliahan(idPerkuliahan, kodeMk, namaMk,
                                    kodeKelas, namaRuangan, pertemuanKe, tanggal, waktuMulai, waktuSelesai);
                            showJadwalTerpilih();
                            mProgressDialog.dismiss();
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
                params.put("id_perkuliahan", idPerkuliahan);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
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
        mProgressDialog.setMessage("Mengubah Jadwal Sementara ...");
        mProgressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.CONFIRM_PENJADWALAN_SEMENTARA,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "response konfirmasi: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            String status = jsonObject.getString("status");
                            switch (code) {
                                case "1":
                                    Toast.makeText(getApplicationContext(), status,
                                            Toast.LENGTH_SHORT).show();
                            }
                            mProgressDialog.dismiss();
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
                params.put("id_ruangan", idRuangan);
                params.put("id_perkuliahan", idPerkuliahan);
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

    private void addRuangRadioButton() {
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

    private void showAlertDialog(int btnClickId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (btnClickId) {
            case R.id.btn_konfirmasi_perubahan:
                builder.setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin mengubah jadwal pertemuan ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postJadwalBaru(selectedDate, selectedTime, selectedRoom, idPerkuliahan);
                                dialog.dismiss();
                                finish();
                            }
                        });
                break;
            case R.id.btn_batalkan_perubahan:
                builder.setTitle("Batalkan")
                        .setMessage("Apakah Anda yakin ingin membatalkan perubahan jadwal pertemuan ini?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Iya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}