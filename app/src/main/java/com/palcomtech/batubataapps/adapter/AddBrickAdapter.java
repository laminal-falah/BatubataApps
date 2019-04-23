package com.palcomtech.batubataapps.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.model.BahanMentah;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddBrickAdapter extends FirestoreAdapter<AddBrickAdapter.ViewHolder> {

    private final OnSelectedItemProductListener mListener;

    public AddBrickAdapter(Query query, OnSelectedItemProductListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_add_jadi_mentah, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvProductMentah) TextView tvMentah;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final OnSelectedItemProductListener listener) {
            BahanMentah mentah = snapshot.toObject(BahanMentah.class);
            tvMentah.setText(mentah.getNama_bahan());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onSelectedItemBrickListener(snapshot);
                    }
                }
            });
        }
    }

    public interface OnSelectedItemProductListener {
        void onSelectedItemBrickListener(DocumentSnapshot snapshot);
    }
}
