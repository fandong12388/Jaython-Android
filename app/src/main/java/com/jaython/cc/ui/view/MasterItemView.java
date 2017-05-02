package com.jaython.cc.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.utils.helper.ViewUtils;


/**
 * time: 15/7/27
 * description:
 *
 * @author crab
 */
public class MasterItemView extends LinearLayout implements View.OnClickListener {
    private TextView mTextView, mRightTextView, mRightTextViewWithDrawable;
    private ImageButton mRightArrow;
    private ImageView mLeftDrawable;
    private View mLine;
    private OnMasterItemClickListener mItemClickListener;

    public MasterItemView(Context context) {
        this(context, null);
    }

    public MasterItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MasterItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //mLeftDrawable = (ImageView) findViewById(R.id.id_master_item_left_icon);
        mTextView = (TextView) findViewById(R.id.master_item_text_view);
        mRightTextView = (TextView) findViewById(R.id.master_item_right_text_view);
        mRightArrow = (ImageButton) findViewById(R.id.master_item_right_arrow_view);
        mRightTextViewWithDrawable = (TextView) findViewById(R.id.master_item_right_text_view_with_arrow);
        mLine = findViewById(R.id.master_item_line);
        setOnClickListener(this);
        mRightTextView.setOnClickListener(this);
        mTextView.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);
        mRightTextViewWithDrawable.setOnClickListener(this);
        //mLeftDrawable.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        ViewUtils.setDelayedClickable(view, 500);
        if (mItemClickListener != null) {
            mItemClickListener.onMasterItemClick(this);
        }
    }

    public void setLeftDrawableVisible(int visible) {
        //mLeftDrawable.setVisibility(visible);
    }

    public void setLeftDrawableRes(int res) {
        // mLeftDrawable.setImageResource(res);
        Drawable drawable = getResources().getDrawable(res);
        mTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
    }

    public void setLineVisible(int visible) {
        mLine.setVisibility(visible);
    }

    public void setText(int resId) {
        mTextView.setText(resId);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setRightTextViewVisible(int visible) {
        mRightTextView.setVisibility(visible);
    }

    public void setRightArrowVisible(int visible) {
        mRightArrow.setVisibility(visible);
    }

    public void setRightText(String text) {
        mRightTextView.setText(text);
    }

    public void setRightTextViewWithArrowVisible(int visible) {
        mRightTextViewWithDrawable.setVisibility(visible);
    }

    public void setRightTextWithArrowText(String text) {
        mRightTextViewWithDrawable.setText(text);
    }

    public void setItemEnable(boolean enable) {
        setEnabled(enable);
        mTextView.setEnabled(enable);
        mRightTextView.setEnabled(enable);
        mRightArrow.setEnabled(enable);
        mRightTextViewWithDrawable.setEnabled(enable);
    }

    public void setOnMasterItemClickListener(OnMasterItemClickListener listener) {
        mItemClickListener = listener;
    }

    public interface OnMasterItemClickListener {
        public void onMasterItemClick(View v);
    }
}
