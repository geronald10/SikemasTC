package id.ac.its.sikemastc.activity.dosen;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import id.ac.its.sikemastc.activity.BaseActivity;
import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.adapter.JadwalMataKuliahAdapter;
import id.ac.its.sikemastc.model.JadwalMataKuliahModel;

public class ListKelasDiampu extends BaseActivity {

    private final String TAG = ListKelasDiampu.class.getSimpleName();

    private Context mContext;
    private RecyclerView mRecyclerView;
    private JadwalMataKuliahAdapter mAdapter;
    private List<JadwalMataKuliahModel> jadwalMatakuliahList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_kelas_diampu);
        mContext = this;

        jadwalMatakuliahList = new ArrayList<>();
        jadwalMatakuliahList = JadwalMataKuliahModel.getDummyDataList();

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_list_kelas);
        mAdapter = new JadwalMataKuliahAdapter(mContext, jadwalMatakuliahList);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }
}
