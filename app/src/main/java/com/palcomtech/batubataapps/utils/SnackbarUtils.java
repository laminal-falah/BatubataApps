package com.palcomtech.batubataapps.utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.palcomtech.batubataapps.MyApp;

public final class SnackbarUtils {

    private static Snackbar snackbar;
    private final Context mContext;
    private View view = null;
    private ViewGroup viewGroup;

    public SnackbarUtils(Context mContext) {
        this.mContext = mContext;
    }

    private View getRootViewCustom() {
        if (mContext instanceof Activity) {
            viewGroup = ((Activity) mContext).findViewById(android.R.id.content);
        } else {
            viewGroup = MyApp.appActivity.findViewById(android.R.id.content);
        }
        if (viewGroup != null) {
            view = viewGroup.getChildAt(0);
        }
        if (view == null) {
            view = MyApp.appActivity.getWindow().getDecorView().getRootView();
        }
        return view;
    }

    private void addInterceptLayoutToViewGroup(Snackbar snackbar) {
        View contentView = MyApp.appActivity.findViewById(android.R.id.content);
        ViewGroup vg = (ViewGroup) ((ViewGroup) contentView).getChildAt(0);
        if (!(vg.getChildAt(0) instanceof InterceptTouchEventLayout)) {
            InterceptTouchEventLayout interceptLayout = new InterceptTouchEventLayout(MyApp.appActivity.getApplicationContext());
            interceptLayout.setSnackbar(snackbar);
            for (int i = 0; i < vg.getChildCount(); i++) {
                View view = vg.getChildAt(i);
                vg.removeView(view);
                interceptLayout.addView(view);
            }
            vg.addView(interceptLayout, 0);
        } else {
            InterceptTouchEventLayout interceptLayout =
                    (InterceptTouchEventLayout) vg.getChildAt(0);
            interceptLayout.setSnackbar(snackbar);
        }
    }

    public void snackbarShort(String message) {
        snackbar = Snackbar.make(getRootViewCustom(), message, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    public void snackBarLong(String message) {
        snackbar = Snackbar.make(getRootViewCustom(), message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void snackBarInfinite(String message) {
        snackbar = Snackbar.make(getRootViewCustom(), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(60*1000);
        snackbar.show();
        addInterceptLayoutToViewGroup(snackbar);
    }

    public void snackBarInfinite(String message, String action) {
        snackbar = Snackbar.make(getRootViewCustom(), message, Snackbar.LENGTH_INDEFINITE);
        snackbar.setDuration(60*1000);
        snackbar.setAction(action, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
        addInterceptLayoutToViewGroup(snackbar);
    }

    class InterceptTouchEventLayout extends FrameLayout {

        Snackbar mSnackbar;

        public InterceptTouchEventLayout(Context context) {
            super(context);
            setLayoutParams(new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.MATCH_PARENT));
        }

        public void setSnackbar(Snackbar snackbar) {
            mSnackbar = snackbar;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            int action = ev.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    if (mSnackbar != null && mSnackbar.isShown()) {
                        mSnackbar.dismiss();
                    }
                    break;
            }
            return super.onInterceptTouchEvent(ev);
        }
    }
    /*
    private static void snack(HashMap<String, View.OnClickListener> actions, int priority, String message, Context context) {
        if(MyApp.appActivity != null){
            snackbar = Snackbar.make(getRootViewCustom(), message, Snackbar.LENGTH_LONG);
            if (actions != null) {
                Iterator iterator = actions.entrySet().iterator();
                snackbar.setDuration(Snackbar.LENGTH_LONG);
                while (iterator.hasNext()) {
                    Map.Entry pair = (Map.Entry) iterator.next();
                    snackbar.setAction((String) pair.getKey(), (View.OnClickListener) pair.getValue());
                    iterator.remove();
                }
            }
            switch (priority) {
                case 0:
                    snackbar.getView().setBackgroundColor(Color.BLACK);
                    break;
                case 1:
                    snackbar.getView().setBackgroundColor(Color.parseColor("#66ccff"));
                    break;
                case 2:
                    snackbar.getView().setBackgroundColor(Color.parseColor("#0dc113"));
                    break;
            }
            snackbar.show();
        }
    }
    */
}
