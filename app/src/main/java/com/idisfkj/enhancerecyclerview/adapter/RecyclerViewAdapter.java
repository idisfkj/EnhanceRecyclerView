package com.idisfkj.enhancerecyclerview.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.idisfkj.enhancerecyclerview.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by idisfkj on 16/6/25.
 * Email : idisfkj@qq.com.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context mContext;
    private List<String> mListString;

    public RecyclerViewAdapter(Context context, List<String> listString) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mListString = listString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
//        holder.itemTv.setText(mListString.get(position));
        Glide.with(mContext).load(mListString.get(position))
                .placeholder(R.drawable.def)
                .into(holder.iv);
    }

    @Override
    public int getItemCount() {
        return mListString.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.item_tv)
//        TextView itemTv;
        @Bind(R.id.iv)
        ImageView iv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
