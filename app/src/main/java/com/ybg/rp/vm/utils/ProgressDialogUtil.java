package com.ybg.rp.vm.utils;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by yangbagang on 16/9/1.
 */
public class ProgressDialogUtil {

    /**原生*/
    private static ProgressDialog baseDialog;

    /**原生开始*/
    public static void showDialog(Context mContext, String msgName) {

        if (baseDialog == null) {
            baseDialog = new ProgressDialog(mContext);
            baseDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            baseDialog.setMessage(msgName);
            baseDialog.setCanceledOnTouchOutside(false);
            baseDialog.show();
        }


    }
    /**原生取消*/
    public static void closeDialog() {
        if (null != baseDialog) {
            baseDialog.cancel();
        }
        baseDialog = null;
    }

}
