package com.palcomtech.batubataapps.ui.dashboard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.adapter.MenuAdapter;
import com.palcomtech.batubataapps.model.MenuDashboard;
import com.palcomtech.batubataapps.ui.auth.LoginActivity;
import com.palcomtech.batubataapps.ui.bahanJadi.BahanJadiActivity;
import com.palcomtech.batubataapps.ui.bahanMentah.BahanMentahActivity;
import com.palcomtech.batubataapps.ui.penjualan.PenjualanActivity;
import com.palcomtech.batubataapps.ui.produk.ProdukActivity;
import com.palcomtech.batubataapps.ui.profil.ProfileActivity;
import com.palcomtech.batubataapps.utils.LocaleUtils;
import com.palcomtech.batubataapps.utils.ProgressBarUtils;
import com.palcomtech.batubataapps.utils.SharedManager;
import com.palcomtech.batubataapps.utils.SnackbarUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DashboardActivity extends AppCompatActivity implements MenuAdapter.OnMenuSelectedListener {

    private static final String TAG = DashboardActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.rvMenu) RecyclerView rvMenu;

    private String[] titleMenu;
    private TypedArray iconMenu;

    private FirebaseAuth mAuth;
    private ArrayList<MenuDashboard> menus;
    private MenuAdapter menuAdapter;
    private ProgressBarUtils barUtils;
    private SnackbarUtils snackbarUtils;
    private Drawable drawable;

    private static long back_pressed;

    private SharedManager mSharedManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        mSharedManager = new SharedManager(this);

        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        drawable = getResources().getDrawable(R.drawable.ic_bricks_icon);
        drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        toolbar.setTitleMarginStart(72);
        toolbar.setLogo(drawable);

        barUtils = new ProgressBarUtils(this);
        snackbarUtils = new SnackbarUtils(this);

        initAuth();

        initMenu();
    }

    @Override
    public void onMenuSelected(int i) {
        if (i == 0) {
            startActivity(new Intent(this, BahanMentahActivity.class));
        } else if (i == 1) {
            startActivity(new Intent(this, BahanJadiActivity.class));
        } else if (i == 2) {
            startActivity(new Intent( this, PenjualanActivity.class));
        } else if (i == 3) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleUtils.onAttach(base));
    }

    @Override
    public void onBackPressed() {
        if (back_pressed + 3000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.msg_exit), Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }

    private void initAuth() {
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
            finish();
        }
    }

    private void initMenu() {
        barUtils.show();
        titleMenu = getResources().getStringArray(R.array.menu_dashboard);
        iconMenu = getResources().obtainTypedArray(R.array.icon_menu);
        @SuppressLint("ResourceType") String[][] listMenu = {
                {String.valueOf(iconMenu.getResourceId(0,-1)), titleMenu[0]},
                {String.valueOf(iconMenu.getResourceId(1,-1)), titleMenu[1]},
                //{String.valueOf(iconMenu.getResourceId(2,-1)), titleMenu[2]},
                {String.valueOf(iconMenu.getResourceId(3,-1)), titleMenu[3]},
                //{String.valueOf(iconMenu.getResourceId(4,-1)), titleMenu[4]},
                {String.valueOf(iconMenu.getResourceId(5,-1)), titleMenu[5]},
        };

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 2);
        rvMenu.setLayoutManager(layoutManager);
        rvMenu.setItemAnimator(new DefaultItemAnimator());
        rvMenu.setHasFixedSize(true);

        menus = new ArrayList<>();

        for (String[] aListMenu : listMenu) {
            menus.add(new MenuDashboard(Integer.decode(aListMenu[0]), aListMenu[1]));
        }

        menuAdapter = new MenuAdapter(menus, this);
        rvMenu.setAdapter(menuAdapter);
        barUtils.hide();

    }

    @OnClick(R.id.fab_shop) void shopping() {
        startActivity(new Intent(this, ProdukActivity.class));
    }
}
