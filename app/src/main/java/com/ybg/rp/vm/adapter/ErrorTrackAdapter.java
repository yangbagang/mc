package com.ybg.rp.vm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.bean.TrackError;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/29.
 */
public class ErrorTrackAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<TrackError> errorList;

    public ErrorTrackAdapter(Context context, ArrayList<TrackError> errorList) {
        this.mContext = context;
        this.errorList = errorList;
    }

    @Override
    public int getCount() {
        return errorList.size();
    }

    @Override
    public Object getItem(int position) {
        return errorList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            convertView= LayoutInflater.from(mContext).inflate(R.layout.item_list_test,parent,false);
            holder=new ViewHolder();
            holder.tv_track=(TextView)convertView.findViewById(R.id.testBtn_tv_layer);

            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }

        TrackError bean=errorList.get(position);
        holder.tv_track.setText(bean.getTrackNo());
        boolean isSelect=bean.isSelect();
        if (isSelect){
            holder.tv_track.setTextColor(mContext.getResources().getColor(android.R.color.holo_green_light));
            holder.tv_track.setBackgroundResource(R.drawable.border_bg_green_dark);
        }else {
            holder.tv_track.setBackgroundResource(R.drawable.border_bg_green);
            holder.tv_track.setTextColor(mContext.getResources().getColor(android.R.color.white));
        }
        return convertView;
    }

    class ViewHolder {
        private TextView tv_track;
    }

    /**
     * 初始化数据
     */
    public static ArrayList<TrackError> initErrorTrack(ArrayList<TrackBean> list, int position) {
        ArrayList<TrackError> errors = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TrackBean bean = list.get(i);
            TrackError track = new TrackError();
            track.setLayerNo(bean.getLayerNo());
            track.setTrackNo(bean.getTrackNo());
            track.setSelect(false);

            errors.add(track);
        }
        if (position < errors.size()) {
            errors.get(position).setSelect(true);
        }
        return errors;
    }

    /**
     * 全选 全不选
     */
    public static ArrayList<TrackError> setAllSelet(boolean selectAll, ArrayList<TrackError> list) {
        ArrayList<TrackError> errors = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setSelect(selectAll);
        }
        errors.addAll(list);
        return errors;
    }

    /**
     * 获取选中的下标
     */
    public static ArrayList<Integer> getSelectTrack(ArrayList<TrackError> list) {
        ArrayList<Integer> indexList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            TrackError error = list.get(i);
            if (error.isSelect()) {
                indexList.add(i);
            }
        }
        return indexList;
    }

}
