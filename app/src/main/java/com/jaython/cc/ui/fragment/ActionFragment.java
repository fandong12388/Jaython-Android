package com.jaython.cc.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.ActionGroup;
import com.jaython.cc.data.cache.ACache;
import com.jaython.cc.data.manager.ActionDownloadManager;
import com.jaython.cc.data.model.HomeModel;
import com.jaython.cc.ui.adapter.ActionPagerAdapter;
import com.jaython.cc.ui.adapter.HomeTitleAdapter;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.jaython.cc.utils.helper.UIHelper;
import com.tiny.volley.utils.GsonUtil;
import com.tiny.volley.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/11/24
 * description:
 *
 * @author fandong
 */

public class ActionFragment extends BaseFragment<HomeModel> {
    //保存的动作名称
    private static final String BLOCK_DATA = "action_block";
    //保存的动作名称
    private static final String ACTION_DATA = "action_data";
    @InjectView(R.id.title_recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.home_view_pager)
    ViewPager mViewPager;

    //需要显示的数据
    private List<ActionGroup> mGroups;
    //
    private HomeTitleAdapter mTitleAdapter;

    private ActionPagerAdapter mPagerAdapter;


    //监听
    private ViewPager.OnPageChangeListener mOnPageChangeListener;
    private HomeTitleAdapter.OnItemClickListener mOnItemClickListener;

    {
        this.mOnItemClickListener = new HomeTitleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                invalidateRecyclerView(position);
                mViewPager.setCurrentItem(position);
            }
        };
        this.mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mTitleAdapter.setSelected(position);
                invalidateRecyclerView(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_action, container, false);
    }

    @Override
    public void initView(View view) {
        //1.初始化控件
        ButterKnife.inject(this, view);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        //2.使用缓存
        ACache cache = ACache.get(mActivity, BLOCK_DATA);
        JSONArray content = cache.getAsJSONArray(ACTION_DATA);
        if (content != null && content.length() > 0) {
            mGroups = new ArrayList<>();
            for (int i = 0; i < content.length(); i++) {
                try {
                    JSONObject obj = content.getJSONObject(i);
                    mGroups.add(GsonUtil.fromJson(obj.toString(), ActionGroup.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //3.自线程开始请求数据
        Subscription subscription = mViewModel.requestActionGroup()
                .subscribe(this::onNext, this::onError, this::showContentView);
        addSubscription(subscription);
        //4.初始化界面
        initialize();
    }

    private void onNext(List<ActionGroup> list) {
        //1.缓存
        ACache cache = ACache.get(mActivity, BLOCK_DATA);
        cache.put(ACTION_DATA, GsonUtil.toJson(list));
        //2.初始化界面
        if (!ValidateUtil.isValidate(mGroups)) {
            mGroups = new ArrayList<ActionGroup>();
            mGroups.addAll(list);
            initialize();
            //3.下载
            ActionDownloadManager.getInstance().addList(mGroups);
            ActionDownloadManager.getInstance().downLoad();
        }

    }

    private void onError(Throwable e) {
        showErrorView();
        Logger.e(e);
    }


    private void initialize() {
        if (ValidateUtil.isValidate(mGroups)) {
            //1.显示内容界面
            showContentView();
            //2.初始化recyclerView
            mTitleAdapter = new HomeTitleAdapter(JaythonApplication.getCurrentActivity()
                    , mGroups);
            mTitleAdapter.setOnItemClickListener(mOnItemClickListener);
            LinearLayoutManager layoutManager = new LinearLayoutManager(
                    JaythonApplication.getCurrentActivity()
                    , LinearLayoutManager.HORIZONTAL
                    , false);
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(mTitleAdapter);
            //3.mPagerAdapter
            mPagerAdapter = new ActionPagerAdapter(mActivity, mGroups);
            mViewPager.setAdapter(mPagerAdapter);
        } else {
            showProgressView();
        }
    }

    private void invalidateRecyclerView(int position) {
        //将recyclerView滑动到中心
        int left = 0;
        int width = 0;
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            //1.样式改变
            View view = mRecyclerView.getChildAt(i);
            Integer tag = (Integer) view.getTag();
            TextView title = (TextView) view.findViewById(R.id.home_title_text);
            TextView indicator = (TextView) view.findViewById(R.id.home_title_indicator);
            if (tag == position) {
                left = view.getLeft();
                width = view.getWidth();
                TextPaint paint = title.getPaint();
                paint.setFakeBoldText(true);
                title.invalidate();
                indicator.setVisibility(View.VISIBLE);
            } else {
                TextPaint paint = title.getPaint();
                paint.setFakeBoldText(false);
                title.invalidate();
                indicator.setVisibility(View.INVISIBLE);
            }

        }
        //2.滑动
        int distance = left - (ResHelper.getScreenWidth() - width) / 2;
        mRecyclerView.scrollBy(distance, 0);
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(getContext())) {
            //1.显示内容
            showProgressView();
            //3.自线程开始请求数据
            Subscription subscription = mViewModel.requestActionGroup()
                    .subscribe(this::onNext, this::onError, this::showContentView);
            addSubscription(subscription);
        } else {
            UIHelper.shortToast(R.string.network_error);
        }

    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }
}
