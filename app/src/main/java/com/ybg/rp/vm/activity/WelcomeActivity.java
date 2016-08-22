package com.ybg.rp.vm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        String vmCode = preferences.getVMCode();
        String clientId = PushManager.getInstance().getClientid(WelcomeActivity.this);
        String url = AppConstant.HOST + "vendMachineInfo/updateClientIdByVmCode";
        RequestParams params = new RequestParams(url);
        params.addBodyParameter("vmCode", vmCode);
        params.addBodyParameter("clientId", clientId);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String s) {
                try {
                    JSONObject json = new JSONObject(s);
                    String success = json.getString("success");
                    String machineId = json.getString("machineId");
                    String msg = json.getString("msg");

                    if ("true".equals(success)) {
                        preferences.setVmId(machineId);
                        startAnima();
                    } else {
                        preferences.setVmCode("");
                        Toast.makeText(WelcomeActivity.this, "更新失败,,请稍后重试。" + msg, Toast
                                .LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Toast.makeText(WelcomeActivity.this, "更新失败,请稍后重试。" + e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                            .show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {
                LogUtil.i("#WelcomeActivity:", throwable.getMessage());
                Toast.makeText(WelcomeActivity.this, "获取数据异常,请稍后重试。", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
}
