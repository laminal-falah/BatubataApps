package com.palcomtech.batubataapps.ui.bahanMentah;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.Transaction;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.LogRawAdapter;
import com.palcomtech.batubataapps.model.BahanMentah;
import com.palcomtech.batubataapps.model.LogBahanMentah;
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
import butterknife.OnClick;

public class DetailMentahActivity extends AppCompatActivity implements
        EventListener<DocumentSnapshot>, LogRawAdapter.onLogSelectedListener, DialogAddLog.LogListener {

    private static final String TAG = DetailMentahActivity.class.getSimpleName();

    public static final String KEY_RAW_ID = "key_raw_id";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tvDetailRaw) TextView tvName;
    @BindView(R.id.tvStokRaw) TextView tvStok;
    @BindView(R.id.icon_detail) ImageView icon_detail;
    @BindView(R.id.icon_edit) ImageView icon_edit;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.rvLogs) RecyclerView rvLog;

    private DialogAddLog mDialogAddLog;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference mBahanMentahRef;
    private ListenerRegistration mBahanMentahRegistration;
    private Query mQuery;

    private LogRawAdapter rawAdapter;

    private final int LIMIT = 500;
    private String rawId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_detail_mentah);
        ButterKnife.bind(this);

        rawId = getIntent().getExtras().getString(KEY_RAW_ID);
        if (rawId == null) {
            throw new IllegalArgumentException("Must pass extra " + KEY_RAW_ID);
        }

        toolbar.setTitle(getString(R.string.title_detail_raw));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        barUtils = new ProgressBarUtils(this);
        barUtils.show();

        snackbarUtils = new SnackbarUtils(this);

        initAuth();
        initFirestore();
        initRecycler();

        mDialogAddLog = new DialogAddLog();

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

        mBahanMentahRef = mFirestore.collection(BahanMentah.COLLECTION).document(rawId);
    }

    private void initRecycler() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }
        mQuery = mBahanMentahRef.collection(LogBahanMentah.COLLECTION)
                .orderBy(LogBahanMentah.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
        rawAdapter = new LogRawAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvLog.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    rvLog.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackBarLong("Error: check logs for info" + e.getMessage());
            }
        };
        rvLog.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration item = new DividerItemDecoration(rvLog.getContext(), RecyclerView.VERTICAL);
        rvLog.addItemDecoration(item);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvLog.setItemAnimator(itemAnimator);
        rvLog.setAdapter(rawAdapter);
        barUtils.hide();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    public void onStart() {
        super.onStart();

        rawAdapter.startListening();
        mBahanMentahRegistration = mBahanMentahRef.addSnapshotListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();

        rawAdapter.stopListening();

        if (mBahanMentahRegistration != null) {
            mBahanMentahRegistration.remove();
            mBahanMentahRegistration = null;
        }
    }

    @Override
    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
        if (e != null) {
            Log.w(TAG, "raw materials:onEvent", e);
            return;
        }
        onLoadRawMaterials(snapshot.toObject(BahanMentah.class));
    }

    private void onLoadRawMaterials(@Nullable BahanMentah bahanMentah) {
        icon_detail.setImageDrawable(RandomColorUtils.setDrawText(bahanMentah.getNama_bahan()));
        tvName.setText(bahanMentah.getNama_bahan());
        tvStok.setText(getString(R.string.stok_mentah,
                NumberFormat
                        .getNumberInstance(Locale.getDefault())
                        .format(bahanMentah.getStok()),
                bahanMentah.getSatuan()
            )
        );
        icon_edit.setImageResource(R.drawable.ic_edit_black_24dp);
        icon_edit.setColorFilter(getResources().getColor(R.color.colorAccent));
    }

    @Override
    public void onLogSelected(DocumentSnapshot snapshot) {
    }

    @Override
    public void onLogListener(LogBahanMentah logBahanMentah) {
        addLogs(mBahanMentahRef, logBahanMentah)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        snackbarUtils.snackBarLong(getString(R.string.snack_bar_success_log_0));
                        rvLog.smoothScrollToPosition(0);
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbarUtils.snackBarLong(getString(R.string.snack_bar_error_log, e.getMessage()));
                    }
                });
    }

    private Task<Void> addLogs(final DocumentReference mBahanMentahRef, final LogBahanMentah logBahanMentah) {
        final DocumentReference logRef = mBahanMentahRef.collection(LogBahanMentah.COLLECTION).document();
        return mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                BahanMentah bahanMentah = transaction.get(mBahanMentahRef).toObject(BahanMentah.class);
                double newStok = bahanMentah.getStok() + logBahanMentah.getQuantity();
                bahanMentah.setStok(newStok);
                transaction.set(mBahanMentahRef, bahanMentah);
                transaction.set(logRef, logBahanMentah);
                return null;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @android.support.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddEditMentahActivity.REQUEST_UPDATE) {
            if (resultCode == AddEditMentahActivity.RESULT_UPDATE_SUCCESS) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_success_raw_1),"OK");
            } else if (resultCode == AddEditMentahActivity.RESULT_UPDATE_FAILED) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_error_raw, data.getStringExtra("ERROR")), "DISMISS");
            }
        }
    }

    @OnClick(R.id.fab) void addLog() {
        Bundle bundle = new Bundle();
        bundle.putString(DialogAddLog.ID, rawId);
        mDialogAddLog.setArguments(bundle);
        mDialogAddLog.show(getSupportFragmentManager(), DialogAddLog.TAG);
    }

    @OnClick(R.id.icon_edit) void editRaw() {
        startActivityForResult(new Intent(this, AddEditMentahActivity.class)
                        .putExtra(AddEditMentahActivity.KEY_MENTAH_ID, rawId),
                        AddEditMentahActivity.REQUEST_UPDATE);
    }

}
