package com.pratham.prathamdigital.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.pratham.prathamdigital.R;

/**
 * Created by HP on 02-08-2017.
 */

public class Label_LinearLayout extends LinearLayout{
    private int mWidth;
    private int mHeight;


    public Label_LinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Paint mPaint = new Paint();
        int color = getResources().getColor(R.color.charcoal);
        mPaint.setColor(color);
        Path mPath = new Path();
        mPath.moveTo(.0f, this.getHeight());
        mPath.lineTo(0.8f * this.getWidth(), this.getHeight());
        mPath.lineTo(this.getWidth(), 0.5f * this.getHeight());
        mPath.lineTo(0.8f * this.getWidth(), .0f);
        mPath.lineTo(.0f, .0f);
        mPath.lineTo(.0f, this.getHeight());

        canvas.clipPath(mPath);
        canvas.drawPath(mPath, mPaint);
        super.onDraw(canvas);
    }
}
