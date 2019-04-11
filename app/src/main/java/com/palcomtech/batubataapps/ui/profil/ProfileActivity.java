package com.palcomtech.batubataapps.ui.profil;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.ProfileAdapter;
import com.palcomtech.batubataapps.helper.UserHelper;
import com.palcomtech.batubataapps.model.Profile;
import com.palcomtech.batubataapps.model.User;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SharedManager;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements ProfileAdapter.onProfileSelectedListener {

    private static final String TAG = ProfileActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvProfile) RecyclerView rvProfile;

    private String[] msg;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseFirestore db;

    private ArrayList<Profile> profiles;
    private ProfileAdapter profileAdapter;
    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;
    private SharedManager mSharedManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        initFirebase();
        initProfile();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSharedManager = new SharedManager(this);
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
    }

    private void initProfile() {
        barUtils.show();
        rvProfile.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        DividerItemDecoration item = new DividerItemDecoration(rvProfile.getContext(), RecyclerView.VERTICAL);
        rvProfile.addItemDecoration(item);
        rvProfile.setItemAnimator(new DefaultItemAnimator());
        rvProfile.setHasFixedSize(true);

        msg = getResources().getStringArray(R.array.menu_profile);
        profiles = new ArrayList<>();
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser != null) {
            boolean emailVerified = mUser.isEmailVerified();
            if (!emailVerified) {
                Toast.makeText(getApplicationContext(), "Get verification email", Toast.LENGTH_SHORT).show();
            }
            UserHelper.getUser(mUser.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User u = documentSnapshot.toObject(User.class);
                    profiles.add(new Profile(msg[0], u.getFullname()));
                    profiles.add(new Profile(msg[1], u.getEmail()));
                    profiles.add(new Profile(msg[2], u.getPhone()));
                    profileAdapter = new ProfileAdapter(profiles, ProfileActivity.this);
                    rvProfile.setAdapter(profileAdapter);
                    toolbar.setTitle(getResources().getString(R.string.title_profile, u.getFullname()));
                    barUtils.hide();
                }
            });
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onProfileSelected(int i) {
        snackbarUtils.snackbarShort(profiles.get(i).getBody());
    }

    @OnClick(R.id.fab) void logout() {
        mAuth.signOut();
        Toast.makeText(getApplicationContext(), getString(R.string.msg_logout), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }
}
