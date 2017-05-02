package com.jaython.cc.ui.view.divider;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * time:2017/1/15
 * description:
 *
 * @author fandong
 */
public class TransparentLinearLayoutMananger extends RecyclerView.ItemDecoration {
    private int bottomSpace;
    private int leftSpace;
    private int rightSpace;
    private int topSpace;

    public TransparentLinearLayoutMananger(int l, int t, int r, int b) {
        this.leftSpace = l;
        this.topSpace = t;
        this.rightSpace = r;
        this.bottomSpace = b;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = leftSpace;
        outRect.right = rightSpace;
        outRect.top = topSpace;
        outRect.bottom = bottomSpace;
    }
}
