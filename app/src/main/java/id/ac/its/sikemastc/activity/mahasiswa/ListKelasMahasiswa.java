package id.ac.its.sikemastc.activity.mahasiswa;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import java.util.HashMap;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.adapter.KelasMahasiswaAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.data.SikemasSessionManager;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class ListKelasMahasiswa extends BaseActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        KelasMahasiswaAdapter.KelasMahasiswaAdapterOnClickHandler {

    private final String TAG = ListKelasMahasiswa.class.getSimpleName();

    public static final String[] MAIN_LIST_KELAS_MAHASISWA_PROJECTION = {
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

    private static final int ID_LIST_KELAS_MAHASISWA_LOADER = 46;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String idMahasiswa;
    private RecyclerView mRecyclerView;
    private CardView cvEmptyView;
    private KelasMahasiswaAdapter mKelasMahasiswaAdapter;
    private int mPosition = RecyclerView.NO_POSITION;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lihat_jadwal);

        SikemasSessionManager session = new SikemasSessionManager(this);
        HashMap<String, String> userDetail = session.getUserDetails();
        idMahasiswa = userDetail.get(SikemasSessionManager.KEY_USER_ID);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_kelas);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        cvEmptyView = (CardView) findViewById(R.id.empty_view);

        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.swipe_color_1),
                ContextCompat.getColor(this, R.color.swipe_color_2),
                ContextCompat.getColor(this, R.color.swipe_color_3),
                ContextCompat.getColor(this, R.color.swipe_color_4));

        if (!mSwipeRefreshLayout.isRefreshing()) {
            showLoading();
        }

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mKelasMahasiswaAdapter = new KelasMahasiswaAdapter(this, this);
        mRecyclerView.setAdapter(mKelasMahasiswaAdapter);

        showLoading();

        getSupportLoaderManager().initLoader(ID_LIST_KELAS_MAHASISWA_LOADER, null, this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        Log.d(TAG, "masuk loader jadwal kelas");
        switch (loaderId) {
            case ID_LIST_KELAS_MAHASISWA_LOADER:
                Log.d("TAG", "masuk on create loader");
                Uri listKelasQueryUri = SikemasContract.KelasEntry.CONTENT_URI;
                String sortOrder = SikemasContract.KelasEntry.KEY_KODE_SEMESTER + " ASC";

                return new CursorLoader(this,
                        listKelasQueryUri,
                        MAIN_LIST_KELAS_MAHASISWA_PROJECTION,
                        null,
                        null,
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d(TAG, "masuk on load finished");
        mKelasMahasiswaAdapter.swapCursor(data);

        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);

        Log.d(TAG, String.valueOf(data.getCount()));
        if (data.getCount() != 0) {
            mKelasMahasiswaAdapter.notifyDataSetChanged();
            showKelasDataView();
        }
        else {
            mKelasMahasiswaAdapter.notifyDataSetChanged();
            showEmptyDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mKelasMahasiswaAdapter.swapCursor(null);
    }

    private void showKelasDataView() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        cvEmptyView.setVisibility(View.GONE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        cvEmptyView.setVisibility(View.GONE);
    }

    private void showEmptyDataView() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
        cvEmptyView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String idKelas, String infoKelas) {
        Intent intentToDetailKelas = new Intent(ListKelasMahasiswa.this, DetailJadwalKelasMahasiswa.class);
        intentToDetailKelas.putExtra("id_kelas", idKelas);
        intentToDetailKelas.putExtra("id_mahasiswa", idMahasiswa);
        intentToDetailKelas.putExtra("info_kelas", infoKelas);
        startActivity(intentToDetailKelas);
    }

    private void initiateRefresh() {
        Log.d(TAG, "initiate Refresh");
        if (!mSwipeRefreshLayout.isRefreshing())
            showLoading();
        SikemasSyncUtils.startImmediatePerkuliahanMahasiswaSync(this);
        getSupportLoaderManager().initLoader(ID_LIST_KELAS_MAHASISWA_LOADER, null, this);
    }
}
