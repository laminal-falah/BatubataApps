package com.palcomtech.batubataapps.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.LogBahanMentah;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LogRawAdapter extends FirestoreAdapter<LogRawAdapter.ViewHolder> {

    private final onLogSelectedListener mListener;

    public LogRawAdapter(Query query, onLogSelectedListener mListener) {
        super(query);
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_bahan_mentah_log, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mListener);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvQty) TextView tvQty;
        @BindView(R.id.tvDate) TextView tvDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final DocumentSnapshot snapshot, final onLogSelectedListener listener) {
            LogBahanMentah mentah = snapshot.toObject(LogBahanMentah.class);
            Resources resources = itemView.getResources();
            tvQty.setText(resources.getString(R.string.title_qty, mentah.getQuantity()));
            DocumentSnapshot.ServerTimestampBehavior behavior = DocumentSnapshot.ServerTimestampBehavior.ESTIMATE;
            Date date = snapshot.getDate("timestamps", behavior);
            tvDate.setText(date.toString());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onLogSelected(snapshot);
                    }
                }
            });
        }
    }

    public interface onLogSelectedListener {
        void onLogSelected(DocumentSnapshot snapshot);
    }
}
