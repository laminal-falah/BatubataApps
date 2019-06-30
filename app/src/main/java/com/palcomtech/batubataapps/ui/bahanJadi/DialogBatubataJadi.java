package com.palcomtech.batubataapps.ui.bahanJadi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.BahanJadi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogBatubataJadi extends DialogFragment {

    public static final String TAG = DialogBatubataJadi.class.getSimpleName();

    public static final String ID = "uid";
    public static final String NAME_PRODUCT = "nama_mentah";
    public static final String STOCK_PRODUCT = "stok_mentah";

    private View mView;

    private AddBrickFinishListener mListener;

    @BindView(R.id.tvProductRaw) TextView tvProduk;
    @BindView(R.id.tl_name_brick_finish) TextInputLayout tl_name_brick;
    @BindView(R.id.edt_name_brick_finish) TextInputEditText edt_name_brick;
    @BindView(R.id.tl_price_brick_finish) TextInputLayout tl_price_brick;
    @BindView(R.id.edt_price_brick_finish) TextInputEditText edt_price_brick;
    @BindView(R.id.tvCounting) TextView tvCount;
    @BindView(R.id.btnPlus) Button btnPlus;
    @BindView(R.id.btnMinus) Button btnMinus;
    @BindView(R.id.btnAddJadi) Button btnAdd;

    private String uid, namaBatu, namaBarang;
    private double stok = 0, harga = 0;
    private int count = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            uid = args.getString(ID);
            namaBarang = args.getString(NAME_PRODUCT);
            stok = args.getDouble(STOCK_PRODUCT, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_add_jadi, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, mView);
        btnMinus.setEnabled(false);
        tvProduk.setText(namaBarang);
        if (stok < 1000) {
            count = (int) stok;
            getJmlh(count);
            btnAdd.setEnabled(false);
        }
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddBrickFinishListener) {
            mListener = (AddBrickFinishListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        edt_name_brick.requestFocus();
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        p.x = 200;
        getDialog().getWindow().setAttributes(p);
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private boolean validate() {
        boolean valid = true;

        namaBatu = tl_name_brick.getEditText().getText().toString();
        harga = Double.valueOf(tl_price_brick.getEditText().getText().toString());

        if (TextUtils.isEmpty(namaBatu)) {
            valid = false;
            tl_name_brick.setError(getString(R.string.error_name_goods));
        } else {
            tl_name_brick.setError(null);
        }


        if (TextUtils.isEmpty(tl_price_brick.getEditText().getText().toString())) {
            valid = false;
            tl_price_brick.setError(getString(R.string.error_harga_goods_0));
        } else if (Double.parseDouble(tl_price_brick.getEditText().getText().toString()) == 0 ||
                Double.parseDouble(tl_price_brick.getEditText().getText().toString()) < 50) {
            valid = false;
            tl_price_brick.setError(getString(R.string.error_harga_goods_1));
        } else {
            tl_price_brick.setError(null);
        }

        return valid;
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
        getJmlh(count);
    }

    @OnClick(R.id.btnPlus) void plus() {
        if (count > stok - 1000) {
            btnPlus.setEnabled(false);
            btnMinus.setEnabled(true);
        } else {
            btnPlus.setEnabled(true);
            btnMinus.setEnabled(true);
            count+=1000;
        }
        getJmlh(count);
    }

    @OnClick(R.id.btnCancel) void cancel() {
        resetForm();
        dismiss();
    }

    @OnClick(R.id.btnAddJadi) void addBahanJadi() {
        if (!validate()) {
            return;
        }
        if (mListener != null) {
            mListener.onAddBrickFinishListener(setBahanJadi());
        }
        dismiss();
        resetForm();
    }

    private void resetForm() {
        if (mView != null) {
            count = 1000;
            tvCount.setText(String.valueOf(count));
        }
    }

    private void getJmlh(int c) {
        tvCount.setText(String.valueOf(c));
    }

    public BahanJadi setBahanJadi() {
        BahanJadi bahanJadi = new BahanJadi();
        if (mView != null) {
            bahanJadi.setUidMentah(uid);
            bahanJadi.setNama_bahan(namaBatu);
            bahanJadi.setHarga(harga);
            bahanJadi.setStok(count);
        }
        return bahanJadi;
    }

    public interface AddBrickFinishListener {
        void onAddBrickFinishListener(BahanJadi bahanJadi);
    }

}
