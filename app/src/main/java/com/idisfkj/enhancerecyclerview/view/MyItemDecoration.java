package com.idisfkj.enhancerecyclerview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.idisfkj.enhancerecyclerview.R;

/**
 * Created by idisfkj on 16/7/2.
 * Email : idisfkj@qq.com.
 */
public class MyItemDecoration extends RecyclerView.ItemDecoration {

    private Drawable drawable;

    public MyItemDecoration(Context context) {
        drawable = context.getResources().getDrawable(R.drawable.item_decoration_bg);
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int count = parent.getChildCount();
        int left;
        int right;
        int top;
        int bottom;
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            left = parent.getLeft() - parent.getPaddingLeft();
            right = parent.getRight() - parent.getPaddingRight();
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child.getVisibility() == View.GONE)
                    continue;
                top = child.getBottom() - child.getPaddingBottom();
                bottom = child.getBottom() + drawable.getIntrinsicHeight();
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        } else {
            //画竖线
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child.getVisibility() == View.GONE)
                    continue;
                left = child.getRight();
                right = child.getRight() + drawable.getIntrinsicWidth();
                top = child.getTop() - child.getPaddingTop();
                bottom = child.getBottom() - child.getPaddingBottom();
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
            //画横线
            for (int i = 0; i < count; i++) {
                View child = parent.getChildAt(i);
                if (child.getVisibility() == View.GONE)
                    continue;
                left = child.getLeft() - child.getPaddingLeft();
                right = child.getRight() - child.getPaddingRight();
                top = child.getBottom() - child.getPaddingBottom();
                bottom = child.getBottom() + drawable.getIntrinsicHeight();
                drawable.setBounds(left, top, right, bottom);
                drawable.draw(c);
            }
        }

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(0, 0, 0, drawable.getIntrinsicHeight());
    }
}
