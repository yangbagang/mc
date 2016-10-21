package com.ybg.rp.vm.help;

import android.content.Context;
import android.os.AsyncTask;

import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vmbase.callback.ResultCallback;
import com.ybg.rp.vmbase.utils.CharacterUtil;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class SettingHelper {
    private static SettingHelper helper;
    private VMDBManager dbUtil;
    private Context mContext;


    public static SettingHelper getInstance(Context context) {
        if (null == helper) {
            helper = new SettingHelper(context.getApplicationContext());
        }
        return helper;
    }

    public SettingHelper(Context context) {
        this.mContext = context;
        dbUtil = VMDBManager.getInstance();

        initMainLayer();
    }

    /**
     * 初始化主机信息
     */
    public ArrayList<LayerBean> initMainLayer() {
        try {
            ArrayList<LayerBean> listN = dbUtil.findAll(LayerBean.class, "device_type", "=", "0");
            if (listN == null || listN.size() <= 0) {
                LogUtil.i("[-初始化数据-主机层数信息-]");
                /**初始化主机 层数、和轨道数*/
                listN = new ArrayList<>();
                for (int i = 1; i <= 6; i++) {
                    LayerBean layer = new LayerBean();
                    layer.setLayerNo("0" + i);//层编号
                    layer.setTrackNum(10);//轨道数
                    layer.setDeviceType(0);//1：格子柜,0：不是格子柜

                    listN.add(layer);
                    dbUtil.getDb().saveOrUpdate(layer);
                    //dbUtil.saveLog("--初始化数据-主机信息-" + 6 + "层");
                }
                initMainTrack(listN);
                return listN;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 初始化主机轨道信息
     * 设置默认最大排放量
     *
     * @param list
     */
    public void initMainTrack(ArrayList<LayerBean> list) {
        LogUtil.i("[-初始化数据-主机轨道信息-]");
        for (int i = 0; i < list.size(); i++) {
            LayerBean lv = list.get(i);

            ArrayList<TrackBean> oldList = dbUtil.findAll(TrackBean.class, "layer_no", "=", lv.getLayerNo());
            int size = 0;
            if (oldList != null) {
                size = oldList.size();
            }
            if (size != lv.getTrackNum()) {
                for (int j = 0; j < lv.getTrackNum(); j++) {
                    TrackBean tv = new TrackBean();
                    tv.setFault(0);
                    tv.setLayerNo(lv.getLayerNo());//01-06 机器编号 0 层级1-8
                    tv.setGridMark(0);//1：格子柜,0：不是格子柜
                    tv.setTrackNo(lv.getLayerNo() + "" + j + "");// 0-9 =>010,019,060,069
                    tv.setMaxInventory(10);//最大库存
                    dbUtil.saveOrUpdate(tv);
                }
            }
        }
    }

    /**
     * 设置主机层级轨道数量
     *
     * @param layerNo 层编号
     * @param num     轨道数
     */
    public void setMainTrack(final String layerNo, final int num) {
        LogUtil.i("设置主机层级轨道数量 layerNo=" + layerNo + " ;num=" + num);
        new AsyncTask<String, Integer, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                //层级 01/02/03/04/05/06/07/08 选择轨道数据 4/5/8/10
                try {

                    LayerBean lb = dbUtil.getDb().selector(LayerBean.class).where("layer_no", "=", layerNo).findFirst();
                    if (lb != null) {
                        if (lb.getTrackNum() != num) {
                            /** 轨道数据不相同-覆盖*/
                            lb.setTrackNum(num);
                            dbUtil.getDb().saveOrUpdate(lb);

                            ArrayList<TrackBean> beanArrayList = (ArrayList<TrackBean>) dbUtil.getDb().selector(TrackBean.class).where("layer_no", "=", layerNo).findAll();
                            if (beanArrayList != null) {
                                for (int i = 0; i < beanArrayList.size(); i++) {
                                    TrackBean t = beanArrayList.get(i);
                                    dbUtil.getDb().delete(t);
                                }
                                LogUtil.e("删除数据(track_vending) - " + layerNo);
                            }

                            setTrackList(layerNo, num);
                        }
                    } else {
                        /**没有就创建*/
                        lb = new LayerBean();
                        lb.setLayerNo(layerNo);//层编号
                        lb.setTrackNum(10);//轨道数
                        lb.setDeviceType(0);//1：格子柜,0：不是格子柜
                        /*设置对应排放量*/
                        dbUtil.getDb().saveOrUpdate(lb);

                        setTrackList(layerNo, num);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return true;
            }
        }.execute();
    }

    /**
     * 设置当前层排放信息
     */
    public void setTrackList(String layerNo, int num) {
        LogUtil.i("[添加(TrackBean)  数据]");
        XApplication xApplication = (XApplication) mContext.getApplicationContext();
        dbUtil.saveLog(xApplication.getOperator(),"设置主机轨道数量 layerNo=" + layerNo + " ;num=" + num);
        for (int j = 0; j < num; j++) {
            TrackBean tv = new TrackBean();
            tv.setFault(TrackBean.FAULT_O);
            tv.setLayerNo(layerNo);//01-06 机器编号 0 层级1-8
            tv.setGridMark(0);//1：格子柜,0：不是格子柜
            tv.setTrackNo(layerNo + "" + j + "");// 0-9 =>010,019,060,069
            tv.setMaxInventory(10);//最大库存
            dbUtil.saveOrUpdate(tv);
        }
    }


    /**
     * 设置主机轨道最大排放量
     * @param trackNo 轨道号
     * @param max 最大排放数
     */
    public void setTrackmax(final String trackNo, final int max) {
        LogUtil.i("----轨道---" + trackNo + "--修改最大排放为:" + max);
        try {
            XApplication xApplication = (XApplication) mContext.getApplicationContext();
            TrackBean tb = dbUtil.getDb().selector(TrackBean.class).where("track_no", "=",
                    trackNo).findFirst();
            if (tb != null) {
                tb.setMaxInventory(max);
                dbUtil.getDb().saveOrUpdate(tb);
                dbUtil.saveLog(xApplication.getOperator(), "-轨道-" + trackNo + "--修改最大排放为:" + max);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 获取主机 层级轨道信息
     */
    public ArrayList<LayerBean> getMainLayer() {
        ArrayList<LayerBean> lyList = new ArrayList<>();
        try {
            /**读取主机信息 没有就初始化*/
            //ArrayList<LayerBean> list = dbUtil.findAll(LayerBean.class, "GRID_MARK", "=", "0");
            ArrayList<LayerBean> list = (ArrayList<LayerBean>) dbUtil.getDb().selector(LayerBean.class)
                    .where("device_type", "=", "0").orderBy("layer_no").findAll();
            if (null == list || list.size() <= 0) {
                /**初始化主机 层数、和轨道数*/
                ArrayList<LayerBean> datas = initMainLayer();
                if (null != datas && datas.size() > 0)
                    lyList.addAll(datas);
            } else {
                lyList.addAll(list);
            }
            LogUtil.i("获取主机 层级轨道信息 list=" + (null != list ? list.toString() : "NULL"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lyList;
    }

    /**
     * 获取当前层 排放量设置信息
     */
    public ArrayList<TrackBean> getTrackList(String layer) {
        ArrayList<TrackBean> trackList = new ArrayList<>();
        try {
            ArrayList<TrackBean> list = (ArrayList<TrackBean>) dbUtil.getDb().selector(TrackBean.class)
                    .where("layer_no", "=", layer)
                    .orderBy("track_no").findAll();
            LogUtil.i("[- get trackList:" + list.toString() + "-]");
            if (list != null && list.size() > 0) {
                trackList.addAll(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return trackList;
    }


    /**
     * 获取设置的格子柜信息
     */
    public ArrayList<LayerBean> getCabinetList() {
        ArrayList<LayerBean> list = new ArrayList<>();
        try {
            //1：格子柜,0：不是格子柜
            list = (ArrayList<LayerBean>) dbUtil.getDb().selector(LayerBean.class)
                    .where("device_type", "=", "1").orderBy("layer_no").findAll();
            if (list == null) {
                list = new ArrayList<LayerBean>();
            }
            LogUtil.i("获取设置的格子柜信息 list=" + list.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 获取设置的副柜信息
     */
    public ArrayList<LayerBean> getFuguiList() {
        ArrayList<LayerBean> list = new ArrayList<>();
        try {
            //1：格子柜,0：不是格子柜
            list = (ArrayList<LayerBean>) dbUtil.getDb().selector(LayerBean.class)
                    .where("device_type", "=", "2").orderBy("layer_no").findAll();
            if (list == null) {
                list = new ArrayList<LayerBean>();
            }
            LogUtil.i("获取设置的格子柜信息 list=" + list.toString());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    /**
     * 更新一条格子柜数据,有返回
     *
     * @param cabinetNo 层编号
     * @param num       数据量
     * @param max       排放量
     */
    public void setCabinet(final ResultCallback.ResultListener listener,
                           final String cabinetNo, final Integer num, final Integer max) {
        LogUtil.i("[-更新格子柜数据-" + cabinetNo + "--轨道数据:" + num+" -排放量:"+max);

        new AsyncTask<String, Integer, Boolean>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startFunction();
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                /**结果返回*/
                listener.isResultOK(aBoolean);
            }

            @Override
            protected Boolean doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                try {
                    if (cabinetNo != null && !"".equals(cabinetNo)) {
                        LayerBean layer = dbUtil.getDb().selector(LayerBean.class)
                                .where("layer_no", "=", cabinetNo).findFirst();
                        if (null != layer && layer.getTrackNum() == num) {
                            /**存在格子柜 且 柜门数对应*/

                            /**初始化对应的格子(轨道)排放量*/
                            return initCabinetTrack(cabinetNo, num, max);
                        } else {
                            /**不存在格子柜 或 柜门数不对应*/
                            if (layer == null) {
                                layer = new LayerBean();
                            }
                            layer.setDeviceType(1);
                            layer.setLayerNo(cabinetNo);////1-6
                            layer.setTrackNum(num);
                            dbUtil.saveOrUpdate(layer);

                            dbUtil.saveLog(xApplication.getOperator(), "-更新格子柜数据-" + cabinetNo +
                                    "--轨道数据:" + num);

                            /**初始化对应的格子(轨道)排放量*/
                            return initCabinetTrack(cabinetNo, num, max);
                        }

                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        }.execute();
    }

    /**
     * 初始化格子柜 格子信息
     * 基础层级信息
     *
     * @param max 排放量
     */
    public boolean initCabinetTrack(String cabinetNo, int trackNum, int max) {
        try {
            List<TrackBean> trackBeans = dbUtil.findAll(TrackBean.class, "layer_no", "=", cabinetNo);
            if (null!=trackBeans && trackBeans.size()>0){
                TrackBean bean=trackBeans.get(0);
                if (trackBeans.size() == trackNum && bean.getMaxInventory()==max) {
                    /**轨道数相同 ,统一排放量相同*/
                    return false;
                }else {
                    for (int i = 0; i < trackBeans.size(); i++) {
                        dbUtil.getDb().delete(trackBeans.get(i));
                    }
                }
            }

            for (int j = 1; j <= trackNum; j++) {
                TrackBean tb = new TrackBean();
                tb.setFault(TrackBean.FAULT_O);
                tb.setLayerNo(cabinetNo);//机器编号 1-6
                String num = CharacterUtil.autoCompZero(String.valueOf(j), 2);// 11 ,01
                tb.setTrackNo(cabinetNo + num);//102,201,312
                tb.setGridMark(1);//1：格子柜,0：不是格子柜
                tb.setMaxInventory(max);//库存
                dbUtil.saveOrUpdate(tb);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 删除数据
     * 格子柜/主机，整层
     *
     * @param cabinetNo 层编号
     */
    public void delLayer(final String cabinetNo) {
        LogUtil.i("[- 删除机器数据 cabinetNo=" + cabinetNo + "-]");
        try {
            LayerBean layer = dbUtil.getDb().selector(LayerBean.class).where("layer_no", "=", cabinetNo)
                    .findFirst();
            if (layer != null) {
                dbUtil.getDb().delete(layer);
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                dbUtil.saveLog(xApplication.getOperator(), " 删除机器数据 layer_no=" + cabinetNo);
            }
            ArrayList<TrackBean> tracks = dbUtil.findAll(TrackBean.class, "layer_no", "=", cabinetNo);
            if (tracks != null && tracks.size() > 0) {
                for (int i = 0; i < tracks.size(); i++) {
                    dbUtil.getDb().delete(tracks.get(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取错误轨道
     */
    public ArrayList<TrackBean> getErrorTack() {
        ArrayList<TrackBean> trackList = new ArrayList<>();
        /**读取轨道*/
        ArrayList<TrackBean> all = dbUtil.findAll(TrackBean.class);
        if (null != all && all.size() > 0) {
            for (int i = 0; i < all.size(); i++) {
                TrackBean t = all.get(i);
                /**加入错误轨道*/
                if (t.getFault() == 1) {
                    trackList.add(t);
                }
            }
        }
        if (trackList.size() > 0)
            LogUtil.i("有错误轨道");
        return trackList;
    }

    public void addFugui(String layerNo, int layerNum, int orbitalNum, int maxNum) {
        try {
            //插入层数据
            LayerBean layer = new LayerBean();
            layer.setLayerNo(layerNo);//层编号
            layer.setTrackNum(layerNum * orbitalNum);//轨道数
            layer.setDeviceType(2);//1：格子柜,0：不是格子柜, 2 副柜
            dbUtil.getDb().saveOrUpdate(layer);
            //插入轨道数据
            for (int i = 1; i <= layerNum; i++) {
                for (int j = 0; j < orbitalNum; j++) {
                    TrackBean tb = new TrackBean();
                    tb.setFault(TrackBean.FAULT_O);
                    tb.setLayerNo(layerNo);//层编号
                    tb.setTrackNo(layerNo + i + j);//A12,B23
                    tb.setGridMark(2);//1：格子柜,0：不是格子柜
                    tb.setMaxInventory(maxNum);//库存
                    dbUtil.saveOrUpdate(tb);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
