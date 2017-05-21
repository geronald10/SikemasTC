package id.ac.its.sikemastc.activity.dosen;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.adapter.PerkuliahanDosenAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;
import id.ac.its.sikemastc.utilities.NetworkUtils;
import id.ac.its.sikemastc.utilities.VolleySingleton;

public class HalamanUtamaDosen extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PerkuliahanDosenAdapter.PerkuliahanAdapterOnClickHandler {

    private final String TAG = HalamanUtamaDosen.class.getSimpleName();

    public static final String[] MAIN_LIST_PERKULIAHAN_DOSEN_PROJECTION = {
            SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_STATUS_DOSEN,
            SikemasContract.PerkuliahanEntry.KEY_PERTEMUAN_KE,
            SikemasContract.PerkuliahanEntry.KEY_TANGGAL_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_NAMA_MK,
            SikemasContract.PerkuliahanEntry.KEY_NAMA_RUANGAN,
            SikemasContract.PerkuliahanEntry.KEY_KODE_KELAS,
            SikemasContract.PerkuliahanEntry.KEY_HARI,
            SikemasContract.PerkuliahanEntry.KEY_MULAI,
            SikemasContract.PerkuliahanEntry.KEY_SELESAI
    };

    public static final int INDEX_ID_PERKULIAHAN = 0;
    public static final int INDEX_STATUS_DOSEN = 1;
    public static final int INDEX_PERTEMUAN_KE = 2;
    public static final int INDEX_TANGGAL_PERKULIAHAN = 3;
    public static final int INDEX_NAMA_MK = 4;
    public static final int INDEX_NAMA_RUANGAN = 5;
    public static final int INDEX_KODE_KELAS = 6;
    public static final int INDEX_HARI = 7;
    public static final int INDEX_MULAI = 8;
    public static final int INDEX_SELESAI = 9;

    private static final int ID_LIST_PERKULIAHAN_LOADER = 50;

    private Button btnAktifkanKelas;
    private Button btnBatalkanKelas;
    private Button btnAkhiriKelas;
    private Button btnStatusKelasBerakhir;

    private RecyclerView mRecyclerView;
    private PerkuliahanDosenAdapter mPerkuliahanDosenAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private View emptyView;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_halaman_utama_dosen);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_jadwal_utama_dosen);
        emptyView = findViewById(R.id.empty_view);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPerkuliahanDosenAdapter = new PerkuliahanDosenAdapter(this, this);
        mRecyclerView.setAdapter(mPerkuliahanDosenAdapter);

        btnAktifkanKelas = (Button) findViewById(R.id.btn_aktifkan_kelas);
        btnBatalkanKelas = (Button) findViewById(R.id.btn_nonaktifkan_kelas);
        btnAkhiriKelas = (Button) findViewById(R.id.btn_akhiri_kelas);
        btnStatusKelasBerakhir = (Button) findViewById(R.id.btn_status_perkuliahan);

        showLoading();

        getSupportLoaderManager().initLoader(ID_LIST_PERKULIAHAN_LOADER, null, this);

        SikemasSyncUtils.initializePerkuliahan(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_PERKULIAHAN_LOADER:
                Log.d("TAG", "masuk on create loader");
                Uri listPerkuliahanQueryUri = SikemasContract.PerkuliahanEntry.CONTENT_URI;
                return new CursorLoader(this,
                        listPerkuliahanQueryUri,
                        MAIN_LIST_PERKULIAHAN_DOSEN_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mPerkuliahanDosenAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0)
            showPerkuliahanDataView();
        else
            showEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPerkuliahanDosenAdapter.swapCursor(null);
    }

    private void showPerkuliahanDataView() {
        emptyView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        emptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        emptyView.setVisibility(View.VISIBLE);
    }

    private void showAlertDialog(final int itemId, final String idPerkuliahan,
                                 final String mataKuliah, final String ruangKelas) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        switch (itemId) {
            case 1:
                builder.setTitle("Aktifkan Perkuliahan?")
                        .setMessage("Apakah Anda yakin ingin mengaktifkan perkuliahan " + mataKuliah + " "
                                + ruangKelas + "?")
                        .setPositiveButton(R.string.aktifkan, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                changeStatusPerkuliahan(idPerkuliahan, "1");
                            }
                        })
                        .setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                break;
            case 2:
                builder.setTitle("Akhiri Perkuliahan?")
                        .setMessage("Apakah Anda yakin ingin mengakhiri perkuliahan " + mataKuliah +
                                " " + ruangKelas + "?")
                        .setPositiveButton(R.string.akhiri, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                changeStatusPerkuliahan(idPerkuliahan, "2");
                            }
                        })
                        .setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                break;
            case 3:
                builder.setTitle("Batalkan Perkuliahan?")
                        .setMessage("Apakah Anda yakin ingin membatalkan perkuliahan " + mataKuliah +
                                " " + ruangKelas + "?")
                        .setPositiveButton(R.string.batalkan, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                changeStatusPerkuliahan(idPerkuliahan, "3");
                            }
                        })
                        .setNegativeButton(R.string.batal, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                dialog.dismiss();
                            }
                        });
                break;
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(int itemId, String idPerkuliahan, String mataKuliah, String kodeKelas) {
        if (itemId == 1 || itemId == 2 || itemId == 3) {
            showAlertDialog(itemId, idPerkuliahan, mataKuliah, kodeKelas);
        } else {
            Intent intentToDetailPerkuliahan = new Intent(HalamanUtamaDosen.this, DetailPerkuliahan.class);
            Uri uriPerkuliahanClicked = SikemasContract.PerkuliahanEntry.buildPerkuliahanUriPerkuliahanId(idPerkuliahan);
            intentToDetailPerkuliahan.setData(uriPerkuliahanClicked);
            startActivity(intentToDetailPerkuliahan);
        }
    }

    private void changeStatusPerkuliahan(final String idPerkuliahan, final String statusDosen) {
        Log.d("idPerkuliahan", idPerkuliahan);
        Log.d("statusDosen", statusDosen);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                NetworkUtils.CHANGE_STATUS_KELAS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "Change Status Perkuliahan Response: " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String code = jsonObject.getString("code");
                            if (code.equals("1")) {
                                ContentValues updatedValues = new ContentValues();
                                updatedValues.put(SikemasContract.PerkuliahanEntry.KEY_STATUS_DOSEN, statusDosen);
                                Uri mUri = SikemasContract.PerkuliahanEntry.CONTENT_URI;
                                String selection = SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN + " = ? ";
                                getContentResolver().update(
                                        mUri,
                                        updatedValues,
                                        selection,
                                        new String[]{idPerkuliahan}
                                );
                                switch (statusDosen) {
                                    case "1":
                                        Toast.makeText(getApplicationContext(), "Perkuliahan berhasil diaktifkan",
                                                Toast.LENGTH_LONG).show();

                                        break;
                                    case "2":
                                        Toast.makeText(getApplicationContext(), "Perkuliahan berhasil diakhiri",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    case "3":
                                        Toast.makeText(getApplicationContext(), "Perkuliahan berhasil dibatalkan",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                }
                            } else {
                                switch (statusDosen) {
                                    case "1":
                                        Toast.makeText(getApplicationContext(), "Perkuliahan gagal diaktifkan",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    case "2":
                                        Toast.makeText(getApplicationContext(), "Perkuliahan gagal diakhiri",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                    case "3":
                                        Toast.makeText(getApplicationContext(), "Perkuliahan gagal dibatalkan",
                                                Toast.LENGTH_LONG).show();
                                        break;
                                }
                            }
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
                params.put("status_dosen", statusDosen);
                return params;
            }
        };
        VolleySingleton.getmInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }
}
