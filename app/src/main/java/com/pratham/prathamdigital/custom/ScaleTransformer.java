package com.pratham.prathamdigital.custom;

import android.view.View;

/**
 * Created by HP on 17-08-2017.
 */

public class ScaleTransformer implements GalleryLayoutManager.ItemTransformer {

    @Override
    public void transformItem(GalleryLayoutManager layoutManager, View item, float fraction) {
        item.setPivotX(item.getWidth() / 2.0f);
        item.setPivotY(item.getHeight() / 2.0f);
        float scale = 1.1f - 0.3f * Math.abs(fraction);
        item.setScaleX(scale);
        item.setScaleY(scale);
    }
}
