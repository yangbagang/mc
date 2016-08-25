package com.ybg.rp.vm.activity.login;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ybg.rp.vm.R;
import com.ybg.rp.vm.utils.QRUtil;
import com.ybg.rp.vmbase.preference.VMPreferences;

/**
 * Created by yangbagang on 16/8/20.
 */
public class LoginActivity extends Activity {

    private ImageView iv_code;  //二维码
    private TextView tv_sn;     //机器MAC
    private Bitmap bitmap;

    public static LoginActivity loginActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        loginActivity= this;

        iv_code = (ImageView) findViewById(R.id.login_iv_code);
        tv_sn = (TextView) findViewById(R.id.login_tv_sn);

        createCode2();
    }

    /**
     * 生成二维码
     */
    private void createCode2() {
        String vmCode = VMPreferences.getInstance().getVMCode();
        try {
            if (vmCode != null && !"".equals(vmCode)) {
                tv_sn.setText("设备编号 :    " + vmCode);
                bitmap = QRUtil.create2DCode(vmCode);
                iv_code.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "生成登录二维码故障", Toast.LENGTH_LONG).show();
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null && !bitmap.isRecycled()) {
            iv_code.setImageBitmap(null);
            bitmap.recycle();
            bitmap = null;
        }
    }

}
