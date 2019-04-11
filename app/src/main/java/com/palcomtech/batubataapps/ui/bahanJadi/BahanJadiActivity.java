package com.palcomtech.batubataapps.ui.bahanJadi;

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
import com.palcomtech.batubataapps.adapter.BahanJadiAdapter;
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BahanJadiActivity extends AppCompatActivity implements
        BahanJadiAdapter.OnBahanJadiSelectedListener, DialogFilterBahanJadi.FilterListener,
        DialogSearchBahanJadi.SearchListener {

    private static final String TAG = BahanJadiActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvBahanJadi) RecyclerView rvJadi;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;

    private BahanJadiAdapter mAdapter;
    private BahanJadiViewModel mViewModel;
    private DialogFilterBahanJadi mFilterBahanJadi;
    private DialogSearchBahanJadi mDialogSearch;

    private final int LIMIT = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_bahan_jadi);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        mViewModel = ViewModelProviders.of(this).get(BahanJadiViewModel.class);

        FirebaseFirestore.setLoggingEnabled(true);

        toolbar.setTitle(getString(R.string.title_bahan_jadi));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFilterBahanJadi = new DialogFilterBahanJadi();
        mDialogSearch = new DialogSearchBahanJadi();

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
        mQuery = mFirestore.collection(BahanJadi.COLLECTION)
                .orderBy(BahanJadi.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }
        barUtils.show();
        mAdapter = new BahanJadiAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackbarShort("Error: check logs for info" + e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvJadi.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    rvJadi.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                barUtils.hide();
            }
        };
        rvJadi.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration item = new DividerItemDecoration(rvJadi.getContext(), RecyclerView.VERTICAL);
        rvJadi.addItemDecoration(item);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(500);
        itemAnimator.setRemoveDuration(500);
        rvJadi.setItemAnimator(itemAnimator);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            private final Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_white_24dp);
            private final Drawable editIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_edit_black_24dp);
            private final ColorDrawable backgroundDelete = new ColorDrawable(Color.parseColor("#f44336"));
            private final ColorDrawable backgroundEdit = new ColorDrawable(Color.parseColor("#2bc657"));
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                if (i == ItemTouchHelper.LEFT) {
                    mAdapter.deleteItem(viewHolder.getAdapterPosition());
                    snackbarUtils.snackBarLong(getString(R.string.snack_bar_success_goods_2));
                }
                if (i == ItemTouchHelper.RIGHT) {
                    mAdapter.editItem(viewHolder.getAdapterPosition());
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
                        backgroundEdit.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dXSwipe, itemView.getBottom());
                        backgroundEdit.draw(c);
                        // draw icon
                        int top = (itemView.getTop() + itemView.getBottom() - editIcon.getIntrinsicHeight()) / 2;
                        int left = itemView.getLeft() + 48;
                        editIcon.setBounds(left, top, left + editIcon.getIntrinsicWidth(), top + editIcon.getIntrinsicHeight());
                        editIcon.draw(c);
                    } else {
                        // draw background
                        backgroundDelete.setBounds(itemView.getRight() + dXSwipe, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        backgroundDelete.draw(c);
                        // draw icon
                        int top = (itemView.getTop() + itemView.getBottom() - deleteIcon.getIntrinsicHeight()) / 2;
                        int right = itemView.getRight() - 48;
                        deleteIcon.setBounds(right - deleteIcon.getIntrinsicWidth(), top, right, top + deleteIcon.getIntrinsicHeight());
                        deleteIcon.draw(c);
                    }
                }
            }
        }).attachToRecyclerView(rvJadi);
        rvJadi.setAdapter(mAdapter);
    }

    @OnClick(R.id.fab) void addJadi() {
        startActivityForResult(new Intent(this, AddEditJadiActivity.class)
                .putExtra(AddEditJadiActivity.KEY_JADI_ID, AddEditJadiActivity.ADD_DATA),
                AddEditJadiActivity.REQUEST_ADD);
    }

    @Override
    protected void onStart() {
        super.onStart();

        onFilter(mViewModel.getmFiltersBahanJadi());

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
        if (requestCode == AddEditJadiActivity.REQUEST_ADD) {
            if (resultCode == AddEditJadiActivity.RESULT_ADD_SUCCESS) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_success_goods_0), "OK");
            } else if (resultCode == AddEditJadiActivity.RESULT_ADD_FAILED && data != null) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_error_goods,
                        data.getStringExtra("ERROR")), "DISMISS");
            }
        } else if (requestCode == AddEditJadiActivity.REQUEST_UPDATE) {
            if (resultCode == AddEditJadiActivity.RESULT_UPDATE_SUCCESS) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_success_goods_1), "OK");
            } else if (resultCode == AddEditJadiActivity.RESULT_UPDATE_FAILED) {
                snackbarUtils.snackBarInfinite(getString(R.string.snack_bar_error_goods,
                        data.getStringExtra("ERROR")), "DISMISS");
            }
        }
    }

    @Override
    public void onBahanJadiSelected(DocumentSnapshot snapshot) {
        startActivityForResult(new Intent(this, AddEditJadiActivity.class)
                        .putExtra(AddEditJadiActivity.KEY_JADI_ID, snapshot.getId()),
                AddEditJadiActivity.REQUEST_UPDATE);
    }

    @Override
    public void onFilter(FiltersBahanJadi filtersBahanJadi) {
        Query query = mFirestore.collection(BahanJadi.COLLECTION);

        if (filtersBahanJadi.hasSortBy()) {
            query = query.orderBy(filtersBahanJadi.getSortBy(), filtersBahanJadi.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;
        mAdapter.setQuery(mQuery);

        mViewModel.setmFiltersBahanJadi(filtersBahanJadi);
    }

    @Override
    public void onSearchListener(String query) {
        if (!TextUtils.isEmpty(query)) {
            mQuery = mFirestore.collection(BahanJadi.COLLECTION)
                    .orderBy(BahanJadi.FIELD_BAHAN)
                    .startAt(query)
                    .endAt(query+'\uf8ff');
            mAdapter.setQuery(mQuery);
        } else {
            snackbarUtils.snackBarLong("Field is required");
        }
    }

    @Override
    public void onResetSearchListener(FiltersBahanJadi filtersBahanJadi) {
        Query query = mFirestore.collection(BahanJadi.COLLECTION);

        if (filtersBahanJadi.hasSortBy()) {
            query = query.orderBy(filtersBahanJadi.getSortBy(), filtersBahanJadi.getSortDirection());
        }

        query = query.limit(LIMIT);

        mQuery = query;
        mAdapter.setQuery(mQuery);

        mViewModel.setmFiltersBahanJadi(filtersBahanJadi);
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
                mFilterBahanJadi.show(getSupportFragmentManager(), DialogFilterBahanJadi.TAG);
                break;
            case R.id.menu_search:
                mDialogSearch.show(getSupportFragmentManager(), DialogSearchBahanJadi.TAG);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
