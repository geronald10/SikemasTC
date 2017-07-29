package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.mahasiswa.ListKelasMahasiswa;
import id.ac.its.sikemastc.utilities.SikemasDateUtils;

public class KelasMahasiswaAdapter extends RecyclerView.Adapter<KelasMahasiswaAdapter.KelasMahasiswaAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    // handle clicks on items
    final private KelasMahasiswaAdapterOnClickHandler mClickHandler;

    // interface that receives onClick message
    public interface  KelasMahasiswaAdapterOnClickHandler {
        void onClick(String idKelas, String infoKelas);
    }

    public KelasMahasiswaAdapter(Context context, KelasMahasiswaAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public KelasMahasiswaAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item_jadwal_mahasiswa, viewGroup, false);
        view.setFocusable(true);

        return new KelasMahasiswaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(KelasMahasiswaAdapterViewHolder kelasMahasiswaAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String kelasMk = mCursor.getString(ListKelasMahasiswa.INDEX_KODE_KELAS);
        String namaMk = mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_NAMA_MK);
        String kodeMk = mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_KODE_MK);
        String ruangMK = mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_NAMA_RUANGAN);
        String hari = mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_HARI);
        String waktuMulai = SikemasDateUtils.formatTime(mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_MULAI));
        String waktuSelesai= SikemasDateUtils.formatTime(mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_SELESAI));

        kelasMahasiswaAdapterViewHolder.tvKodeMK.setText(kodeMk);
        kelasMahasiswaAdapterViewHolder.tvNamaMK.setText(namaMk);
        kelasMahasiswaAdapterViewHolder.tvKelasMK.setText(kelasMk);
        kelasMahasiswaAdapterViewHolder.tvRuangMK.setText(ruangMK);
        kelasMahasiswaAdapterViewHolder.tvHariMK.setText(hari);
        kelasMahasiswaAdapterViewHolder.tvWaktuMulaiMK.setText(waktuMulai);
        kelasMahasiswaAdapterViewHolder.tvWaktuSelesaiMK.setText(waktuSelesai);
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

    public class KelasMahasiswaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvKodeMK;
        private TextView tvNamaMK;
        private TextView tvKelasMK;
        private TextView tvHariMK;
        private TextView tvWaktuMulaiMK;
        private TextView tvWaktuSelesaiMK;
        private TextView tvRuangMK;

        public KelasMahasiswaAdapterViewHolder(View itemView) {
            super(itemView);

            tvKodeMK = (TextView) itemView.findViewById(R.id.tv_kode_mata_kuliah);
            tvNamaMK = (TextView) itemView.findViewById(R.id.tv_nama_mata_kuliah);
            tvKelasMK = (TextView) itemView.findViewById(R.id.tv_kelas);
            tvHariMK = (TextView) itemView.findViewById(R.id.tv_hari);
            tvWaktuMulaiMK = (TextView) itemView.findViewById(R.id.tv_waktu_mulai);
            tvWaktuSelesaiMK = (TextView) itemView.findViewById(R.id.tv_waktu_selesai);
            tvRuangMK = (TextView) itemView.findViewById(R.id.tv_ruang);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String idKelas = mCursor.getString(ListKelasMahasiswa.INDEX_ID_KELAS);
            String infoKelas = mCursor.getString(ListKelasMahasiswa.INDEX_KELAS_NAMA_MK) + " " +
                    mCursor.getString(ListKelasMahasiswa.INDEX_KODE_KELAS);
            mClickHandler.onClick(idKelas, infoKelas);
        }
    }
}
