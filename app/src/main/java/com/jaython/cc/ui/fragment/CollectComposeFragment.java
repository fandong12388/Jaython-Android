package com.jaython.cc.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.ActionCompose;
import com.jaython.cc.data.cache.ACache;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.model.ComposeModel;
import com.jaython.cc.ui.ComposeDetailActivity;
import com.jaython.cc.ui.adapter.ActionComposeAdapter;
import com.jaython.cc.ui.view.FooterView;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.volley.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time: 2017/2/14
 * description:
 *
 * @author fandong
 */
public class CollectComposeFragment extends BaseFragment<ComposeModel> {

    //保存的动作名称
    private static final String BLOCK_DATA = "action_block";
    //保存的组合名称
    private static final String COMPOSE_DATA = "collect_compose_data";
    @InjectView(R.id.compose_list_view)
    ListView mListView;
    private List<ActionCompose> mCompose;
    private ActionComposeAdapter mAdapter;
    private FooterView mFooterView;
    private FooterView.OnLoadMoreClickListener mOnLoadMoreClickListener;
    private AbsListView.OnScrollListener mOnScrollListener;
    private AbsListView.OnItemClickListener mOnItemClickListener;
    //是否已经加载完毕了
    private boolean mIsCompleted;

    {
        this.mOnLoadMoreClickListener = __ -> loadMore();
        this.mOnItemClickListener = (parent, view, position, id) ->
                ComposeDetailActivity.launch(mActivity, ((ActionCompose) mAdapter.getItem(position)).getId());
        this.mOnScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (mListView.getLastVisiblePosition() >= mAdapter.getCount()
                            && !mIsCompleted) {
                        loadMore();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
    }

    public static CollectComposeFragment newInstance() {
        return new CollectComposeFragment();
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_compose, container, false);
    }

    @Override
    public void initView(View view) {
        //1.得到控件
        ButterKnife.inject(this, view);
        //2.得到缓存数据
        ACache cache = ACache.get(mActivity, BLOCK_DATA);
        JSONArray content = cache.getAsJSONArray(COMPOSE_DATA + LoginManager.getInstance().getUid());
        if (content != null && content.length() > 0) {
            mCompose = new ArrayList<>();
            for (int i = 0; i < content.length(); i++) {
                try {
                    JSONObject obj = content.getJSONObject(i);
                    mCompose.add(GsonUtil.fromJson(obj.toString(), ActionCompose.class));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //3.初始化界面
        initialize();
        //4.自线程开始请求数据
        Subscription subscription = mViewModel.refreshCollectCompose()
                .subscribe(this::onNext, this::onError);
        addSubscription(subscription);

    }


    private void onNext(List<ActionCompose> composes) {
        ACache cache = ACache.get(mActivity, BLOCK_DATA);
        cache.put(COMPOSE_DATA + LoginManager.getInstance().getUid(), GsonUtil.toJson(composes));
        if (mCompose == null) {
            mCompose = new ArrayList<>();
        } else {
            mCompose.clear();
        }
        mCompose.addAll(composes);
        if (mListView.getAdapter() == null) {
            if (ValidateUtil.isValidate(mCompose)) {
                initialize();
            } else {
                showEmptyView();
            }
        } else {
            mAdapter.clearCompose();
            mAdapter.addCompose(mCompose);
            if (mAdapter.getCount() > 0) {
                mAdapter.notifyDataSetChanged();
                //footer
                if (composes.size() >= 10 && composes.size() % 10 == 0) {
                    mFooterView.showLoadMoreTv();
                } else {
                    mIsCompleted = true;
                    mFooterView.hideView();
                }
            } else {
                showEmptyView();
            }
        }
    }

    private void onError(Throwable e) {
        if (!ValidateUtil.isValidate(mCompose)) {
            showErrorView();
        }
        Logger.e(e);
    }

    //加载更多
    private void loadMore() {
        //1.换成正在加载
        mFooterView.showLoadMorePb();
        //2.加载更多
        String sp = null;
        if (ValidateUtil.isValidate(mCompose)) {
            int size = mCompose.size();
            sp = mCompose.get(size - 1).getShowd();
        }
        Subscription subscription = mViewModel.loadMoreCollectCompose(sp)
                .subscribe(this::onLoadMoreNext, this::onError);
        addSubscription(subscription);
    }

    private void onLoadMoreNext(List<ActionCompose> composes) {
        if (ValidateUtil.isValidate(composes)) {
            mCompose.addAll(composes);
            mAdapter.addCompose(composes);
            mAdapter.notifyDataSetChanged();
            mFooterView.showLoadMoreTv();
        } else {
            mIsCompleted = true;
            mFooterView.hideView();
        }
    }


    private void initialize() {
        if (ValidateUtil.isValidate(mCompose)) {
            //1.显示内容界面
            showContentView();
            //2.初始化listview
            mAdapter = new ActionComposeAdapter(
                    JaythonApplication.getCurrentActivity(),
                    mCompose,
                    mViewModel
            );
            mAdapter.setCollected(true);
            mListView.setAdapter(mAdapter);
            mListView.setOnScrollListener(mOnScrollListener);
            mListView.setOnItemClickListener(mOnItemClickListener);
            mFooterView = new FooterView(JaythonApplication.getCurrentActivity());
            mFooterView.setOnLoadMoreClickListener(mOnLoadMoreClickListener);
            if (mCompose.size() >= 10 && mCompose.size() % 10 == 0) {
                mFooterView.showLoadMoreTv();
            } else {
                mIsCompleted = true;
                mFooterView.hideView();
            }
            mListView.addFooterView(mFooterView);
        } else {
            showProgressView();
        }
    }


    @Override
    protected boolean hasBaseLayout() {
        return true;
    }
}
