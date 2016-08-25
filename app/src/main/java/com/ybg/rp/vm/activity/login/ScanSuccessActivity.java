package com.ybg.rp.vm.activity.login;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ybg.rp.vm.R;

/**
 * 扫描成功
 */
public class ScanSuccessActivity extends Activity {

    private TextView tv_back;
    public static ScanSuccessActivity scanSuccessActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_layout);

        scanSuccessActivity = this;

        tv_back = (TextView) findViewById(R.id.scanSuccess_tv_back);


        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
