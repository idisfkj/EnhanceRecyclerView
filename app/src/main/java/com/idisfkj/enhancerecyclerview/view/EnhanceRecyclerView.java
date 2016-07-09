package com.idisfkj.enhancerecyclerview.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idisfkj.enhancerecyclerview.R;
import com.idisfkj.enhancerecyclerview.adapter.WrapperRecyclerViewAdapter;

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
    private int startY = 0;
    private int endY;
    private int moveY = 0;
    private TextView text;
    private PullToRefresh pullToRefresh;

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
    }

    public EnhanceRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EnhanceRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void init() {
        text = (TextView) getHeaderView(0).findViewById(R.id.header_text);
        this.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (lastItem == adapter.getItemCount() && newState == RecyclerView.SCROLL_STATE_IDLE && !isLoad) {
                    Log.d("TAG", "--------------->OK<----------------");
                    ViewGroup.LayoutParams params = getHeaderView(0).getLayoutParams();
                    params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    getHeaderView(0).setLayoutParams(params);
                    getHeaderView(0).setVisibility(View.VISIBLE);
                    recyclerView.smoothScrollToPosition(totalCount);
                    isLoad = true;
//                    mHandler.sendEmptyMessageDelayed(1, 2000);
                }
                Log.d("TAG", firstVisible + "==");
                if (firstVisible == 0) {
                    isTop = true;
                } else {
                    isTop = false;
                    RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
                    params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                    params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                    params.setMargins(0, -100, 0, 0);
                    getHeaderView(0).setLayoutParams(params);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalCount = recyclerView.getLayoutManager().getItemCount();
                if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                    lastItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    firstVisible = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
                } else {
                    into = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPositions(into);
                    firstInto = ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPositions(firstInto);
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
                            startY = (int) event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            endY = (int) event.getY();
                            moveY = endY - startY;
                            //防止item向上滑出
                            if (moveY > 0 && !isRefreshing) {
                                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
                                params.width = RecyclerView.LayoutParams.MATCH_PARENT;
                                params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
                                //使header随moveY的值从顶部渐渐出现
                                moveY = moveY - 100;
                                params.setMargins(0, moveY, 0, 0);
                                getHeaderView(0).setLayoutParams(params);
                                if (moveY > 180) {
                                    text.setText(getResources().getString(R.string.release_to_refresh));
                                } else {
                                    text.setText(getResources().getString(R.string.pull_to_refresh));
                                }
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!isRefreshing) {
                                RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
                                params1.width = RecyclerView.LayoutParams.MATCH_PARENT;
                                params1.height = RecyclerView.LayoutParams.WRAP_CONTENT;

                                if (moveY >= 180) {
                                    text.setText(getResources().getString(R.string.refreshing));
                                    params1.setMargins(0, 0, 0, 0);
                                    isRefreshing = true;
                                    //刷新数据
                                    pullToRefresh.onRefreshing();
                                } else {
                                    params1.setMargins(0, -100, 0, 0);
                                }
                                getHeaderView(0).setLayoutParams(params1);
                            }
                            break;
                    }
                }
                return false;
            }
        });
    }

    public void addHeaderView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, -100, 0, 0);
        view.setLayoutParams(params);

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

    public interface PullToRefresh {
        void onRefreshing();
    }

    public void setPullToRefreshing(PullToRefresh pullToRefresh) {
        init();
        this.pullToRefresh = pullToRefresh;
    }

    public void setRefreshComplete() {
        RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) getHeaderView(0).getLayoutParams();
        params1.width = RecyclerView.LayoutParams.MATCH_PARENT;
        params1.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        params1.setMargins(0, -100, 0, 0);
        getHeaderView(0).setLayoutParams(params1);
        this.getAdapter().notifyDataSetChanged();
        isRefreshing = false;
    }
}
