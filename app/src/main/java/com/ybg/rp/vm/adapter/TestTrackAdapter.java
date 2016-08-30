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
 * Created by yangbagang on 16/8/29.
 */
public class TestTrackAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TrackBean> trackList;

    public TestTrackAdapter(Context context, ArrayList<TrackBean> trackList) {
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
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_list_test,null);
            holder=new ViewHolder();
            holder.tv_track=(TextView)convertView.findViewById(R.id.testBtn_tv_layer);

            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        TrackBean bean=trackList.get(position);
        holder.tv_track.setText(bean.getTrackNo()+"测试");
        return convertView;
    }

    class ViewHolder{
        private TextView tv_track;
    }

}
