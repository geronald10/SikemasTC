package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.LihatKehadiran;

public class KehadiranAdapter extends RecyclerView.Adapter<KehadiranAdapter.KehadiranAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    final private KehadiranAdapterOnClickHandler mClickHandler;

    public interface KehadiranAdapterOnClickHandler {
        void onClick(String idPerkuliahan, String idMahasiswa, String nrpMahasiswa, String namaMahasiswa);
    }

    public KehadiranAdapter(Context context, KehadiranAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public KehadiranAdapter.KehadiranAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item_kehadiran_mahasiswa, viewGroup, false);
        view.setFocusable(true);

        return new KehadiranAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KehadiranAdapterViewHolder kehadiranAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String nrpPeserta = mCursor.getString(LihatKehadiran.INDEX_NRP_MAHASISWA);
        String namaPeserta = mCursor.getString(LihatKehadiran.INDEX_NAMA_MAHASISWA);
        String ketPeserta = mCursor.getString(LihatKehadiran.INDEX_KET_KEHADIRAN);
        String waktuHadir = mCursor.getString(LihatKehadiran.INDEX_WAKTU_CHECKIN);
        switch (ketPeserta) {
            case "A":
                kehadiranAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusAbsen);
                kehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusAbsen);
                kehadiranAdapterViewHolder.tvStatus.setText(R.string.status_absen);
                break;
            case "M":
                kehadiranAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusHadir);
                kehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusHadir);
                kehadiranAdapterViewHolder.tvStatus.setText(R.string.status_hadir);
                break;
            case "I":
                kehadiranAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusIjin);
                kehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusIjin);
                kehadiranAdapterViewHolder.tvStatus.setText(R.string.status_ijin);
                break;
            default:
                kehadiranAdapterViewHolder.ivStatusLabel.setImageResource(R.color.colorStatusIdle);
                kehadiranAdapterViewHolder.ivStatusKehadiran.setImageResource(R.color.colorStatusIdle);
                kehadiranAdapterViewHolder.tvStatus.setText(R.string.tv_empty_value);
                break;
        }
        kehadiranAdapterViewHolder.tvNRPPeserta.setText(nrpPeserta);
        kehadiranAdapterViewHolder.tvNamaPeserta.setText(namaPeserta);
        kehadiranAdapterViewHolder.tvWaktuHadir.setText(waktuHadir);

    }

    class KehadiranAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNRPPeserta;
        private TextView tvNamaPeserta;
        private TextView tvWaktuHadir;
        private TextView tvStatus;
        private ImageView ivStatusLabel;
        private ImageView ivStatusKehadiran;

        public KehadiranAdapterViewHolder(View itemView) {
            super(itemView);
            tvNRPPeserta = (TextView) itemView.findViewById(R.id.tv_nrp_peserta);
            tvNamaPeserta = (TextView) itemView.findViewById(R.id.tv_nama_peserta);
            tvWaktuHadir = (TextView) itemView.findViewById(R.id.tv_waktu_kehadiran);
            tvStatus = (TextView) itemView.findViewById(R.id.tv_status);
            ivStatusKehadiran = (ImageView) itemView.findViewById(R.id.iv_status_kehadiran);
            ivStatusLabel = (ImageView) itemView.findViewById(R.id.iv_status_label);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String IdPerkuliahan = mCursor.getString(LihatKehadiran.INDEX_ID_PERKULIAHAN_MAHASISWA);
            String IdMahasiswa = mCursor.getString(LihatKehadiran.INDEX_ID_MAHASISWA);
            String nrpMahasiswa = mCursor.getString(LihatKehadiran.INDEX_NRP_MAHASISWA);
            String namaMahasiswa = mCursor.getString(LihatKehadiran.INDEX_NAMA_MAHASISWA);
            mClickHandler.onClick(IdPerkuliahan, IdMahasiswa, nrpMahasiswa, namaMahasiswa);
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
}
