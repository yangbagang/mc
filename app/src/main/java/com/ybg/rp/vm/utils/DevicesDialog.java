package com.ybg.rp.vm.utils;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.ybg.rp.vm.R;
import com.ybg.rp.vmbase.preference.VMPreferences;
import com.ybg.rp.vmbase.utils.DateUtil;
import com.ybg.rp.vmbase.utils.SDUtil;
import com.ybg.rp.vmbase.utils.VMConstant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by yangbagang on 16/8/20.
 */
public class DevicesDialog extends DialogFragment {

    public static DevicesDialog newInstance(int theme) {
        DevicesDialog dialogFragment = new DevicesDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("theme", theme);
        dialogFragment.setArguments(bundle);
        return dialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int theme = getArguments().getInt("theme", 0);
        setStyle(DialogFragment.STYLE_NO_TITLE, theme);//设置样式
    }

    private int pos = 0;
    private ArrayList<String> list;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//标题
        getDialog().setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失

        View view = inflater.inflate(R.layout.dialog_device, container);
        ListView lv = (ListView) view.findViewById(R.id.lv_item_devices);
        Button cancel = (Button) view.findViewById(R.id.btn_item_cancel);
        Button saveOk = (Button) view.findViewById(R.id.btn_item_save);

        Window dialogWindow = getDialog().getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.5); // 高度设置为屏幕的0.8
        lp.height = (int) (d.heightPixels * 0.5);
//        lp.width = d.widthPixels; // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.removeDialog(getActivity());
            }
        });
        saveOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String basePath = list.get(pos);
                if (basePath.contains("sdcard")) {
                    Toast.makeText(getActivity(), "不能选中当前SD卡路径", Toast.LENGTH_SHORT).show();
                    return;
                }
                String targetDir = basePath + "/cnpay_vm_" + DateUtil.getCurrentDate(DateUtil.dateFormatYMD) + "_"
                        + VMPreferences.getInstance().getVMId();

                SDUtil.copyFolder(VMConstant.BASE_PATH, targetDir);
                DialogUtil.removeDialog(getActivity());

            }
        });

        list = SDUtil.getStorageDirectory();
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < list.size(); i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("deviceName", list.get(i));
            data.add(map);
        }
        lv.setAdapter(new SimpleAdapter(getActivity(), data, R.layout.item_device
                , new String[]{"deviceName"}, new int[]{R.id.tv_item_deivceName}));

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                view.setSelected(true);
                pos = position;
            }
        });

        return view;
    }
}
