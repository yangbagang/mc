package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.bean.TrackBean;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/9/1.
 */
public class MaxSetAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TrackBean> trackList;

    public MaxSetAdapter(Context context, ArrayList<TrackBean> trackList) {
        this.mContext = context;
        this.trackList = trackList;
    }

    @Override
    public int getCount() {
        return trackList.size();
    }

    @Override
    public Object getItem(int position) {
        return trackList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_max,null);
            holder = new ViewHolder();
            holder.tv_layer = (TextView) convertView.findViewById(R.id.item_maxSet_tv_layer);
            holder.tv_max = (TextView) convertView.findViewById(R.id.item_maxSet_tv_num);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        TrackBean bean = trackList.get(position);
        holder.tv_layer.setText(bean.getTrackNo());
        /**排放量*/
        int selectMax = bean.getMaxInventory();
        holder.tv_max.setText(String.valueOf(selectMax));

        return convertView;
    }

    class ViewHolder{
        TextView tv_layer;
        TextView tv_max;
    }

}
