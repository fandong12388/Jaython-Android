package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.jaython.cc.R;
import com.jaython.cc.bean.ActionCompose;
import com.jaython.cc.data.cache.ACache;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.model.ComposeModel;
import com.jaython.cc.ui.adapter.ComposeDetailAdapter;
import com.jaython.cc.ui.view.divider.LinearLayoutManagerDivider;
import com.tiny.volley.utils.GsonUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;

/**
 * time:2017/1/15
 * description:
 *
 * @author fandong
 */
public class ComposeDetailActivity extends BaseLoginActivity<ComposeModel> {
    //组合数据
    public static final String COMPOSE_DATA = "compose_data";
    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    private Integer mId;

    private ACache mACache;

    private ActionCompose mActionCompose;
    private ComposeDetailAdapter mAdapter;

    public static void launch(Context context, Integer id) {
        Intent intent = new Intent(context, ComposeDetailActivity.class);
        intent.putExtra("composeId", id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_compose_detail);
        //1.得到控件
        ButterKnife.inject(this);
        mId = getIntent().getIntExtra("composeId", 0);
        //2.得到缓存
        mACache = ACache.get(this, COMPOSE_DATA);
        String text = mACache.getAsString(mId + "");
        if (!TextUtils.isEmpty(text)) {
            mActionCompose = GsonUtil.fromJson(text, ActionCompose.class);
            initRecyclerView();
        } else {
            //显示滚动条
            showProgressView();
        }
        //3.刷新数据
        Subscription subscription = mViewModel.getActionCompose(mId, LoginManager.getInstance().getUid())
                .subscribe(actionCompose -> {
                    mActionCompose = actionCompose;
                    if (null != mActionCompose) {
                        mACache.put(mActionCompose.getId() + "", GsonUtil.toJson(mActionCompose));
                        initRecyclerView();
                    }
                });
        addSubscription(subscription);
    }


    @OnClick(R.id.clock)
    public void onclick(View view) {
        ClockActivity.launch(this);
    }

    //初始化
    private void initRecyclerView() {
        if (null != mActionCompose) {
            //1.显示内容
            showContentView();
            //2.显示标题
            mToolbarLayout.setTitleTxt(mActionCompose.getTitle());
            this.mAdapter = new ComposeDetailAdapter(this, mActionCompose, mViewModel);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

            LinearLayoutManagerDivider divider = new LinearLayoutManagerDivider(this, LinearLayoutManagerDivider.VERTICAL_LIST, R.drawable.divider_bg);
            this.mRecyclerView.addItemDecoration(divider);

            this.mRecyclerView.setLayoutManager(layoutManager);
            this.mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    protected boolean hasBaseLayout() {
        return true;
    }
}
