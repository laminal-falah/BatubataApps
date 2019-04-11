package com.palcomtech.batubataapps.ui.bahanJadi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.BahanJadi;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEditJadiActivity extends AppCompatActivity {

    private static final String TAG = AddEditJadiActivity.class.getSimpleName();

    public static final String KEY_JADI_ID = "key_jadi_id";

    public static final String ADD_DATA = "add_data";

    public static final int REQUEST_ADD = 300;
    public static final int RESULT_ADD_SUCCESS = 301;
    public static final int RESULT_ADD_FAILED = 302;

    public static final int REQUEST_UPDATE = 400;
    public static final int RESULT_UPDATE_SUCCESS = 401;
    public static final int RESULT_UPDATE_FAILED = 402;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tl_name) TextInputLayout tl_name;
    @BindView(R.id.edt_name) TextInputEditText edt_name;
    @BindView(R.id.tl_stock) TextInputLayout tl_stock;
    @BindView(R.id.edt_stock) TextInputEditText edt_stock;
    @BindView(R.id.tl_price) TextInputLayout tl_price;
    @BindView(R.id.edt_price) TextInputEditText edt_price;
    @BindView(R.id.btnSaveJadi) Button btnSave;

    private String rawId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference colBahanJadi;
    private DocumentReference refBahanJadi;

    private ProgressBarUtils barUtils;

    private boolean isEdit;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_edit_jadi);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        intent = new Intent();

        if (getIntent().getExtras().getString(KEY_JADI_ID).equals(ADD_DATA)) {
            isEdit = false;
            toolbar.setTitle(getString(R.string.title_add_goods));
        } else {
            barUtils.show();
            isEdit = true;
            rawId = getIntent().getExtras().getString(KEY_JADI_ID);
            toolbar.setTitle(getString(R.string.title_edit_goods));
            btnSave.setText(getString(R.string.btn_update_goods));
        }

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

        if (isEdit) {
            refBahanJadi = mFirestore.collection(BahanJadi.COLLECTION).document(rawId);
            refBahanJadi.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {
                        BahanJadi bahanJadi = snapshot.toObject(BahanJadi.class);
                        setBahanJadi(bahanJadi);
                    } else {
                        intent.putExtra("ERROR", "Data not found");
                        setResult(RESULT_UPDATE_FAILED, intent);
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    intent.putExtra("ERROR", e.getMessage());
                    setResult(RESULT_UPDATE_FAILED, intent);
                    finish();
                }
            });
            barUtils.hide();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    private void setBahanJadi(BahanJadi bahanJadi) {
        edt_name.setText(bahanJadi.getNama_bahan());
        edt_stock.setText(String.valueOf(bahanJadi.getStok()));
        edt_price.setText(String.valueOf(bahanJadi.getHarga()));
    }

    private boolean validate() {
        boolean valid = true;

        if (TextUtils.isEmpty(setDataCharNya(tl_name.getEditText().getText().toString()))) {
            valid = false;
            tl_name.setError(getString(R.string.error_name_goods));
        } else {
            tl_name.setError(null);
        }

        if (TextUtils.isEmpty(setDataCharNya(tl_stock.getEditText().getText().toString()))) {
            valid = false;
            tl_stock.setError(getString(R.string.error_stock_goods_0));
        } else if (Double.parseDouble(tl_stock.getEditText().getText().toString()) == 0) {
            valid = false;
            tl_stock.setError(getString(R.string.error_stock_goods_1));
        } else {
            tl_stock.setError(null);
        }

        if (TextUtils.isEmpty(setDataCharNya(tl_price.getEditText().getText().toString()))) {
            valid = false;
            tl_price.setError(getString(R.string.error_harga_goods_0));
        } else if (Double.parseDouble(tl_price.getEditText().getText().toString()) == 0 ||
                Double.parseDouble(tl_price.getEditText().getText().toString()) < 50) {
            valid = false;
            tl_price.setError(getString(R.string.error_harga_goods_1));
        } else {
            tl_price.setError(null);
        }

        return valid;
    }

    @OnClick(R.id.btnSaveJadi) void save() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) {
            return;
        }
        barUtils.show();

        Map<String, Object> jadi = new HashMap<>();
        jadi.put("nama_bahan", setDataObjectNya(tl_name.getEditText().getText().toString()));
        jadi.put("stok", setDataDouble(tl_stock.getEditText().getText().toString()));
        jadi.put("harga", setDataDouble(tl_price.getEditText().getText().toString()));
        jadi.put("timestamps", Timestamp.now());

        if (!isEdit) {
            colBahanJadi = mFirestore.collection(BahanJadi.COLLECTION);
            colBahanJadi
                    .add(jadi)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Map<String, Object> uid = new HashMap<>();
                            uid.put("uid",documentReference.getId());
                            documentReference.set(uid, SetOptions.merge());
                            resetField();
                            barUtils.hide();
                            setResult(RESULT_ADD_SUCCESS);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            resetField();
                            barUtils.hide();
                            intent.putExtra("ERROR", e.getMessage());
                            setResult(RESULT_ADD_FAILED, intent);
                            finish();
                        }
                    });
        } else {
            refBahanJadi.update(jadi).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    resetField();
                    barUtils.hide();
                    setResult(RESULT_UPDATE_SUCCESS);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    resetField();
                    barUtils.hide();
                    intent.putExtra("ERROR", e.getMessage());
                    setResult(RESULT_UPDATE_FAILED, intent);
                    finish();
                }
            });
        }
    }

    private Double setDataDouble(String s) {
        return Double.parseDouble(s);
    }

    private CharSequence setDataCharNya(CharSequence sequence) {
        return sequence;
    }

    private Object setDataObjectNya(Object o) {
        return o;
    }

    private void resetField() {
        edt_name.setText(null);
        edt_stock.setText(null);
        edt_price.setText(null);
    }
}
