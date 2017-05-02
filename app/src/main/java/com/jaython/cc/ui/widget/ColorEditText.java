package com.jaython.cc.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TextView;

import com.jaython.cc.R;

import java.lang.reflect.Field;

/**
 * time: 17/1/29
 * description:
 *
 * @author fandong
 */
public class ColorEditText extends EditText {

    public ColorEditText(Context context) {
        super(context);
        initCursorColor(context, null);
    }

    public ColorEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCursorColor(context, attrs);
    }

    public ColorEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCursorColor(context, attrs);
    }

    @TargetApi(21)
    public ColorEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initCursorColor(context, attrs);
    }

    private void initCursorColor(Context context, AttributeSet attrs) {
        if (null != attrs) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorEditText, 0, 0);
            a.getColor(R.styleable.ColorEditText_cursor, 0x00000000);
            try {
                Field field = TextView.class.getDeclaredField("mCursorDrawableRes");
                field.setAccessible(true);
                field.setInt(this, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            a.recycle();
        }
    }

}
