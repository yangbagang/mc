package com.ybg.rp.vm.activity.shopping;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.activity.login.LoginActivity;
import com.ybg.rp.vm.activity.setting.ManageActivity;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.listener.NumInputChangedListener;
import com.ybg.rp.vm.listener.NumTouchListener;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/20.
 */
public class TrackWindowActivity extends Activity {

    private EditText edit_trackNum;
    private TextView tv_inputInfo;
    private TextView bt_delete, bt_a, bt_b, bt_c, bt_close;
    private TextView num_1, num_2, num_3, num_4;
    private TextView num_5, num_6, num_7, num_8;
    private TextView num_9, num_0;

    private ArrayList<TrackBean> allTrackList;

    private int time = 120;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    private static Logger logger = Logger.getLogger(TrackWindowActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.track_window);

        allTrackList = VMDBManager.getInstance().findAll(TrackBean.class);

        initView();
        setClickListener();

        mHandler.postDelayed(runnable, 1000);
    }

    private void initView() {
        tv_inputInfo = (TextView) findViewById(R.id.tv_input);
        edit_trackNum = (EditText) findViewById(R.id.edit_trackNum);

        num_1 = (TextView) findViewById(R.id.num_01);
        num_2 = (TextView) findViewById(R.id.num_02);
        num_3 = (TextView) findViewById(R.id.num_03);
        num_4 = (TextView) findViewById(R.id.num_04);
        num_5 = (TextView) findViewById(R.id.num_05);
        num_6 = (TextView) findViewById(R.id.num_06);
        num_7 = (TextView) findViewById(R.id.num_07);
        num_8 = (TextView) findViewById(R.id.num_08);
        num_9 = (TextView) findViewById(R.id.num_09);
        num_0 = (TextView) findViewById(R.id.num_0);
        bt_a = (TextView) findViewById(R.id.bt_a);
        bt_b = (TextView) findViewById(R.id.bt_b);
        bt_c = (TextView) findViewById(R.id.bt_c);
        bt_delete = (TextView) findViewById(R.id.bt_delete);
        bt_close = (TextView) findViewById(R.id.bt_close);
    }

    private void setClickListener() {
        tv_inputInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if ("78".equals(edit_trackNum.getText().toString())) {
                    /** 跳转到登录*/
                    logger.info("准备进入登录界面");
                    Intent login = new Intent();
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    login.setClass(TrackWindowActivity.this, LoginActivity.class);
                    //login.setClass(TrackWindowActivity.this, ManageActivity.class);
                    startActivity(login);
                    finish();
                }
                return true;
            }
        });
        bt_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**取消倒计时*/
                mHandler.removeCallbacks(runnable);
                finish();
            }
        });

        /**键盘点击*/
        NumTouchListener touchListener = new NumTouchListener(this, edit_trackNum, tv_inputInfo);
        bt_delete.setOnTouchListener(touchListener);
        bt_delete.setLongClickable(true);
        bt_delete.setClickable(true);

        bt_a.setOnTouchListener(touchListener);
        bt_a.setLongClickable(true);
        bt_a.setClickable(true);

        bt_b.setOnTouchListener(touchListener);
        bt_b.setLongClickable(true);
        bt_b.setClickable(true);

        bt_c.setOnTouchListener(touchListener);
        bt_c.setLongClickable(true);
        bt_c.setClickable(true);

        num_0.setOnTouchListener(touchListener);
        num_0.setLongClickable(true);
        num_0.setClickable(true);

        num_1.setOnTouchListener(touchListener);
        num_1.setLongClickable(true);
        num_1.setClickable(true);

        num_2.setOnTouchListener(touchListener);
        num_2.setLongClickable(true);
        num_2.setClickable(true);

        num_3.setOnTouchListener(touchListener);
        num_3.setLongClickable(true);
        num_3.setClickable(true);

        num_4.setOnTouchListener(touchListener);
        num_4.setLongClickable(true);
        num_4.setClickable(true);

        num_5.setOnTouchListener(touchListener);
        num_5.setLongClickable(true);
        num_5.setClickable(true);

        num_6.setOnTouchListener(touchListener);
        num_6.setLongClickable(true);
        num_6.setClickable(true);

        num_7.setOnTouchListener(touchListener);
        num_7.setLongClickable(true);
        num_7.setClickable(true);

        num_8.setOnTouchListener(touchListener);
        num_8.setLongClickable(true);
        num_8.setClickable(true);

        num_9.setOnTouchListener(touchListener);
        num_9.setLongClickable(true);
        num_9.setClickable(true);

        /**数字输入变化*/
        NumInputChangedListener textChangedListener = new NumInputChangedListener
                (TrackWindowActivity.this, edit_trackNum, tv_inputInfo, allTrackList);
        edit_trackNum.addTextChangedListener(textChangedListener);
    }

    /**
     * 倒计时
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            time--;
            if (time == 0) {
                /**关闭窗口*/
                finish();
            } else {
                tv_inputInfo.setText(time + "S");
            }
            mHandler.postDelayed(this, 1000);
        }
    };

    public void resetTimer() {
        time = 120;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(runnable);
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

}
