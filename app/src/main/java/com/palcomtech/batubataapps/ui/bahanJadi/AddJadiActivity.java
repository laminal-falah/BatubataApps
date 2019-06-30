package com.palcomtech.batubataapps.ui.bahanJadi;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.AddBrickAdapter;
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.model.BahanMentah;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddJadiActivity extends AppCompatActivity implements DialogBatubataJadi.AddBrickFinishListener,
        AddBrickAdapter.OnSelectedItemProductListener {

    public static final String TAG = AddJadiActivity.class.getSimpleName();

    public static final String ID = "id";

    public static final int REQUEST_ADD = 300;
    public static final int RESULT_ADD_SUCCESS = 301;
    public static final int RESULT_ADD_FAILED = 302;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvMentahAdd) RecyclerView rvMentahAdd;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.tvViewEmpty) TextView tvEmptyView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference colBahanJadi;
    private DocumentReference docBahanMentah;
    private Query mQuery;

    private AddBrickAdapter mAdapter;
    private DialogBatubataJadi mDialogBatubataJadi;

    private String rawId;
    private int LIMIT = 500;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_jadi);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        toolbar.setTitle(getString(R.string.title_add_goods));
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

        mDialogBatubataJadi = new DialogBatubataJadi();
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
        mQuery = mFirestore.collection(BahanMentah.COLLECTION)
                .orderBy(BahanMentah.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }
        barUtils.show();
        mAdapter = new AddBrickAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackbarShort("Error: check logs for info : " + e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvMentahAdd.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    tvEmptyView.setText(getString(R.string.msg_data_add_brick));
                } else {
                    rvMentahAdd.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                barUtils.hide();
            }
        };
        rvMentahAdd.setLayoutManager(new LinearLayoutManager(this));
        rvMentahAdd.addItemDecoration(new DividerItemDecoration(rvMentahAdd.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvMentahAdd.setItemAnimator(itemAnimator);
        rvMentahAdd.setAdapter(mAdapter);
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
    public void onSelectedItemBrickListener(DocumentSnapshot snapshot) {
        String name = snapshot.getString(BahanMentah.FIELD_BAHAN);
        double stok = snapshot.getDouble(BahanMentah.FIELD_STOK);
        if (stok > 999) {
            Bundle args = new Bundle();
            args.putString(DialogBatubataJadi.ID, snapshot.getId());
            args.putString(DialogBatubataJadi.NAME_PRODUCT, name);
            args.putDouble(DialogBatubataJadi.STOCK_PRODUCT, stok);
            mDialogBatubataJadi.setArguments(args);
            mDialogBatubataJadi.show(getSupportFragmentManager(), DialogBatubataJadi.TAG);
        } else {
            snackbarUtils.snackBarLong("Stok " + name + " kurang dari seribu !");
        }
    }

    @Override
    public void onAddBrickFinishListener(final BahanJadi bahanJadi) {
        barUtils.show();
        Map<String, Object> jadi = new HashMap<>();
        jadi.put(BahanJadi.FIELD_FID, bahanJadi.getUidMentah());
        jadi.put(BahanJadi.FIELD_BAHAN, bahanJadi.getNama_bahan());
        jadi.put(BahanJadi.FIELD_STOCK, bahanJadi.getStok());
        jadi.put(BahanJadi.FIELD_PRICE, bahanJadi.getHarga());
        jadi.put("timestamps", Timestamp.now());
        mFirestore.collection(BahanMentah.COLLECTION)
                .document(bahanJadi.getUidMentah())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        double nowStok = snapshot.getDouble(BahanMentah.FIELD_STOK) - bahanJadi.getStok();
                        Map<String, Object> updated = new HashMap<>();
                        updated.put(BahanMentah.FIELD_STOK, nowStok);
                        mFirestore.collection(BahanMentah.COLLECTION)
                                .document(bahanJadi.getUidMentah())
                                .update(updated);
                    }
                });
        colBahanJadi = mFirestore.collection(BahanJadi.COLLECTION);
        colBahanJadi
                .add(jadi)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Map<String, Object> uid = new HashMap<>();
                        uid.put("uid",documentReference.getId());
                        documentReference.set(uid, SetOptions.merge());
                        barUtils.hide();
                        setResult(RESULT_ADD_SUCCESS);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Intent intent = new Intent();
                intent.putExtra("ERROR", e.getMessage());
                setResult(RESULT_ADD_FAILED, intent);
                finish();
            }
        });
    }
}
