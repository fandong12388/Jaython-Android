package com.jaython.cc.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by xiechaojun on 15-8-19.
 * description:用户动态图片展示的view
 *
 * @author crab
 */
public class DynamicPhotoView extends ImageView {
    private int mImageWidth = -1;
    private int mImageHeight = -1;

    public DynamicPhotoView(Context context) {
        super(context);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public DynamicPhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    public DynamicPhotoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        if (mImageHeight > 0 && mImageWidth > 0) {
            int bmpWidth = mImageWidth;
            int bmpHeight = mImageHeight;
            float ratio = bmpWidth * 1.0f / bmpHeight;
            if (ratio > 1.0f) {
                setMeasuredDimension(width, (int) (width * 0.75));
            } else if (ratio < 1.0f) {
                setMeasuredDimension(width, (int) (width * 1.15f));
            } else {
                setMeasuredDimension(width, width);
            }
        } else {
            setMeasuredDimension(width, width);
        }
    }

    /**
     * 设置实际图片的大小,防止等图片加载完成才去计算的话回到值view动态改变，
     * 效果不好
     */
    public void setImageSize(int imageWidth, int imageHeight) {
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        requestLayout();
    }

}
