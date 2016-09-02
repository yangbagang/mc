package com.ybg.rp.vm.listener;

/**
 * Created by yangbagang on 16/9/2.
 */
public interface LoadResultCallBack {

    int SUCCESS_OK = 1001;
    int SUCCESS_NONE = 1002;
    int ERROR_NET = 1003;

    void onSuccess(int result, Object object);

    void onError(int code, String msg);

}
