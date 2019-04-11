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

public class BahanJadiAdapter extends FirestoreAdapter<BahanJadiAdapter.ViewHolder> {

    private final OnBahanJadiSelectedListener mOnBahanJadiSelectedListener;

    public BahanJadiAdapter(Query query, OnBahanJadiSelectedListener mOnBahanJadiSelectedListener) {
        super(query);
        this.mOnBahanJadiSelectedListener = mOnBahanJadiSelectedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_bahan_jadi, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mOnBahanJadiSelectedListener);
    }

    public Object editItem(int position) {
        if (mOnBahanJadiSelectedListener != null) {
            mOnBahanJadiSelectedListener.onBahanJadiSelected(getSnapshot(position));
        }
        return mOnBahanJadiSelectedListener;
    }

    public void deleteItem(int position) {
        getSnapshot(position).getReference().delete();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivTextDrawable) ImageView icon;
        @BindView(R.id.tvTitleJadi) TextView tvTitleJadi;
        @BindView(R.id.tvStokJadi) TextView tvStokJadi;
        @BindView(R.id.tvHargaJadi) TextView tvHargaJadi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnBahanJadiSelectedListener listener) {
            BahanJadi bahanJadi = snapshot.toObject(BahanJadi.class);
            Resources resources = itemView.getResources();
            icon.setImageDrawable(RandomColorUtils.setDrawText(bahanJadi.getNama_bahan()));
            tvTitleJadi.setText(bahanJadi.getNama_bahan());
            tvStokJadi.setText(resources.getString(R.string.stok_jadi, bahanJadi.getStok()));
            tvHargaJadi.setText(resources.getString(R.string.harga_jadi,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(bahanJadi.getHarga())
                    )
            );
            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onBahanJadiSelected(snapshot);
                    }
                }
            });*/
        }
    }

    public interface OnBahanJadiSelectedListener {
        void onBahanJadiSelected(DocumentSnapshot snapshot);
    }
}
