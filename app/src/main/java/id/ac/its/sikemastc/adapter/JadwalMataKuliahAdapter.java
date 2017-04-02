package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jadwal, parent, false);
        JadwalViewHolder holder = new JadwalViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(JadwalViewHolder holder, int position) {
        JadwalMataKuliahModel currentJadwal = jadwalMataKuliahList.get(position);
        holder.setData(currentJadwal, position);
    }

    @Override
    public int getItemCount() {
        return jadwalMataKuliahList.size();
    }

    class JadwalViewHolder extends RecyclerView.ViewHolder {

        private TextView tvKodeMK;
        private TextView tvNamaMK;
        private TextView tvKelasMK;
        private TextView tvHariMK;
        private TextView tvWaktuMK;
        private TextView tvRuangMK;
        private int position;
        private JadwalMataKuliahModel currentJadwal;

        public JadwalViewHolder(View itemView) {
            super(itemView);
            tvKodeMK = (TextView) itemView.findViewById(R.id.tv_kode_mata_kuliah);
            tvNamaMK = (TextView) itemView.findViewById(R.id.tv_nama_mata_kuliah);
            tvKelasMK = (TextView) itemView.findViewById(R.id.tv_kelas);
            tvHariMK = (TextView) itemView.findViewById(R.id.tv_hari);
            tvWaktuMK = (TextView) itemView.findViewById(R.id.tv_waktu);
            tvRuangMK = (TextView) itemView.findViewById(R.id.tv_ruang);

//            itemView.setOnClickListener(this);
        }

        public void setData(JadwalMataKuliahModel current, int position) {
            this.tvKodeMK.setText(current.getKodeMK());
            this.tvNamaMK.setText(current.getNamaMK());
            this.tvKelasMK.setText(current.getKelasMK());
            this.tvHariMK.setText(current.getHariMK());
            this.tvRuangMK.setText(current.getRuangMK());
            this.tvWaktuMK.setText(current.getWaktuMK());
            this.position = position;
            this.currentJadwal = current;
        }
    }
}
