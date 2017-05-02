package com.jaython.cc.ui.adapter;

import android.widget.BaseAdapter;

import com.jaython.cc.utils.ValidateUtil;

import java.util.ArrayList;
import java.util.List;


public abstract class AbsListAdapter<T> extends BaseAdapter {
    protected List<T> mData;

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public T getItem(int position) {
        if (mData != null && mData.size() > 0) {
            return mData.get(position);
        }
        return null;
    }

    public void addData(List<T> beans) {
        if (null == mData) {
            mData = new ArrayList<>();
        }
        if (ValidateUtil.isValidate(beans)) {
            mData.removeAll(beans);
            mData.addAll(beans);
        }
    }

    public void addData(T t) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (!mData.contains(t)) {
            mData.add(t);
        }
    }

    public void addData(int index, T t) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        if (index >= 0 && !mData.contains(t)) {
            mData.add(index, t);
        }
    }

    public void clear() {
        if (mData != null) {
            mData.clear();
        }
    }

    final public int getRealSize() {
        return ValidateUtil.isValidate(mData) ? mData.size() : 0;
    }

    public List<T> getData() {
        return mData;
    }

    public void setData(List<T> beans) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.clear();
        if (ValidateUtil.isValidate(beans)) {
            for (T t : beans) {
                if (!mData.contains(t)) {
                    mData.add(t);
                }
            }
        }
    }

}
