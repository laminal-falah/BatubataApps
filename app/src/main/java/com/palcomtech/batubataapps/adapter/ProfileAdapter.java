package com.palcomtech.batubataapps.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.Profile;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileHolder> {

    private final ArrayList<Profile> profiles;
    private final onProfileSelectedListener mListener;

    public ProfileAdapter(ArrayList<Profile> profiles, onProfileSelectedListener mListener) {
        this.profiles = profiles;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ProfileHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ProfileHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_profile, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileHolder profileHolder, int i) {
        profileHolder.bind(profiles.get(i), mListener);
    }

    @Override
    public int getItemCount() {
        return profiles.size() > 0 ? profiles.size() : 0;
    }

    class ProfileHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvTitleProfile) TextView tvTitleProfile;
        @BindView(R.id.tvBodyProfile) TextView tvBodyProfile;

        ProfileHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(final Profile profile, final onProfileSelectedListener mListener) {
            tvTitleProfile.setText(profile.getTitle());
            tvBodyProfile.setText(profile.getBody());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onProfileSelected(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface onProfileSelectedListener {
        void onProfileSelected(int i );
    }
}
