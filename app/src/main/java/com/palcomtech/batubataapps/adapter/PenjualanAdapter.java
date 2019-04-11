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
import com.palcomtech.batubataapps.model.Penjualan;
import com.palcomtech.batubataapps.utils.RandomColorUtils;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PenjualanAdapter extends FirestoreAdapter<PenjualanAdapter.ViewHolder> {

    private final OnPenjualanSelectedListener mListener;

    public PenjualanAdapter(Query query, OnPenjualanSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_penjualan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivTextDrawable) ImageView icon;
        @BindView(R.id.tvTitlePenjualan) TextView tvTitle;
        @BindView(R.id.tvTotalPenjualan) TextView tvTotal;
        @BindView(R.id.tvTglPenjualan) TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnPenjualanSelectedListener listener) {
            Penjualan penjualan = snapshot.toObject(Penjualan.class);
            Resources resources = itemView.getResources();
            icon.setImageDrawable(RandomColorUtils.setDrawText(penjualan.getNamePembeli()));
            tvTitle.setText(resources.getString(R.string.title_pembeli, penjualan.getNamePembeli()));
            tvTotal.setText(resources.getString(R.string.total_beli,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(penjualan.getTotalPembelian())
                    )
            );
            tvDate.setText(resources.getString(R.string.tanggal_beli, penjualan.getTglPembelian()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onPenjualanSelectedListener(snapshot);
                    }
                }
            });
        }
    }

    public interface OnPenjualanSelectedListener {
        void onPenjualanSelectedListener(DocumentSnapshot snapshot);
    }
}
