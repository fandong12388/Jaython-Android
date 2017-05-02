package com.jaython.cc.ui.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jaython.cc.ui.widget.SquareImageView;
import com.tiny.loader.TinyImageLoader;

import java.util.List;


/**
 * time:15-10-16
 * description:九宫格图片
 *
 * @author sunjianfei
 */
public class ImageGridLayout extends AbsGridLayout<String> {


    public ImageGridLayout(Context context) {
        super(context);
    }

    public ImageGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void addChildViews(int count) {
        for (int i = 0; i < count; i++) {
            SquareImageView view = new SquareImageView(getContext());
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addView(view);
        }
    }

    @Override
    public void setData(List<String> list) {
        super.setData(list);
        int size = list.size();
        //最新的子View数量
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView view = (ImageView) getChildAt(i);
            int width = view.getMeasuredWidth();
            if (i >= size) {
                view.setVisibility(GONE);
            } else {
                String imageUrl = list.get(i);
                view.setVisibility(VISIBLE);
                TinyImageLoader.create(imageUrl)
                        .setQiniu(width, width)
                        .setDisplayType(TinyImageLoader.DISPLAY_DEFAULT)
                        .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                        .into(view);
                final int finalIndex = i;
                view.setTag(i);
                if (null != mClickListener) {
                    view.setOnClickListener(v -> {
                        int index = finalIndex + position * itemRowViewCount;
                        mClickListener.onImageClick(index, v);
                    });
                }
            }
        }
    }
}
