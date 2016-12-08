package com.ybg.rp.vm.activity.login;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.ybg.rp.vm.R;
import com.ybg.rp.vm.utils.QRUtil;
import com.ybg.rp.vmbase.preference.VMPreferences;

import org.apache.log4j.Logger;

/**
 * Created by yangbagang on 16/8/20.
 */
public class LoginActivity extends Activity {

    private ImageView iv_code;  //二维码
    private TextView tv_sn;     //机器MAC
    private Bitmap bitmap;

    public static LoginActivity loginActivity;

    private Logger logger = Logger.getLogger(LoginActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        loginActivity = this;

        iv_code = (ImageView) findViewById(R.id.login_iv_code);
        tv_sn = (TextView) findViewById(R.id.login_tv_sn);

        logger.debug("begin to create code ");
        createCode2();
    }

    /**
     * 生成二维码
     */
    private void createCode2() {
        String vmCode = VMPreferences.getInstance().getVMCode();
        logger.debug("vmCode=|" + vmCode + "|");
        try {
            if (vmCode != null && !"".equals(vmCode)) {
                tv_sn.setText("设备编号 :    " + vmCode);
                bitmap = QRUtil.create2DCode(vmCode);
                iv_code.setImageBitmap(bitmap);
            } else {
                Toast.makeText(this, "生成登录二维码故障", Toast.LENGTH_LONG).show();
            }
        } catch (WriterException e) {
            logger.debug("生成登录二维码失败", e);
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

    public void closeWin(View view) {
        finish();
    }
}
