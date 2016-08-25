package com.ybg.rp.vm.activity.shopping;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.ybg.rp.vm.R;

public class PayFailedActivity extends Activity {

    private TextView tv_tint;
    private TextView tv_time;

    private int time = 10;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_failed);

        tv_time = (TextView) findViewById(R.id.payFailed_tv_time);
        tv_tint = (TextView) findViewById(R.id.payFailed_tv_tint);

        mHandler.postDelayed(runnable, 1000);
    }

    /**
     * 倒计时
     */
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            if (time == 0) {
                finish();
            } else {
                tv_time.setText(time + "S返回主页");
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler = null;
        tv_time = null;
        //System.gc();
    }

}
