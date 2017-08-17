package id.ac.its.sikemastc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import id.ac.its.sikemastc.R;
import id.ac.its.sikemastc.activity.dosen.PesertaKelasFragment;


public class PesertaAdapter extends RecyclerView.Adapter<PesertaAdapter.PesertaAdapterViewHolder> {

    private final Context mContext;
    private Cursor mCursor;

    final private PesertaAdapterOnClickHandler mClickHandler;

    public PesertaAdapter(Context context, PesertaAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public interface PesertaAdapterOnClickHandler {
        void onClick(String idKelas);
    }

    @Override
    public PesertaAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.list_item_peserta, viewGroup, false);

        return new PesertaAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PesertaAdapterViewHolder pesertaAdapterViewHolder, int position) {
        mCursor.moveToPosition(position);

        String idPeserta = mCursor.getString(PesertaKelasFragment.INDEX_ID_PESERTA);
        String nrpPeserta = mCursor.getString(PesertaKelasFragment.INDEX_NRP_PESERTA);
        String namaPeserta = mCursor.getString(PesertaKelasFragment.INDEX_NAMA_PESERTA);
        String countHadirPeserta = mCursor.getString(PesertaKelasFragment.INDEX_HADIR_PESERTA);
        String countIjinPeserta = mCursor.getString(PesertaKelasFragment.INDEX_IJIN_PESERTA);
        String countAbsenPeserta = mCursor.getString(PesertaKelasFragment.INDEX_ABSEN_PESERTA);

        Log.d("peserta", namaPeserta + " " + countHadirPeserta + " " + countIjinPeserta + " " +
        countAbsenPeserta);

        pesertaAdapterViewHolder.tvNumber.setText("#" + (position+1));
        pesertaAdapterViewHolder.tvNrpPeserta.setText(nrpPeserta);
        pesertaAdapterViewHolder.tvNamaPeserta.setText(namaPeserta);
        pesertaAdapterViewHolder.tvHadirCount.setText(countHadirPeserta);
        pesertaAdapterViewHolder.tvIjinCount.setText(countIjinPeserta);
        pesertaAdapterViewHolder.tvAbsenCount.setText(countAbsenPeserta);
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

    class PesertaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvNumber;
        private TextView tvNrpPeserta;
        private TextView tvNamaPeserta;
        private TextView tvHadirCount;
        private TextView tvIjinCount;
        private TextView tvAbsenCount;

        public PesertaAdapterViewHolder(View itemView) {
            super(itemView);

            tvNumber = (TextView) itemView.findViewById(R.id.tv_number);
            tvNrpPeserta = (TextView) itemView.findViewById(R.id.tv_nrp_peserta);
            tvNamaPeserta = (TextView) itemView.findViewById(R.id.tv_nama_peserta);
            tvHadirCount = (TextView) itemView.findViewById(R.id.tv_hadir_count);
            tvIjinCount = (TextView) itemView.findViewById(R.id.tv_ijin_count);
            tvAbsenCount = (TextView) itemView.findViewById(R.id.tv_absen_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String IdPeserta = mCursor.getString(PesertaKelasFragment.INDEX_ID_PESERTA);
            mClickHandler.onClick(IdPeserta);
        }
    }
}
