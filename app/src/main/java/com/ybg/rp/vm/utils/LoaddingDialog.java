package com.ybg.rp.vm.utils;

import android.app.DialogFragment;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.views.PowerImageView;

/**
 * Created by yangbagang on 16/8/20.
 */
public class LoaddingDialog extends DialogFragment {

    private PowerImageView gif_iv_erweima;

    public static LoaddingDialog newInstance(int theme) {
        LoaddingDialog dialogFragment = new LoaddingDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("theme", theme);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = getArguments().getInt("theme", 0);
        setStyle(DialogFragment.STYLE_NO_TITLE, theme);//设置样式
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//标题
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        View view = inflater.inflate(R.layout.dialog_loading, container);
        gif_iv_erweima = (PowerImageView) view.findViewById(R.id.loading);
        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.5); // 高度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.5);
//        lp.width = d.widthPixels; // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * 清空图片的内存
         */
        if (gif_iv_erweima!=null){
            Drawable d = gif_iv_erweima.getDrawable();
            if (d != null && d instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) d).getBitmap();
                bmp.recycle();
                bmp = null;
            }
            gif_iv_erweima.setImageBitmap(null);
            if (d != null) {
                d.setCallback(null);
            }
        }
    }

}
