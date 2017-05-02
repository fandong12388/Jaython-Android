package com.jaython.cc.ui;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jaython.cc.R;
import com.jaython.cc.bean.NewsComment;
import com.jaython.cc.bean.NewsContent;
import com.jaython.cc.bean.NewsDetailsPage;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.model.NewsDetailsModel;
import com.jaython.cc.data.transformer.DelayTransformer;
import com.jaython.cc.ui.adapter.NewsCommentAdapter;
import com.jaython.cc.ui.view.DynamicPhotoView;
import com.jaython.cc.ui.view.FooterView;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.LikeAnimation;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.jaython.cc.utils.helper.UIHelper;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.core.assist.ImageScaleType;
import com.tiny.volley.utils.NetworkUtil;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time:2016/11/25
 * description:
 *
 * @author sunjianfei
 */
public class NewsDetailsActivity extends BaseActivity<NewsDetailsModel> {
    private static final String KEY_PARAM = "param_id";

    @InjectView(R.id.comment_list_view)
    ListView mListView;
    @InjectView(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout mHeaderLayout;
    @InjectView(R.id.comment_edit_view)
    View mCommentEditView;
    @InjectView(R.id.comment_bottom_default_view)
    View mCommentBottomDefaultView;
    @InjectView(R.id.comment_edit_tv)
    EditText mCommentEditText;
    @InjectView(R.id.root_layout)
    View mRootLayout;
    @InjectView(R.id.comment_send_btn)
    Button mSendButton;
    //点赞
    @InjectView(R.id.news_praise)
    ImageView mPraise;
    @InjectView(R.id.news_collect_num)
    TextView mCollectNum;
    //收藏
    @InjectView(R.id.news_collect)
    ImageView mCollect;

    NewsCommentAdapter mAdapter;
    private int keyHeight;
    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    //加载更多的视图
    private FooterView mFooterView;

    private FooterView.OnLoadMoreClickListener mOnLoadMoreClickListener;
    private AbsListView.OnScrollListener mOnScrollListener;
    //评论是否已经加载完了
    private boolean mCommentFinished;

    private View.OnClickListener mOnClickListener;

    {
        this.mOnRefreshListener = this::refresh;
        this.mOnLoadMoreClickListener = v -> {
            //显示进度
            mFooterView.showLoadMorePb();
            //加载更多
            loadMore();
        };
        this.mOnScrollListener = new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (AbsListView.OnScrollListener.SCROLL_STATE_IDLE == scrollState
                        && mListView.getLastVisiblePosition() >= mAdapter.getCount() + 1
                        && !mCommentFinished
                        && mListView.getFooterViewsCount() > 0) {
                    mFooterView.showLoadMorePb();
                    loadMore();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        };
        this.mOnClickListener = v -> {
            int id = v.getId();
            if (id == R.id.news_collect) {
                boolean collected = (Boolean) v.getTag();
                if (!collected) {
                    v.setTag(true);
                    ((ImageView) v).setImageResource(R.drawable.icon_collected);
                    //1.动画
                    v.startAnimation(new LikeAnimation(2.0f, 0.8f, 1.0f));

                    String text = mCollectNum.getText().toString();
                    Integer num = Integer.valueOf(text.trim());
                    mCollectNum.setText("" + (num + 1));
                    //2.请求
                    mViewModel.requestNewsAction(1);
                }
            } else if (id == R.id.news_praise) {
                boolean praise = (Boolean) v.getTag();
                if (!praise) {
                    v.setTag(true);
                    ((ImageView) v).setImageResource(R.drawable.icon_like);
                    //1.动画
                    v.startAnimation(new LikeAnimation(2.0f, 0.8f, 1.0f));
                    //2.请求
                    mViewModel.requestNewsAction(0);
                }
            }
        };
    }

    public static void launch(Context context, int id) {
        Intent intent = new Intent(context, NewsDetailsActivity.class);
        intent.putExtra(KEY_PARAM, id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_news_details);
        ButterKnife.inject(this);
        //初始化数据
        initData();
        initView();
        //刷新数据
        refresh();
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }

    @Override
    protected int getProgressBg() {
        return R.color.bg_main;
    }

    @Override
    public void onClickRetry() {
        if (NetworkUtil.isConnected(this)) {
            showProgressView();
            refresh();
        } else {
            UIHelper.shortToast(R.string.network_error);
        }
    }

    private void initView() {
        keyHeight = ResHelper.getScreenHeight() / 3;

        //得到HeaderView
        mHeaderLayout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.vw_news_details_header_layout, null, false);

        mListView.addHeaderView(mHeaderLayout);
        mAdapter = new NewsCommentAdapter();
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright);

        //设置刷新逻辑
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
        setViewTreeObserver();

        //设置输入监听
        RxTextView.textChanges(mCommentEditText)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .map(CharSequence::toString)
                .subscribe(s -> mSendButton.setEnabled(!TextUtils.isEmpty(s)));

        //设置评论发送按钮点击事件,主要是为了加防抖操作
        RxView.clicks(mSendButton)
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .throttleFirst(1, TimeUnit.SECONDS)
                .subscribe(__ -> comment());

        mFooterView = new FooterView(this);
        mFooterView.setOnLoadMoreClickListener(mOnLoadMoreClickListener);
        mListView.setOnScrollListener(mOnScrollListener);
    }

    private void initData() {
        int id = getIntent().getIntExtra(KEY_PARAM, -1);
        mViewModel.setId(id);
    }

    private void refresh() {
        mViewModel.requestNewsContent()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(new DelayTransformer<>())
                .subscribe(this::onNext, this::onError, this::onComplete);
    }

    private void loadMore() {
        mViewModel.requestNewsComment()
                .compose(bindUntilEvent(ActivityEvent.DESTROY))
                .compose(new DelayTransformer<>())
                .subscribe(this::onLoadMoreNext, this::onError);
    }

    private void onNext(NewsDetailsPage page) {
        //1.添加header
        mHeaderLayout.removeAllViews();
        //设置标题
        //mToolbarLayout.setTitleTxt(page.getTitle());
        //1.添加顶部标题
        LayoutInflater inflater = LayoutInflater.from(this);
        View mTitleLayout = inflater.inflate(R.layout.vw_news_details_item_title, null, false);
        TextView mTitleTv = (TextView) mTitleLayout.findViewById(R.id.news_title_tv);
        TextView mNewsDataTv = (TextView) mTitleLayout.findViewById(R.id.news_data_tv);
        TextView mTopicTv = (TextView) mTitleLayout.findViewById(R.id.topic_name_tv);

        mTitleTv.setText(page.getTitle());
        mNewsDataTv.setText(getString(R.string.news_total_number, page.getVisit(), page.getComment(), page.getCollect()));
        mTopicTv.setText(page.getCategory());
        mHeaderLayout.addView(mTitleLayout, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //2.开始添加内容
        List<NewsContent> mContentList = page.getSubNewses();
        int topMargin = (int) PixelUtil.dp2px(5);
        if (ValidateUtil.isValidate(mContentList)) {
            for (NewsContent content : mContentList) {
                int type = content.getType();
                switch (type) {
                    case NewsContent.TYPE_TITLE:
                        TextView tv = (TextView) inflater.inflate(R.layout.vw_news_details_title, null, false);
                        tv.setText(content.getContent());
                        mHeaderLayout.addView(tv, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ((LinearLayout.LayoutParams) tv.getLayoutParams()).topMargin = topMargin;
                        break;
                    case NewsContent.TYPE_IMAGE: {
                        ImageView imageView = new ImageView(this);
                        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        //计算真正图片的大小
                        int screenWidth = ResHelper.getScreenWidth();
                        float imageHeight = ((screenWidth - PixelUtil.dp2px(10)) / (float) content.getWidth()) * content.getHeight();

                        //int imageHeight = (int) (screenWidth * 1.0f / content.getWidth() * content.getHeight());
                        mHeaderLayout.addView(imageView, LinearLayout.LayoutParams.MATCH_PARENT, (int) imageHeight);
                        TinyImageLoader.create(content.getContent())
                                .setQiniu(screenWidth, (int) imageHeight)
                                .setImageScaleType(ImageScaleType.EXACTLY)
                                .setDisplayType(TinyImageLoader.DISPLAY_DEFAULT)
                                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                                .into(imageView);
                        ((LinearLayout.LayoutParams) imageView.getLayoutParams()).topMargin = topMargin;
                    }
                    break;
                    case NewsContent.TYPE_VIDEO: {
                        View view = inflater.inflate(R.layout.vw_news_details_video, null, false);
                        DynamicPhotoView imageView = (DynamicPhotoView) view.findViewById(R.id.video_image);
                        imageView.setImageSize(content.getWidth(), content.getHeight());
                        mHeaderLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT);
                        TinyImageLoader.create(content.getThumb())
                                .setImageScaleType(ImageScaleType.EXACTLY)
                                .setDisplayType(TinyImageLoader.DISPLAY_DEFAULT)
                                .setDefaultDrawable(new ColorDrawable(0xffe0dedc))
                                .into(imageView);
                        ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = topMargin;
                    }
                    break;
                    case NewsContent.TYPE_TEXT:
                        TextView view = (TextView) inflater.inflate(R.layout.vw_news_details_text, null, false);
                        view.setText(content.getContent());
                        mHeaderLayout.addView(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = topMargin;
                        break;
                    default:
                        break;
                }
            }
        }
        //2.评论内容
        mAdapter.setData(page.getNewsComments());
        mAdapter.notifyDataSetChanged();
        //3.更新点赞和收藏
        if (page.isHasCollect()) {
            mCollect.setImageResource(R.drawable.icon_collected);
        } else {
            mCollect.setImageResource(R.drawable.icon_collect_normal);
            mCollect.setTag(false);
            mCollect.setOnClickListener(mOnClickListener);
        }
        //点赞
        if (page.isHasPraise()) {
            mPraise.setImageResource(R.drawable.icon_like);
        } else {
            mPraise.setImageResource(R.drawable.icon_like_normal);
            mPraise.setTag(false);
            mPraise.setOnClickListener(mOnClickListener);
        }
        //收藏的数量
        mCollectNum.setText("" + page.getCollect());

    }

    private void onLoadMoreNext(List<NewsComment> list) {
        if (ValidateUtil.isValidate(list)) {
            mAdapter.addData(list);
            mAdapter.notifyDataSetChanged();
            mFooterView.showLoadMoreTv();
            mCommentFinished = false;
        } else {
            mCommentFinished = true;
            mFooterView.hideView();
            JToast.show("没有更多评论了", this);
        }
    }

    private void onError(Throwable e) {
        if (mAdapter == null || mAdapter.getCount() <= 0) {
            showErrorView();
        }
        Logger.e(e);
    }

    private void onComplete() {
        //1.更新视图
        showContentView();
        mSwipeRefreshLayout.setRefreshing(false);
        //2.添加FooterView
        if (mAdapter.getCount() > 0) {
            mCommentFinished = false;

            if (mListView.getFooterViewsCount() <= 0
                    && mAdapter.getCount() > 10) {
                mFooterView.showLoadMoreTv();
                mListView.addFooterView(mFooterView);
            }
            if (mListView.getFooterViewsCount() > 0) {
                mFooterView.showLoadMoreTv();
            }
        }
    }

    @OnClick({R.id.comment_tv})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_tv:
                updateEditTextShowStatus(true);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mCommentEditView.getVisibility() == View.VISIBLE) {
                updateEditTextShowStatus(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 设置软键盘监听
     */
    private void setViewTreeObserver() {
        mRootLayout.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
            if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {

            } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                updateEditTextShowStatus(false);
            }
        });
    }


    /**
     * 更新底部输入框的显示状态
     *
     * @param show
     */
    private void updateEditTextShowStatus(boolean show) {
        if (show) {
            mCommentBottomDefaultView.setVisibility(View.GONE);
            mCommentEditView.setVisibility(View.VISIBLE);
            mCommentEditText.requestFocus();
            UIHelper.showSoftInput(mCommentEditText);
        } else {
            mCommentBottomDefaultView.setVisibility(View.VISIBLE);
            mCommentEditView.setVisibility(View.GONE);
            UIHelper.hideSoftInput(mCommentEditText);
        }
    }

    /**
     * 评论
     */
    private void comment() {
        if (LoginManager.getInstance().isLoginValidate()) {
            //1.获取评论内容
            String commentContent = mCommentEditText.getText().toString();
            mCommentEditText.setText("");
            //2.构建评论内容并发送请求
            NewsComment comment = new NewsComment();
            comment.setContent(commentContent);
            comment.setUserProfile(LoginManager.getInstance().getLoginUser().getUserProfile());
            mViewModel.submitComment(commentContent)
                    .compose(bindUntilEvent(ActivityEvent.DESTROY))
                    .subscribe();
            //3.重置评论输入框样式
            updateEditTextShowStatus(false);
            //4.适配器
            mAdapter.addData(0, comment);
            mAdapter.notifyDataSetChanged();
        } else {
            JToast.show(R.string.tip_login, this);
        }
    }
}
