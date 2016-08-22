package com.ybg.rp.vm.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.activity.shopping.GoodsWindowActivity;
import com.ybg.rp.vm.activity.shopping.TrackWindowActivity;
import com.ybg.rp.vm.views.BannerFrame;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private BannerFrame adFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        adFrame = (BannerFrame) findViewById(R.id.adFrame);
        adFrame.setImageResorces(getAdList());
    }

    private List<Integer> getAdList() {
        List<Integer> adList = new ArrayList<Integer>();
        adList.add(R.mipmap.ad1);
        adList.add(R.mipmap.ad2);
        adList.add(R.mipmap.ad3);
        adList.add(R.mipmap.ad4);
        return adList;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (adFrame != null) {
            adFrame.startPlay();
        }
    }

    @Override
    protected void onStop() {
        if (adFrame != null) {
            adFrame.stopPlay();
        }

        super.onStop();
    }

    public void openGoodsWin(View view) {
        Intent intent = new Intent(MainActivity.this, GoodsWindowActivity.class);
        startActivity(intent);
    }

    public void openTrackWin(View view) {
        Intent intent = new Intent(MainActivity.this, TrackWindowActivity.class);
        startActivity(intent);
    }

}
