package com.ybg.rp.vm.listener;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.ybg.rp.vm.db.VMDBManager;
import com.ybg.rp.vm.help.SettingHelper;
import com.ybg.rp.vmbase.utils.LogUtil;

/**
 * Created by yangbagang on 16/9/1.
 */
public class TrackNoChangedListener implements TextWatcher {

    private EditText editText;
    private String trackNo;
    private SettingHelper helper;

    public TrackNoChangedListener(String trackNo, EditText editText, SettingHelper helper) {
        this.editText = editText;
        this.trackNo = trackNo;
        this.helper = helper;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        int num = 0;
        try {
            num = Integer.valueOf(editText.getText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            num = 0;
        }
        if (num != 0) {
            helper.setMainTrack(trackNo, num);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
