package com.ybg.rp.vmbase.task;

import java.util.List;

/**
 * Created by yangbagang on 16/8/29.
 */
public abstract class TaskListListener extends TaskListener {

    /**
     * Gets the list.
     *
     * @return 返回的结果列表
     */
    public abstract List<?> getList();

    /**
     * 描述：执行完成后回调.
     * 不管成功与否都会执行
     * @param paramList 返回的List
     */
    public abstract void update(List<?> paramList);

}
