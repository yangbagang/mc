package com.ybg.rp.vm.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.TestRecordAdapter;
import com.ybg.rp.vm.adapter.TestTrackAdapter;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.help.DeviceHelper;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vmbase.callback.ResultCallback;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/29.
 */
public class TestCabinetActivity extends Activity {

    private GridView gv_trackList;
    private ListView lv_testResult;

    private DeviceHelper helper;
    private SettingHelper dbHelper;
    private ArrayList<TrackBean> trackList;
    private TestTrackAdapter trackAdapter;
    private TestRecordAdapter recordAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_cabinet);

        initView();

        helper = DeviceHelper.getInstance(this);
        dbHelper = SettingHelper.getInstance(this);
        trackList = new ArrayList<>();

        loadData();
    }

    private void initView() {
        gv_trackList = (GridView) findViewById(R.id.trackTest_cabinet_gv_trackList);
        lv_testResult = (ListView) findViewById(R.id.trackTest_cabinet_lv_testResult);
    }

    /**
     * 加载数据
     */
    private void loadData() {
        ArrayList<LayerBean> list = dbHelper.getCabinetList();
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                trackList.addAll(dbHelper.getTrackList(list.get(i).getLayerNo()));
            }
            trackAdapter = new TestTrackAdapter(TestCabinetActivity.this, trackList);
            gv_trackList.setAdapter(trackAdapter);
            gv_trackList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    /**触发测试*/
                    TrackBean bean = trackList.get(position);
                    testTrack(bean);
                }
            });
        } else {
            Toast.makeText(TestCabinetActivity.this, "请先设置格子柜", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * 测试指定轨道
     */
    private void testTrack(TrackBean track) {
        helper.testCabinetItem(track, new ResultCallback.ReturnListener() {
            @Override
            public void startRecord() {
                //YFDialogUtil.showLoadding(TestCabinetActivity.this);
            }

            @Override
            public void returnRecord(ArrayList<String> list) {
                showRecord(list);
                //YFDialogUtil.removeDialog(TestCabinetActivity.this);
            }
        });
    }

    /**
     * 结果显示
     */
    private void showRecord(ArrayList<String> records) {
        recordAdapter = new TestRecordAdapter(TestCabinetActivity.this, records);
        lv_testResult.setAdapter(recordAdapter);
        recordAdapter.notifyDataSetChanged();
    }

    public void closeWin(View view) {
        finish();
    }
}
