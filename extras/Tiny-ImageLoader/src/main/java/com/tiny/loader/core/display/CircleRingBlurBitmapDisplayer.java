package com.tiny.loader.core.display;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.tiny.loader.core.util.GaussianBlur;
import com.tiny.loader.internal.core.assist.LoadedFrom;
import com.tiny.loader.internal.core.imageaware.ImageAware;


/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class CircleRingBlurBitmapDisplayer extends CircleBitmapDisplayer {
    private float mStrokeWidth;
    private int mColor;
    private float mRingPadding;
    private float mDepth;

    public CircleRingBlurBitmapDisplayer() {
    }

    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        GaussianBlur blurProcess = new GaussianBlur();
        Bitmap blurBitmap = blurProcess.blur(bitmap, mDepth);
        imageAware.setImageDrawable(new CircleBlurRingDrawable(blurBitmap, mStrokeWidth, mColor,
                mRingPadding));
    }

    public CircleRingBlurBitmapDisplayer setStrokeWidth(float width) {
        this.mStrokeWidth = width;
        return this;
    }

    public CircleRingBlurBitmapDisplayer setDepth(float deep) {
        this.mDepth = deep;
        return this;
    }

    public CircleRingBlurBitmapDisplayer setColor(int color) {
        this.mColor = color;
        return this;
    }

    public CircleRingBlurBitmapDisplayer setRingPadding(float ringPadding) {
        this.mRingPadding = ringPadding;
        return this;
    }

    public static class CircleBlurRingDrawable extends CircleDrawable {
        private float mStrokeWidth;
        private int mColor;
        private float mRingPadding;

        public CircleBlurRingDrawable(Bitmap bitmap, float strokeWidth, int color, float ringPadding) {
            super(bitmap);
            this.mStrokeWidth = strokeWidth;
            this.mColor = color;
            this.mRingPadding = ringPadding;
        }

        public void draw(Canvas canvas) {
            float ringMargin = mStrokeWidth;
            RectF rectRing = new RectF(this.mRect.left + ringMargin, this.mRect.top + ringMargin, this.mRect.right
                    - ringMargin, this.mRect.bottom - ringMargin);
            Paint paint1 = new Paint();
            paint1.setAntiAlias(true);
            paint1.setColor(mColor);
            paint1.setStyle(Paint.Style.STROKE);
            paint1.setStrokeWidth(mStrokeWidth * 2);
            canvas.drawOval(rectRing, paint1);
            canvas.drawCircle(this.mRect.width() / 2.0F, this.mRect.height() / 2.0F, this.mRect.width() / 2.0F
                    - mRingPadding * 2 - mStrokeWidth * 2, this.paint);
        }
    }
}
