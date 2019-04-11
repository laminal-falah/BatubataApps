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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.DetailPenjualanAdapter;
import com.palcomtech.batubataapps.model.DetailPenjualan;
import com.palcomtech.batubataapps.model.Penjualan;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.RandomColorUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailPenjualanActivity extends AppCompatActivity implements EventListener<DocumentSnapshot>,
    DetailPenjualanAdapter.OnDetailPenjualanSelectedListener {

    private static final String TAG = DetailPenjualanActivity.class.getSimpleName();

    public static final String KEY_SELLING_ID = "key_selling_id";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.rvDetailPembeli) RecyclerView rvDetailPembeli;
    @BindView(R.id.iconNameBuy) ImageView iconName;
    @BindView(R.id.tvNameBuy) TextView tvName;
    @BindView(R.id.tvTglBuy) TextView tvTgl;
    @BindView(R.id.tvTotalAll) TextView tvTotalAll;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mDetailPenjualanRef;
    private ListenerRegistration mDetailPenjualanRegistration;
    private Query mQuery;

    private DetailPenjualanAdapter mAdapter;

    private final int LIMIT = 500;
    private String rawId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_detail_penjualan);
        ButterKnife.bind(this);

        rawId = getIntent().getExtras().getString(KEY_SELLING_ID);
        if (rawId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_SELLING_ID);
        }

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        toolbar.setTitle(getString(R.string.title_detail_selling));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        barUtils.show();

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
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

        mDetailPenjualanRef = mFirestore.collection(Penjualan.COLLECTION).document(rawId);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }

        mQuery = mDetailPenjualanRef.collection(DetailPenjualan.COLLECTION).limit(LIMIT);

        mAdapter = new DetailPenjualanAdapter(mQuery) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackBarLong("Error: check logs for info" + e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvDetailPembeli.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    rvDetailPembeli.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }
        };

        rvDetailPembeli.setLayoutManager(new LinearLayoutManager(this));
        rvDetailPembeli.addItemDecoration(new DividerItemDecoration(rvDetailPembeli.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvDetailPembeli.setItemAnimator(itemAnimator);
        rvDetailPembeli.setAdapter(mAdapter);
        barUtils.hide();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
        mDetailPenjualanRegistration = mDetailPenjualanRef.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
        if (mDetailPenjualanRegistration != null) {
            mDetailPenjualanRegistration.remove();
            mDetailPenjualanRegistration = null;
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "onEvent", e);
            return;
        }
        onLoadDetail(snapshot.toObject(Penjualan.class));
    }

    private void onLoadDetail(@Nullable Penjualan penjualan) {
        iconName.setImageDrawable(RandomColorUtils.setDrawText(penjualan.getNamePembeli()));
        tvName.setText(getString(R.string.title_pembeli, penjualan.getNamePembeli()));
        tvTgl.setText(getString(R.string.tanggal_beli, penjualan.getTglPembelian()));
        tvTotalAll.setText(getString(R.string.total_bahan_detail,
                NumberFormat
                        .getNumberInstance(Locale.getDefault())
                        .format(penjualan.getTotalPembelian())
            )
        );
    }

    @Override
    public void onDetailPenjualanSelected(DocumentSnapshot snapshot) {

    }
}
