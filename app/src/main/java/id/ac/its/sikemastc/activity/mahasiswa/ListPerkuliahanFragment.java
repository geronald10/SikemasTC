package id.ac.its.sikemastc.activity.mahasiswa;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.DetailPerkuliahan;
import id.ac.its.sikemastc.adapter.PerkuliahanDosenAdapter;
import id.ac.its.sikemastc.adapter.PerkuliahanMahasiswaAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class ListPerkuliahanFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PerkuliahanMahasiswaAdapter.PerkuliahanMahasiswaAdapterOnClickHandler{

    private final String TAG = ListPerkuliahanFragment.class.getSimpleName();
    public static int jmlPerkuliahan;

    public static final String[] MAIN_LIST_PERKULIAHAN_PROJECTION = {
            SikemasContract.PerkuliahanEntry.KEY_ID_PERKULIAHAN,
            SikemasContract.PerkuliahanEntry.KEY_ID_KELAS,
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
    public static final int INDEX_ID_KELAS = 1;
    public static final int INDEX_STATUS_DOSEN = 2;
    public static final int INDEX_PERTEMUAN_KE = 3;
    public static final int INDEX_TANGGAL_PERKULIAHAN = 4;
    public static final int INDEX_NAMA_MK = 5;
    public static final int INDEX_NAMA_RUANGAN = 6;
    public static final int INDEX_KODE_KELAS = 7;
    public static final int INDEX_HARI = 8;
    public static final int INDEX_MULAI = 9;
    public static final int INDEX_SELESAI = 10;

    private static final int ID_LIST_PERKULIAHAN_MAHASISWA_LOADER = 55;

    private ProgressBar mLoadingIndicator;
    private CardView cvEmptyView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PerkuliahanMahasiswaAdapter mPerkuliahanMahasiswaAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String bundleIdUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_perkuliahan, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            bundleIdUser = bundle.getString("id_mahasiswa");
        }
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_perkuliahan_mahasiswa);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
        cvEmptyView = (CardView) view.findViewById(R.id.empty_view);

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!mSwipeRefreshLayout.isRefreshing()) {
            showLoading();
        }

        mPerkuliahanMahasiswaAdapter = new PerkuliahanMahasiswaAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mPerkuliahanMahasiswaAdapter);

        showLoading();

        getLoaderManager().initLoader(ID_LIST_PERKULIAHAN_MAHASISWA_LOADER, null, this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_PERKULIAHAN_MAHASISWA_LOADER:
                Uri perkuliahanQueryUri = SikemasContract.PerkuliahanEntry.CONTENT_URI;
                return new CursorLoader(getActivity(),
                        perkuliahanQueryUri,
                        MAIN_LIST_PERKULIAHAN_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mPerkuliahanMahasiswaAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0) {
            mPerkuliahanMahasiswaAdapter.notifyDataSetChanged();
            jmlPerkuliahan = data.getCount();
            showPerkuliahanDataView();
        }
        else {
            mPerkuliahanMahasiswaAdapter.notifyDataSetChanged();
            jmlPerkuliahan = data.getCount();
            showEmptyView();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPerkuliahanMahasiswaAdapter.swapCursor(null);
    }

    @Override
    public void onClick(String idKelas, String idPerkuliahan) {
        Uri uriPerkuliahanClicked = SikemasContract.PerkuliahanEntry.buildPerkuliahanUriPerkuliahanId(idPerkuliahan);
        Intent intentToDetailPerkuliahan = new Intent(getActivity(), DetailPerkuliahanMahasiswa.class);
        intentToDetailPerkuliahan.putExtra("id_kelas", idKelas);
        Log.d("idKelas", idKelas);
        intentToDetailPerkuliahan.setData(uriPerkuliahanClicked);
        startActivity(intentToDetailPerkuliahan);
    }

    private void showPerkuliahanDataView() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.GONE);
        cvEmptyView.setVisibility(View.GONE);
    }

    private void showLoading() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        cvEmptyView.setVisibility(View.GONE);
    }

    private void showEmptyView() {
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
        cvEmptyView.setVisibility(View.VISIBLE);
    }

    private void initiateRefresh() {
        Log.d(TAG, "initiate Refresh");
        if (!mSwipeRefreshLayout.isRefreshing())
            showLoading();
        SikemasSyncUtils.startImmediatePerkuliahanMahasiswaSync(getContext());
        getLoaderManager().initLoader(ID_LIST_PERKULIAHAN_MAHASISWA_LOADER, null, this);
    }
}
