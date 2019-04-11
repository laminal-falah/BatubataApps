package com.palcomtech.batubataapps.ui.bahanMentah;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

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
import com.palcomtech.batubataapps.model.BahanMentah;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddEditMentahActivity extends AppCompatActivity {

    private static final String TAG = AddEditMentahActivity.class.getSimpleName();

    public static final String KEY_MENTAH_ID = "key_mentah_id";

    public static final String ADD_DATA = "add_data";

    public static final int REQUEST_ADD = 100;
    public static final int RESULT_ADD_SUCCESS = 101;
    public static final int RESULT_ADD_FAILED = 102;

    public static final int REQUEST_UPDATE = 200;
    public static final int RESULT_UPDATE_SUCCESS = 201;
    public static final int RESULT_UPDATE_FAILED = 202;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tl_name) TextInputLayout tl_name;
    @BindView(R.id.edt_name) TextInputEditText edt_name;
    @BindView(R.id.tl_stock) TextInputLayout tl_stock;
    @BindView(R.id.edt_stock) TextInputEditText edt_stock;
    @BindView(R.id.spinner_unit) Spinner mSpinnerUnit;
    @BindView(R.id.btnSaveMentah) Button btnSave;

    private TextView tvUnit;

    private String rawId;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private CollectionReference colBahanMentah;
    private DocumentReference refBahanMentah;

    private ProgressBarUtils barUtils;

    private boolean isEdit;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_add_update_mentah);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        intent = new Intent();

        if (getIntent().getExtras().getString(KEY_MENTAH_ID).equals(ADD_DATA)) {
            isEdit = false;
            toolbar.setTitle(getString(R.string.title_add_raw));
        } else {
            barUtils.show();
            isEdit = true;
            toolbar.setTitle(getString(R.string.title_edit_raw));
            rawId = getIntent().getExtras().getString(KEY_MENTAH_ID);
            btnSave.setText(getString(R.string.btn_update_raw));
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

        initFirestore();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUnit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
            refBahanMentah = mFirestore.collection(BahanMentah.COLLECTION).document(rawId);
            refBahanMentah.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot snapshot) {
                    if (snapshot.exists()) {
                        BahanMentah bahanMentah = snapshot.toObject(BahanMentah.class);
                        setBahanMentah(bahanMentah);
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

    private void setBahanMentah(BahanMentah bahanMentah) {
        edt_name.setText(bahanMentah.getNama_bahan());
        edt_stock.setText(String.valueOf(bahanMentah.getStok()));
        int selectedUnit = 0;
        String[] unit = getResources().getStringArray(R.array.units);
        for (int i = 0; i < unit.length; i++) {
            if (bahanMentah.getSatuan().equals(unit[i])) {
                selectedUnit = i;
            }
        }
        mSpinnerUnit.setSelection(selectedUnit);
    }

    private void getUnit() {
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(R.array.units)) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        mSpinnerUnit.setAdapter(unitAdapter);
    }

    private boolean validate() {
        boolean valid = true;

        tvUnit = (TextView) mSpinnerUnit.getSelectedView();

        if (TextUtils.isEmpty(setDataCharNya(tl_name.getEditText().getText().toString()))) {
            valid = false;
            tl_name.setError(getString(R.string.error_name_raw));
        } else {
            tl_name.setError(null);
        }

        if (TextUtils.isEmpty(setDataCharNya(tl_stock.getEditText().getText().toString()))) {
            valid = false;
            tl_stock.setError(getString(R.string.error_stock_raw_0));
        } else if (Double.parseDouble(tl_stock.getEditText().getText().toString()) == 0) {
            valid = false;
            tl_stock.setError(getString(R.string.error_stock_raw_1));
        } else {
            tl_stock.setError(null);
        }

        if (setDataCharNya(mSpinnerUnit.getSelectedItem().toString()).equals(getString(R.string.value_any_unit))) {
            valid = false;
            tvUnit.setError(getString(R.string.error_unit));
        } else {
            tvUnit.setError(null);
        }

        return valid;
    }

    @OnClick(R.id.btnSaveMentah) void save() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getWindow().getDecorView().getRootView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        if (!validate()) {
            return;
        }
        barUtils.show();

        Map<String, Object> mentah = new HashMap<>();
        mentah.put("nama_bahan", setDataObjectNya(tl_name.getEditText().getText().toString()));
        mentah.put("stok", setDataDouble(tl_stock.getEditText().getText().toString()));
        mentah.put("satuan", setDataObjectNya(mSpinnerUnit.getSelectedItem().toString()));
        mentah.put("timestamps", Timestamp.now());

        if (!isEdit) {
            colBahanMentah = mFirestore.collection(BahanMentah.COLLECTION);
            colBahanMentah
                    .add(mentah)
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
            refBahanMentah.update(mentah).addOnSuccessListener(new OnSuccessListener<Void>() {
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
        mSpinnerUnit.setSelection(0);
    }
}
