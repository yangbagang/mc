package com.ybg.rp.vm.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.MaxSetAdapter;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vm.utils.ProgressDialogUtil;
import com.ybg.rp.vm.views.MaxSelectDialog;
import com.ybg.rp.vmbase.callback.ResultCallback;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/30.
 */
public class MaxGoodSetActivity extends AppCompatActivity {

    private TextView tv_title, tv_all;
    private ListView lv_track;

    private SettingHelper helper;
    private MaxSetAdapter adapter;
    private ArrayList<TrackBean> trackList;
    private String selectLayer;//选中主机轨道
    private MaxSelectDialog selectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_max);

        Intent intent = getIntent();
        selectLayer = intent.getStringExtra("layer");

        helper = SettingHelper.getInstance(this);
        findLayout();
        loadData();
    }

    private void findLayout() {
        tv_title = (TextView) findViewById(R.id.maxSet_tv_layer);
        tv_all = (TextView) findViewById(R.id.maxSet_tv_changeAll);
        lv_track = (ListView) findViewById(R.id.maxSet_lv_track);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        trackList = new ArrayList<>();
        trackList.addAll(helper.getTrackList(selectLayer));
        if (trackList.size() < 0) {
            /**数据不存在就初始化*/
            initLayout(selectLayer);
        }
        tv_title.setText("第" + Integer.parseInt(selectLayer) + "层排放数设置");
        tv_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**设置全部*/
                showSelect(null);
            }
        });
        adapter = new MaxSetAdapter(MaxGoodSetActivity.this, trackList);
        lv_track.setAdapter(adapter);
        lv_track.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**点击单个 设置最大排放量*/
                TrackBean bean=trackList.get(position);
                showSelect(bean.getTrackNo());
            }
        });
    }

    /**
     * 如果数据为空
     * 初始化对应层级
     */
    private void initLayout(final String layerNo) {
        try {
            VMDBManager dbUtil = VMDBManager.getInstance();
            LayerBean lb = dbUtil.getDb().selector(LayerBean.class).where("layer_no", "=", layerNo).findFirst();
            helper.setMainTrack(layerNo, lb.getTrackNum());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置单个格子
     */
    private void setTack(final String trackNo, final int max) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ProgressDialogUtil.showDialog(MaxGoodSetActivity.this, "");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    adapter.notifyDataSetChanged();
                }
                ProgressDialogUtil.closeDialog();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                helper.setTrackmax(trackNo, max);

                trackList.clear();
                trackList.addAll(helper.getTrackList(selectLayer));
                return true;
            }
        }.execute();
    }

    /**
     * 设置全部格子的排放量
     */
    private void setAllTrack(final int maxValue) {
        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                ProgressDialogUtil.showDialog(MaxGoodSetActivity.this,"");
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    adapter.notifyDataSetChanged();
                }
                ProgressDialogUtil.closeDialog();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                if (trackList.size() > 0 && adapter != null) {
                    for (int i = 0; i < trackList.size(); i++) {
                        trackList.get(i).setMaxInventory(maxValue);
                        /**写入数据库*/
                        helper.setTrackmax(trackList.get(i).getTrackNo(), maxValue);
                    }
                    return true;
                }
                return false;
            }
        }.execute();

    }

    /**
     * 弹框选择
     * @param trackNo 对应轨道或者格子 可为空
     */
    private void showSelect(final String trackNo) {
        selectDialog = new MaxSelectDialog(MaxGoodSetActivity.this, new ResultCallback.MaxListener() {
            @Override
            public void selectMax(int max) {
                if (trackNo != null && !"".equals(trackNo)) {
                    /**设置单个*/
                    setTack(trackNo, max);
                } else {
                    /**设置全部*/
                    setAllTrack(max);
                }
                selectDialog.hideDialog();
            }
        });
        selectDialog.showDialog();
    }

    public void closeWin(View view) {
        finish();
    }
}
