package id.ac.its.sikemastc.activity.dosen;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.adapter.PerkuliahanAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class HalamanUtamaDosen extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PerkuliahanAdapter.PerkuliahanAdapterOnClickHandler {

    private final String TAG = HalamanUtamaDosen.class.getSimpleName();

    public static final String[] MAIN_LIST_PERKULIAHAN_DOSEN_PROJECTION = {
            SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_STATUS_PERKULIAHAN,
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
    public static final int INDEX_STATUS_PERKULIAHAN = 1;
    public static final int INDEX_PERTEMUAN_KE = 2;
    public static final int INDEX_TANGGAL_PERKULIAHAN = 3;
    public static final int INDEX_NAMA_MK = 4;
    public static final int INDEX_NAMA_RUANGAN = 5;
    public static final int INDEX_KODE_KELAS = 6;
    public static final int INDEX_HARI = 7;
    public static final int INDEX_MULAI = 8;
    public static final int INDEX_SELESAI = 9;

    private static final int ID_LIST_PERKULIAHAN_LOADER = 50;

    private RecyclerView mRecyclerView;
    private PerkuliahanAdapter mPerkuliahanAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private View emptyView;
    private Button btnTestNotifikasi;

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
        mPerkuliahanAdapter = new PerkuliahanAdapter(this, this);
        mRecyclerView.setAdapter(mPerkuliahanAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_LIST_PERKULIAHAN_LOADER, null, this);

        btnTestNotifikasi = (Button) findViewById(R.id.btn_test_notifikasi);
        btnTestNotifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
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
        mPerkuliahanAdapter.swapCursor(data);
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
        mPerkuliahanAdapter.swapCursor(null);
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

    @Override
    public void onClick(String idPerkuliahan) {
        Intent intentToDetailPerkuliahan = new Intent(HalamanUtamaDosen.this, DetailPerkuliahan.class);
        Uri uriPerkuliahanCLicked = SikemasContract.PerkuliahanEntry.buildPerkuliahanUriPerkuliahanId(idPerkuliahan);
        intentToDetailPerkuliahan.setData(uriPerkuliahanCLicked);
        startActivity(intentToDetailPerkuliahan);
    }
}
