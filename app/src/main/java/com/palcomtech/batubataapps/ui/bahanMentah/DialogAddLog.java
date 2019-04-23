package com.palcomtech.batubataapps.ui.bahanMentah;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.palcomtech.batubataapps.R;
import com.palcomtech.batubataapps.model.LogBahanMentah;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DialogAddLog extends DialogFragment {

    public static final String TAG = DialogAddLog.class.getSimpleName();

    public static final String ID = "id";

    private View mView;

    private LogListener mLogListener;

    @BindView(R.id.tl_qty) TextInputLayout tl_qty;
    @BindView(R.id.edt_qty) TextInputEditText edt_qty;

    private String qty, id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.dialog_add_log, container, false);
        ButterKnife.bind(this, mView);
        Bundle bundle = getArguments();
        id = bundle.getString(ID);
        getDialog().setCanceledOnTouchOutside(false);
        edt_qty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEND) {
                    onSubmitClicked();
                }
                return false;
            }
        });
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LogListener) {
            mLogListener = (LogListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        edt_qty.requestFocus();
        WindowManager.LayoutParams p = getDialog().getWindow().getAttributes();
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        p.x = 200;
        getDialog().getWindow().setAttributes(p);
        getDialog().getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
    }

    private boolean validate() {
        boolean valid = true;

        qty = tl_qty.getEditText().getText().toString();

         if (TextUtils.isEmpty(qty)) {
            valid = false;
            tl_qty.setError(getString(R.string.error_qty_0));
        } else if (Double.parseDouble(qty) == 0) {
             valid = false;
             tl_qty.setError(getString(R.string.error_qty_1));
         } else {
            tl_qty.setError(null);
        }

        return valid;
    }

    @OnClick(R.id.btnSaveLog) void onSubmitClicked() {
        if (!validate()) {
            return;
        }
        qty = tl_qty.getEditText().getText().toString();

        LogBahanMentah mentah = new LogBahanMentah(id,Double.parseDouble(qty));

        if (mLogListener != null) {
            edt_qty.setText(null);
            mLogListener.onLogListener(mentah);
        }
        dismiss();
    }

    @OnClick(R.id.btnCancel) void onCancelClicked(View view) {
        dismiss();
    }

    public interface LogListener {
        void onLogListener(LogBahanMentah logBahanMentah);
    }
}
