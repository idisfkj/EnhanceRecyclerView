package com.idisfkj.enhancerecyclerview.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
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
    private static final int HIGHT_TYPE = 0;
    private static final int LOW_TYPE = 1;

    public RecyclerViewAdapter(Context context, List<String> listString) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mListString = listString;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == HIGHT_TYPE){
            view = mLayoutInflater.inflate(R.layout.itemh_layout,parent,false);
        }else {
            view = mLayoutInflater.inflate(R.layout.item_layout, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Glide.with(mContext).load(mListString.get(position))
                .asBitmap()
                .centerCrop()
                .error(R.mipmap.ic_launcher)
                .placeholder(R.drawable.def)
//                .into(holder.iv);
                .into(new BitmapImageViewTarget(holder.iv) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        super.onResourceReady(resource, glideAnimation);
                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {

                            @Override
                            public void onGenerated(Palette palette) {
                                ((CardView) holder.iv.getParent()).setCardBackgroundColor(palette.getMutedColor(Color.parseColor("#ffffff")));
                            }
                        });
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mListString.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv)
        ImageView iv;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

//            Point point = new Point();
//            ((Activity) iv.getContext()).getWindowManager().getDefaultDisplay().getSize(point);
//            int width = point.x;
//            ViewGroup.LayoutParams params = iv.getLayoutParams();
//            params.height = (int) (400 + Math.random() * 200);
//            iv.setLayoutParams(params);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 ==0 ){
            return HIGHT_TYPE;
        }else {
            return LOW_TYPE;
        }
    }
}
