package com.ybg.rp.vm.views;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.bumptech.glide.RequestManager;
import com.ybg.rp.vm.listener.LoadFinishCallBack;
import com.ybg.rp.vm.listener.LoadMoreListener;
import com.ybg.rp.vm.utils.ImageUtils;

/**
 * Created by yangbagang on 16/8/25.
 */
public class AutoLoadRecyclerView extends RecyclerView implements LoadFinishCallBack {

    private LoadMoreListener loadMoreListener;
    /**
     * 是否正在加载更多
     */
    private boolean isLoadingMore;

    private Context mContext;

    public AutoLoadRecyclerView(Context context) {
        this(context, null);
        this.mContext = context;
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.mContext = context;
    }

    public AutoLoadRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        this.isLoadingMore = false;
        addOnScrollListener(new AutoLoadScrollListener());
    }

    /**
     * 如果需要显示图片，需要设置这几个参数，快速滑动时，暂停图片加载
     *
     * @param pauseOnScroll 拖动时,是否暂停(false)
     * @param pauseOnFling  快速滑动时,是否暂停(true)
     */
    public void setOnPauseListenerParams(boolean pauseOnScroll, boolean pauseOnFling) {
        addOnScrollListener(new AutoLoadScrollListener(ImageUtils.getInstanceRequest(mContext),
                pauseOnScroll, pauseOnFling));
    }

    /**
     * 加载更多 事件
     *
     * @param loadMoreListener
     */
    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    /**
     * 停止加载更多
     */
    @Override
    public void loadFinish() {
        isLoadingMore = false;
    }

    /**
     * 滑动自动加载监听器
     */
    private class AutoLoadScrollListener extends OnScrollListener {

        private RequestManager manager;
        private boolean pauseOnScroll;
        private boolean pauseOnFling;

        public AutoLoadScrollListener(RequestManager manager, boolean pauseOnScroll, boolean pauseOnFling) {
            super();
            this.pauseOnScroll = pauseOnScroll;
            this.pauseOnFling = pauseOnFling;
            this.manager = manager;
        }

        public AutoLoadScrollListener() {
            super();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //由于GridLayoutManager是LinearLayoutManager子类，所以也适用
            if (getLayoutManager() instanceof LinearLayoutManager) {
                LinearLayoutManager layoutManager = ((LinearLayoutManager) getLayoutManager());
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = AutoLoadRecyclerView.this.getAdapter().getItemCount();

//                TbLog.i("!isLoadingMore  == " + (!isLoadingMore));
                if (layoutManager.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    //有回调接口，并且不是加载状态，并且剩下2个item，并且向右滑动，则自动加载
                    if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
                            2 && dx > 0) {
                        loadMoreListener.loadMore();
                        isLoadingMore = true;
                    }
                } else {
                    //有回调接口，并且不是加载状态，并且剩下2个item，并且向下滑动，则自动加载
                    if (loadMoreListener != null && !isLoadingMore && lastVisibleItem >= totalItemCount -
                            2 && dy > 0) {
                        loadMoreListener.loadMore();
                        isLoadingMore = true;
                    }
                }
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            // 图片加载器，是否停止加载
            if (manager != null) {
                switch (newState) {
                    case SCROLL_STATE_IDLE:
                        manager.resumeRequests();
                        break;
                    case SCROLL_STATE_DRAGGING:
                        if (pauseOnScroll) {
                            manager.pauseRequests();
                        } else {
                            manager.resumeRequests();
                        }
                        break;
                    case SCROLL_STATE_SETTLING:
                        if (pauseOnFling) {
                            manager.pauseRequests();
                        } else {
                            manager.resumeRequests();
                        }
                        break;
                }
            }
        }
    }

}
