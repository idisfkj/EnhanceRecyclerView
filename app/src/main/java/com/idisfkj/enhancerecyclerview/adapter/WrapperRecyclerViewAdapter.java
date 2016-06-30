package com.idisfkj.enhancerecyclerview.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.idisfkj.enhancerecyclerview.view.EnhanceRecyclerView;

import java.util.ArrayList;

/**
 * Created by idisfkj on 16/6/27.
 * Email : idisfkj@qq.com.
 */
public class WrapperRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public ArrayList<EnhanceRecyclerView.FixedViewInfo> mHeaderViewInfos;
    public ArrayList<EnhanceRecyclerView.FixedViewInfo> mFooterViewInfos;
    public RecyclerView.Adapter mAdapter;
    public ArrayList<EnhanceRecyclerView.FixedViewInfo> EMPTY_INFO_LIST = new ArrayList<>();
    private boolean isStaggered;


    public WrapperRecyclerViewAdapter(ArrayList<EnhanceRecyclerView.FixedViewInfo> headerViewInfos,
                                      ArrayList<EnhanceRecyclerView.FixedViewInfo> footerViewInfos,
                                      RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        if (headerViewInfos == null) {
            mHeaderViewInfos = EMPTY_INFO_LIST;
        } else {
            mHeaderViewInfos = headerViewInfos;
        }
        if (footerViewInfos == null) {
            mFooterViewInfos = EMPTY_INFO_LIST;
        } else {
            mFooterViewInfos = footerViewInfos;
        }
    }

    public int getHeadersCount() {
        return mHeaderViewInfos.size();
    }

    public int getFootersCount() {
        return mFooterViewInfos.size();
    }

    public boolean isEmpty() {
        return mAdapter == null;
    }

    public boolean removeHeader(View view) {
        for (int i = 0; i < mHeaderViewInfos.size(); i++) {
            if (mHeaderViewInfos.get(i).view == view) {
                mHeaderViewInfos.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(View view) {
        for (int i = 0; i < mFooterViewInfos.size(); i++) {
            if (mFooterViewInfos.get(i).view == view) {
                mFooterViewInfos.remove(i);
                return true;
            }
        }
        return false;
    }

    public void adjustSpanSize(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
            manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int numHeaders = getHeadersCount();
                    int adjPosition = position - numHeaders;
                    if (position < numHeaders || adjPosition >= mAdapter.getItemCount())
                        return manager.getSpanCount();
                    return 1;
                }
            });
        }

        if (recyclerView.getLayoutManager() instanceof StaggeredGridLayoutManager) {
            isStaggered = true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType >= EnhanceRecyclerView.BASE_HEADER_VIEW_TYPE && viewType < EnhanceRecyclerView.BASE_HEADER_VIEW_TYPE + getHeadersCount()) {
            return viewHolder(mHeaderViewInfos.get(viewType - EnhanceRecyclerView.BASE_HEADER_VIEW_TYPE).view);
        } else if (viewType >= EnhanceRecyclerView.BASE_FOOTER_VIEW_TYPE && viewType < EnhanceRecyclerView.BASE_FOOTER_VIEW_TYPE + getFootersCount()) {
            return viewHolder(mFooterViewInfos.get(viewType - EnhanceRecyclerView.BASE_FOOTER_VIEW_TYPE).view);
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return;
        }
        int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mAdapter.onBindViewHolder(holder, adjPosition);
                return;
            }
        }

    }

    @Override
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return mHeaderViewInfos.get(position).viewType;
        }
        int adjPosition = position - numHeaders;
        int adapterPosition = 0;
        if (mAdapter != null) {
            adapterPosition = mAdapter.getItemCount();
            if (adjPosition < adapterPosition) {
                return mAdapter.getItemViewType(adjPosition);
            }
        }

        return mFooterViewInfos.get(position - adapterPosition - getHeadersCount()).viewType;
    }

    @Override
    public int getItemCount() {
        if (mAdapter != null) {
            return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
        } else {
            return getHeadersCount() + getFootersCount();
        }
    }

    private RecyclerView.ViewHolder viewHolder(View itemView) {

        if (isStaggered) {
            StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT,
                    StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
            params.setFullSpan(true);
            itemView.setLayoutParams(params);
        }
        return new RecyclerView.ViewHolder(itemView) {
        };
    }
}
