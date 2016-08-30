package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ybg.rp.vm.R;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/29.
 */
public class TestRecordAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<String> list;

    public TestRecordAdapter(Context context, ArrayList<String> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_record,parent, false);
            holder = new ViewHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.trackTest_tv_item);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(list.get(position));
        return convertView;
    }

    class ViewHolder {
        TextView tv;
    }

}
