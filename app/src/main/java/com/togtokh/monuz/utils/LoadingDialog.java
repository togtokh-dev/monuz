package com.togtokh.monuz.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.togtokh.monuz.R;

public class LoadingDialog {
    Context context;
    Dialog dialog;

    public LoadingDialog(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.loading_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCanceledOnTouchOutside(false);
    }

    public void animate(boolean bool) {
        if(bool) {
            dialog.show();
        } else {
            dialog.dismiss();
        }
    }
}
