package com.ybg.rp.vm.help;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import com.ybg.rp.vm.app.XApplication;
import com.ybg.rp.vm.bean.ErrorTrackNo;
import com.ybg.rp.vm.bean.LayerBean;
import com.ybg.rp.vm.bean.TrackBean;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.serial.BeanTrackSet;
import com.ybg.rp.vm.serial.SerialManager;
import com.ybg.rp.vm.utils.VMRequest;
import com.ybg.rp.vmbase.callback.ResultCallback;
import com.ybg.rp.vmbase.task.TaskItem;
import com.ybg.rp.vmbase.task.TaskObjectListener;
import com.ybg.rp.vmbase.task.TaskQueue;
import com.ybg.rp.vmbase.utils.LogUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import java.util.ArrayList;

public class DeviceHelper {

    private static DeviceHelper helper;
    private SerialManager manager;
    private VMDBManager dbUtil;
    private TaskQueue mTaskQueue;
    private Context mContext;

    public static DeviceHelper getInstance(Context context) {
        if (null == helper) {
            helper = new DeviceHelper(context.getApplicationContext());
        }
        return helper;
    }

    public DeviceHelper(Context context) {
        this.mContext = context;
        dbUtil = VMDBManager.getInstance();
        manager = SerialManager.getInstance(mContext);
        mTaskQueue = TaskQueue.getInstance();
    }


    /**
     * 测试主机全部
     */
    public void testMainAll(final ArrayList<LayerBean> lvs, final ResultCallback.ReturnListener listener) {
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    for (int i = 0; i < lvs.size(); i++) {
                        LayerBean bean = lvs.get(i);
                        LogUtil.i("打开轨道(层)：" + bean.getLayerNo());
                        dbUtil.saveLog(xApplication.getOperator(), "打开轨道(层)：" + bean.getLayerNo());
                        ArrayList<TrackBean> list = (ArrayList<TrackBean>) dbUtil.getDb().selector(TrackBean.class)
                                .where("device_type", "=", "0").and("layer_no", "=", bean
                                        .getLayerNo()).orderBy("track_no").findAll();

                        manager.createSerial(1);// 1:主机 2:格子柜
                        if (null != list && list.size() > 0) {
                            for (int j = 0; j < list.size(); j++) {
                                TrackBean track = list.get(j);
                                String str = track.getTrackNo() + "轨道：";
                                BeanTrackSet trackSet = manager.openMachineTrack(track.getTrackNo());//未定
                                if (trackSet.trackStatus == 1) {
                                    str += "电机正常";
                                } else {
                                    str += trackSet.errorInfo;
                                    track.setFault(TrackBean.FAULT_E);
                                    dbUtil.saveOrUpdate(track);
                                    upLoadTaskQueue(track.getTrackNo(), trackSet.errorInfo);
                                    LogUtil.e("轨道错误-------" + str);
                                }
                                listStr.add(str);
                                SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
                            }
                        }
                        manager.closeSerial();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }


    /**
     * 测试主机 单层轨道
     *
     * @param layerBean 层级数据
     * @param listener  监听
     */
    public void testMainLayer(final LayerBean layerBean, final ResultCallback.ReturnListener listener) {
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    LogUtil.i("打开轨道(层)：" + layerBean.getLayerNo());
                    dbUtil.saveLog(xApplication.getOperator(), "打开轨道(层)：" + layerBean.getLayerNo());
                    ArrayList<TrackBean> list = (ArrayList<TrackBean>) dbUtil.getDb().selector
                            (TrackBean.class).where("device_type", "=", "0")
                            .and("layer_no", "=", layerBean.getLayerNo()).orderBy("track_no").findAll();

                    manager.createSerial(1);// 1:主机 2:格子柜
                    if (null != list && list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            TrackBean track = list.get(i);
                            String str = track.getTrackNo() + "轨道：";
                            BeanTrackSet trackSet = manager.openMachineTrack(track.getTrackNo());
                            if (trackSet.trackStatus == 1) {
                                str += "电机正常";
                            } else {
                                str += trackSet.errorInfo;
                                track.setFault(TrackBean.FAULT_E);
                                dbUtil.saveOrUpdate(track);
                                upLoadTaskQueue(track.getTrackNo(), trackSet.errorInfo);
                                LogUtil.e("轨道错误-------" + str);
                            }
                            listStr.add(str);
                            SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
                        }
                    }
                    manager.closeSerial();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }

    /**
     * 测试主机 单个轨道
     *
     * @param track    轨道数据
     * @param listener 监听
     */
    public void testMainTrack(final TrackBean track, final ResultCallback.ReturnListener listener) {

        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    LogUtil.i("打开轨道(单)：" + track.getTrackNo());
                    dbUtil.saveLog(xApplication.getOperator(), "打开轨道(单)：" + track.getTrackNo());
                    String str = track.getTrackNo() + "轨道：";

                    manager.createSerial(1);// 1:主机 2:格子柜
                    BeanTrackSet trackSet = manager.openMachineTrack(track.getTrackNo());
                    manager.closeSerial();

                    if (trackSet.trackStatus == 1) {
                        str += "电机正常";
                    } else {
                        str += trackSet.errorInfo;
                        track.setFault(TrackBean.FAULT_E);
                        dbUtil.saveOrUpdate(track);
                        upLoadTaskQueue(track.getTrackNo(), trackSet.errorInfo);
                        LogUtil.e("轨道错误-------" + str);
                    }
                    listStr = new ArrayList<String>();
                    listStr.add(str);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }

    /**
     * 测试格子柜 所有
     *
     * @param datas    所有格子柜轨道数据
     * @param listener 监听
     */
    public void testCabinetAll(final ArrayList<LayerBean> datas,
                               final ResultCallback.ReturnListener listener) {
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    for (int i = 0; i < datas.size(); i++) {
                        LayerBean bean = datas.get(i);
                        LogUtil.i("打开格子柜(层)：" + bean.getLayerNo());
                        dbUtil.saveLog(xApplication.getOperator(), "打开格子柜(层)：" + bean.getLayerNo());
                        ArrayList<TrackBean> list = (ArrayList<TrackBean>) dbUtil.getDb()
                                .selector(TrackBean.class).where("device_type", "=", "1")
                                .and("layer_no", "=", bean.getLayerNo()).orderBy("track_no").findAll();

                        manager.createSerial(2);// 1:主机 2:格子柜
                        if (null != list && list.size() > 0) {
                            for (int j = 0; j < list.size(); j++) {
                                TrackBean track = list.get(j);
                                String str = track.getTrackNo() + "格子轨道：";
                                BeanTrackSet trackSet = manager.openMachineTrack(track.getTrackNo());
                                if (trackSet.trackStatus == 1) {
                                    str += "电机正常";
                                } else {
                                    str += trackSet.errorInfo;
                                    track.setFault(TrackBean.FAULT_E);
                                    dbUtil.saveOrUpdate(track);
                                    upLoadTaskQueue(track.getTrackNo(), trackSet.errorInfo);
                                    LogUtil.e("轨道错误-------" + str);
                                }
                                listStr.add(str);
                                SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
                            }
                        }
                        manager.closeSerial();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }

    /**
     * 测试格子柜 单个格子柜
     *
     * @param layer    层级轨道数据u
     * @param listener 监听
     */
    public void testCabinetLayer(final LayerBean layer, final ResultCallback.ReturnListener listener) {
        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    ArrayList<TrackBean> itemList = (ArrayList<TrackBean>) dbUtil.getDb().selector(TrackBean.class).
                            where("layer_no", "=", layer.getLayerNo()).and("device_type", "=", "1")
                            .orderBy("track_no").findAll();
                    LogUtil.i("打开格子柜(层)：" + layer.getLayerNo());
                    dbUtil.saveLog(xApplication.getOperator(), "打开格子柜(层)：" + layer.getLayerNo());

                    manager.createSerial(2);// 1:主机 2:格子柜

                    if (null != itemList && itemList.size() > 0) {
                        for (int i = 0; i < itemList.size(); i++) {
                            TrackBean track = itemList.get(i);
                            LogUtil.i("-----TrackNo = " + track.getTrackNo());
                            String str = track.getTrackNo() + "-格子轨道：";
                            BeanTrackSet trackSet = manager.openMachineTrack(track.getTrackNo());
                            if (trackSet.trackStatus == 1) {
                                str += "电机正常";
                            } else {
                                str += trackSet.errorInfo;
                                track.setFault(TrackBean.FAULT_E);
                                dbUtil.saveOrUpdate(track);
                                upLoadTaskQueue(track.getTrackNo(), trackSet.errorInfo);
                                LogUtil.e("轨道错误-------" + str);
                            }
                            listStr.add(str);
                            SystemClock.sleep(VMConstant.CYCLE_INTERVAL);
                        }
                    }
                    manager.closeSerial();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }

    /**
     * 测试格子柜 单个格子
     */
    public void testCabinetItem(final TrackBean bean, final ResultCallback.ReturnListener listener) {

        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    LogUtil.i("打开格子柜(单)：" + bean.getTrackNo());
                    dbUtil.saveLog(xApplication.getOperator(), "打开格子柜(单)：" + bean.getTrackNo());
                    String str = bean.getTrackNo() + "格子轨道：";

                    manager.createSerial(2);// 1:主机 2:格子柜
                    BeanTrackSet trackSet = manager.openMachineTrack(bean.getTrackNo());
                    manager.closeSerial();
                    if (trackSet.trackStatus == 1) {
                        str += "电机正常";
                    } else {
                        str += trackSet.errorInfo;
                        bean.setFault(TrackBean.FAULT_E);
                        dbUtil.saveOrUpdate(bean);
                        upLoadTaskQueue(bean.getLayerNo(), trackSet.errorInfo);
                        LogUtil.e("轨道错误-------" + str);
                    }
                    listStr = new ArrayList<>();
                    listStr.add(str);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }

    /**
     * 测试副柜柜 单个轨道
     */
    public void testFuguiItem(final TrackBean bean, final ResultCallback.ReturnListener listener) {

        new AsyncTask<String, Integer, ArrayList<String>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                /**启动*/
                listener.startRecord();
            }

            @Override
            protected void onPostExecute(ArrayList<String> list) {
                super.onPostExecute(list);
                /**返回*/
                listener.returnRecord(list);
            }

            @Override
            protected ArrayList<String> doInBackground(String... params) {
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                ArrayList<String> listStr = new ArrayList<String>();
                try {
                    LogUtil.i("打开副柜(单)：" + bean.getTrackNo());
                    dbUtil.saveLog(xApplication.getOperator(), "打开副柜(单)：" + bean.getTrackNo());
                    String str = bean.getTrackNo() + "副柜轨道：";

                    manager.createSerial(3);// 1:主机 2:格子柜 3:副柜
                    BeanTrackSet trackSet = manager.openMachineTrack(bean.getTrackNo());
                    manager.closeSerial();
                    if (trackSet.trackStatus == 1) {
                        str += "电机正常";
                    } else {
                        str += trackSet.errorInfo;
                        bean.setFault(TrackBean.FAULT_E);
                        dbUtil.saveOrUpdate(bean);
                        upLoadTaskQueue(bean.getLayerNo(), trackSet.errorInfo);
                        LogUtil.e("副柜轨道错误-------" + str);
                    }
                    listStr = new ArrayList<>();
                    listStr.add(str);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                return listStr;
            }
        }.execute();
    }


    /**
     * 队列上传错误轨道信息
     *
     * @param trackNo  错误轨道
     * @param errorMsg 错误信息
     */
    private void upLoadTaskQueue(final String trackNo, final String errorMsg) {
        TaskItem mTaskItem = new TaskItem(new TaskObjectListener() {
            @Override
            public <T> void update(T obj) {
            }

            @Override
            public <T> T getObject() {
                ErrorTrackNo errorTrackNo = new ErrorTrackNo();
                errorTrackNo.setTrackNo(trackNo);
                errorTrackNo.setErrMsg(errorMsg);
                XApplication xApplication = (XApplication) mContext.getApplicationContext();
                VMRequest.getInstance(mContext).addFaultInfo(xApplication.getOperator(),
                        errorTrackNo);
                return null;
            }
        });
        mTaskQueue.execute(mTaskItem);
    }
}
