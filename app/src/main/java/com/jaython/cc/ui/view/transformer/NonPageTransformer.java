package com.jaython.cc.ui.view.transformer;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * time:2016/6/7
 * description:ViewPager切换动画
 *
 * @author sunjianfei
 */
public class NonPageTransformer implements ViewPager.PageTransformer {
    public static final ViewPager.PageTransformer INSTANCE = new NonPageTransformer();

    @Override
    public void transformPage(View page, float position) {
        page.setScaleX(0.999f);//hack
    }
}
