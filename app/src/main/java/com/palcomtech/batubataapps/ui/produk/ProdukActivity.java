package com.palcomtech.batubataapps.ui.produk;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.ProdukAdapter;
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.model.Cart;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.ui.bahanJadi.BahanJadiViewModel;
import com.palcomtech.batubataapps.ui.bahanJadi.DialogFilterBahanJadi;
import com.palcomtech.batubataapps.ui.bahanJadi.DialogSearchBahanJadi;
import com.palcomtech.batubataapps.ui.bahanJadi.FiltersBahanJadi;
import com.palcomtech.batubataapps.utils.CountDrawable;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProdukActivity extends AppCompatActivity implements ProdukAdapter.OnProdukSelectedListener,
        DialogFilterBahanJadi.FilterListener, DialogSearchBahanJadi.SearchListener, DialogProduk.AddCartListener {

    private static final String TAG = ProdukActivity.class.getSimpleName();

    public static final String DELETE_CART = "delete_cart";

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvProduk) RecyclerView rvProduk;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private DocumentReference colRef;
    private Query mQuery;

    private ProdukAdapter mAdapter;
    private BahanJadiViewModel mViewModel;
    private DialogFilterBahanJadi mFilterProduk;
    private DialogSearchBahanJadi mDialogSearch;
    private DialogProduk mDialogProduk;

    private final int LIMIT = 500;
    private CountDrawable badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_produk);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        mViewModel = ViewModelProviders.of(this).get(BahanJadiViewModel.class);

        FirebaseFirestore.setLoggingEnabled(true);

        toolbar.setTitle(getString(R.string.title_produk));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mFilterProduk = new DialogFilterBahanJadi();
        mDialogSearch = new DialogSearchBahanJadi();
        mDialogProduk = new DialogProduk();

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
                .whereGreaterThan(BahanJadi.FIELD_STOCK, 1)
                .orderBy(BahanJadi.FIELD_STOCK)
                .orderBy(BahanJadi.TIMESTAMPS, Query.Direction.DESCENDING)
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }
        barUtils.show();
        mAdapter = new ProdukAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackbarShort("Error: check logs for info" + e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                if (getItemCount() == 0) {
                    rvProduk.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                } else {
                    rvProduk.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                }
                initBadgeCart();
                barUtils.hide();
            }
        };
        rvProduk.setLayoutManager(new GridLayoutManager(this, 3));
        rvProduk.setItemAnimator(new DefaultItemAnimator());
        rvProduk.setAdapter(mAdapter);
    }

    private void initBadgeCart() {
        mFirestore.collection(Cart.COLLECTION + "_" + mAuth.getCurrentUser().getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            badge.setCount(String.valueOf(task.getResult().size()));
                        } else {
                            badge.setCount(String.valueOf(task.getResult().size()));
                        }
                    }
                });
    }

    private void setCount(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.menu_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(this);
        }
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
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
        if (requestCode == CartActivity.REQUEST_CART) {
            if (resultCode == CartActivity.RESULT_CART) {
                snackbarUtils.snackBarLong(getString(R.string.snack_bar_checkout_0));
            }
        }
    }

    @Override
    public void onProdukSelected(DocumentSnapshot snapshot) {
        Bundle args = new Bundle();
        args.putString(DialogProduk.UID, snapshot.getId());
        args.putString(DialogProduk.NAME_PRODUCT, snapshot.getString(BahanJadi.FIELD_BAHAN));
        args.putDouble(DialogProduk.STOCK_PRODUCT, snapshot.getDouble(BahanJadi.FIELD_STOCK));
        args.putDouble(DialogProduk.PRICE_PRODUCT, snapshot.getDouble(BahanJadi.FIELD_PRICE));
        mDialogProduk.setArguments(args);
        mDialogProduk.show(getSupportFragmentManager(), DialogProduk.TAG);
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
    public void onAddCartListener(Cart cart) {
        barUtils.show();
        Map<Object, Object> krjng = new HashMap<>();
        krjng.put(Cart.FIELD_ID, cart.getIdBahan());
        krjng.put(Cart.FIELD_NAMA, cart.getNmBahan());
        krjng.put(Cart.FIELD_JMLH, cart.getJlBahan());
        krjng.put(Cart.FIELD_HARGA, cart.getHrgBahan());
        krjng.put(Cart.FIELD_SUBTOTAL, cart.getSubTotal());
        colRef = mFirestore.collection(Cart.COLLECTION + "_" + mAuth.getCurrentUser().getEmail()).document(cart.getIdBahan());
        colRef.set(krjng)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        snackbarUtils.snackBarLong(getString(R.string.snack_bar_success_cart_0));
                        barUtils.hide();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        snackbarUtils.snackBarLong(e.getMessage());
                        barUtils.hide();
                    }
                });
        initBadgeCart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop, menu);
        setCount(menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                mFilterProduk.show(getSupportFragmentManager(), DialogFilterBahanJadi.TAG);
                break;
            case R.id.menu_search:
                mDialogSearch.show(getSupportFragmentManager(), DialogSearchBahanJadi.TAG);
                break;
            case R.id.menu_cart:
                startActivityForResult(new Intent(this, CartActivity.class), CartActivity.REQUEST_CART);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
