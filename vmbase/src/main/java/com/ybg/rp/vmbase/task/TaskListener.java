package com.ybg.rp.vmbase.task;

/**
 * Created by yangbagang on 16/8/29.
 */
public class TaskListener {

    /**
     * Gets the.
     * 返回的结果对象
     */
    public void get(){};

    /**
     * 描述：执行开始后调用.
     * */
    public void update(){};

    /**
     * 监听进度变化.
     *
     * @param values the values
     */
    public void onProgressUpdate(Integer... values){};

}
