package id.ac.its.sikemastc.activity.dosen;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.KelasDosenAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class ListKelasActivity extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        KelasDosenAdapter.KelasDosenAdapterOnClickHandler {

    private final String TAG = ListKelasActivity.class.getSimpleName();

    public static final String[] MAIN_LIST_KELAS_DOSEN_PROJECTION = {
            SikemasContract.KelasEntry.KEY_ID_KELAS,
            SikemasContract.KelasEntry.KEY_KODE_KELAS,
            SikemasContract.KelasEntry.KEY_KODE_SEMESTER,
            SikemasContract.KelasEntry.KEY_KODE_MK,
            SikemasContract.KelasEntry.KEY_NAMA_MK,
            SikemasContract.KelasEntry.KEY_NAMA_RUANGAN,
            SikemasContract.KelasEntry.KEY_HARI,
            SikemasContract.KelasEntry.KEY_MULAI,
            SikemasContract.KelasEntry.KEY_SELESAI
    };

    public static final int INDEX_ID_KELAS = 0;
    public static final int INDEX_KODE_KELAS = 1;
    public static final int INDEX_KODE_SEMESTER = 2;
    public static final int INDEX_KELAS_KODE_MK = 3;
    public static final int INDEX_KELAS_NAMA_MK = 4;
    public static final int INDEX_KELAS_NAMA_RUANGAN = 5;
    public static final int INDEX_KELAS_HARI = 6;
    public static final int INDEX_KELAS_MULAI = 7;
    public static final int INDEX_KELAS_SELESAI = 8;

    private static final int ID_LIST_KELAS_LOADER = 45;

    private RecyclerView mRecyclerView;
    private KelasDosenAdapter mKelasDosenAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kelas_diampu);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_kelas);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mKelasDosenAdapter = new KelasDosenAdapter(this, this);
        mRecyclerView.setAdapter(mKelasDosenAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_LIST_KELAS_LOADER, null, this);

        SikemasSyncUtils.startImmediateKelasSync(this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_KELAS_LOADER:
                Log.d("TAG", "masuk on create loader");
                Uri listKelasQueryUri = SikemasContract.KelasEntry.CONTENT_URI;
                String sortOrder = SikemasContract.KelasEntry.KEY_KODE_SEMESTER + " ASC";

                return new CursorLoader(this,
                        listKelasQueryUri,
                        MAIN_LIST_KELAS_DOSEN_PROJECTION,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mKelasDosenAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0)
            showKelasDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKelasDosenAdapter.swapCursor(null);
    }

    private void showKelasDataView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String idListKelas) {
        Intent intentToDetailKelas = new Intent(ListKelasActivity.this, DetailListKelas.class);
        intentToDetailKelas.putExtra("id_kelas", idListKelas);
        startActivity(intentToDetailKelas);
    }

}
