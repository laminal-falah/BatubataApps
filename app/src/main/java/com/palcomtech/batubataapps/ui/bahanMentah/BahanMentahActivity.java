package com.palcomtech.batubataapps.ui.bahanMentah;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.BahanMentahAdapter;
import com.palcomtech.batubataapps.model.BahanMentah;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BahanMentahActivity extends AppCompatActivity implements
        BahanMentahAdapter.OnBahanMentahSelectedListener, DialogFilterBahanMentah.FilterListener,
        DialogSearchBahanMentah.SearchListener {

    private static final String TAG = BahanMentahActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvBahanMentah) RecyclerView rvMentah;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private BahanMentahAdapter mAdapter;
    private BahanMentahViewModel mViewModel;
    private DialogFilterBahanMentah mFilterBahanMentah;
    private DialogSearchBahanMentah mDialogSearch;

    private final int LIMIT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_bahan_mentah);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        mViewModel = ViewModelProviders.of(this).get(BahanMentahViewModel.class);

        FirebaseFirestore.setLoggingEnabled(true);

        toolbar.setTitle(getString(R.string.title_bahan_mentah));
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

        mFilterBahanMentah = new DialogFilterBahanMentah();
        mDialogSearch = new DialogSearchBahanMentah();
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
        mAdapter = new BahanMentahAdapter(mQuery, this) {
            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvMentah.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    rvMentah.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                barUtils.hide();
            }

            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackbarShort("Error: check logs for info : " + e.getMessage());
            }
        };
        rvMentah.setLayoutManager(new LinearLayoutManager(this));
        rvMentah.addItemDecoration(new DividerItemDecoration(rvMentah.getContext(), RecyclerView.VERTICAL));
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvMentah.setItemAnimator(itemAnimator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            private final Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_white_24dp);
            private final ColorDrawable background = new ColorDrawable(Color.parseColor("#f44336"));

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (i == ItemTouchHelper.LEFT || i == ItemTouchHelper.RIGHT) {
                    mAdapter.deleteItem(viewHolder.getAdapterPosition());
                    snackbarUtils.snackBarLong(getString(R.string.snack_bar_success_raw_2));
                }
            }

            @Override
            public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
                return animationType == ItemTouchHelper.ANIMATION_TYPE_DRAG ? DEFAULT_DRAG_ANIMATION_DURATION
                        : DEFAULT_SWIPE_ANIMATION_DURATION;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    int dXSwipe = (int) (dX * 1.05);
                    if (dX > 0) {
                        // draw background
                        background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dXSwipe, itemView.getBottom());
                        background.draw(c);
                        // draw icon
                        int top = (itemView.getTop() + itemView.getBottom() - deleteIcon.getIntrinsicHeight()) / 2;
                        int left = itemView.getLeft() + 48;
                        deleteIcon.setBounds(left, top, left + deleteIcon.getIntrinsicWidth(), top + deleteIcon.getIntrinsicHeight());
                        deleteIcon.draw(c);
                    } else {
                        // draw background
                        background.setBounds(itemView.getRight() + dXSwipe, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        background.draw(c);
                        // draw icon
                        int top = (itemView.getTop() + itemView.getBottom() - deleteIcon.getIntrinsicHeight()) / 2;
                        int right = itemView.getRight() - 48;
                        deleteIcon.setBounds(right - deleteIcon.getIntrinsicWidth(), top, right, top + deleteIcon.getIntrinsicHeight());
                        deleteIcon.draw(c);
                    }
                }
            }

        }).attachToRecyclerView(rvMentah);
        rvMentah.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab) void addRaw() {
        startActivityForResult(new Intent(this, AddEditMentahActivity.class)
                        .putExtra(AddEditMentahActivity.KEY_MENTAH_ID, AddEditMentahActivity.ADD_DATA),
                AddEditMentahActivity.REQUEST_ADD);
    }

    @Override
    protected void onStart() {
        super.onStart();

        onFilter(mViewModel.getmFiltersBahanMentah());

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AddEditMentahActivity.REQUEST_ADD) {
            if (resultCode == AddEditMentahActivity.RESULT_ADD_SUCCESS) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_success_raw_0), "OK");
            } else if (resultCode == AddEditMentahActivity.RESULT_ADD_FAILED && data != null) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_error_raw,
                        data.getStringExtra("ERROR")), "DISMISS");
            }
        }
    }

    @Override
    public void onBahanMentahSelected(DocumentSnapshot bahanMentah) {
        startActivity(new Intent(this, DetailMentahActivity.class)
                .putExtra(DetailMentahActivity.KEY_RAW_ID, bahanMentah.getId()));
    }

    @Override
    public void onFilter(FiltersBahanMentah filtersBahanMentah) {
        Query query = mFirestore.collection(BahanMentah.COLLECTION);

        if (filtersBahanMentah.hasSortBy()) {
            query = query.orderBy(filtersBahanMentah.getSortBy(), filtersBahanMentah.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;
        mAdapter.setQuery(mQuery);

        mViewModel.setmFiltersBahanMentah(filtersBahanMentah);
    }

    @Override
    public void onSearchListener(String query) {
        if (!TextUtils.isEmpty(query)) {
            mQuery = mFirestore.collection(BahanMentah.COLLECTION)
                    .orderBy(BahanMentah.FIELD_BAHAN)
                    .startAt(query)
                    .endAt(query+'\uf8ff');
            mAdapter.setQuery(mQuery);
        } else {
            snackbarUtils.snackBarLong("Field is required");
        }
    }

    @Override
    public void onResetSearchListener(FiltersBahanMentah filtersBahanMentah) {
        Query query = mFirestore.collection(BahanMentah.COLLECTION);

        if (filtersBahanMentah.hasSortBy()) {
            query = query.orderBy(filtersBahanMentah.getSortBy(), filtersBahanMentah.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;
        mAdapter.setQuery(mQuery);

        mViewModel.setmFiltersBahanMentah(filtersBahanMentah);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                mFilterBahanMentah.show(getSupportFragmentManager(), DialogFilterBahanMentah.TAG);
                break;
            case R.id.menu_search:
                mDialogSearch.show(getSupportFragmentManager(), DialogSearchBahanMentah.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
