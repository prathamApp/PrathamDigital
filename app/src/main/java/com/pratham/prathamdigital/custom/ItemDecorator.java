package com.pratham.prathamdigital.custom;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by HP on 02-08-2017.
 */

public class ItemDecorator extends RecyclerView.ItemDecoration {
    private final int mSpace;

    public ItemDecorator(int space) {
        this.mSpace = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = mSpace;
        outRect.right = mSpace;
        outRect.bottom = mSpace;
        outRect.top = 0;
    }
}
