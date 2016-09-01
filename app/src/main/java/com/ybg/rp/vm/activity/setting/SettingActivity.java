package com.ybg.rp.vm.activity.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.CabinetListAdapter;
import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vm.listener.TrackNoChangedListener;
import com.ybg.rp.vm.utils.AppConstant;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vm.utils.ProgressDialogUtil;
import com.ybg.rp.vm.utils.ViewUtil;
import com.ybg.rp.vmbase.bean.VMOperator;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.GsonUtil;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/25.
 */
public class SettingActivity extends Activity implements View.OnClickListener, CompoundButton
        .OnCheckedChangeListener {

    private ScrollView scrollView;
    /**
     * 开关 主机
     */
    private ToggleButton tg_main_7, tg_main_8;
    private TextView tv_07, tv_08;
    /**
     * 层级数量
     */
    private EditText edit_main_track_1, edit_main_track_2, edit_main_track_3, edit_main_track_4,
            edit_main_track_5, edit_main_track_6, edit_main_track_7, edit_main_track_8;
    /**
     * 排放量设置
     */
    private TextView tv_main_emission_1, tv_main_emission_2, tv_main_emission_3,
            tv_main_emission_4, tv_main_emission_5, tv_main_emission_6, tv_main_emission_7,
            tv_main_emission_8;

    /**
     * 格子柜设置
     */
    private TextView tv_addCabinet;
    private ListView lv_cabinet;
    private CabinetListAdapter listAdapter;

    private SettingHelper helper;

    private ArrayList<LayerBean> lyList;
    private ArrayList<LayerBean> cabinetList;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    String str = (String) msg.obj;
                    Toast.makeText(SettingActivity.this, str, Toast.LENGTH_SHORT).show();
                    break;
                case 2:
                    /** 主机设置*/
                    LayerBean layerBean = (LayerBean) msg.obj;
                    setMainLayout(layerBean.getLayerNo(), layerBean.getTrackNum());
                    break;
                case 3:
                    /** 格子柜设置*/
                    ViewUtil.setListViewHeightBasedOnChildren(lv_cabinet);
                    listAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        helper = SettingHelper.getInstance(this);

        lyList = new ArrayList<>();
        cabinetList = new ArrayList<>();
        initView();
        initListener();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.sv_layout);
        tg_main_7 = (ToggleButton) findViewById(R.id.container_sv_main_7);
        tg_main_8 = (ToggleButton) findViewById(R.id.container_sv_main_8);
        tv_07 = (TextView) findViewById(R.id.container_tv_main_track_7);
        tv_08 = (TextView) findViewById(R.id.container_tv_main_track_8);

        edit_main_track_1 = (EditText) findViewById(R.id.container_edit_main_track_1);
        edit_main_track_2 = (EditText) findViewById(R.id.container_edit_main_track_2);
        edit_main_track_3 = (EditText) findViewById(R.id.container_edit_main_track_3);
        edit_main_track_4 = (EditText) findViewById(R.id.container_edit_main_track_4);
        edit_main_track_5 = (EditText) findViewById(R.id.container_edit_main_track_5);
        edit_main_track_6 = (EditText) findViewById(R.id.container_edit_main_track_6);
        edit_main_track_7 = (EditText) findViewById(R.id.container_edit_main_track_7);
        edit_main_track_8 = (EditText) findViewById(R.id.container_edit_main_track_8);

        tv_main_emission_1 = (TextView) findViewById(R.id.container_tv_main_emission_1);
        tv_main_emission_2 = (TextView) findViewById(R.id.container_tv_main_emission_2);
        tv_main_emission_3 = (TextView) findViewById(R.id.container_tv_main_emission_3);
        tv_main_emission_4 = (TextView) findViewById(R.id.container_tv_main_emission_4);
        tv_main_emission_5 = (TextView) findViewById(R.id.container_tv_main_emission_5);
        tv_main_emission_6 = (TextView) findViewById(R.id.container_tv_main_emission_6);
        tv_main_emission_7 = (TextView) findViewById(R.id.container_tv_main_emission_7);
        tv_main_emission_8 = (TextView) findViewById(R.id.container_tv_main_emission_8);

        tv_addCabinet = (TextView) findViewById(R.id.container_tv_addCabinet);
        lv_cabinet = (ListView) findViewById(R.id.container_lv_cabinet);

        edit_main_track_1.addTextChangedListener(new TrackNoChangedListener("01",
                edit_main_track_1, helper));
        edit_main_track_2.addTextChangedListener(new TrackNoChangedListener("02",
                edit_main_track_2, helper));
        edit_main_track_3.addTextChangedListener(new TrackNoChangedListener("03",
                edit_main_track_3, helper));
        edit_main_track_4.addTextChangedListener(new TrackNoChangedListener("04",
                edit_main_track_4, helper));
        edit_main_track_5.addTextChangedListener(new TrackNoChangedListener("05",
                edit_main_track_5, helper));
        edit_main_track_6.addTextChangedListener(new TrackNoChangedListener("06",
                edit_main_track_6, helper));
        edit_main_track_7.addTextChangedListener(new TrackNoChangedListener("07",
                edit_main_track_7, helper));
        edit_main_track_8.addTextChangedListener(new TrackNoChangedListener("08",
                edit_main_track_8, helper));
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(SettingActivity.this, MaxGoodSetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        switch (v.getId()) {
            case R.id.container_tv_main_emission_1:
                intent.putExtra("layer", "01");
                break;
            case R.id.container_tv_main_emission_2:
                intent.putExtra("layer", "02");
                break;
            case R.id.container_tv_main_emission_3:
                intent.putExtra("layer", "03");
                break;
            case R.id.container_tv_main_emission_4:
                intent.putExtra("layer", "04");
                break;
            case R.id.container_tv_main_emission_5:
                intent.putExtra("layer", "05");
                break;
            case R.id.container_tv_main_emission_6:
                intent.putExtra("layer", "06");
                break;
            case R.id.container_tv_main_emission_7:
                intent.putExtra("layer", "07");
                break;
            case R.id.container_tv_main_emission_8:
                intent.putExtra("layer", "08");
                break;
        }

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**开始*/
                ProgressDialogUtil.showDialog(SettingActivity.this, "正在加载数据...");
                setMainSelect(null, false);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                /**结果返回*/
                ProgressDialogUtil.closeDialog();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                /**
                 * 获取主机信息
                 */
                lyList.clear();
                lyList.addAll(helper.getMainLayer());
                /**
                 * 获取当前保存格子柜信息
                 */
                cabinetList.clear();
                cabinetList.addAll(helper.getCabinetList());

                if (lyList.size() > 0) {
                    for (LayerBean lb : lyList) {
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = lb;
                        handler.sendMessage(msg);
                    }
                }

                if (cabinetList.size() > 0) {
                    Message msg = new Message();
                    msg.what = 3;
                    //msg.obj = "";
                    handler.sendMessage(msg);
                }
                return true;
            }
        }.execute();
    }


    /**
     * 根据读取的数据设置布局
     * 设置主机
     */
    private void setMainLayout(String layer, int selectNum) {
        if (layer.equals("01")) {
            edit_main_track_1.setText(String.valueOf(selectNum));
            edit_main_track_1.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("02")) {
            edit_main_track_2.setText(String.valueOf(selectNum));
            edit_main_track_2.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("03")) {
            edit_main_track_3.setText(String.valueOf(selectNum));
            edit_main_track_3.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("04")) {
            edit_main_track_4.setText(String.valueOf(selectNum));
            edit_main_track_4.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("05")) {
            edit_main_track_5.setText(String.valueOf(selectNum));
            edit_main_track_5.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("06")) {
            edit_main_track_6.setText(String.valueOf(selectNum));
            edit_main_track_6.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("07")) {
            tg_main_7.setChecked(true);
            edit_main_track_7.setText(String.valueOf(selectNum));
            edit_main_track_7.setSelection(String.valueOf(selectNum).length());
        } else if (layer.equals("08")) {
            tg_main_8.setChecked(true);
            edit_main_track_8.setText(String.valueOf(selectNum));
            edit_main_track_8.setSelection(String.valueOf(selectNum).length());
        }
    }


    /**
     * 设置监听
     */
    private void initListener() {
        tv_main_emission_1.setOnClickListener(this);
        tv_main_emission_2.setOnClickListener(this);
        tv_main_emission_3.setOnClickListener(this);
        tv_main_emission_4.setOnClickListener(this);
        tv_main_emission_5.setOnClickListener(this);
        tv_main_emission_6.setOnClickListener(this);
        tv_main_emission_7.setOnClickListener(this);
        tv_main_emission_8.setOnClickListener(this);

        tg_main_7.setOnCheckedChangeListener(this);
        tg_main_8.setOnCheckedChangeListener(this);

        listAdapter = new CabinetListAdapter(new CabinetListAdapter.CabinetListener() {
            @Override
            public void modifyCabinet(int position) {
                /**修改*/
                LayerBean cabinet = cabinetList.get(position);
                if (cabinet != null) {
                    fixCabinet(cabinet);
                }
            }

            @Override
            public void deleteCabinet(int position) {
                /**删除*/
                LayerBean cabinet = cabinetList.get(position);
                if (cabinet != null) {
                    showWarn(cabinet.getLayerNo(), position);
                }
            }
        }, SettingActivity.this, cabinetList);
        lv_cabinet.setAdapter(listAdapter);

        tv_addCabinet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCabinet();
            }
        });
    }


    /*********************************************************************
     * 主机操作部分
     ******************************************************************/

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        /**可选择启用与否*/
        switch (buttonView.getId()) {
            case R.id.container_sv_main_7:
                setMainSelect("07", isChecked);
                break;
            case R.id.container_sv_main_8:
                setMainSelect("08", isChecked);
                break;
        }
    }

    /**
     * 根据设置,设置主机轨道数不可输入
     */
    private void setMainSelect(String cabinetNo, boolean isSelect) {
        if (isSelect) {
            LogUtil.i("[-选中 添加主机 :" + cabinetNo + " 层");
        } else {
            LogUtil.i("[-不选中 删除主机 :" + cabinetNo + " 层");
        }
        if (cabinetNo != null && !"".equals(cabinetNo)) {
            if (cabinetNo.equals("07")) {
                if (isSelect) {
                    edit_main_track_7.setVisibility(View.VISIBLE);
                    tv_07.setVisibility(View.VISIBLE);
                } else {
                    edit_main_track_7.setVisibility(View.GONE);
                    tv_07.setVisibility(View.GONE);
                    deleteMain("07");
                }
            } else if (cabinetNo.equals("08")) {
                if (isSelect) {
                    edit_main_track_8.setVisibility(View.VISIBLE);
                    tv_08.setVisibility(View.VISIBLE);
                } else {
                    edit_main_track_8.setVisibility(View.GONE);
                    tv_08.setVisibility(View.GONE);
                    deleteMain("08");
                }
            }
        } else {
            edit_main_track_7.setVisibility(View.GONE);
            tv_07.setVisibility(View.GONE);
            edit_main_track_8.setVisibility(View.GONE);
            tv_08.setVisibility(View.GONE);
        }
    }

    /**
     * 删除主机信息
     * @param layerNo 指定层级轨道
     */
    private void deleteMain(final String layerNo) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                synchronized (this) {
                    try {
                        helper.delLayer(layerNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        }.execute();
    }
    /****************************************************
     *                  格子柜操作
     * ***********************************************/
    /**
     * 提示
     */
    private void showWarn(final String cabinetNo, final int position) {
        new AlertDialog.Builder(SettingActivity.this)
                       .setMessage("删除后无法进行购买")
                       .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               LogUtil.i("-------ok-------------");
                               DialogUtil.removeDialog(SettingActivity.this);

                               cutCabinet(cabinetNo);
                               /**刷新页面*/
                               cabinetList.remove(position);
                               Message msg = new Message();
                               msg.what = 3;
                               handler.sendMessage(msg);
                           }
                       })
                       .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               LogUtil.i("-------ocancel-------------");
                               DialogUtil.removeDialog(SettingActivity.this);
                           }
                       })
                       .create().show();
    }

    /**
     * 删除格子柜
     */
    private void cutCabinet(final String cabinetNo) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                DialogUtil.showLoading(SettingActivity.this);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                DialogUtil.removeDialog(SettingActivity.this);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                helper.delLayer(cabinetNo);
                return null;
            }
        }.execute();

    }

    /**
     * 跳转修改格子柜信息
     *
     * @param cabinet 指定格子柜
     */
    private void fixCabinet(LayerBean cabinet) {
        Intent intent = new Intent(SettingActivity.this, SelectCabinetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("op", 1);
        Bundle bundle = new Bundle();
        bundle.putSerializable("cabinet", cabinet);
        intent.putExtras(bundle);
        startActivityForResult(intent, 1000);
    }

    /**
     * 新增格子柜
     */
    private void addCabinet() {
        if (cabinetList.size() >= 6) {
            Toast.makeText(SettingActivity.this, "格子柜数量已满", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(SettingActivity.this, SelectCabinetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("op", 0);
        intent.putExtra("index", 0);
        startActivityForResult(intent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == 1001) {
            LogUtil.i("[-格子柜操作成功=" + data.getBooleanExtra("select", true) + "]");
        }
    }

    /********************************************************
     *                  数据上传
     * *****************************************************/
    /**
     * 层级、轨道数保存
     */
    public void updBaseVMData(View view) {
        XApplication xApplication = (XApplication) getApplication();
        VMOperator operator = xApplication.getOperator();
        if (null == operator) {
            Toast.makeText(SettingActivity.this, "操作员错误，请重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        final long operatorId = operator.getOperatorId();
        final ArrayList<LayerBean> lvList = VMDBManager.getInstance().findAll(LayerBean.class);
        String url = AppConstant.HOST + "vendMachineInfo/updateLayerNo";
        RequestParams params = new RequestParams(url);
        // 添加请求参数
        params.addBodyParameter("machineId", preferences.getVMId());//机器ID
        params.addBodyParameter("operatorId", ""+operatorId);//轨道编号
        String gsons = GsonUtil.toJsonProperties(lvList, "layerNo", "trackNum");
        LogUtil.i("---上传的轨道信息:" + gsons);
        params.addBodyParameter("layers", gsons);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                updTrack(operatorId, lvList);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e(ex.getLocalizedMessage());
                Toast.makeText(SettingActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 最大库存数据
     *
     * @param operatorId
     * @param lvList
     */
    private void updTrack(final long operatorId, final ArrayList<LayerBean> lvList) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                //YFDialogUtil.removeDialog(ContainerManyActivity.this);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    String url = AppConstant.HOST + "vendMachineInfo/updateMaxNum";
                    for (int i = 0; i < lvList.size(); i++) {
                        LayerBean layerBean = lvList.get(i);
                        ArrayList<TrackBean> itemList = (ArrayList<TrackBean>) VMDBManager.getInstance()
                                .getDb().selector(TrackBean.class)
                                .where("layer_no", "=", layerBean.getLayerNo()).findAll();
                        // 添加请求参数
                        RequestParams request = new RequestParams(url);
                        request.addBodyParameter("machineId", preferences.getVMId());//机器ID
                        request.addBodyParameter("operatorId", ""+operatorId);

                        request.addBodyParameter("layerNo", layerBean.getLayerNo());//当前层

                        String gsons = GsonUtil.toJsonProperties(itemList, "trackNo", "maxInventory");
                        LogUtil.i("机器id" + preferences.getVMId()+"-----------gsons:"+gsons);
                        request.addBodyParameter("gsons", gsons);//数据
                        //同步请求
                        try {
                            String result = x.http().postSync(request, String.class);
                            JSONObject json = new JSONObject(result);
                            String success = json.getString("success");
                            if ("true".equals(success)) {
                                LogUtil.i("数据上传成功" + i + "-----" + lvList.size());
                                if (lvList.size() == (i + 1)) {
                                    LogUtil.i("数据上传成功--- ");
                                    sendMsg("数据上传成功");
                                }
                            } else {
                                LogUtil.i("上传失败--- ");
                                sendMsg("上传失败");
                                break;
                            }
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                            LogUtil.e("上传失败--- ");
                            sendMsg("上传失败");
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        }.execute();
    }

    public void sendMsg(String obj) {
        Message msg = new Message();
        msg.what = 1;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

    public void closeWin(View view) {
        finish();
    }

    private VMPreferences preferences = VMPreferences.getInstance();
}
