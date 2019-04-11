package com.palcomtech.batubataapps.ui.produk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.Penjualan;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogCheckout extends DialogFragment {

    public static final String TAG = DialogCheckout.class.getSimpleName();

    public static final String TOTAL = "total";

    private View mView;

    private AddCheckoutListener mAddCheckoutListener;

    @BindView(R.id.tlNameBuyer) TextInputLayout tlNameBuyer;
    @BindView(R.id.edtNameBuyer) TextInputEditText edtNameBuyer;
    @BindView(R.id.tvTotalPembayaran) TextView tvTotalPembayaran;
    @BindView(R.id.btnSimpanPembelian) Button btnSimpan;

    private String name;
    private double total = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            total = args.getDouble(TOTAL, 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_checkout, container, false);
        getDialog().setCanceledOnTouchOutside(false);
        ButterKnife.bind(this, mView);
        tvTotalPembayaran.setText(getString(R.string.harga_produk,
                NumberFormat
                        .getNumberInstance(Locale.getDefault())
                        .format(total)
            )
        );
        edtNameBuyer.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                    simpan();
                }
                return false;
            }
        });
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddCheckoutListener) {
            mAddCheckoutListener = (AddCheckoutListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        edtNameBuyer.requestFocus();
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

        name = tlNameBuyer.getEditText().getText().toString();

        if (TextUtils.isEmpty(name)) {
            valid = false;
            tlNameBuyer.setError(getString(R.string.error_checkout_0));
        } else {
            tlNameBuyer.setError(null);
        }

        return valid;
    }

    @OnClick(R.id.btnCancel) void cancel() {
        dismiss();
    }

    @OnClick(R.id.btnSimpanPembelian) void simpan() {
        if (!validate()) {
            return;
        }
        if (mAddCheckoutListener != null) {
            mAddCheckoutListener.onAddCheckoutListener(setPenjualan());
        }
        dismiss();
    }

    private String getDate() {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        return sfd.format(new Date());
    }

    public Penjualan setPenjualan() {
        Penjualan penjualan = new Penjualan();
        if (mView != null) {
            penjualan.setNamePembeli(name);
            penjualan.setTglPembelian(getDate());
            penjualan.setTotalPembelian(total);
        }
        return penjualan;
    }

    public interface AddCheckoutListener {
        void onAddCheckoutListener(Penjualan penjualan);
    }
}
