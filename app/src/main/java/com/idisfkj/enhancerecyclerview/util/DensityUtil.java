package com.idisfkj.enhancerecyclerview.util;

import android.content.Context;

/**
 * Created by idisfkj on 16/7/13.
 * Email : idisfkj@qq.com.
 */
public class DensityUtil {

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float dpValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue / scale + 0.5);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dipValue){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5);
    }
}
