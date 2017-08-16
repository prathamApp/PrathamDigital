package com.pratham.prathamdigital.custom.carousel;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
/**
 * Created by HP on 16-08-2017.
 */

import android.content.Context;

public class CustomPageTransformer implements ViewPager.PageTransformer {

    private ViewPager viewPager;

    public CustomPageTransformer(Context context) {
    }

    public void transformPage(View view, float position) {
        if (viewPager == null) {
            viewPager = (ViewPager) view.getParent();
        }
        view.setScaleY(1-Math.abs(position));
        view.setScaleX(1-Math.abs(position));
    }

}