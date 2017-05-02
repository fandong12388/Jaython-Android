package com.jaython.cc.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.helper.ResHelper;

/**
 * time:2017/1/15
 * description:
 *
 * @author fandong
 */
public class FooterView extends RelativeLayout implements View.OnClickListener {

    private TextView mMoreTv;
    private RelativeLayout mPbLayout;
    private OnLoadMoreClickListener mOnLoadMoreClickListener;

    public FooterView(Context context) {
        super(context);
        initView(context);
    }

    public FooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);

    }

    public FooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

    }

    @TargetApi(21)
    public FooterView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = ResHelper.getScreenWidth();
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = (int) PixelUtil.dp2px(42.f);
        if (specMode == MeasureSpec.AT_MOST) {
            result = specSize;
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        }
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.compose_list_click_more_tv:
                if (null != mOnLoadMoreClickListener) {
                    mOnLoadMoreClickListener.onLoadMoreClick(v);
                }
                break;
            case R.id.compose_list_pb_layout:

                break;
            default:
                break;
        }
    }


    //1.显示加载更多
    public void showLoadMoreTv() {
        mMoreTv.setVisibility(View.VISIBLE);
        mPbLayout.setVisibility(View.GONE);
    }

    //2.显示进度条
    public void showLoadMorePb() {
        mMoreTv.setVisibility(View.GONE);
        mPbLayout.setVisibility(View.VISIBLE);
    }

    //3.所有都不显示
    public void hideView() {
        mMoreTv.setVisibility(View.GONE);
        mPbLayout.setVisibility(View.GONE);
    }

    public boolean isShowing() {
        return mMoreTv.getVisibility() == View.VISIBLE
                || mPbLayout.getVisibility() == View.VISIBLE;
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.vw_compose_list_footer_view, this);
        mMoreTv = (TextView) findViewById(R.id.compose_list_click_more_tv);
        mPbLayout = (RelativeLayout) findViewById(R.id.compose_list_pb_layout);
        mPbLayout.setOnClickListener(this);
        mMoreTv.setOnClickListener(this);
    }


    public void setOnLoadMoreClickListener(OnLoadMoreClickListener listener) {
        this.mOnLoadMoreClickListener = listener;
    }

    public interface OnLoadMoreClickListener {
        void onLoadMoreClick(View view);
    }
}
