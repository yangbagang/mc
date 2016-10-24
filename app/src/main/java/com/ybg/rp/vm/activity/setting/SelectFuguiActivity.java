package com.ybg.rp.vm.activity.setting;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.FuguiItemAdapter;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vm.utils.ProgressDialogUtil;
import com.ybg.rp.vm.utils.ViewUtil;

import java.util.ArrayList;

/**
 * Created by yangbagang on 2016/10/20.
 */

public class SelectFuguiActivity extends AppCompatActivity {

    private LinearLayout createFugui;
    private EditText layerNum;
    private EditText orbitalNum;
    private EditText maxNum;
    private LinearLayout updateFugui;
    private ListView fuguiItem;
    private FuguiItemAdapter adapter;
    private ArrayList<TrackBean> trackList;

    private SettingHelper helper;

    private String layerNo = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_select_fugui);

        helper = SettingHelper.getInstance(this);

        trackList = new ArrayList<TrackBean>();

        initView();
        initListener();
    }

    private void initView() {
        createFugui = (LinearLayout) findViewById(R.id.createFugui);
        updateFugui = (LinearLayout) findViewById(R.id.updateFugui);
        fuguiItem = (ListView) findViewById(R.id.container_lv_fugui);
        layerNum = (EditText) findViewById(R.id.layerNum);
        orbitalNum = (EditText) findViewById(R.id.orbitalNum);
        maxNum = (EditText) findViewById(R.id.maxNum);

        Intent intent = getIntent();
        int opType = intent.getIntExtra("op", 0);
        if (opType == 0) {
            //新增副柜
            int index = intent.getIntExtra("index", 0);
            String[] layers = {"A", "B", "C"};
            layerNo = layers[index];
            updateFugui.setVisibility(View.GONE);
        } else if (opType == 1) {
            //修改副柜
            createFugui.setVisibility(View.GONE);
            layerNo = intent.getStringExtra("layerNo");
            loadData();
        }
    }

    private void initListener() {
        //格子柜
        adapter = new FuguiItemAdapter(new FuguiItemAdapter.FuguiItemListener() {
            @Override
            public void modifyFuguiItem(int position, int max) {
                /**修改*/
                TrackBean trackBean = trackList.get(position);
                if (trackBean != null) {
                    updateTrackBean(trackBean, max);
                }
            }

            @Override
            public void deleteFuguiItem(int position) {
                /**删除*/
                TrackBean trackBean = trackList.get(position);
                if (trackBean != null) {
                    //showWarn(trackBean, position);
                    //TODO 删除暂时不做，等新增一起做
                }
            }
        }, SelectFuguiActivity.this, trackList);
        fuguiItem.setAdapter(adapter);
    }

    public void addFugui(View v) {
        int layer = -1;
        int orbital = -1;
        int max = -1;
        try {
            layer = Integer.parseInt(layerNum.getText().toString());
            orbital = Integer.parseInt(orbitalNum.getText().toString());
            max = Integer.parseInt(maxNum.getText().toString());
        } catch (NumberFormatException e) {
            //
        }
        if (layer < 1 || orbital < 1 || max < 0) {
            Toast.makeText(SelectFuguiActivity.this, "参数为空或非法", Toast.LENGTH_SHORT).show();
            return;
        }
        final int finalLayer = layer;
        final int finalOrbital = orbital;
        final int finalMax = max;
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**开始*/
                ProgressDialogUtil.showDialog(SelectFuguiActivity.this, "正在保存设置，请稍候...");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                /**结果返回*/
                ProgressDialogUtil.closeDialog();
                createFugui.setVisibility(View.GONE);
                updateFugui.setVisibility(View.VISIBLE);
                loadData();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                helper.addFugui(layerNo, finalLayer, finalOrbital, finalMax);
                return true;
            }
        }.execute();
    }

    public void closeWin(View view) {
        Intent intent = new Intent();
        intent.putExtra("select", true);
        setResult(2001, intent);
        finish();
    }

    private void loadData() {
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**开始*/
                ProgressDialogUtil.showDialog(SelectFuguiActivity.this, "正在加载数据...");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                /**结果返回*/
                ProgressDialogUtil.closeDialog();
                //ViewUtil.setListViewHeightBasedOnChildren(fuguiItem);
                adapter.notifyDataSetChanged();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                trackList.clear();
                trackList.addAll(helper.getTrackList(layerNo));
                return true;
            }
        }.execute();
    }

    private void updateTrackBean(final TrackBean trackBean, final int max) {
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**开始*/
                ProgressDialogUtil.showDialog(SelectFuguiActivity.this, "正在加载数据...");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                /**结果返回*/
                ProgressDialogUtil.closeDialog();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                helper.setTrackmax(trackBean.getTrackNo(), max);
                return true;
            }
        }.execute();
    }
}
