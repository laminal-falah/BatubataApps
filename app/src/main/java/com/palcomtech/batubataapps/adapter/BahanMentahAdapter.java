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
import com.palcomtech.batubataapps.model.BahanMentah;
import com.palcomtech.batubataapps.utils.RandomColorUtils;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BahanMentahAdapter extends FirestoreAdapter<BahanMentahAdapter.ViewHolder> {

    private final OnBahanMentahSelectedListener mListener;

    public BahanMentahAdapter(Query query, OnBahanMentahSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_bahan_mentah, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mListener);
    }

    public void deleteItem(int position) {
        getSnapshot(position).getReference().delete();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivTextDrawable) ImageView icon;
        @BindView(R.id.tvTitleMentah) TextView tvTitleMentah;
        @BindView(R.id.tvStokMentah) TextView tvStokMentah;
        @BindView(R.id.tvHargaMentah) TextView tvHarga;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnBahanMentahSelectedListener listener) {
            BahanMentah mentah = snapshot.toObject(BahanMentah.class);
            Resources resources = itemView.getResources();
            icon.setImageDrawable(RandomColorUtils.setDrawText(mentah.getNama_bahan()));
            tvTitleMentah.setText(mentah.getNama_bahan());
            tvStokMentah.setText(resources.getString(R.string.stok_mentah,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(mentah.getStok()),
                    mentah.getUkuran()
                )
            );
            tvHarga.setText(resources.getString(R.string.harga_jadi,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(mentah.getHarga())
                )
            );
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onBahanMentahSelected(snapshot);
                    }
                }
            });
        }
    }

    public interface OnBahanMentahSelectedListener {
        void onBahanMentahSelected(DocumentSnapshot bahanMentah);
    }
}
