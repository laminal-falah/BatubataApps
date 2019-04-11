package com.palcomtech.batubataapps.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.palcomtech.batubataapps.R;

public final class ProgressBarUtils {

    private final RelativeLayout rl;
    private final ProgressBar mProgressBar;
    private final ViewGroup layout;

    public ProgressBarUtils(Context context) {
        layout = (ViewGroup) ((Activity) context).findViewById(android.R.id.content).getRootView();

        ThreeBounce threeBounce = new ThreeBounce();
        threeBounce.setColor(context.getResources().getColor(R.color.colorAccent));

        mProgressBar = new ProgressBar(context);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setClickable(false);
        mProgressBar.setIndeterminateDrawable(threeBounce);

        RelativeLayout.LayoutParams params = new
                RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        rl = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rl.setBackgroundColor(context.getColor(R.color.colorDark));
        } else {
            rl.setBackgroundColor(Color.parseColor("#A3272925"));
        }
        rl.setGravity(Gravity.CENTER);
        rl.addView(mProgressBar);

        layout.addView(rl, params);
        hide();
    }

    public void show() {
        rl.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void hide() {
        rl.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }
}
