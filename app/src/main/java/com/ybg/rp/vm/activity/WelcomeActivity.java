package com.ybg.rp.vm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.igexin.sdk.PushManager;
import com.ybg.rp.vm.R;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * Created by yangbagang on 16/8/18.
 */
public class WelcomeActivity extends Activity {

    private VMPreferences preferences;
    private LinearLayout ll_welcome;
    private ImageView init_img;
    private EditText vm_code;
    private Button btn_update_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);

        XApplication app = (XApplication) getApplication();
        preferences = app.getPreference();

        ll_welcome = (LinearLayout) findViewById(R.id.init_ll_welcome);
        init_img = (ImageView) findViewById(R.id.init_img);
        vm_code = (EditText) findViewById(R.id.vm_code);
        btn_update_code = (Button) findViewById(R.id.btn_update_code);
        btn_update_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vmCode = vm_code.getText().toString();
                if (vmCode != null && !"".equals(vmCode)) {
                    preferences.setVmCode(vmCode);

                    updateClientId();
                }
            }
        });

        if (preferences.isFirstUse()) {
            init_img.setVisibility(View.GONE);
            vm_code.setVisibility(View.VISIBLE);
            btn_update_code.setVisibility(View.VISIBLE);
        } else {
            vm_code.setVisibility(View.GONE);
            btn_update_code.setVisibility(View.GONE);
            init_img.setVisibility(View.VISIBLE);
            updateClientId();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void startAnima() {
        //做动画初始化信息
        AlphaAnimation animation = new AlphaAnimation(0.3f, 1.0f);
        animation.setDuration(3000);
        ll_welcome.startAnimation(animation);
        animation.setAnimationListener(new InitAnimatiom(this));
    }

    private class InitAnimatiom implements Animation.AnimationListener {

        private Activity mActivity;

        public InitAnimatiom(Activity mActivity) {
            this.mActivity = mActivity;
        }

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            Intent intent = new Intent();
            intent.setClass(mActivity, MainActivity.class);
            mActivity.startActivity(intent);
            mActivity.finish();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }

    private void updateClientId() {
        new Thread() {
            @Override
            public void run() {
                String vmCode = preferences.getVMCode();
                String clientId = PushManager.getInstance().getClientid(WelcomeActivity.this);
                String url = AppConstant.HOST + "vendMachineInfo/updateClientIdByVmCode";
                RequestParams params = new RequestParams(url);
                params.addBodyParameter("vmCode", vmCode);
                params.addBodyParameter("clientId", clientId);
                boolean connected = false;
                while (!connected) {
                    try {
                        String result = x.http().postSync(params, String.class);
                        JSONObject json = new JSONObject(result);
                        String success = json.getString("success");
                        String machineId = json.getString("machineId");
                        String msg = json.getString("msg");

                        if ("true".equals(success)) {
                            preferences.setVmId(machineId);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    startAnima();
                                }
                            });
                            connected = true;
                            break;
                        } else {
                            preferences.setVmCode("");
                            Toast.makeText(WelcomeActivity.this, "更新失败,,正在重试。。。" + msg, Toast
                                    .LENGTH_SHORT).show();
                        }
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    //1秒后重试
                    SystemClock.sleep(2000);
                }
            }
        }.start();
    }
}
