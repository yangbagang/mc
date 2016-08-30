package com.ybg.rp.vmbase.callback;

import java.util.ArrayList;

/**
 * Created by yangbagang on 16/8/29.
 */
public class ResultCallback {

    public interface ReturnListener {
        /**
         * 启动
         */
        public void startRecord();

        /**
         * 返回结果
         */
        public void returnRecord(ArrayList<String> list);
    }

    public interface ResultListener {
        /**
         * 启动
         */
        public void startFunction();

        /**
         * 返回结果
         */
        public void isResultOK(Boolean ok);
    }

    /**
     * 用于最大排放量的设置
     */
    public interface MaxListener {
        public void selectMax(int max);
    }

}
