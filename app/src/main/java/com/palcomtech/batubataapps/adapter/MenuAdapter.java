package com.palcomtech.batubataapps.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.MenuDashboard;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {

    private final ArrayList<MenuDashboard> menuDashboards;

    private final OnMenuSelectedListener onMenuSelectedListener;

    public MenuAdapter(ArrayList<MenuDashboard> menuDashboards, OnMenuSelectedListener onMenuSelectedListener) {
        this.menuDashboards = menuDashboards;
        this.onMenuSelectedListener = onMenuSelectedListener;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MenuHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_menu, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MenuHolder menuHolder, int i) {
        menuHolder.bind(menuDashboards.get(i), onMenuSelectedListener);
    }

    @Override
    public int getItemCount() {
        return menuDashboards.size() > 0 ? menuDashboards.size() : 0;
    }

    class MenuHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iconMenu) ImageView icon;
        @BindView(R.id.tvTitleMenu) TextView title;

        MenuHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void bind(final MenuDashboard menuDashboard, final OnMenuSelectedListener mListener) {
            icon.setImageResource(menuDashboard.getIcon());
            title.setText(menuDashboard.getTitle());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onMenuSelected(getAdapterPosition());
                    }
                }
            });
        }
    }

    public interface OnMenuSelectedListener {
        void onMenuSelected(int i);
    }
}
