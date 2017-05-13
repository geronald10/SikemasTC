package id.ac.its.sikemastc.activity.mahasiswa;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.PerkuliahanAdapter;
import id.ac.its.sikemastc.data.SikemasContract;

public class ListPerkuliahanFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PerkuliahanAdapter.PerkuliahanAdapterOnClickHandler {

    private final String TAG = ListPerkuliahanFragment.class.getSimpleName();

    public static final String[] MAIN_LIST_PERKULIAHAN_PROJECTION = {
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

    private static final int ID_LIST_PERKULIAHAN_MAHASISWA_LOADER = 55;

    private ProgressBar mLoadingIndicator;

    private RecyclerView mRecyclerView;
    private PerkuliahanAdapter mPerkuliahanAdapter;
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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_perkuliahan_mahasiswa);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPerkuliahanAdapter = new PerkuliahanAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mPerkuliahanAdapter);

        showLoading();

        getLoaderManager().initLoader(ID_LIST_PERKULIAHAN_MAHASISWA_LOADER, null, this);

        super.onViewCreated(view, savedInstanceState);
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
        mPerkuliahanAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0)
            showPerkuliahanDataView();
//        else
//            showEmptyView();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPerkuliahanAdapter.swapCursor(null);
    }

    @Override
    public void onClick(int itemId, String idListKelas, String mataKuliah, String kodeKelas) {

    }

    private void showPerkuliahanDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

//    private void showEmptyView() {
//        mRecyclerView.setVisibility(View.GONE);
//        mLoadingIndicator.setVisibility(View.INVISIBLE);
//        emptyView.setVisibility(View.VISIBLE);
//    }
}
