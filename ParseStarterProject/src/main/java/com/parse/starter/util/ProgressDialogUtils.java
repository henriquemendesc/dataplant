package com.parse.starter.util;

import android.app.ProgressDialog;
import android.content.Context;

import com.parse.starter.R;

/**
 * Created by Bob on 23/11/2017.
 */

public class ProgressDialogUtils {
    private ProgressDialog dialog;
    public void startProgress(Context context){
        dialog = ProgressDialog.show(context, context.getString(R.string.app_name),context.getString(R.string.loading), false, true);
        dialog.setCancelable(false);
    }
    public void endProgress(Context context){
        dialog.dismiss();
    }
}
