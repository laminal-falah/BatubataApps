package com.palcomtech.batubataapps.ui.produk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.Cart;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogProduk extends DialogFragment {

    public static final String TAG = DialogProduk.class.getSimpleName();

    public static final String UID = "uid";
    public static final String NAME_PRODUCT = "nama_bahan";
    public static final String STOCK_PRODUCT = "stok_bahan";
    public static final String PRICE_PRODUCT = "harga_bahan";

    private View mView;

    @BindView(R.id.tvProdukDialog) TextView tvProduk;
    @BindView(R.id.tvStokDialog) TextView tvStok;
    @BindView(R.id.tvCounting) TextView tvCount;
    @BindView(R.id.tvTotalProduk) TextView tvTotal;
    @BindView(R.id.btnMinus) Button btnMinus;
    @BindView(R.id.btnPlus) Button btnPlus;
    @BindView(R.id.btnAddCart) Button btnAddCart;

    private AddCartListener mAddCartListener;

    private String uid, nama;
    private double stok = 0, hrga = 0, total = 0;
    private int count = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            uid = args.getString(UID);
            nama = args.getString(NAME_PRODUCT);
            stok = args.getDouble(STOCK_PRODUCT, 0);
            hrga = args.getDouble(PRICE_PRODUCT, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_cart, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, mView);
        btnMinus.setEnabled(false);
        if (stok > 1) {
            btnAddCart.setEnabled(true);
        } else {
            btnAddCart.setEnabled(false);
        }
        tvProduk.setText(nama);
        tvStok.setText(getString(R.string.stok_jadi, stok));
        getTotal(count);
        return mView;
    }

    private void getJmlh(int c) {
        tvCount.setText(String.valueOf(c));
    }

    private void getTotal(int c) {
        total = c * hrga;
        tvTotal.setText(getString(R.string.harga_produk, NumberFormat.getNumberInstance(Locale.getDefault()).format(total)));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddCartListener) {
            mAddCartListener = (AddCartListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @OnClick(R.id.btnMinus) void minus() {
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
    }

    @OnClick(R.id.btnPlus) void add() {
        if (count > stok - 1) {
            btnPlus.setEnabled(false);
            btnMinus.setEnabled(true);
        } else {
            btnPlus.setEnabled(true);
            btnMinus.setEnabled(true);
            count+=1000;
        }
        getTotal(count);
        getJmlh(count);
    }

    @OnClick(R.id.btnCancel) void cancel() {
        resetCart();
        dismiss();
    }

    @OnClick(R.id.btnAddCart) void addCart() {
        if (mAddCartListener != null) {
            mAddCartListener.onAddCartListener(setCart());
        }
        dismiss();
        resetCart();
    }

    private void resetCart() {
        if (mView != null) {
            count = 1000;
            tvCount.setText(String.valueOf((int) total));
        }
    }

    public Cart setCart() {
        Cart cart = new Cart();
        if (mView != null) {
            cart.setIdBahan(uid);
            cart.setNmBahan(nama);
            cart.setJlBahan(count);
            cart.setHrgBahan(hrga);
            cart.setSubTotal(total);
        }
        return cart;
    }

    public interface AddCartListener {
        void onAddCartListener(Cart cart);
    }
}
