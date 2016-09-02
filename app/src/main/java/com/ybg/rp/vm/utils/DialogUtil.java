package com.ybg.rp.vm.utils;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.activity.WelcomeActivity;
import com.ybg.rp.vmbase.utils.LogUtil;

/**
 * Created by yangbagang on 16/8/20.
 */
public class DialogUtil {

    private static String mDialogTag = "加载中...";

    private static ProgressDialog progressDialog;

    public static void showLoading(Context mContext) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle(mDialogTag);
            progressDialog.setMessage("正在加载数据，请稍候。");
        }

        try {
            progressDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideLoading() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    /**
     * 显示USB设备列表
     *
     * @param mContext
     */
    public static void showListDeviceDialog(Context mContext) {
        Activity activity = (Activity) mContext;
        if (activity == null) {
            LogUtil.e("页面为NULL--不能显示DIALOG");
            return;
        }
        removeDialog(mContext);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        DevicesDialog dialogFragment = DevicesDialog.newInstance(R.style.Dialog_Fullscreen);
        ft.add(dialogFragment, mDialogTag).commitAllowingStateLoss();
    }


    /**
     * 重新启动应用!
     *
     * @param activity
     * @param msg
     */
    public static void onStartApp(final Activity activity, String msg) {
        new AlertDialog.Builder(activity).setMessage(msg)
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //启动程序
                       Intent intent = new Intent();
                       intent.setClass(activity, WelcomeActivity.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                       activity.startActivity(intent);
                   }
               })
               .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       //取消
                       removeDialog(activity);
                   }
               })
               .create().show();
    }


    /**
     * 描述：移除Fragment.
     */
    public static void removeDialog(Context mContext) {
        try {
            Activity activity = (Activity) mContext;
            if (activity == null) {
                LogUtil.e("页面为NULL--不能-关闭-DIALOG");
                return;
            }
            FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
            // 指定一个过渡动画
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
            Fragment prev = activity.getFragmentManager().findFragmentByTag(mDialogTag);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            ft.commit();
        } catch (Exception e) {
            // 可能有Activity已经被销毁的异常
			e.printStackTrace();
        }
    }

    /**
     * 显示一个一般的对话框（View内容）.
     *
     * @param view 对话框标题内容
     */
    public static AlertDialogFragment showAlertDialog(View view) {
        if (null == view) return null;
        FragmentActivity activity = (FragmentActivity) view.getContext();
        removeDialog(activity);
        AlertDialogFragment newFragment = AlertDialogFragment.newInstance(0, null, null, view, null);
        FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
        // 指定一个过渡动画
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        newFragment.show(ft, mDialogTag);
        ft.add(newFragment,mDialogTag).commitAllowingStateLoss();
        return newFragment;
    }

    /**
     * 描述：移除Fragment和View
     *
     * @param view
     */
    public static void removeDialog(View view) {
        if (null == view) return;
        removeDialog(view.getContext());
        removeSelfFromParent(view);
    }

    /**
     * 从父亲布局中移除自己
     *
     * @param v
     */
    private static void removeSelfFromParent(View v) {
        ViewParent parent = v.getParent();
        if (parent != null) {
            if (parent instanceof ViewGroup) {
                ((ViewGroup) parent).removeView(v);
            }
        }
    }
}
