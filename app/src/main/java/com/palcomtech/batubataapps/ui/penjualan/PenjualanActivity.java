package com.palcomtech.batubataapps.ui.penjualan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.PenjualanAdapter;
import com.palcomtech.batubataapps.model.Penjualan;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PenjualanActivity extends AppCompatActivity implements PenjualanAdapter.OnPenjualanSelectedListener {

    private static final String TAG = PenjualanActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.rvPenjualan) RecyclerView rvPenjualan;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private PenjualanAdapter mAdapter;

    private final int LIMIT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_penjualan);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        FirebaseFirestore.setLoggingEnabled(true);

        toolbar.setTitle(getString(R.string.title_penjualan));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        initAuth();
        initFirestore();
        initRecyclerView();
    }

    private void initAuth() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    private void initFirestore() {
        mFirestore = FirebaseFirestore.getInstance();
        mQuery = mFirestore.collection(Penjualan.COLLECTION)
                .orderBy(Penjualan.FIELD_TGL, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }
        barUtils.show();
        mAdapter = new PenjualanAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackbarShort("Error: check logs for info" + e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvPenjualan.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    rvPenjualan.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                barUtils.hide();
            }
        };
        rvPenjualan.setLayoutManager(new LinearLayoutManager(this));
        rvPenjualan.addItemDecoration(new DividerItemDecoration(rvPenjualan.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvPenjualan.setItemAnimator(itemAnimator);
        rvPenjualan.setAdapter(mAdapter);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onPenjualanSelectedListener(DocumentSnapshot snapshot) {
        startActivity(new Intent(this, DetailPenjualanActivity.class)
                .putExtra(DetailPenjualanActivity.KEY_SELLING_ID, snapshot.getId()));
    }
}
