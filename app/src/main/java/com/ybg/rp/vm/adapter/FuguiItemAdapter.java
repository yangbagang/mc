package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.DeviceHelper;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vmbase.callback.ResultCallback;

import org.apache.log4j.Logger;

import java.util.ArrayList;

/**
 * Created by yangbagang on 2016/10/19.
 */

public class FuguiItemAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TrackBean> fuguiList;
    private FuguiItemAdapter.FuguiItemListener listener;
    private SettingHelper dHelper;
    private DeviceHelper deviceHelper;
    private VMDBManager dbUtil;

    private Logger logger = Logger.getLogger(FuguiItemAdapter.class);

    public FuguiItemAdapter(FuguiItemAdapter.FuguiItemListener listener, Context context,
                            ArrayList<TrackBean> fuguiList) {
        this.listener = listener;
        this.mContext = context;
        this.fuguiList = fuguiList;
        this.dHelper = SettingHelper.getInstance(mContext);
        this.deviceHelper = DeviceHelper.getInstance(mContext);
        this.dbUtil = VMDBManager.getInstance();
    }

    @Override
    public int getCount() {
        return fuguiList.size();
    }

    @Override
    public Object getItem(int position) {
        return fuguiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final FuguiItemAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_fugui_orbital, parent,
                    false);
            holder = new FuguiItemAdapter.ViewHolder();
            holder.tv_no = (TextView) convertView.findViewById(R.id.fugui_item_tv_no);
            holder.maxNum = (EditText) convertView.findViewById(R.id.maxNum);
            holder.tv_update = (TextView) convertView.findViewById(R.id.item_tv_update);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.item_tv_delete);
            holder.tv_test = (TextView) convertView.findViewById(R.id.item_tv_test);

            convertView.setTag(holder);
        } else {
            holder = (FuguiItemAdapter.ViewHolder) convertView.getTag();
        }
        final TrackBean trackBean = fuguiList.get(position);
        holder.tv_no.setText(trackBean.getTrackNo());
        holder.maxNum.setText("" + trackBean.getMaxInventory());

        holder.tv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String max = holder.maxNum.getText().toString();
                if (max == null || "".equals(max)) {
                    Toast.makeText(mContext, "最大排放数不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int m = -1;
                try {
                    m = Integer.parseInt(max.trim());
                } catch (NumberFormatException e) {
                    //e.printStackTrace();
                }
                if (m < 1) {
                    Toast.makeText(mContext, "最大排放数不正确", Toast.LENGTH_SHORT).show();
                    return;
                }
                listener.modifyFuguiItem(position, m);
            }
        });

        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteFuguiItem(position);
            }
        });

        holder.tv_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logger.debug("开始测试副柜轨道：" + trackBean.getLayerNo() + "," + trackBean.getTrackNo());
                deviceHelper.testFuguiItem(trackBean, new ResultCallback.ReturnListener() {
                    @Override
                    public void startRecord() {

                    }

                    @Override
                    public void returnRecord(ArrayList<String> list) {
                        for (String msg : list) {
                            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView tv_no;
        private EditText maxNum;
        private TextView tv_update;
        private TextView tv_delete;
        private TextView tv_test;
    }

    public interface FuguiItemListener {

        void modifyFuguiItem(int position, int max);
        void deleteFuguiItem(int position);

    }

}
