package com.ybg.rp.vm.serial;

import android.content.Context;

import com.ybg.rp.vm.bean.DeviceBrand;
import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.serial.factory.OperaBase;
import com.ybg.rp.vm.serial.factory.OperaFactory;
import com.ybg.rp.vmbase.utils.LogUtil;

import org.apache.log4j.Logger;

public class SerialManager {
    private static SerialManager manager;
    private Context mContext;
    private VMDBManager vmdbManager;
    /**
     * 当前使用的串口品牌
     */
    private String brand_main = DeviceBrand.yifeng;
    private String brand_cabinet = DeviceBrand.yifeng;
    private String brand_fugui = DeviceBrand.yifeng;

    private OperaBase operate;


    public static SerialManager getInstance(Context context) {
        if (null == manager) {
            manager = new SerialManager(context);
        }
        return manager;
    }

    public SerialManager(Context context) {
        this.vmdbManager = VMDBManager.getInstance();
        this.mContext=context;
        loadData();
    }


    /**
     * 初始化
     * 获取当前设置的品牌
     */
    private void loadData() {
        try {
            /**1：格子柜,0：不是格子柜*/
            /**获取主机选择的品牌*/
            DeviceBrand main_b = vmdbManager.getDb().selector(DeviceBrand.class).where("device_type", "=",
                    "0").findFirst();
            if (main_b == null) {
                main_b = new DeviceBrand();
                main_b.setDeviceType(0);
                main_b.setBrand(DeviceBrand.yifeng);
                vmdbManager.saveOrUpdate(main_b);

                this.brand_main = DeviceBrand.yifeng;
            } else {
                this.brand_main = main_b.getBrand();
            }

            /**获取格子柜的选择品牌*/
            DeviceBrand cabinet_b = vmdbManager.getDb().selector(DeviceBrand.class).where("device_type", "=",
                    "1").findFirst();
            if (cabinet_b == null) {
                cabinet_b = new DeviceBrand();
                cabinet_b.setDeviceType(1);
                cabinet_b.setBrand(DeviceBrand.yifeng);
                vmdbManager.saveOrUpdate(cabinet_b);

                this.brand_cabinet = DeviceBrand.yifeng;
            } else {
                this.brand_cabinet = cabinet_b.getBrand();
            }

            /**获取副柜的选择品牌*/
            DeviceBrand fugui_b = vmdbManager.getDb().selector(DeviceBrand.class).where
                    ("device_type", "=", "2").findFirst();
            if (fugui_b == null) {
                fugui_b = new DeviceBrand();
                fugui_b.setDeviceType(2);
                fugui_b.setBrand(DeviceBrand.yifeng);
                vmdbManager.saveOrUpdate(fugui_b);

                this.brand_fugui = DeviceBrand.yifeng;
            } else {
                this.brand_fugui = cabinet_b.getBrand();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 创造串口
     *
     * @param type 1:主机 2: 格子机 3: 副柜
     */
    public void createSerial(int type) {
        if (this.operate!=null){
            closeSerial();
        }
        /**打开新串口*/
        if (type == 1) {
            this.operate = new OperaFactory(mContext).createMain(this.brand_main);
        } else if (type == 2) {
            this.operate = new OperaFactory(mContext).createCabinet(this.brand_cabinet);
        } else if (type == 3) {
            this.operate = new OperaFactory(mContext).createFugui(this.brand_fugui);
        }
        /** 打开串口 */
        this.operate.openSerialPort();
    }


    /**
     * 关闭串口
     */
    public void closeSerial() {
        this.operate.closeSerialPort();
        this.operate=null;
    }

    /**
     * 机器开门
     * 发送指令和接收
     *
     * @param track 指定轨道
     */
    public BeanTrackSet openMachineTrack(String track) {
        LogUtil.i("打开机器轨道:" + track);
        BeanTrackSet var5 = new BeanTrackSet();
        if (track == null || "".equals(track)) {
            var5.trackStatus = 0;
            var5.errorInfo = "轨道不存在-出货";
            return var5;
        }

        var5 = this.operate.operaMachines(operate.openCommand(track));
        return var5;
    }

}
