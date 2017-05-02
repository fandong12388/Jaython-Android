package com.jaython.cc.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.Dynamic;
import com.jaython.cc.bean.DynamicComment;
import com.jaython.cc.bean.User;
import com.jaython.cc.data.cache.ACache;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.manager.RxBusManager;
import com.jaython.cc.data.model.DynamicModel;
import com.jaython.cc.ui.DynamicEditActivity;
import com.jaython.cc.ui.adapter.DynamicAdapter;
import com.jaython.cc.ui.view.FooterView;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.divider.LinearLayoutManagerDivider;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.UIHelper;
import com.tiny.volley.utils.GsonUtil;
import com.tiny.volley.utils.NetworkUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time: 2017/1/13
 * description:
 *
 * @author fandong
 */
public class DynamicFragment extends BaseFragment<DynamicModel> {
    //保存的动态区块名称
    private static final String BLOCK_DATA = "dynamic_block";
    //保存的动态名称
    private static final String DYNAMIC_DATA = "dynamic_data";
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private DynamicAdapter mDynamicAdapter;

    private RecyclerView.OnScrollListener mOnScrollListener;

    {
        this.mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int count = mRecyclerView.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View view = mRecyclerView.getChildAt(i);
                        if ((view instanceof FooterView)
                                && !mDynamicAdapter.isCompleted()) {
                            FooterView footerView = (FooterView) view;
                            if (footerView.isShowing()) {
                                ((FooterView) view).showLoadMorePb();
                                loadMore();
                            }
                        }
                    }
                }
            }
        };
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_dynamic, container, false);
    }

    @Override
    public void initView(View view) {
        showProgressView();
        //1.得到控件
        ButterKnife.inject(this, view);
        //2.初始化recyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(mActivity
                , LinearLayoutManagerDivider.VERTICAL_LIST
                , R.drawable.divider_bg);
        divider.setMaxLast(2);
        mRecyclerView.addItemDecoration(divider);
        mDynamicAdapter = new DynamicAdapter(mActivity, mViewModel);
        mRecyclerView.setAdapter(mDynamicAdapter);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        //3.监听
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright);
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        //4.发布一条动态
        RxBusManager.register(this, EventConstant.PUBLISH_DYNAMIC, Dynamic.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dynamic -> {
                    mDynamicAdapter.addDynamic(0, dynamic);
                    mDynamicAdapter.notifyDataSetChanged();
                }, Logger::e);
    }

    public void setFrozen(boolean frozen) {
        mRecyclerView.setLayoutFrozen(frozen);
    }

    @Override
    protected void onLazyLoad() {
        //2.使用缓存
        ACache cache = ACache.get(mActivity, BLOCK_DATA);
        JSONArray content = cache.getAsJSONArray(DYNAMIC_DATA);
        ArrayList<Dynamic> mDynamics = null;
        if (content != null && content.length() > 0) {
            mDynamics = new ArrayList<>();
            for (int i = 0; i < content.length(); i++) {
                try {
                    JSONObject obj = content.getJSONObject(i);
                    Dynamic dynamic = GsonUtil.fromJson(obj.toString(), Dynamic.class);
                    mDynamics.add(dynamic);
                    if (i == content.length() - 1) {
                        mViewModel.setSp(dynamic.getCreated());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (ValidateUtil.isValidate(mDynamics)) {
            mDynamicAdapter.refreshDynamics(mDynamics);
            mDynamicAdapter.notifyDataSetChanged();
            showContentView();
        }
        //2.刷新界面
        refresh();
        //3.注册事件
        RxBusManager.register(this, EventConstant.COMMENT_DYNAMIC_PUBLISH, DynamicComment.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dynamicComment -> {
                    int position = mDynamicAdapter.addDynamicComment(dynamicComment);
                    mDynamicAdapter.notifyItemChanged(position);
                }, Logger::e);
        //登录
        RxBusManager.register(this, EventConstant.KEY_LOGIN, User.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> refresh(), Logger::e);
        //登出
        RxBusManager.register(this, EventConstant.KEY_LOGIN_OUT, String.class)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> refresh(), Logger::e);
    }

    private void refresh() {
        Subscription subscription = mViewModel.refresh()
                .subscribe(this::onRefreshNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void loadMore() {
        Subscription subscription = mViewModel.loadMore()
                .subscribe(this::onLoadMoreNext, this::onError, this::onComplete);
        addSubscription(subscription);
    }

    private void onRefreshNext(ArrayList<Dynamic> dynamics) {
        if (ValidateUtil.isValidate(dynamics)) {
            //1.保存数据
            ACache cache = ACache.get(JaythonApplication.gContext, BLOCK_DATA);
            cache.put(DYNAMIC_DATA, GsonUtil.toJson(dynamics));

            mDynamicAdapter.refreshDynamics(dynamics);
            mDynamicAdapter.notifyDataSetChanged();
            mDynamicAdapter.setCompleted(false);
        }
        showContentView();
    }

    private void onError(Throwable e) {
        if (mRecyclerView.getAdapter() == null || mDynamicAdapter.getItemCount() <= 0) {
            showErrorView();
        }
        Logger.e(e);
    }

    private void onLoadMoreNext(ArrayList<Dynamic> dynamics) {
        if (ValidateUtil.isValidate(dynamics)) {
            mDynamicAdapter.addDynamics(dynamics);
            mDynamicAdapter.notifyDataSetChanged();
            closeProgress(false);
        } else {
            closeProgress(true);
            mDynamicAdapter.setCompleted(true);
        }
    }

    private void onComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void closeProgress(boolean closeLoadMore) {
        mSwipeRefreshLayout.setRefreshing(false);
        int count = mRecyclerView.getChildCount();
        for (int i = 0; i < count; i++) {
            View view = mRecyclerView.getChildAt(i);
            if (view instanceof FooterView) {
                if (closeLoadMore) {
                    ((FooterView) view).hideView();
                } else {
                    ((FooterView) view).showLoadMoreTv();
                }
            }
        }
    }


    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(getContext())) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @OnClick(R.id.icon_input_edit)
    public void onClick(View view) {
        if (LoginManager.getInstance().isLoginValidate()) {
            DynamicEditActivity.launch(mActivity);
        } else {
            JToast.show(R.string.tip_login, mActivity);
        }
    }
}
