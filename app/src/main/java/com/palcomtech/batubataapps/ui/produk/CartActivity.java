package com.palcomtech.batubataapps.ui.produk;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.CartAdapter;
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.model.Cart;
import com.palcomtech.batubataapps.model.DetailPenjualan;
import com.palcomtech.batubataapps.model.Penjualan;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartActivity extends AppCompatActivity implements CartAdapter.OnCartListener,
        DialogCheckout.AddCheckoutListener {

    private static final String TAG = CartActivity.class.getSimpleName();

    public static final int REQUEST_CART = 23;
    public static final int RESULT_CART = 76;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvCart) RecyclerView rvCart;
    @BindView(R.id.viewEmpty) ViewGroup mEmptyView;
    @BindView(R.id.tvViewEmpty) TextView tvEmptyView;
    @BindView(R.id.tvTotalAll) TextView tvTotalAll;
    @BindView(R.id.btnCheckout) Button btnCheckout;

    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Query mQuery;
    private ListenerRegistration mCartRegistration;

    private Bundle args;
    private CartAdapter mAdapter;
    private DialogCheckout mDialogCheckout;

    private final int LIMIT = 500;

    private int total = 0;
    private int itemCount = 0;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        FirebaseFirestore.setLoggingEnabled(true);

        toolbar.setTitle(getString(R.string.title_cart));
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

        getTotalAll(String.valueOf(total));
        getCountBtn(String.valueOf(itemCount));

        mDialogCheckout = new DialogCheckout();

        args = new Bundle();
    }

    private void getTotalAll(String s) {
        tvTotalAll.setText(getString(R.string.total_harga,
                NumberFormat
                    .getNumberInstance(Locale.getDefault())
                    .format(Double.valueOf(s))
                )
        );
    }

    private void getCountBtn(String s) {
        if (s.equals("0")) {
            btnCheckout.setEnabled(false);
        } else {
            btnCheckout.setEnabled(true);
        }
        btnCheckout.setText(getString(R.string.btn_checkout, s));
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
        mQuery = mFirestore.collection(Cart.COLLECTION + "_" + mAuth.getCurrentUser().getEmail())
                .limit(LIMIT);
    }

    private void initRecyclerView() {
        if (mQuery == null) {
            Log.w(TAG,"No Query, not installizing Recycler View");
        }
        barUtils.show();
        mAdapter = new CartAdapter(mQuery, this) {
            @Override
            protected void onError(FirebaseFirestoreException e) {
                snackbarUtils.snackbarShort("Error: check logs for info" + e.getMessage());
            }

            @Override
            protected void onDataChanged() {
                itemCount = getItemCount();
                if (getItemCount() == 0) {
                    total = 0;
                    getCountBtn(String.valueOf(itemCount));
                    getTotalAll(String.valueOf(total));
                    rvCart.setVisibility(View.GONE);
                    mEmptyView.setVisibility(View.VISIBLE);
                    tvEmptyView.setText(getString(R.string.msg_data_cart));
                } else {
                    total = 0;
                    getCountBtn(String.valueOf(itemCount));
                    rvCart.setVisibility(View.VISIBLE);
                    mEmptyView.setVisibility(View.GONE);
                    rvCart.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < rvCart.getChildCount(); i++) {
                                if (rvCart.getChildAt(i) != null) {
                                    TextView text = rvCart.getChildAt(i).findViewById(R.id.tvTotalCart);
                                    String[] t = text.getText().toString().split(". ");
                                    if (t[1].contains(",")) {
                                        total = total + Integer.parseInt(t[1].replace(",",""));
                                    } else if (t[1].contains(".")) {
                                        total = total + Integer.parseInt(t[1].replace(".",""));
                                    } else {
                                        total = total + Integer.parseInt(t[1]);
                                    }
                                }
                            }
                            getTotalAll(String.valueOf(total));
                        }
                    },500);
                }
                barUtils.hide();
            }
        };
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration item = new DividerItemDecoration(rvCart.getContext(), RecyclerView.VERTICAL);
        rvCart.addItemDecoration(item);
        rvCart.setItemAnimator(new DefaultItemAnimator());
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            private final Drawable deleteIcon = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_delete_white_24dp);
            private final ColorDrawable backgroundDelete = new ColorDrawable(Color.parseColor("#f44336"));
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                mAdapter.deleteItem(viewHolder.getAdapterPosition());
                snackbarUtils.snackbarShort(getString(R.string.snack_bar_success_cart_2));
                total = 0;
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
                        backgroundDelete.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dXSwipe, itemView.getBottom());
                        backgroundDelete.draw(c);
                        // draw icon
                        int top = (itemView.getTop() + itemView.getBottom() - deleteIcon.getIntrinsicHeight()) / 2;
                        int left = itemView.getLeft() + 48;
                        deleteIcon.setBounds(left, top, left + deleteIcon.getIntrinsicWidth(), top + deleteIcon.getIntrinsicHeight());
                        deleteIcon.draw(c);
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
        }).attachToRecyclerView(rvCart);
        rvCart.setAdapter(mAdapter);
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

        //mCartRegistration = docRef.addSnapshotListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.stopListening();
        }
    }

    @Override
    public void onUpdateCartListener(DocumentSnapshot snapshot, Cart cart) {
        barUtils.show();
        Map<String, Object> krjng = new HashMap<>();
        krjng.put(Cart.FIELD_JMLH, cart.getJlBahan());
        krjng.put(Cart.FIELD_SUBTOTAL, cart.getSubTotal());
        mFirestore.collection(Cart.COLLECTION + "_" + mAuth.getCurrentUser().getEmail()).document(snapshot.getId())
                .update(krjng)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        total = 0;
                        snackbarUtils.snackBarLong(getString(R.string.snack_bar_success_cart_1));
                        barUtils.hide();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        total = 0;
                        snackbarUtils.snackBarLong(e.getMessage());
                        barUtils.hide();
                    }
                });
    }

    @OnClick(R.id.btnCheckout) void checkout() {
        args.putDouble(DialogCheckout.TOTAL, (double) total);
        mDialogCheckout.setArguments(args);
        mDialogCheckout.show(getSupportFragmentManager(), DialogCheckout.TAG);
    }

    @Override
    public void onAddCheckoutListener(Penjualan penjualan) {
        barUtils.show();
        Map<Object, Object> detail = new HashMap<>();
        detail.put(Penjualan.FIELD_NAME, penjualan.getNamePembeli());
        detail.put(Penjualan.FIELD_TOTAL, penjualan.getTotalPembelian());
        detail.put(Penjualan.FIELD_TGL, penjualan.getTglPembelian());
        mFirestore.collection(Penjualan.COLLECTION)
                .add(detail)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        cartToSell(documentReference.getId());
                    }
                });
    }

    private void cartToSell(final String s) {
        mCartRegistration = mFirestore.collection(Cart.COLLECTION + "_" + mAuth.getCurrentUser().getEmail())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        List<DetailPenjualan> penjualanList = new ArrayList<>();
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                            DetailPenjualan penjualan = snapshot.toObject(DetailPenjualan.class);
                            penjualanList.add(penjualan);
                        }
                        addDetailExm(s, penjualanList);
                    }
                });
    }

    private void addDetailExm(String id, List<DetailPenjualan> penjualans) {
        final Map<String, Object> detailPenjualan = new HashMap<>();
        List<String> idBahan = new ArrayList<>();
        for (DetailPenjualan penjualan : penjualans) {
            detailPenjualan.put(DetailPenjualan.FIELD_ID, penjualan.getIdBahan());
            detailPenjualan.put(DetailPenjualan.FIELD_NM, penjualan.getNmBahan());
            detailPenjualan.put(DetailPenjualan.FIELD_HRG, penjualan.getHrgBahan());
            detailPenjualan.put(DetailPenjualan.FIELD_JMLH, penjualan.getJlBahan());
            detailPenjualan.put(DetailPenjualan.FIELD_SUB, penjualan.getSubTotal());

            mFirestore.collection(Penjualan.COLLECTION)
                    .document(id)
                    .collection(DetailPenjualan.COLLECTION)
                    .add(detailPenjualan);

            final double jmlhItem = penjualan.getJlBahan();
            mFirestore.collection(BahanJadi.COLLECTION)
                    .document(penjualan.getIdBahan())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot snapshot) {
                            double stokLama = snapshot.getDouble(BahanJadi.FIELD_STOCK);
                            double jmlh = jmlhItem;
                            double nowStok = stokLama - jmlh;
                            Map<String, Object> jadi = new HashMap<>();
                            jadi.put(BahanJadi.FIELD_STOCK, nowStok);
                            mFirestore.collection(BahanJadi.COLLECTION).document(snapshot.getId()).update(jadi);
                        }
                    });
            count++;
            idBahan.add(penjualan.getIdBahan());
        }

        if (count == penjualans.size()) {
            if (mCartRegistration != null) {
                mCartRegistration.remove();
                mCartRegistration = null;
            }
            delete(idBahan);
        }
    }

    private void delete(List<String> id) {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        CollectionReference del = firebaseFirestore.collection(Cart.COLLECTION + "_" + mAuth.getCurrentUser().getEmail());
        for (int i = 0; i < id.size(); i++) {
            del.document(id.get(i)).delete();
        }
        total = 0;
        getTotalAll(String.valueOf(total));
        barUtils.hide();
        finishActivity(REQUEST_CART);
        setResult(RESULT_CART);
        finish();
    }
}
