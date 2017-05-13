package id.ac.its.sikemastc.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.model.PesertaPerkuliahan;

public class KehadiranPerkuliahanAdapter extends RecyclerView.Adapter<KehadiranPerkuliahanAdapter.StatusKehadiranAdapterViewHolder> {

    private List<PesertaPerkuliahan> pesertaPerkuliahanList;

    // handle clicks on items
    final private StatusKehadiranAdapterOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface StatusKehadiranAdapterOnClickHandler {
        void onClick(int itemId, String idPerkuliahan, String idPeserta, String nrpPeserta,
                     String namaPeserta);
    }

    public KehadiranPerkuliahanAdapter(StatusKehadiranAdapterOnClickHandler clickHandler, List<PesertaPerkuliahan> pesertaPerkuliahenList) {
        this.mClickHandler = clickHandler;
        this.pesertaPerkuliahanList = pesertaPerkuliahenList;
    }

    @Override
    public StatusKehadiranAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.list_item_detail_pertemuan, viewGroup, false);
        view.setFocusable(true);

        return new StatusKehadiranAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(StatusKehadiranAdapterViewHolder statusKehadiranAdapterViewHolder, int position) {
        PesertaPerkuliahan currentPeserta = pesertaPerkuliahanList.get(position);

        String nrpPeserta = currentPeserta.getNrpMahasiswa();
        String namaPeserta = currentPeserta.getNamaMahasiswa();
        String statusKehadiran = currentPeserta.getStatusKehadiran();

        statusKehadiranAdapterViewHolder.tvNrpPeserta.setText(nrpPeserta);
        statusKehadiranAdapterViewHolder.tvNamaPeserta.setText(namaPeserta);

        switch (statusKehadiran) {
            case "M":
                statusKehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_green);
                break;
            case "I":
                statusKehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_yellow);
                break;
            case "A":
                statusKehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_red);
                break;
            default:
                statusKehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_gray);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pesertaPerkuliahanList.size();
    }

    class StatusKehadiranAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNrpPeserta;
        private TextView tvNamaPeserta;
        private ImageView ivStatusKehadiran;
        private ImageButton ibChangeStatus;

        public StatusKehadiranAdapterViewHolder(View itemView) {
            super(itemView);
            tvNrpPeserta = (TextView) itemView.findViewById(R.id.tv_nrp_peserta);
            tvNamaPeserta = (TextView) itemView.findViewById(R.id.tv_nama_peserta);
            ivStatusKehadiran = (ImageView) itemView.findViewById(R.id.iv_status_kehadiran);
            ibChangeStatus = (ImageButton) itemView.findViewById(R.id.ib_change_status);

            ibChangeStatus.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String idPerkuliahan = pesertaPerkuliahanList.get(adapterPosition).getIdPerkuliahan();
            String idPeserta = pesertaPerkuliahanList.get(adapterPosition).getIdMahasiswa();
            String namaPeserta = pesertaPerkuliahanList.get(adapterPosition).getNamaMahasiswa();
            String nrpPeserta = pesertaPerkuliahanList.get(adapterPosition).getNrpMahasiswa();
            switch (v.getId()) {
                case R.id.ib_change_status:
                    mClickHandler.onClick(v.getId(), idPerkuliahan, idPeserta, nrpPeserta, namaPeserta);
                    break;
                default:
                    break;
            }
        }
    }

    public void clear() {
        pesertaPerkuliahanList.clear();
        notifyDataSetChanged();
    }
}
