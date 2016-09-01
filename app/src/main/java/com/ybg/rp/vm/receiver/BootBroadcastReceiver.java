package com.ybg.rp.vm.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ybg.rp.vm.activity.WelcomeActivity;


/**
 * 自动启动程序
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String action_boot = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        //开机启动程序
        if (intent.getAction().equals(action_boot)) {
            Intent ootStartIntent = new Intent(context, WelcomeActivity.class);
            ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(ootStartIntent);
        }
    }
}
