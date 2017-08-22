package id.ac.its.sikemastc.activity.dosen;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.PesertaAdapter;
import id.ac.its.sikemastc.data.SikemasContract;
import id.ac.its.sikemastc.sync.SikemasSyncUtils;

public class PesertaKelasFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = PesertaKelasFragment.class.getSimpleName();

    public static final String[] MAIN_LIST_PESERTA_PROJECTION = {
            SikemasContract.PesertaEntry.KEY_ID_KELAS,
            SikemasContract.PesertaEntry.KEY_ID_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_NRP_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_NAMA_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_EMAIL_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_COUNT_HADIR,
            SikemasContract.PesertaEntry.KEY_COUNT_IJIN,
            SikemasContract.PesertaEntry.KEY_COUNT_SAKIT,
            SikemasContract.PesertaEntry.KEY_COUNT_ABSEN
    };

    public static final int INDEX_ID_KELAS = 0;
    public static final int INDEX_ID_PESERTA = 1;
    public static final int INDEX_NRP_PESERTA = 2;
    public static final int INDEX_NAMA_PESERTA = 3;
    public static final int INDEX_EMAIL_PESERTA = 4;
    public static final int INDEX_COUNT_HADIR = 5;
    public static final int INDEX_COUNT_IJIN = 6;
    public static final int INDEX_COUNT_SAKIT = 7;
    public static final int INDEX_COUNT_ABSEN = 8;

    private static final int ID_LIST_PESERTA_LOADER = 80;
    private ProgressBar mLoadingIndicator;

    private CardView emptyView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PesertaAdapter mPesertaAdapter;
    private int mPosition = RecyclerView.NO_POSITION;
    private String bundleIdKelas;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_peserta_kelas, container, false);
        Bundle bundle = getArguments();
        if (bundle != null) {
            bundleIdKelas = bundle.getString("id_kelas");
        }

        emptyView = (CardView) view.findViewById(R.id.empty_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_peserta);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getContext(), R.color.swipe_color_1),
                ContextCompat.getColor(getContext(), R.color.swipe_color_2),
                ContextCompat.getColor(getContext(), R.color.swipe_color_3),
                ContextCompat.getColor(getContext(), R.color.swipe_color_4));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        if (!mSwipeRefreshLayout.isRefreshing()) {
            showLoading();
        }

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPesertaAdapter = new PesertaAdapter(getContext());
        mRecyclerView.setAdapter(mPesertaAdapter);

        showLoading();

        getLoaderManager().initLoader(ID_LIST_PESERTA_LOADER, null, this);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateRefresh();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        switch (loaderId) {
            case ID_LIST_PESERTA_LOADER:
                Uri pertemuanQueryUri = SikemasContract.PesertaEntry.CONTENT_URI;
                String sortOrder = "CAST (" + SikemasContract.PesertaEntry.KEY_NRP_MAHASISWA + " AS REAL)" + " ASC";
                String selection = SikemasContract.PertemuanEntry.KEY_ID_KELAS + " = ? ";
                String selectionArgs = bundleIdKelas;

                return new CursorLoader(getActivity(),
                        pertemuanQueryUri,
                        MAIN_LIST_PESERTA_PROJECTION,
                        selection,
                        new String[]{selectionArgs},
                        sortOrder);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        mPesertaAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0) {
            mPesertaAdapter.notifyDataSetChanged();
            showPesertaDataView();
        } else {
            showEmptyDataView();
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        mPesertaAdapter.swapCursor(null);
    }

    private void showPesertaDataView() {
        mLoadingIndicator.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private void showLoading() {
        emptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void showEmptyDataView() {
        emptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mLoadingIndicator.setVisibility(View.GONE);
    }

    private void initiateRefresh() {
        Log.d(TAG, "initiate Refresh");
        if (!mSwipeRefreshLayout.isRefreshing())
            showLoading();
        SikemasSyncUtils.startImmediateKelasSync(getContext());
    }

    @Override
    public void onResume() {
        SikemasSyncUtils.startImmediateKelasSync(getContext());
        super.onResume();
    }
}
