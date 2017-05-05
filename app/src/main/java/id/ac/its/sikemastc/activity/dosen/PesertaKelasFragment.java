package id.ac.its.sikemastc.activity.dosen;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.PertemuanAdapter;
import id.ac.its.sikemastc.adapter.PesertaAdapter;
import id.ac.its.sikemastc.data.SikemasContract;

public class PesertaKelasFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        PesertaAdapter.PesertaAdapterOnClickHandler {

    private final String TAG = PesertaKelasFragment.class.getSimpleName();

    public static final String[] MAIN_LIST_PESERTA_PROJECTION = {
            SikemasContract.PesertaEntry.KEY_ID_KELAS,
            SikemasContract.PesertaEntry.KEY_ID_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_NRP_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_NAMA_MAHASISWA,
            SikemasContract.PesertaEntry.KEY_EMAIL_MAHASISWA,
    };

    public static final int INDEX_ID_KELAS = 0;
    public static final int INDEX_ID_PESERTA = 1;
    public static final int INDEX_NRP_PESERTA = 2;
    public static final int INDEX_NAMA_PESERTA = 3;
    public static final int INDEX_EMAIL_PESERTA = 4;

    private static final int ID_LIST_PESERTA_LOADER = 80;
    private ProgressBar mLoadingIndicator;

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
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_list_peserta);
        mLoadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPesertaAdapter = new PesertaAdapter(getActivity(), this);
        mRecyclerView.setAdapter(mPesertaAdapter);

        showLoading();

        getLoaderManager().initLoader(ID_LIST_PESERTA_LOADER, null, this);

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
        mPesertaAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION)
            mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() > 0)
            showPesertaDataVIew();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    private void showPesertaDataVIew() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(String idKelas) {

    }
}
