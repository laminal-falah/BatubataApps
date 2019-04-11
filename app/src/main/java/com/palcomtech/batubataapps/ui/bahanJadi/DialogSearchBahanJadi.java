package com.palcomtech.batubataapps.ui.bahanJadi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.BahanMentah;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogSearchBahanJadi extends DialogFragment {
    public static final String TAG = DialogSearchBahanJadi.class.getSimpleName();

    private View view;

    @BindView(R.id.icon_back) ImageView iconBack;
    @BindView(R.id.icon_search) ImageView iconSearch;
    @BindView(R.id.edt_query) EditText edtSearch;

    private SearchListener mSearchListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme);
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_search, container, false);
        ButterKnife.bind(this, view);
        getDialog().setCanceledOnTouchOutside(true);
        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    onSearch();
                }
                return false;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        edtSearch.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBack();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchListener) {
            mSearchListener = (SearchListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setGravity(Gravity.TOP | Gravity.CENTER);
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

    @OnClick(R.id.icon_back) void onBack() {
        if (view != null) {
            edtSearch.setText(null);
        }
        if (mSearchListener != null) {
            mSearchListener.onResetSearchListener(getFilters());
        }
        dismiss();
    }

    @OnClick(R.id.icon_search) void onSearch() {
        if (mSearchListener != null) {
            mSearchListener.onSearchListener(getQuery());
        }
        //dismiss();
    }

    public String getQuery() {
        return edtSearch.getText().toString();
    }

    @Nullable
    private String getSelectedSortBy() {
        return BahanMentah.TIMESTAMPS;
    }

    @Nullable
    private Query.Direction getSortDirection() {
        return Query.Direction.DESCENDING;
    }

    public FiltersBahanJadi getFilters() {
        FiltersBahanJadi filters = new FiltersBahanJadi();

        if (mSearchListener != null) {
            filters.setSortBy(getSelectedSortBy());
            filters.setSortDirection(getSortDirection());
        }

        return filters;
    }

    public interface SearchListener {
        void onSearchListener(String query);
        void onResetSearchListener(FiltersBahanJadi filtersBahanJadi);
    }
}
