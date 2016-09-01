package com.ybg.rp.vm.activity.setting;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.SelectCabinetAdapter;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.bean.TrackError;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vm.utils.AlertDialogFragment;
import com.ybg.rp.vm.utils.DialogUtil;
import com.ybg.rp.vm.utils.ProgressDialogUtil;
import com.ybg.rp.vmbase.callback.ResultCallback;
import com.ybg.rp.vmbase.utils.LogUtil;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/30.
 */
public class SelectCabinetActivity extends AppCompatActivity implements View.OnClickListener {

    private GridView gv_no, gv_door, gv_max;
    private TextView tv_ok, tv_cancel;
    /**
     * 编号
     */
    private ArrayList<TrackError> noList;
    /**
     * 柜门数量
     */
    private ArrayList<TrackError> doorList;
    /**
     * 排放量
     */
    private ArrayList<TrackError> maxList;

    private SettingHelper dHelper;
    private VMDBManager dbUtil;
    /**
     * 操作方式 0:新增 1:修改
     */
    private int opType = 0;
    private int noIndex = 0;//传进来新增编号
    private boolean isAdd = false;//是否新增格子柜
    private String baseNo;//传进来的原本编号

    private String selectNo;
    private String selectNum;
    private String selectMax;

    private SelectCabinetAdapter noAdapter;
    private SelectCabinetAdapter doorAdapter;
    private SelectCabinetAdapter maxAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_select_cabinet);

        dbUtil = VMDBManager.getInstance();
        dHelper = SettingHelper.getInstance(this);
        findLayout();
        initData();
        setListener();


        Intent intent = getIntent();
        opType = intent.getIntExtra("op", 0);

        if (opType == 0) {
            /**新增格子柜*/
            noIndex = intent.getIntExtra("index", 0);

            selectNo = noList.get(noIndex).getLayerNo();
            selectNum = "64";
            selectMax = "1";
            isAdd = true;
            setBaseSelect();
        } else {
            /**编辑格子柜*/
            LayerBean cabinet = (LayerBean) intent.getSerializableExtra("cabinet");
            isAdd = false;
            try {
                if (cabinet != null) {
                    baseNo = cabinet.getLayerNo();//保存原本编号
                    selectNo = cabinet.getLayerNo();
                    selectNum = String.valueOf(cabinet.getTrackNum());
                    /**读取轨道样例 获取统一的排放量*/
                    TrackBean tb = dbUtil.getDb().selector(TrackBean.class).where("layer_no", "=", selectNo).findFirst();
                    if (tb != null) {
                        selectMax = String.valueOf(tb.getMaxInventory());
                    } else {
                        selectMax = "1";
                    }
                    setBaseSelect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void findLayout() {
        gv_no = (GridView) findViewById(R.id.select_gv_no);
        gv_door = (GridView) findViewById(R.id.select_gv_num);
        gv_max = (GridView) findViewById(R.id.select_gv_max);
        tv_ok = (TextView) findViewById(R.id.select_tv_ok);
        tv_cancel = (TextView) findViewById(R.id.select_tv_cancel);
    }

    private void setListener() {
        tv_ok.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

        noAdapter = new SelectCabinetAdapter(SelectCabinetActivity.this, noList);
        gv_no.setAdapter(noAdapter);
        gv_no.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectNo = noList.get(position).getLayerNo();
                refreshChoose(noList, position);
                noAdapter.notifyDataSetChanged();
            }
        });

        doorAdapter = new SelectCabinetAdapter(SelectCabinetActivity.this, doorList);
        gv_door.setAdapter(doorAdapter);
        gv_door.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectNum = doorList.get(position).getLayerNo();
                refreshChoose(doorList, position);
                //ArrayList<TrackError> list = refreshChoose(doorList, position);
                //doorList.clear();
                //doorList.addAll(list);
                doorAdapter.notifyDataSetChanged();
            }
        });

        maxAdapter = new SelectCabinetAdapter(SelectCabinetActivity.this, maxList);
        gv_max.setAdapter(maxAdapter);
        gv_max.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectMax = maxList.get(position).getLayerNo();
                refreshChoose(maxList, position);
                maxAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_tv_ok:
                if (isAdd) {
                    /**新增*/
                    if (canChoose()) {
                        saveCabinet();
                    } else {
                        Toast.makeText(SelectCabinetActivity.this, "该编号已存在", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    /**修改*/
                    if (selectNo.equals(baseNo)) {
                        /**编号未改变*/
                        saveCabinet();
                    } else {
                        /**改变编号*/
                        showWarn();
                    }
                }

                break;
            case R.id.select_tv_cancel:
                cancelSelect(false);
                break;
        }
    }

    /*************************************************************
     *                  数据设置
     * ************************************************************/

    /**
     * 初始化设置数据
     */
    private void initData() {
        noList = new ArrayList<>();
        doorList = new ArrayList<>();
        maxList = new ArrayList<>();
        /**编号 1~6*/
        for (int i = 1; i < 7; i++) {
            TrackError no = new TrackError();
            no.setLayerNo(String.valueOf(i));
            no.setSelect(false);
            noList.add(no);
        }

        /**柜门*/
        TrackError door01 = new TrackError();
        door01.setLayerNo("1");
        door01.setSelect(false);
        doorList.add(door01);

        TrackError door02 = new TrackError();
        door02.setLayerNo("50");
        door02.setSelect(false);
        doorList.add(door02);

        TrackError door03 = new TrackError();
        door03.setLayerNo("64");
        door03.setSelect(false);
        doorList.add(door03);

        TrackError door04 = new TrackError();
        door04.setLayerNo("88");
        door04.setSelect(false);
        doorList.add(door04);

        TrackError door05 = new TrackError();
        door05.setLayerNo("99");
        door05.setSelect(false);
        doorList.add(door05);

        /**排放量 1~15*/
        for (int i = 1; i < 16; i++) {
            TrackError max = new TrackError();
            max.setLayerNo(String.valueOf(i));
            max.setSelect(false);
            maxList.add(max);
        }
    }

    /**
     * 设置初始化选中
     */
    private void setBaseSelect() {
        /*编号*/
        for (int i = 0; i < noList.size(); i++) {
            if (noList.get(i).getLayerNo().equals(selectNo)) {
                noList.get(i).setSelect(true);
            }
        }
        /*柜门*/
        for (int i = 0; i < doorList.size(); i++) {
            if (doorList.get(i).getLayerNo().equals(selectNum)) {
                doorList.get(i).setSelect(true);
            }
        }
        /*排放量*/
        for (int i = 0; i < maxList.size(); i++) {
            if (maxList.get(i).getLayerNo().equals(selectMax)) {
                maxList.get(i).setSelect(true);
            }
        }

        noAdapter.notifyDataSetChanged();
        doorAdapter.notifyDataSetChanged();
        maxAdapter.notifyDataSetChanged();
    }


    /**
     * 刷新选择
     */
    private void refreshChoose(ArrayList<TrackError> datas, int position) {
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setSelect(false);
        }
        datas.get(position).setSelect(true);
    }

    /**
     * 提示是否新增
     */
    private void showWarn() {
        new AlertDialog.Builder(SelectCabinetActivity.this).setMessage("是否新增格子柜")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtil.i("-------ok-------------");
                        DialogUtil.removeDialog(SelectCabinetActivity.this);
                        if (canChoose()) {
                            saveCabinet();
                        } else {
                            Toast.makeText(SelectCabinetActivity.this, "该编号已存在", Toast
                                    .LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtil.i("-------cancel-------------");
                        DialogUtil.removeDialog(SelectCabinetActivity.this);
                    }
                }).create().show();

    }

    /**
     * 判断格子柜编号是否可用
     */
    private boolean canChoose() {
        try {
            /**判断*/
            ArrayList<LayerBean> list = (ArrayList<LayerBean>) dbUtil.getDb().selector(LayerBean.class)
                    .where("device_type", "=", "1").orderBy("layer_no").findAll();
            if (list != null && list.size() > 0) {
                for (LayerBean bean : list) {
                    if (bean.getLayerNo().equals(selectNo)) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * 保存设置
     */
    private void saveCabinet() {
        if (selectNo != null && !"".equals(selectNo) && selectNum != null && !"".equals
                (selectNum) && selectMax != null && !"".equals(selectMax)) {
            /**保存*/
            dHelper.setCabinet(new ResultCallback.ResultListener() {
                @Override
                public void startFunction() {
                    ProgressDialogUtil.showDialog(SelectCabinetActivity.this, "");
                }

                @Override
                public void isResultOK(Boolean ok) {
                    ProgressDialogUtil.closeDialog();
                    cancelSelect(true);
                }
            }, selectNo, Integer.parseInt(selectNum), Integer.parseInt(selectMax));
        }
    }

    /**
     * 取消选择
     */
    private void cancelSelect(Boolean isSelect) {
        Intent intent = new Intent();
        intent.putExtra("select", isSelect);
        setResult(1001, intent);
        finish();
    }

}
