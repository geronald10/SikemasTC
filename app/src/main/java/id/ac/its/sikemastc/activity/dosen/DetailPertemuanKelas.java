package id.ac.its.sikemastc.activity.dosen;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.ChangeKehadiranAdapter;
import id.ac.its.sikemastc.data.SikemasContract;

public class DetailPertemuanKelas extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ChangeKehadiranAdapter.ChangeKehadiranAdapterOnClickHandler {

    private final String TAG = DetailPertemuanKelas.class.getSimpleName();

    public static final String[] MAIN_DETAIL_PERTEMUAN_KEHADIRAN_PROJECTION = {
            SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_ID_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_NRP_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_NAMA_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_KET_KEHADIRAN,
    };

    public static final int INDEX_ID_PERKULIAHAN = 0;
    public static final int INDEX_ID_MAHASISWA = 1;
    public static final int INDEX_NRP_MAHASISWA = 2;
    public static final int INDEX_NAMA_MAHASISWA = 3;
    public static final int INDEX_KET_KEHADIRAN = 4;

    private static final int ID_DETAIL_PERTEMUAN_KEHADIRAN_LOADER = 90;

    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private ChangeKehadiranAdapter mChangeKehadiranAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String idPertemuan;
    private String idKelas;
    private String pertemuanKe;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pertemuan);

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
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mChangeKehadiranAdapter = new ChangeKehadiranAdapter(this, this);
        mRecyclerView.setAdapter(mChangeKehadiranAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_DETAIL_PERTEMUAN_KEHADIRAN_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_DETAIL_PERTEMUAN_KEHADIRAN_LOADER:
                Log.d(TAG, "masuk sini");
                Uri listPertemuanKehadiranQuery = SikemasContract.KehadiranEntry.CONTENT_URI;
                String sortOrder = "CAST (" + SikemasContract.KehadiranEntry.KEY_ID_MAHASISWA + " AS REAL) ASC";
                String selection = SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA + " = ? ";
                String selectionArgs = idPertemuan;

                return new CursorLoader(this,
                        listPertemuanKehadiranQuery,
                        MAIN_DETAIL_PERTEMUAN_KEHADIRAN_PROJECTION,
                        selection,
                        new String[]{selectionArgs},
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mChangeKehadiranAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0)
            showPertemuanKehadiranDataView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mChangeKehadiranAdapter.swapCursor(null);
    }

    private void showPertemuanKehadiranDataView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(int itemId, String idPertemuan, String idKelas) {

    }
}
