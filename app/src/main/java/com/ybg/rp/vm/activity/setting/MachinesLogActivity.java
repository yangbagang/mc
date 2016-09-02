package com.ybg.rp.vm.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;

import com.ybg.rp.vm.R;
import com.ybg.rp.vm.adapter.MachinesLogAdapter;
import com.ybg.rp.vm.listener.LoadMoreListener;
import com.ybg.rp.vm.listener.LoadResultCallBack;
import com.ybg.rp.vm.views.AutoLoadRecyclerView;

/**
 * Created by yangbagang on 16/8/29.
 */
public class MachinesLogActivity extends Activity implements LoadResultCallBack {

    private AutoLoadRecyclerView av_recycler;
    private RelativeLayout ll_noDataLayout;
    private SwipeRefreshLayout sl_swipeRefreshLayout;
    private MachinesLogAdapter logAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_log);

        findLayout();
        initListener();
    }

    private void findLayout() {
        av_recycler = (AutoLoadRecyclerView) findViewById(R.id.log_av_recycler);
        ll_noDataLayout = (RelativeLayout) findViewById(R.id.log_ll_noDataLayout);
        sl_swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.log_sl_swipeRefreshLayout);
    }

    /**
     * 设置监听
     */
    private void initListener() {

        /**下拉*/
        sl_swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
        sl_swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                logAdapter.loadFirst();
            }
        });
        av_recycler.setLayoutManager(new LinearLayoutManager(MachinesLogActivity.this));
        av_recycler.setHasFixedSize(true);
        /**加载更多*/
        av_recycler.setLoadMoreListener(new LoadMoreListener() {
            @Override
            public void loadMore() {
                logAdapter.loadNextPage();
            }
        });


        logAdapter = new MachinesLogAdapter(av_recycler, this);
        av_recycler.setAdapter(logAdapter);
        logAdapter.loadFirst();
    }


    @Override
    public void onSuccess(int result, Object object) {
        if (sl_swipeRefreshLayout.isRefreshing()) {
            sl_swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(int code, String msg) {
        if (sl_swipeRefreshLayout.isRefreshing()) {
            sl_swipeRefreshLayout.setRefreshing(false);
        }
    }

    public void closeWin(View view) {
        finish();
    }

}
