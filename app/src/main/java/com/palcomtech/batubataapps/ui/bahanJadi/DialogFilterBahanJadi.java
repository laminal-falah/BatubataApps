package com.palcomtech.batubataapps.ui.bahanJadi;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Spinner;

import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.BahanJadi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogFilterBahanJadi extends DialogFragment {

    public static final String TAG = DialogFilterBahanJadi.class.getSimpleName();

    private View mRootView;

    @BindView(R.id.spinner_sort) Spinner mSortSpinner;

    private FilterListener mFilterListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialog);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.dialog_filter_bahan_jadi, container, false);
        ButterKnife.bind(this, mRootView);
        getDialog().setCanceledOnTouchOutside(true);
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FilterListener) {
            mFilterListener = (FilterListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.x = 200;
        getDialog().getWindow().setAttributes(p);
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    @OnClick(R.id.button_search) public void onSearchClicked() {
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }
        dismiss();
    }

    @OnClick(R.id.button_cancel) public void onCancelClicked() {
        dismiss();
    }

    @OnClick(R.id.button_reset) public void onResetFilters() {
        resetFilters();
        if (mFilterListener != null) {
            mFilterListener.onFilter(getFilters());
        }

        dismiss();
    }

    @Nullable
    private String getSelectedSortBy() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_name_0).equals(selected) || getString(R.string.sort_by_name_1).equals(selected)) {
            return BahanJadi.FIELD_BAHAN;
        }
        if (getString(R.string.sort_by_stok_0).equals(selected) || getString(R.string.sort_by_stok_1).equals(selected)) {
            return BahanJadi.FIELD_STOCK;
        }
        if (getString(R.string.sort_by_price_0).equals(selected) || getString(R.string.sort_by_price_1).equals(selected)) {
            return BahanJadi.FIELD_PRICE;
        }
        if (getString(R.string.value_any_sort).equals(selected)) {
            return BahanJadi.TIMESTAMPS;
        }

        return null;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        String selected = (String) mSortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_name_0).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_by_name_1).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_stok_0).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_stok_1).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        if (getString(R.string.sort_by_price_0).equals(selected)) {
            return Query.Direction.DESCENDING;
        }
        if (getString(R.string.sort_by_price_1).equals(selected)) {
            return Query.Direction.ASCENDING;
        }
        return Query.Direction.DESCENDING;
    }

    public void resetFilters() {
        if (mRootView != null) {
            mSortSpinner.setSelection(0);
        }
    }

    public FiltersBahanJadi getFilters() {
        FiltersBahanJadi filters = new FiltersBahanJadi();
        if (mRootView != null) {
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }
        return filters;
    }

    public interface FilterListener {
        void onFilter(FiltersBahanJadi filtersBahanJadi);
    }
}
