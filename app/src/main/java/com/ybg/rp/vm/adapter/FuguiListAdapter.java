package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.SettingHelper;

import java.util.ArrayList;

/**
 * Created by yangbagang on 2016/10/19.
 */

public class FuguiListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LayerBean> fuguiList;
    private FuguiListAdapter.FuguiListener listener;
    private SettingHelper dHelper;
    private VMDBManager dbUtil;

    public FuguiListAdapter(FuguiListAdapter.FuguiListener listener, Context context, ArrayList<LayerBean> fuguiList) {
        this.listener = listener;
        this.mContext = context;
        this.fuguiList = fuguiList;
        this.dHelper = SettingHelper.getInstance(mContext);
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
        FuguiListAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_fugui, parent,
                    false);
            holder = new FuguiListAdapter.ViewHolder();
            holder.tv_no = (TextView) convertView.findViewById(R.id.fugui_item_tv_no);
            holder.ll_data = (LinearLayout) convertView.findViewById(R.id.fugui_item_ll_data);
            holder.tv_delete = (TextView) convertView.findViewById(R.id.center_item_tv_delete);

            convertView.setTag(holder);
        } else {
            holder = (FuguiListAdapter.ViewHolder) convertView.getTag();
        }
        LayerBean cabinet = fuguiList.get(position);
        holder.tv_no.setText("副" + cabinet.getLayerNo());

        holder.ll_data.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.modifyFugui(position);
            }
        });

        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.deleteFugui(position);
            }
        });

        return convertView;
    }

    class ViewHolder {
        private TextView tv_no;
        private LinearLayout ll_data;
        private TextView tv_delete;
    }

    public interface FuguiListener {
        void modifyFugui(int position);

        void deleteFugui(int position);
    }

}
