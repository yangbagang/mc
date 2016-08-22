package com.ybg.rp.vm.app;

import com.igexin.sdk.PushManager;
import com.ybg.rp.vmbase.app.VMApplication;

import org.xutils.x;

/**
 * Created by yangbagang on 16/8/20.
 */
public class XApplication extends VMApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        x.Ext.init(this);
        PushManager.getInstance().initialize(this.getApplicationContext());
    }
}
