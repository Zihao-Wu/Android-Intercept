package com.wzh.androidintercept.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wzh.androidintercept.R;

/**
 * <li>Package:com.jx.jingxuan.controller.home</li>
 * <li>Author: Administrator  </li>
 * <li>Date: 2019/3/19</li>
 * <li>Description:   </li>
 */
public class CommonItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;
    private Paint mPaint;
    private boolean leftMarin;//左边需要边距
    private int leftMarinWidth;//左边距宽度
    private boolean rightMarin;//右边需要边距
    private int rightMarinWidth;//右边距宽度
    private int spaceHeight = 1;//分割线宽度


    public CommonItemDecoration(Context context, int drawableId) {
        mDivider = ContextCompat.getDrawable(context, drawableId);
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.gray_E5E5E5));
    }

    public CommonItemDecoration(Context context, int drawableId, boolean leftMarin, boolean rightMarin) {
        this(context, drawableId);
        this.leftMarin = leftMarin;
        this.rightMarin = rightMarin;
    }

    public CommonItemDecoration(Context context, int drawableId, boolean leftMarin, boolean rightMarin, int spaceHeight) {
        this(context, drawableId);
        this.leftMarin = leftMarin;
        this.rightMarin = rightMarin;
        this.spaceHeight = spaceHeight;
    }

    public CommonItemDecoration(Context context, int drawableId, boolean leftMarin, boolean rightMarin, int leftMarinWidth, int rightMarinWidth, int spaceHeight) {
        this(context, drawableId);
        this.leftMarin = leftMarin;
        this.rightMarin = rightMarin;
        this.leftMarinWidth = leftMarinWidth;
        this.rightMarinWidth = rightMarinWidth;
        this.spaceHeight = spaceHeight;
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        drawHorizontal(c, parent);
    }

    //绘制横向 item 分割线
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        int marinLeft = 0;
        int marinRight = 0;
        if (leftMarin) {
            marinLeft = leftMarinWidth == 0 ? 36 : leftMarinWidth;
        }
        if (rightMarin) {
            marinRight = rightMarinWidth == 0 ? 36 : rightMarinWidth;
        }
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + spaceHeight;
            if (mDivider != null) {
                mDivider.setBounds(marinLeft, top, right - marinRight, bottom);
                mDivider.draw(canvas);
            } else if (mPaint != null) {
                canvas.drawRect(marinLeft, top, right - marinRight, bottom, mPaint);
            }
        }
    }
}
