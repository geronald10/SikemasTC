package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.DetailPertemuanKelas;

public class ChangeKehadiranAdapter extends RecyclerView.Adapter<ChangeKehadiranAdapter.ChangeKehadiranAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    // handle clicks on items
    final private ChangeKehadiranAdapterOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface ChangeKehadiranAdapterOnClickHandler {
        void onClick(int itemId, String idPertemuan, String idKelas);
    }

    public ChangeKehadiranAdapter(Context context, ChangeKehadiranAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public ChangeKehadiranAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item_detail_pertemuan, viewGroup, false);
        view.setFocusable(true);

        return new ChangeKehadiranAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChangeKehadiranAdapterViewHolder changeKehadiranViewHolder, int position) {
        mCursor.moveToPosition(position);

        String nrpPeserta = mCursor.getString(DetailPertemuanKelas.INDEX_NRP_MAHASISWA);
        String namaPeserta = mCursor.getString(DetailPertemuanKelas.INDEX_NAMA_MAHASISWA);
        String statusKehadiran = mCursor.getString(DetailPertemuanKelas.INDEX_KET_KEHADIRAN);

        changeKehadiranViewHolder.tvNrpPeserta.setText(nrpPeserta);
        changeKehadiranViewHolder.tvNamaPeserta.setText(namaPeserta);

        switch (statusKehadiran) {
            case "M":
                changeKehadiranViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_green);
                break;
            case "I":
                changeKehadiranViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_yellow);
                break;
            case "A":
                changeKehadiranViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_red);
                break;
            default:
                changeKehadiranViewHolder.ivStatusKehadiran.setImageResource(R.drawable.circle_gray);
                break;
        }
    }

    @Override
    public int getItemCount() {
        if (null == mCursor)
            return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class ChangeKehadiranAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNrpPeserta;
        private TextView tvNamaPeserta;
        private ImageView ivStatusKehadiran;
        private ImageButton ibChangeStatus;

        public ChangeKehadiranAdapterViewHolder(View itemView) {
            super(itemView);
            tvNrpPeserta = (TextView) itemView.findViewById(R.id.tv_nrp_peserta);
            tvNamaPeserta = (TextView) itemView.findViewById(R.id.tv_nama_peserta);
            ivStatusKehadiran = (ImageView) itemView.findViewById(R.id.iv_status_kehadiran);
            ibChangeStatus = (ImageButton) itemView.findViewById(R.id.ib_change_status);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String idPertemuan = mCursor.getString(DetailPertemuanKelas.INDEX_ID_PERKULIAHAN);
            String idPeserta = mCursor.getString(DetailPertemuanKelas.INDEX_ID_MAHASISWA);
            switch (v.getId()) {
                case R.id.ib_change_status:
                    mClickHandler.onClick(1, idPertemuan, idPeserta);
                    break;
                default:
                    mClickHandler.onClick(2, idPertemuan, idPeserta);
                    break;
            }
        }
    }
}
