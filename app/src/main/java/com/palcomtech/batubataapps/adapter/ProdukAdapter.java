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
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.utils.RandomColorUtils;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProdukAdapter extends FirestoreAdapter<ProdukAdapter.ViewHolder> {

    private final OnProdukSelectedListener mOnProdukSelectedListener;

    public ProdukAdapter(Query query, OnProdukSelectedListener mOnProdukSelectedListener) {
        super(query);
        this.mOnProdukSelectedListener = mOnProdukSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_produk, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mOnProdukSelectedListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iconProduk) ImageView mIcon;
        @BindView(R.id.tvTitleProduk) TextView mTitle;
        @BindView(R.id.tvHargaProduk) TextView mHarga;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnProdukSelectedListener listener) {
            BahanJadi produk = snapshot.toObject(BahanJadi.class);
            Resources resources = itemView.getResources();
            mIcon.setImageDrawable(RandomColorUtils.setDrawText(produk.getNama_bahan()));
            mTitle.setText(produk.getNama_bahan());
            mHarga.setText(resources.getString(R.string.harga_produk,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(produk.getHarga())
                    )
            );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onProdukSelected(snapshot);
                    }
                }
            });
        }
    }

    public interface OnProdukSelectedListener {
        void onProdukSelected(DocumentSnapshot snapshot);
    }
}
