package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.JadwalMataKuliahModel;

public class JadwalMataKuliahAdapter extends RecyclerView.Adapter<JadwalMataKuliahAdapter.JadwalViewHolder>{

    private Context mContext;
    private List<JadwalMataKuliahModel> jadwalMataKuliahList;

    public JadwalMataKuliahAdapter(Context mContext, List<JadwalMataKuliahModel> jadwalMataKuliahList) {
        this.mContext = mContext;
        this.jadwalMataKuliahList = jadwalMataKuliahList;
    }

    @Override
    public JadwalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false)
        JadwalViewHolder holder = new JadwalViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(JadwalViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class JadwalViewHolder extends RecyclerView.ViewHolder {

        public JadwalViewHolder(View itemView) {
            super(itemView);
        }
    }
}
