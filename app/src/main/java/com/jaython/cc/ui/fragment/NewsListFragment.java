package com.jaython.cc.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.bean.NewsItem;
import com.jaython.cc.data.cache.ACache;
import com.jaython.cc.data.model.NewsModel;
import com.jaython.cc.ui.NewsDetailsActivity;
import com.jaython.cc.ui.adapter.NewsListAdapter;
import com.jaython.cc.ui.view.FooterView;
import com.jaython.cc.ui.view.divider.LinearLayoutManagerDivider;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.UIHelper;
import com.tiny.volley.utils.GsonUtil;
import com.tiny.volley.utils.NetworkUtil;

import org.json.JSONArray;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time:2016/11/25
 * description:资讯列表
 *
 * @author sunjianfei
 */
public class NewsListFragment extends BaseFragment<NewsModel> {
    public static final String BLOCK_NEWS = "block_news";
    public static final String BLOCK_NEWS_KEY = "block_news_key";

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @InjectView(R.id.title_tv)
    TextView mTitleTv;
    NewsListAdapter mAdapter;
    ACache mCache;

    private NewsListAdapter.OnItemClickListener mOnItemClickListener;
    private RecyclerView.OnScrollListener mOnScrollListener;

    {
        this.mOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int count = mRecyclerView.getChildCount();
                    for (int i = 0; i < count; i++) {
                        View view = mRecyclerView.getChildAt(i);
                        if ((view instanceof FooterView) && !mAdapter.isCompleted()) {
                            ((FooterView) view).showLoadMorePb();
                            loadMore();
                        }
                    }
                }
            }
        };
        this.mOnItemClickListener = new NewsListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(View view, int id) {
                NewsDetailsActivity.launch(view.getContext(), id);
            }

            @Override
            public void onLastItemClickListener(FooterView view) {
                //1.显示正在加载
                view.showLoadMorePb();
                //2.加载更多
                loadMore();
            }
        };
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_news, container, false);
    }

    @Override
    public void initView(View view) {
        ButterKnife.inject(this, view);
        //1.初始化swipeRefreshLayout样式
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright);
        //2.显示标题
        mTitleTv.setText(R.string.news);
        //3.初始化recyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addOnScrollListener(mOnScrollListener);
        LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(mActivity, LinearLayoutManagerDivider.VERTICAL_LIST, R.drawable.divider_bg);
        divider.setMaxLast(2);
        mRecyclerView.addItemDecoration(divider);
        //3.1
        mAdapter = new NewsListAdapter();
        mAdapter.setOnItemClickListener(mOnItemClickListener);
        //4.得到缓存
        initCache();
        initRecyclerView();
        //设置刷新逻辑
        mSwipeRefreshLayout.setOnRefreshListener(this::refresh);
        //刷新数据
        refresh();
    }

    private void initCache() {
        mCache = ACache.get(mActivity, BLOCK_NEWS);
        if (mCache != null) {
            JSONArray array = mCache.getAsJSONArray(BLOCK_NEWS_KEY);
            if (null != array) {
                for (int i = 0; i < array.length(); i++) {
                    try {
                        NewsItem item = GsonUtil.fromJson(array.get(i).toString(), NewsItem.class);
                        mAdapter.addData(item);
                        //如果是最后一条记录，就将created字段放到sp当中
                        if (i == array.length() - 1) {
                            mViewModel.setSp(item.getShelvesTime());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mAdapter.getItemCount() <= 0) {
                showProgressView();
            } else {
                showContentView();
            }
        }
    }


    private void initRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
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

    private void refresh() {
        mAdapter.setCompleted(false);
        Subscription subscription = mViewModel.refresh(mAdapter::clear)
                .subscribe(this::onRefreshNext, this::onError, this::showContentView);
        addSubscription(subscription);
    }

    private void loadMore() {
        Subscription subscription = mViewModel.loadMore()
                .subscribe(this::onLoadMoreNext, this::onError);
        addSubscription(subscription);
    }

    private void onRefreshNext(List<NewsItem> items) {
        if (ValidateUtil.isValidate(items)) {
            mAdapter.setData(items);
            mAdapter.notifyDataSetChanged();
            mCache.put(BLOCK_NEWS_KEY, GsonUtil.toJson(items));
        }
        if (mAdapter.getItemCount() > 0) {
            showContentView();
        }
        closeProgress(false);
    }

    private void onLoadMoreNext(List<NewsItem> items) {
        if (ValidateUtil.isValidate(items)) {
            mAdapter.addData(items);
            mAdapter.notifyDataSetChanged();
            closeProgress(false);
        } else {
            closeProgress(true);
            mAdapter.setCompleted(true);
        }
    }

    private void onError(Throwable e) {
        if (!ValidateUtil.isValidate(mAdapter.getData())) {
            showErrorView();
        }
        Logger.e(e);
    }

    /**
     * 关闭上下拉刷新
     */
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
    protected boolean hasBaseLayout() {
        return true;
    }
}
