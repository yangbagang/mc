package com.ybg.rp.vm.listener;

import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ybg.rp.vm.R;
import com.ybg.rp.vmbase.utils.LogUtil;

/**
 * Created by yangbagang on 16/8/20.
 */
public class NumTouchListener implements View.OnTouchListener {

    private EditText et_command;
    private TextView input_text;

    public NumTouchListener(EditText et_command, TextView input_text) {
        this.et_command = et_command;
        this.input_text = input_text;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            LogUtil.i("[- ACTION_DOWN -]");
        }else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            LogUtil.i("[- ACTION_MOVE -]");
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            LogUtil.i("[- ACTION_UP -]");
            getView(v);
        }else if (event.getAction() == MotionEvent.ACTION_CANCEL){
            LogUtil.i("[- ACTION_CANCEL -]");
            getView(v);
        }
        return false;
    }


    private void getView(View v){
        String text = et_command.getText().toString().trim();
        switch (v.getId()) {
            case R.id.bt_delete:
                if (text != null && !"".equals(text)) {
                    text = text.substring(0, text.length() - 1);
                    et_command.setText(text);
                }
                break;
            case R.id.bt_a:
                if (text == null || "".equals(text)) {
                    setEditText("A");
                }
                break;
            case R.id.bt_b:
                if (text == null || "".equals(text)) {
                    setEditText("B");
                }
                break;
            case R.id.bt_c:
                if (text == null || "".equals(text)) {
                    setEditText("C");
                }
                break;
            case R.id.num_0:
                setEditText("0");
                break;
            case R.id.num_01:
                setEditText("1");
                break;
            case R.id.num_02:
                setEditText("2");
                break;
            case R.id.num_03:
                setEditText("3");
                break;
            case R.id.num_04:
                setEditText("4");
                break;
            case R.id.num_05:
                setEditText("5");
                break;
            case R.id.num_06:
                setEditText("6");
                break;
            case R.id.num_07:
                setEditText("7");
                break;
            case R.id.num_08:
                setEditText("8");
                break;
            case R.id.num_09:
                setEditText("9");
                break;
        }
    }

    /**
     * 设置文本框的数值
     *
     * @param num
     */
    private void setEditText(String num) {
        String text = et_command.getText().toString().trim();

        if (text.length() == 3) {
            return;
        }
        /** 拼接轨道字符 */
        text += num;
        et_command.setText(text);
    }

}
