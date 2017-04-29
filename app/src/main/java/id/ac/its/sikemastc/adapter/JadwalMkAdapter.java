package id.ac.its.sikemastc.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.JadwalKelas;

public class JadwalMkAdapter extends RecyclerView.Adapter<JadwalMkAdapter.JadwalViewHolder>{

    private List<JadwalKelas> jadwalMataKuliahList;

    // handle clicks on items
    final private JadwalMKAdapterOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface JadwalMKAdapterOnClickHandler {
        void onClick(String idListKelas);
    }

    public JadwalMkAdapter(JadwalMKAdapterOnClickHandler clickHandler, List<JadwalKelas> jadwalMataKuliahList) {
        this.mClickHandler = clickHandler;
        this.jadwalMataKuliahList = jadwalMataKuliahList;
    }

    @Override
    public JadwalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_jadwal, parent, false);
        view.setFocusable(true);

        return new JadwalViewHolder(view);
    }


    @Override
    public void onBindViewHolder(JadwalViewHolder jadwalAdapterViewHolder, int position) {
        JadwalKelas currentJadwal = jadwalMataKuliahList.get(position);
        String kodeMk = currentJadwal.getKodeMK();
        String namaMk = currentJadwal.getNamaMK();
        String kelasMk = currentJadwal.getKelasMK();
        String ruangMK = currentJadwal.getRuangMK();
        String hari = null, waktu = null;
        for(int i=0; i< currentJadwal.getKelasWaktu().size(); i++) {
            hari = currentJadwal.getKelasWaktu().get(i).getHariKelas();
            waktu = currentJadwal.getKelasWaktu().get(i).getWaktuMulai() +
                    " - " + currentJadwal.getKelasWaktu().get(i).getWaktuSelesai();
            if (i+1 < currentJadwal.getKelasWaktu().size()) {
                hari += "\n";
                waktu += "\n";
            }
        }

        jadwalAdapterViewHolder.tvKodeMK.setText(kodeMk);
        jadwalAdapterViewHolder.tvNamaMK.setText(namaMk);
        jadwalAdapterViewHolder.tvKelasMK.setText(kelasMk);
        jadwalAdapterViewHolder.tvHariMK.setText(hari);
        jadwalAdapterViewHolder.tvRuangMK.setText(ruangMK);
        jadwalAdapterViewHolder.tvWaktuMK.setText(waktu);
    }

    @Override
    public int getItemCount() {
        return jadwalMataKuliahList.size();
    }

    class JadwalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvKodeMK;
        private TextView tvNamaMK;
        private TextView tvKelasMK;
        private TextView tvHariMK;
        private TextView tvWaktuMK;
        private TextView tvRuangMK;

        public JadwalViewHolder(View itemView) {
            super(itemView);
            tvKodeMK = (TextView) itemView.findViewById(R.id.tv_kode_mata_kuliah);
            tvNamaMK = (TextView) itemView.findViewById(R.id.tv_nama_mata_kuliah);
            tvKelasMK = (TextView) itemView.findViewById(R.id.tv_kelas);
            tvHariMK = (TextView) itemView.findViewById(R.id.tv_hari);
            tvWaktuMK = (TextView) itemView.findViewById(R.id.tv_waktu);
            tvRuangMK = (TextView) itemView.findViewById(R.id.tv_ruang);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(jadwalMataKuliahList.get(adapterPosition).getIdListJadwalMK());
        }
    }

    public void clear() {
        jadwalMataKuliahList.clear();
        notifyDataSetChanged();
    }
}
