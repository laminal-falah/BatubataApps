package com.palcomtech.batubataapps.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.DetailPenjualan;
import com.palcomtech.batubataapps.utils.RandomColorUtils;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailPenjualanAdapter extends FirestoreAdapter<DetailPenjualanAdapter.ViewHolder> {

    private OnDetailPenjualanSelectedListener mListener;

    public DetailPenjualanAdapter(Query query, OnDetailPenjualanSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    public DetailPenjualanAdapter(Query query) {
        super(query);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_detail_penjualan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iconDetailPembeli) ImageView iconDetail;
        @BindView(R.id.tvTitleBahanPembeli) TextView tvTitleDetail;
        @BindView(R.id.tvHargaBahanPembeli) TextView tvHrgDetail;
        @BindView(R.id.tvSubTotalPembeli) TextView tvSubtotal;
        @BindView(R.id.tvJmlhPembeli) TextView tvCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnDetailPenjualanSelectedListener listener) {
            DetailPenjualan penjualan = snapshot.toObject(DetailPenjualan.class);
            Resources resources = itemView.getResources();
            iconDetail.setImageDrawable(RandomColorUtils.setDrawText(penjualan.getNmBahan()));
            tvTitleDetail.setText(penjualan.getNmBahan());
            tvHrgDetail.setText(resources.getString(R.string.harga_bahan_detail,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(penjualan.getHrgBahan())
                )
            );
            tvSubtotal.setText(resources.getString(R.string.subtotal_bahan_detail,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(penjualan.getSubTotal())
                )
            );
            tvCount.setText(resources.getString(R.string.jumlah_bahan_detail,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(penjualan.getJlBahan())
                )
            );
        }
    }

    public interface OnDetailPenjualanSelectedListener {
        void onDetailPenjualanSelected(DocumentSnapshot snapshot);
    }
}
