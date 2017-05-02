package com.jaython.cc.ui.widget;

import android.content.Context;
import android.support.annotation.MenuRes;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaython.cc.R;


/**
 * time: 15/6/24
 * description: 黑色主题的标题栏
 *
 * @author sunjianfei
 */
public class ToolbarLayout extends RelativeLayout {
    public TextView mTitleTv;
    protected Toolbar mToolBar;

    private OnClickListener mOnClickListener;

    public ToolbarLayout(Context context) {
        super(context);
        // 这个构造方法仅仅为了在编辑器里面能够预览
    }

    private ToolbarLayout(Context context, View contentView) {
        super(context);
        if (null == contentView) {
            throw new IllegalArgumentException("The content view can not be null.");
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        // content view
        LayoutParams params = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        params.addRule(BELOW, R.id.toolBar);
        addView(contentView, params);
        // titlebar
        mToolBar = (Toolbar) inflater.inflate(R.layout.vw_toolbar, null);
        mTitleTv = (TextView) mToolBar.findViewById(R.id.title_tv);
        mToolBar.setNavigationIcon(R.drawable.ic_action_return);
        setBackgroundResource(android.R.color.transparent);
        int height = (int) getContext().getResources().getDimension(R.dimen.title_bar_height);
        LayoutParams toolbarParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                height);
        toolbarParams.addRule(ALIGN_PARENT_TOP, TRUE);
        addView(mToolBar, toolbarParams);

        //设置返回按钮点击事件
        mToolBar.setNavigationOnClickListener(v -> {
            if (null != mOnClickListener) {
                mOnClickListener.onLeftClick(v);
            }
        });

        //设置中间的点击事件
        mTitleTv.setOnClickListener(v -> {
            if (null != mOnClickListener) {
                mOnClickListener.onCenterClick(v);
            }
        });

        mToolBar.setOnMenuItemClickListener(item -> {
            if (null != mOnClickListener) {
                mOnClickListener.onMenuItemClick(item);
            }
            return true;
        });
    }

    public void setBackground(int resId) {
        setBackgroundResource(resId);
    }

    /**
     * 居中显示标题
     *
     * @param resId
     */
    public void setTitle(int resId) {
        mTitleTv.setVisibility(VISIBLE);
        mTitleTv.setText(resId);
    }

    public void setOnClickListener(OnClickListener listener) {
        this.mOnClickListener = listener;
    }

    /**
     * 居中显示标题
     *
     * @param text
     */
    public void setTitleTxt(Object text) {
        mTitleTv.setVisibility(VISIBLE);
        if (text instanceof Integer) {
            mTitleTv.setText((int) text);
        } else if (text instanceof String) {
            mTitleTv.setText((String) text);
        } else {
            throw new IllegalArgumentException("Icon must be resId,String!");
        }
    }

    /**
     * 设置溢出菜单
     *
     * @param menuRes
     */
    public void inflateMenu(@MenuRes int menuRes) {
        mToolBar.inflateMenu(menuRes);
    }

    public interface OnClickListener {
        void onLeftClick(View view);

        void onCenterClick(View view);

        void onMenuItemClick(MenuItem item);
    }

    public static class Builder {
        private Context mContext;
        private LayoutInflater mInflater;
        private View mContentView;

        public Builder(Context context) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Builder setContentView(View view) {
            mContentView = view;
            return this;
        }

        public Builder setContentView(int layoutId) {
            mContentView = mInflater.inflate(layoutId, null);
            return this;
        }

        public ToolbarLayout build() {
            return new ToolbarLayout(mContext, mContentView);
        }
    }
}
