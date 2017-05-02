package com.jaython.cc.ui.view;

import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class LikeAnimation extends ScaleAnimation {

    private final long INTERVAL = 200;

    private float[] changes;

    private float pivotX;
    private float pivotY;

    public LikeAnimation(float... args) {
        super(1.0f, 1.0f, 1.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        changes = args;
        if (changes != null) {
            setDuration(changes.length * INTERVAL);
            setFillAfter(true);
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (changes == null) {
            super.applyTransformation(interpolatedTime, t);
        } else {
            int size = changes.length;
            float multiplyTime = interpolatedTime * size;
            int which = (int) (multiplyTime);
            float whichInterpolatedTime = multiplyTime - which;

            float from = 1.0f;
            float to = 1.0f;
            float scale = 1.0f;

            if (which <= 0) {
                from = 1.0f;
                to = changes[which];
            } else if (which >= size) {
                from = changes[which - 1];
                to = 1.0f;
            } else {
                from = changes[which - 1];
                to = changes[which];
            }

            scale = from + ((to - from) * whichInterpolatedTime);

            t.getMatrix().setScale(scale, scale, pivotX, pivotY);
        }
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

        pivotX = width / 2;
        pivotY = height / 2;
    }
}
