package com.ybg.rp.vmbase.task;

/**
 * Created by yangbagang on 16/8/29.
 */
public class TaskItem {

    /** 记录的当前索引. */
    private int position;

    /** 执行完成的回调接口. */
    private TaskListener listener;

    /**
     * Instantiates a new ab task item.
     */
    public TaskItem() {
        super();
    }

    /**
     * Instantiates a new ab task item.
     *
     * @param listener the listener
     */
    public TaskItem(TaskListener listener) {
        super();
        this.listener = listener;
    }

    /**
     * Gets the position.
     *
     * @return the position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Sets the position.
     *
     * @param position the new position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Gets the listener.
     *
     * @return the listener
     */
    public TaskListener getListener() {
        return listener;
    }

    /**
     * Sets the listener.
     *
     * @param listener the new listener
     */
    public void setListener(TaskListener listener) {
        this.listener = listener;
    }

}
