package id.ac.its.sikemastc.activity.dosen;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.KehadiranAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;
import id.ac.its.sikemastc.utilities.SikemasJsonUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class LihatKehadiran extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        KehadiranAdapter.KehadiranAdapterOnClickHandler {

    private final String TAG = LihatKehadiran.class.getSimpleName();

    public static final String[] MAIN_LIST_KEHADIRAN_PESERTA_PROJECTION = {
            SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_ID_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_NRP_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_NAMA_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_STATUS_KEHADIRAN,
            SikemasContract.KehadiranEntry.KEY_WAKTU_CHEKIN
    };

    public static final int INDEX_ID_PERKULIAHAN_MAHASISWA = 0;
    public static final int INDEX_ID_MAHASISWA = 1;
    public static final int INDEX_NRP_MAHASISWA = 2;
    public static final int INDEX_NAMA_MAHASISWA = 3;
    public static final int INDEX_KET_KEHADIRAN = 4;
    public static final int INDEX_WAKTU_CHECKIN = 5;

    private static final int ID_LIST_KEHADIRAN_LOADER = 60;
    private ProgressBar mLoadingIndicator;
    private View positiveAction;
    private RadioGroup statusRadioGroup;
    private RadioButton statusRadioButton;
    private EditText edtAlasanIjin;
    private TextView tvAlasanLabel;

    private String selectedPilihan;
    private String selectedKey;
    private String selectedPerkuliahan;

    private RecyclerView mRecyclerView;
    private KehadiranAdapter mKehadiranAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kehadiran);

        selectedPerkuliahan = getIntent().getStringExtra("id_perkuliahan");

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Lihat Kehadiran");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

//         Compatibility
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mToolbar.setElevation(10f);
        }

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String title = (String) item.getTitle();
                Toast.makeText(LihatKehadiran.this, title + " Selected !", Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.refresh:
                        refreshData(selectedPerkuliahan);
                        break;
                }
                return true;
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_kehadiran_peserta);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mKehadiranAdapter = new KehadiranAdapter(this, this);
        mRecyclerView.setAdapter(mKehadiranAdapter);

        showLoading();

        mUri = getIntent().getData();
        if (mUri == null)
            throw new NullPointerException("URI for DetailPerkuliahanActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_LIST_KEHADIRAN_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_KEHADIRAN_LOADER:
                return new CursorLoader(this,
                        mUri,
                        MAIN_LIST_KEHADIRAN_PESERTA_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mKehadiranAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0)
            showKehadiranDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKehadiranAdapter.swapCursor(null);
    }

    private void showKehadiranDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String idPerkuliahan, String idMahasiswa, String nrpMahasiswa, String namaMahasiswa) {
        showAlertDialog(idPerkuliahan, idMahasiswa, nrpMahasiswa, namaMahasiswa);
    }

    private void showAlertDialog(final String idPerkuliahan, final String idMahasiswa,
                                 final String nrpPeserta, final String namaPeserta) {

        selectedKey = "";
        final MaterialDialog dialog = new MaterialDialog.Builder(this)
                .title("Ubah Status Kehadiran")
                .customView(R.layout.dialog_custom_view, true)
                .positiveText(R.string.pilih)
                .negativeText(R.string.batal)
                .build();

        final String[] pesan = {""};
        positiveAction = dialog.getActionButton(DialogAction.POSITIVE);
        statusRadioGroup = (RadioGroup) dialog.getCustomView().findViewById(R.id.rg_status_kehadiran);
        tvAlasanLabel = (TextView) dialog.getCustomView().findViewById(R.id.tv_alasan_ijin_label);
        edtAlasanIjin = (EditText) dialog.getCustomView().findViewById(R.id.edt_alasan_ijin);
        TextView identitas = (TextView) dialog.getCustomView().findViewById(R.id.tv_identitas_mahasiswa);
        identitas.setText(nrpPeserta + " - " + namaPeserta);

        statusRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                statusRadioButton = (RadioButton) dialog.getCustomView().findViewById(checkedId);
                selectedPilihan = statusRadioButton.getText().toString();
                if (selectedPilihan.equals("Ijin")) {
                    selectedKey = "I";
                    tvAlasanLabel.setVisibility(View.VISIBLE);
                    edtAlasanIjin.setVisibility(View.VISIBLE);
                    positiveAction.setEnabled(false);
                } else if (selectedPilihan.equals("Hadir")) {
                    selectedKey = "H";
                    tvAlasanLabel.setVisibility(View.GONE);
                    edtAlasanIjin.setVisibility(View.GONE);
                    positiveAction.setEnabled(true);
                } else if (selectedPilihan.equals("Absen")) {
                    selectedKey = "A";
                    tvAlasanLabel.setVisibility(View.GONE);
                    edtAlasanIjin.setVisibility(View.GONE);
                    positiveAction.setEnabled(true);
                }
            }
        });

        edtAlasanIjin.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        positiveAction.setEnabled(s.toString().trim().length() > 0);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        pesan[0] = edtAlasanIjin.getText().toString();
                    }
                });

        positiveAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedKey.equals(""))
                    Toast.makeText(getApplicationContext(), "Piihan Status Kehadiran tidak boleh kosong",
                            Toast.LENGTH_SHORT).show();
                changeStatusKehadiran(idPerkuliahan, nrpPeserta, selectedKey, pesan[0]);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void changeStatusKehadiran(final String idPerkuliahan, final String idPeserta,
                                      final String ketKehadiran, final String pesan) {
        Log.d("idPerkuliahan", idPerkuliahan);
        Log.d("idMahasiswa", idPeserta);
        Log.d("kodeKehadiran", ketKehadiran);
        Log.d("pesan", pesan);
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
                            Toast.makeText(getApplicationContext(), "Berhasil Mengubah Status Peserta Perkuliahan",
                                    Toast.LENGTH_SHORT).show();
                            refreshData(idPerkuliahan);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " +
                                    e.getMessage(), Toast.LENGTH_LONG).show();
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
                params.put("pesan", pesan);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void refreshData(final String idPerkuliahan) {
        showLoading();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.REFRESH_KEHADIRAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Peserta Kehadiran Response: " + response);
                        try {
                            int totalPeserta = 0;
                            String idMahasiswa, idPerkuliahanMahasiswa, ketKehadiran, waktuHadir,
                                    nrpMahasiswa, namaMahasiswa;

                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray listKehadiran = jsonObject.getJSONArray("list");
                            totalPeserta = listKehadiran.length();
                            ContentValues[] kehadiranPesertaContentValues =
                                    new ContentValues[totalPeserta];
                            for (int i = 0; i < listKehadiran.length(); i++) {
                                JSONObject kehadiran = listKehadiran.getJSONObject(i);
                                idMahasiswa = kehadiran.getString("id_mahasiswa");
                                idPerkuliahanMahasiswa = kehadiran.getString("id_perkuliahanmahasiswa");
                                ketKehadiran = kehadiran.getString("ket_kehadiran");
                                waktuHadir = kehadiran.getString("updated_at");
                                if (!waktuHadir.equals("null"))
                                    waktuHadir = SikemasDateUtils.formatTimeFromTimestamp(waktuHadir);
                                else
                                    waktuHadir = "-";

                                JSONObject mahasiswa = kehadiran.getJSONObject("mahasiswa");
                                nrpMahasiswa = mahasiswa.getString("nrp");
                                namaMahasiswa = mahasiswa.getString("nama");

                                ContentValues kehadiranPesertaValue = new ContentValues();
                                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_NRP_MAHASISWA, nrpMahasiswa);
                                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_NAMA_MAHASISWA, namaMahasiswa);
                                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA, idPerkuliahanMahasiswa);
                                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_ID_MAHASISWA, idMahasiswa);
                                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_STATUS_KEHADIRAN, ketKehadiran);
                                kehadiranPesertaValue.put(SikemasContract.KehadiranEntry.KEY_WAKTU_CHEKIN, waktuHadir);

                                kehadiranPesertaContentValues[i] = kehadiranPesertaValue;
                            }

                            if (kehadiranPesertaContentValues.length != 0) {
                                Log.d("kehadiranLength", String.valueOf(kehadiranPesertaContentValues.length));
                                ContentResolver sikemasContentResolver = getApplicationContext().getContentResolver();
                                sikemasContentResolver.delete(
                                        SikemasContract.KehadiranEntry.CONTENT_URI,
                                        SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA + " = ? ",
                                        new String[]{idPerkuliahan}
                                );
                                sikemasContentResolver.bulkInsert(
                                        SikemasContract.KehadiranEntry.CONTENT_URI,
                                        kehadiranPesertaContentValues
                                );
                            }

                            Toast.makeText(getApplicationContext(), "Berhasil Mengubah Status Peserta Perkuliahan",
                                    Toast.LENGTH_SHORT).show();
                            getApplicationContext().getContentResolver().notifyChange(mUri, null);
                            showKehadiranDataView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "Json error: " +
                                    e.getMessage(), Toast.LENGTH_LONG).show();
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
}
