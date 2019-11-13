package com.palcomtech.batubataapps.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.Cart;
import com.palcomtech.batubataapps.utils.RandomColorUtils;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartAdapter extends FirestoreAdapter<CartAdapter.ViewHolder> {

    private OnCartListener mOnCartListener;

    public CartAdapter(Query query) {
        super(query);
    }

    public CartAdapter(Query query, OnCartListener mOnCartListener) {
        super(query);
        this.mOnCartListener = mOnCartListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_cart, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind(getSnapshot(i), mOnCartListener);
    }

    public void deleteItem(int position) {
        getSnapshot(position).getReference().delete();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iconCart) ImageView icon;
        @BindView(R.id.tvTitleCart) TextView tvTitle;
        @BindView(R.id.tvHargaCart) TextView tvHarga;
        @BindView(R.id.tvTotalCart) TextView tvTotal;
        @BindView(R.id.tvCounting) TextView tvCount;
        @BindView(R.id.btnMinusCart) Button btnMinus;
        @BindView(R.id.btnPlusCart) Button btnPlus;

        private Resources resources;

        private double total = 0, harga = 0;
        private int count = 1000;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            resources = itemView.getResources();
        }

        public void bind(final DocumentSnapshot snapshot, final OnCartListener listener) {
            Cart cart = snapshot.toObject(Cart.class);
            icon.setImageDrawable(RandomColorUtils.setDrawText(cart.getNmBahan()));
            tvTitle.setText(cart.getNmBahan());
            tvHarga.setText(resources.getString(R.string.harga_produk,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(cart.getHrgBahan())
                    )
            );
            harga = cart.getHrgBahan();
            count = (int) cart.getJlBahan();
            getJmlh(count);
            getTotal(count);
            btnPlus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnPlus.setEnabled(true);
                    btnMinus.setEnabled(true);
                    count+=1000;
                    getTotal(count);
                    getJmlh(count);
                    if (listener != null) {
                        listener.onUpdateCartListener(snapshot, setCart());
                    }
                }
            });
            btnMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (count < 2000) {
                        count = 1000;
                        btnMinus.setEnabled(false);
                        btnPlus.setEnabled(true);
                    } else {
                        btnPlus.setEnabled(true);
                        btnMinus.setEnabled(true);
                        count-=1000;
                    }
                    getTotal(count);
                    getJmlh(count);
                    if (listener != null) {
                        listener.onUpdateCartListener(snapshot, setCart());
                    }
                }
            });
        }

        private void getJmlh(int c) {
            tvCount.setText(String.valueOf(c));
        }

        private void getTotal(int c) {
            total = c * harga;
            tvTotal.setText(resources.getString(R.string.harga_produk,
                    NumberFormat
                            .getNumberInstance(Locale.getDefault())
                            .format(total)
                    )
            );
        }

        private Cart setCart() {
            Cart cart = new Cart();
            cart.setJlBahan(count);
            cart.setSubTotal(total);
            return cart;
        }
    }

    public interface OnCartListener {
        void onUpdateCartListener(DocumentSnapshot snapshot, Cart cart);
    }
}
