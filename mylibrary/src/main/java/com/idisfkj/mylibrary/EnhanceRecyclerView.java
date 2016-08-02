package com.idisfkj.mylibrary;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by idisfkj on 16/6/27.
 * Email : idisfkj@qq.com.
 */
public class EnhanceRecyclerView extends RecyclerView {
    private int lastItem;
    private int totalCount;
    private int firstVisible;
    private boolean isLoad = false;
    private boolean isTop = true;
    private boolean isRefreshing = false;
    private int[] into;
    private int[] firstInto;
    private float startY = 0;
    private float endY;
    private float moveY = 0;
    private TextView text;
    private PullToRefreshListener pullToRefresh;
    private LoadMoreListener loadMoreListener;
    private int viewHeight;

    public class FixedViewInfo {
        public View view;
        public int viewType;
    }

    public ArrayList<FixedViewInfo> mHeaderViewInfos = new ArrayList<>();
    public ArrayList<FixedViewInfo> mFooterViewInfos = new ArrayList<>();
    public Adapter mAdapter, adapter;
    private boolean isShouldSpan;
    public static final int BASE_HEADER_VIEW_TYPE = -1 << 10;
    public static final int BASE_FOOTER_VIEW_TYPE = -1 << 11;


    public EnhanceRecyclerView(Context context) {
        super(context);
        initView();
    }

    public EnhanceRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public EnhanceRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    public void initView() {
        View headerView = LayoutInflater.from(getContext()).inflate(R.layout.head_layout, null);
        View footerView = LayoutInflater.from(getContext()).inflate(R.layout.footer_layout, null);
        addHeaderView(headerView);
        addFooterView(footerView);
    }

    public void initListener() {
        text = (TextView) getHeaderView(0).findViewById(R.id.header_text);
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (lastItem == adapter.getItemCount() + 1 && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoad) {
                    ViewGroup.LayoutParams params = getFooterView(0).getLayoutParams();
                    params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    getFooterView(0).setLayoutParams(params);
                    getFooterView(0).setVisibility(View.VISIBLE);
                    smoothScrollToPosition(totalCount);
                    isLoad = true;
                    loadMoreListener.onLoadMore();
                }
                if (firstVisible == 0) {
                    isTop = true;
                } else {
                    isTop = false;
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
                    params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    params.setMargins(0, -getHeaderView(0).getHeight(), 0, 0);
                    getHeaderView(0).setLayoutParams(params);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalCount = getLayoutManager().getItemCount();
                if (getLayoutManager() instanceof LinearLayoutManager) {
                    lastItem = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                    firstVisible = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                } else {
                    into = ((StaggeredGridLayoutManager) getLayoutManager()).findLastVisibleItemPositions(into);
                    firstInto = ((StaggeredGridLayoutManager) getLayoutManager()).findFirstVisibleItemPositions(firstInto);
                    lastItem = into[0];
                    firstVisible = firstInto[0];
                }
            }
        });

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isTop) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            startY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touchMove(event);
                            break;
                        case MotionEvent.ACTION_UP:
                            touchUp();
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void touchMove(MotionEvent event) {
        endY = event.getY();
        moveY = endY - startY;
        //防止item向上滑出
        if (moveY > 0 && !isRefreshing) {
            //防止回退文本显示异常
            scrollToPosition(0);

            if (getHeaderView(0).getVisibility() == GONE)
                getHeaderView(0).setVisibility(VISIBLE);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
            params.width = RecyclerView.LayoutParams.MATCH_PARENT;
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            //使header随moveY的值从顶部渐渐出现
            if (moveY >= 400) {
                moveY = 100 + moveY / 4;
            } else {
                moveY = moveY / 2;
            }
            viewHeight = getHeaderView(0).getHeight();
            if (viewHeight <= 0)
                viewHeight = 130;
            moveY = moveY - viewHeight;
            params.setMargins(0, (int) moveY, 0, 0);
            getHeaderView(0).setLayoutParams(params);
            if (moveY > 80) {
                text.setText(getResources().getString(R.string.release_to_refresh));
            } else {
                text.setText(getResources().getString(R.string.pull_to_refresh));
            }
        } else {
            if (getHeaderView(0).getVisibility() != GONE && !isRefreshing) {
                getHeaderView(0).setVisibility(GONE);
            }
        }
    }

    public void touchUp() {
        if (!isRefreshing && (endY -startY) != 0 ) {

            RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
            params1.width = RecyclerView.LayoutParams.MATCH_PARENT;
            params1.height = RecyclerView.LayoutParams.WRAP_CONTENT;

            if (moveY >= 80) {
                text.setText(getResources().getString(R.string.refreshing));
                params1.setMargins(0, 0, 0, 0);
                isRefreshing = true;
                //刷新数据
                pullToRefresh.onRefreshing();
            } else {
                if (viewHeight <= 0)
                    viewHeight = 130;
                params1.setMargins(0, -viewHeight, 0, 0);
                getHeaderView(0).setVisibility(GONE);
            }
            getHeaderView(0).setLayoutParams(params1);
        }
    }

    public void addHeaderView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0);
//        params.setMargins(0, - 130, 0, 0);
        view.setLayoutParams(params);
        view.setVisibility(GONE);

        FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_HEADER_VIEW_TYPE + mHeaderViewInfos.size();
        mHeaderViewInfos.add(info);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public View getHeaderView(int position) {
        if (mHeaderViewInfos.isEmpty()) {
            throw new IllegalStateException("you must add a HeaderView before!");
        }
        return mHeaderViewInfos.get(position).view;
    }

    public void addFooterView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

        FixedViewInfo info = new FixedViewInfo();
        info.view = view;
        info.viewType = BASE_FOOTER_VIEW_TYPE + mFooterViewInfos.size();
        mFooterViewInfos.add(info);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public View getFooterView(int position) {
        if (mFooterViewInfos.isEmpty()) {
            throw new IllegalStateException("you must add a FooterView before!");
        }
        return mFooterViewInfos.get(position).view;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
        if (!(adapter instanceof WrapperRecyclerViewAdapter))
            mAdapter = new WrapperRecyclerViewAdapter(mHeaderViewInfos, mFooterViewInfos, adapter);
        super.setAdapter(mAdapter);

        if (isShouldSpan) {
            ((WrapperRecyclerViewAdapter) mAdapter).adjustSpanSize(this);
        }
    }

    @Override
    public Adapter getAdapter() {
        return mAdapter;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (layout instanceof GridLayoutManager || layout instanceof StaggeredGridLayoutManager)
            isShouldSpan = true;
        super.setLayoutManager(layout);
    }

    public interface PullToRefreshListener {
        void onRefreshing();
    }

    public void setPullToRefreshListener(PullToRefreshListener pullToRefresh) {
        if (loadMoreListener == null) {
            initListener();
        }
        this.pullToRefresh = pullToRefresh;
    }

    public interface LoadMoreListener {
        void onLoadMore();
    }

    public void setLoadMoreListener(LoadMoreListener loadMoreListener) {
        if (pullToRefresh == null) {
            initListener();
        }
        this.loadMoreListener = loadMoreListener;
    }

    public void setLoadMoreComplete() {
        RecyclerView.LayoutParams params = (LayoutParams) getFooterView(0).getLayoutParams();
        params.width = 0;
        params.height = 0;
        getFooterView(0).setLayoutParams(params);
        getFooterView(0).setVisibility(View.GONE);
        this.getAdapter().notifyDataSetChanged();
        isLoad = false;
    }

    public void setRefreshComplete() {
        RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
        params1.width = RecyclerView.LayoutParams.MATCH_PARENT;
        params1.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        params1.setMargins(0, -getHeaderView(0).getHeight(), 0, 0);
        getHeaderView(0).setLayoutParams(params1);
        getHeaderView(0).setVisibility(GONE);
        this.getAdapter().notifyDataSetChanged();
        isRefreshing = false;
    }
}
