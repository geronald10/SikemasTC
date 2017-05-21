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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.KehadiranAdapter;
import id.ac.its.sikemastc.data.SikemasContract;

public class LihatKehadiran extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        KehadiranAdapter.KehadiranAdapterOnClickHandler {

    private final String TAG = LihatKehadiran.class.getSimpleName();

    public static final String[] MAIN_LIST_KEHADIRAN_PESERTA_PROJECTION = {
            SikemasContract.KehadiranEntry.KEY_ID_PERKULIAHAN_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_ID_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_NRP_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_NAMA_MAHASISWA,
            SikemasContract.KehadiranEntry.KEY_KET_KEHADIRAN
    };

    public static final int INDEX_ID_PERKULIAHAN_MAHASISWA = 0;
    public static final int INDEX_ID_MAHASISWA = 1;
    public static final int INDEX_NRP_MAHASISWA = 2;
    public static final int INDEX_NAMA_MAHASISWA = 3;
    public static final int INDEX_KET_KEHADIRAN = 4;

    private static final int ID_LIST_KEHADIRAN_LOADER = 60;
    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private KehadiranAdapter mKehadiranAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_kehadiran);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Lihat Kehadiran");
        mToolbar.setSubtitle("Update terakhir pukul 12.25");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back);

//         Compatibility
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
                String title =  (String) item.getTitle();
                Toast.makeText(LihatKehadiran.this, title + " Selected !", Toast.LENGTH_SHORT).show();
                switch (item.getItemId()) {
                    case R.id.refresh:
                        break;
                    case R.id.search:
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
        if (mUri == null) throw new NullPointerException("URI for DetailPerkuliahanActivity cannot be null");

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
    public void onClick(String idPerkuliahan, String idMahasiswa) {
        Intent intentToDetailKehadiran = new Intent(LihatKehadiran.this, DetailKehadiranPeserta.class);
//        Uri uriPerkuliahanCLicked = SikemasContract.PerkuliahanEntry.buildPerkuliahanUriPerkuliahanId(idPerkuliahan);
//        intentToDetailPerkuliahan.setData(uriPerkuliahanCLicked);
        startActivity(intentToDetailKehadiran);
    }
}
