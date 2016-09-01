package com.ybg.rp.vm.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.ErrorTrackAdapter;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.bean.TrackError;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vm.help.DeviceHelper;
import com.ybg.rp.vm.serial.BeanTrackSet;
import com.ybg.rp.vm.serial.SerialManager;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vm.utils.ViewUtil;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.json.JSONObject;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/25.
 */
public class ManageActivity extends Activity implements View.OnClickListener {

    /**
     * 轨道状态
     */
    private TextView tv_trackState;

    /**
     * 错误轨道
     */
    private LinearLayout ll_error_track;

    /**
     * 选择全部
     */
    private CheckBox cb_selectAll;

    private GridView gv_track;
    /**
     * 测试轨道,修复轨道,跳转系统设置
     */
    private Button bt_testTrack, bt_fixTrack, btn_setting;

    /**
     * 测试与修复操作
     */
    private TextView tv_operate, tv_operateInfo;
    private LinearLayout ll_operate;

    /**
     * 轨道测试
     */
    private PercentLinearLayout ll_trackTest;

    /**
     * 操作日志
     */
    private PercentLinearLayout ll_log;

    /**
     * 基础设置
     */
    private PercentLinearLayout ll_baseSet;

    private SettingHelper settingHelper;
    private DeviceHelper deviceHelper;
    private SerialManager manager;
    private ErrorTrackAdapter errorAdapter;
    private ArrayList<TrackBean> errorList;//当前的故障轨道
    private ArrayList<TrackBean> selectList;//选中的错误轨道
    private ArrayList<TrackError> errors;//适配数据

    private XApplication xApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_layout);

        findLayout();
        settingHelper = SettingHelper.getInstance(this);
        deviceHelper = DeviceHelper.getInstance(this);
        manager = SerialManager.getInstance(this.getApplicationContext());

        errorList = new ArrayList<>();
        selectList = new ArrayList<>();
        errors = new ArrayList<>();

        setListener();
        xApplication = (XApplication) getApplication();
    }

    private void findLayout() {
        tv_trackState = (TextView) findViewById(R.id.manage_tv_trackState);

        cb_selectAll = (CheckBox) findViewById(R.id.manage_cb_selectAll);
        gv_track = (GridView) findViewById(R.id.manage_gv_track);
        bt_testTrack = (Button) findViewById(R.id.manage_bt_testTrack);
        bt_fixTrack = (Button) findViewById(R.id.manage_bt_fixTrack);
        ll_error_track = (LinearLayout) findViewById(R.id.manage_ll_error_track);

        tv_operate = (TextView) findViewById(R.id.manage_tv_operate);
        tv_operateInfo = (TextView) findViewById(R.id.manage_tv_operateInfo);
        ll_operate = (LinearLayout) findViewById(R.id.manage_ll_operate);

        ll_trackTest = (PercentLinearLayout) findViewById(R.id.manage_ll_trackTest);
        ll_log = (PercentLinearLayout) findViewById(R.id.manage_ll_log);
        ll_baseSet = (PercentLinearLayout) findViewById(R.id.manage_ll_baseSet);

        btn_setting = (Button) findViewById(R.id.manage_btn_setting);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manage_bt_testTrack:
                /**轨道测试*/
                testSelectTrack();
                break;
            case R.id.manage_bt_fixTrack:
                /**轨道修复*/
                repairTrackNo(selectList);
                break;
            case R.id.manage_ll_trackTest:
                /**轨道测试*/
                Intent testIntent = new Intent(ManageActivity.this, TestMachineActivity.class);
                testIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(testIntent);
                break;
            case R.id.manage_ll_log:
                /**操作日志*/
                Intent logIntent = new Intent(ManageActivity.this, MachinesLogActivity.class);
                logIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logIntent);
                break;
            case R.id.manage_ll_baseSet:
                /**基础设置*/
                Intent baseIntent = new Intent(ManageActivity.this, SettingActivity.class);
                baseIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(baseIntent);
                break;
            case R.id.manage_btn_setting:
                /**跳转到系统设置*/
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**开始*/
                DialogUtil.showLoading(ManageActivity.this);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);

                updUi();

                /**结果返回*/
                DialogUtil.hideLoading();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    /**错误轨道*/
                    selectList.clear();
                    errors.clear();
                    errorList.clear();
                    errorList.addAll(settingHelper.getErrorTack());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }.execute();
    }

    private void setListener() {
        ll_trackTest.setOnClickListener(this);
        ll_log.setOnClickListener(this);
        ll_baseSet.setOnClickListener(this);

        /**故障轨道相关*/
        bt_testTrack.setOnClickListener(this);
        bt_fixTrack.setOnClickListener(this);
        btn_setting.setOnClickListener(this);

        errorAdapter = new ErrorTrackAdapter(ManageActivity.this, errors);
        gv_track.setAdapter(errorAdapter);

        gv_track.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**添加选中*/
                boolean isSelect = errors.get(position).isSelect();
                if (isSelect) {
                    errors.get(position).setSelect(false);
                    selectList.remove(errorList.get(position));//从选择数据中移除
                } else {
                    errors.get(position).setSelect(true);
                    selectList.add(errorList.get(position));//添加进选择数据
                }
                refreshView();
            }
        });
        /**选中全部*/
        cb_selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (errors.size() > 0) {
                    /**设置适配数据*/
                    ArrayList<TrackError> list = ErrorTrackAdapter.setAllSelet(isChecked, errors);
                    errors.clear();
                    errors.addAll(list);
                    /**设置选中数据*/
                    selectList.clear();
                    if (isChecked) {
                        selectList.addAll(errorList);
                    }
                    refreshView();
                }
            }
        });
    }

    /**
     * 刷新页面
     */
    private void refreshView() {
        ViewUtil.setListViewHeightBasedOnChildren(gv_track, 6);
        errorAdapter.notifyDataSetChanged();
    }


    /**
     * 更新UI
     */
    private void updUi() {
        /** 没有错误数据*/
        if (errorList.size() <= 0) {
            ll_error_track.setVisibility(View.GONE);
            cb_selectAll.setVisibility(View.GONE);
            tv_trackState.setText("良好");
        } else {
            ll_error_track.setVisibility(View.VISIBLE);
            cb_selectAll.setVisibility(View.VISIBLE);
            tv_trackState.setText("错误");

            int pos = 0;
            errors.addAll(ErrorTrackAdapter.initErrorTrack(errorList, pos));
            selectList.add(errorList.get(pos));
            refreshView();
        }
    }


    /********************************************************
     *                  测试与修复
     * *******************************************************/

    /**
     * 触发轨道测试
     */
    private void testSelectTrack() {
        if (errorList.size() > 0) {
            if (selectList.size() > 0) {
                testTrack(selectList);
            } else {
                Toast.makeText(ManageActivity.this, "请先选择轨道", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ManageActivity.this, "当前无错误轨道", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 测试单个轨道
     *
     * @param list 轨道数据
     */
    private void testTrack(final ArrayList<TrackBean> list) {
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.showLoading(ManageActivity.this);
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**测试结果显示*/
                StringBuffer result = new StringBuffer();
                for (String str : list) {
                    result.append(str).append(",");
                }
                ll_operate.setVisibility(View.VISIBLE);
                tv_operate.setText("测试结果");
                tv_operateInfo.setText(result);
                /**结果返回*/
                DialogUtil.hideLoading();
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                ArrayList<String> resultList = new ArrayList<String>();
                try {

                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            TrackBean track = list.get(i);
                            String str = track.getTrackNo() + "轨道：";
                            VMDBManager.getInstance().saveLog(xApplication.getOperator(), "测试轨道" +
                                    "(修复)：" + track.getTrackNo());
                            /**判断轨道类型 1：格子柜,0：不是格子柜 */
                            BeanTrackSet trackSet = null;
                            int gridMark = track.getGridMark();
                            if (gridMark == 0) {
                                manager.createSerial(0);// 1:主机 2:格子柜
                                trackSet = manager.openMachineTrack(track.getTrackNo());
                            } else if (gridMark == 1) {
                                manager.createSerial(1);// 1:主机 2:格子柜
                                trackSet = manager.openMachineTrack(track.getTrackNo());
                            }
                            if (trackSet.trackStatus == 1) {
                                str += "电机正常";
                            } else {
                                str += trackSet.errorInfo;
                                LogUtil.e("轨道错误-------" + str);
                            }
                            resultList.add(str);
                            SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
                        }
                    }
                    manager.closeSerial();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return resultList;
            }
        }.execute();
    }


    /**
     * 修复错误轨道
     *
     * @param selectList 错误轨道信息
     */
    public void repairTrackNo(final ArrayList<TrackBean> selectList) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.showLoading(ManageActivity.this);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                boolean ok = false;
                String url = AppConstant.HOST + "vendMachineInfo/fixError";
                if (null != selectList && selectList.size() > 0) {
                    LogUtil.i("[selectList size=" + selectList.size() + "]");
                    VMDBManager dbUtil = VMDBManager.getInstance();
                    for (int i = 0; i < selectList.size(); i++) {
                        TrackBean t = selectList.get(i);
                        if (t.getFault() == 1) {
                            // 添加请求参数
                            RequestParams requestParams = new RequestParams(url);
                            requestParams.addBodyParameter("orbitalNo", t.getTrackNo().toUpperCase());
                            requestParams.addBodyParameter("machineId", VMPreferences.getInstance
                                    ().getVMId());
                            try {
                                String result = x.http().postSync(requestParams, String.class);
                                JSONObject json = new JSONObject(result);
                                String success = json.getString("success");
                                if ("true".equals(success)) {
                                    // 请求成功
                                    dbUtil.saveLog(xApplication.getOperator(), "成功修复错误轨道-" +
                                            t.getTrackNo());
                                    //设置为正常
                                    t.setFault(0);
                                    dbUtil.saveOrUpdate(t);
                                } else {
                                    // 请求失败
                                    String msg = json.getString("msg");
                                    LogUtil.e("修复轨道error " + msg);
                                    dbUtil.saveLog(xApplication.getOperator(), "修复错误轨道失败-" +
                                            t.getTrackNo() + "-" + msg);
                                }
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                                // 请求异常
                                LogUtil.e("修复轨道返回: error " + throwable.getLocalizedMessage());
                                dbUtil.saveLog(xApplication.getOperator(), "修复错误轨道失败-" + t
                                        .getTrackNo() + "-" + throwable.getLocalizedMessage());
                            }
                        }
                    }
                    ok = true;
                }
                return ok;
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                DialogUtil.hideLoading();

                if (aBoolean) {
                    Toast.makeText(ManageActivity.this, "修改服务器错误轨道完成！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageActivity.this, "没有需要修复的轨道", Toast.LENGTH_SHORT).show();
                }
                LogUtil.i("修改服务器错误轨道完成！");
                /**加载数据 刷新UI*/
                loadData();
            }
        }.execute();
    }

    public void closeWin(View view) {
        finish();
    }
}
