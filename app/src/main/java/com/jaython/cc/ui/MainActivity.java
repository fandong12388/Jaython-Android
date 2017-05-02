package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.DynamicComment;
import com.jaython.cc.bean.MainFragment;
import com.jaython.cc.bean.Version;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.manager.AppInitManager;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.manager.RxBusManager;
import com.jaython.cc.data.manager.VersionManager;
import com.jaython.cc.data.model.AppModel;
import com.jaython.cc.data.model.DynamicModel;
import com.jaython.cc.ui.adapter.TabSelectedAdapter;
import com.jaython.cc.ui.fragment.DynamicFragment;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.helper.DialogHelper;
import com.jaython.cc.utils.helper.ResHelper;
import com.jaython.cc.utils.helper.UIHelper;
import com.tiny.volley.core.response.HttpResponse;
import com.tiny.volley.utils.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Subscription;

/**
 * time: 2016/11/21 0021
 * description:
 *
 * @author sunjianfei
 */
public class MainActivity extends BaseLoginActivity {
    @InjectView(R.id.navigate_tab_layout)
    TabLayout mTabLayout;

    //--动态评论
    @InjectView(R.id.dynamic_comment_view)
    View mCommentEditView;
    @InjectView(R.id.comment_edit_tv)
    EditText mCommentEditText;
    @InjectView(R.id.root_layout)
    View mRootLayout;
    @InjectView(R.id.comment_send_btn)
    Button mSendLayout;
    @InjectView(R.id.delete_all)
    ImageView mDeleteAll;

    //评论的dynamicId
    private Integer mDynamicId;

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener;

    private FragmentManager mFragmentManager;

    private MainFragment[] mFragments;
    private DynamicModel mDynamicModel;

    private View.OnClickListener mDynamicOnClickListener;

    {
        this.mOnTabSelectedListener = new TabSelectedAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                showFragment(mFragments[tab.getPosition()].getFragment());
            }
        };
        this.mDynamicOnClickListener = v -> {
            if (v.getId() == R.id.delete_all) {
                mCommentEditText.setText("");
            } else if (v.getId() == R.id.comment_send_btn) {
                String comment = null;
                if (mCommentEditText.getEditableText() == null
                        || TextUtils.isEmpty(comment = mCommentEditText.getEditableText().toString())) {
                    JToast.show("评论内容不能为空！", this);
                    return;
                }

                if (NetworkUtil.isAvailable(this)) {
                    //构建一个Dynamic
                    DynamicComment dynamicComment = new DynamicComment();
                    dynamicComment.setUid(LoginManager.getInstance().getUid());
                    dynamicComment.setUser(LoginManager.getInstance().getLoginUser().getUserProfile());
                    dynamicComment.setType(0);
                    dynamicComment.setContent(comment);
                    dynamicComment.setDynamicId(mDynamicId);
                    //请求数据
                    mDynamicModel.requestComment(dynamicComment);
                    //构建查数据
                    RxBusManager.post(EventConstant.COMMENT_DYNAMIC_PUBLISH, dynamicComment);
                    //
                    updateEditTextShowStatus(false);
                } else {
                    JToast.show("您没有连接到网络！", this);
                }
            }
        };
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);
        ButterKnife.inject(this);
        //1.初始化View
        initView();
        //2.注册动态的事件
        mDynamicModel = new DynamicModel();
        RxBusManager.register(this, EventConstant.COMMENT_DYNAMIC, Integer.class)
                .subscribe(id -> {
                    updateEditTextShowStatus(true);
                    mDynamicId = id;
                }, Logger::e);
        //3.关闭activity统计
        MobclickAgent.openActivityDurationTrack(false);
        //3.检查更新
        AppModel model = new AppModel();
        Subscription subscription = model.requestVersionUpdate()
                .subscribe(this::onVersionNext
                        , Logger::e
                        , VersionManager.getInstance()::onUpdateComplete);
        addSubscription(subscription);
        //4.上报设备信息
        Subscription subs = model.requestDevice(AppInitManager.getInstance().initializeDevice(this))
                .subscribe(Logger::i, Logger::e);
        addSubscription(subs);
    }

    private void onVersionNext(HttpResponse<Version> resp) {
        dismissProgressDialog();
        Version version = resp.data;
        if (version != null && version.getVersionCode() != null && version.getVersionCode() > BuildConfig.VERSION_CODE) {
            DialogHelper helper = DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .title("版本升级")
                    .content(version.getDescription());
            //是否强制升级 0：不强制升级 1：强制升级 2：静默安装
            if (version.getUpgrade() == 0) {
                mProgressDialog = helper.cancelable(true)
                        .canceledOnTouchOutside(true)
                        .leftButton(ResHelper.getString(R.string.dialog_cancel), 0xff9c9c9c)
                        .leftBtnClickListener((__, ___) -> dismissProgressDialog())
                        .rightButton(ResHelper.getString(R.string.dialog_confirm), 0xffdf4d69)
                        .rightBtnClickListener((__, ___) -> VersionManager.getInstance().downloadApk(version.getUrl()))
                        .show();
            } else if (version.getUpgrade() == 1) {
                mProgressDialog = helper.cancelable(false)
                        .canceledOnTouchOutside(false)
                        .bottomButton(ResHelper.getString(R.string.dialog_confirm), 0xffdf4d69)
                        .bottomBtnClickListener((__, ___) -> VersionManager.getInstance().downloadApk(version.getUrl()))
                        .show();
            } else {
                VersionManager.getInstance().downloadApk(version.getUrl());
            }

        }

    }

    private void initView() {
        mFragmentManager = getSupportFragmentManager();
        //1.得到LayoutInflater
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        //2.得到要显示的数据
        mFragments = MainFragment.values();
        //3.得到适配器
        //4.遍历数据，进行显示
        for (int i = 0; i < mFragments.length; i++) {
            TextView view = (TextView) inflater.inflate(R.layout.vw_tab_item, null, false);
            view.setText(mFragments[i].getTitle());
            view.setCompoundDrawablesWithIntrinsicBounds(0, mFragments[i].getIconResId(), 0, 0);
            mTabLayout.addTab(mTabLayout.newTab().setCustomView(view));
        }
        //5.设置适配器
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);

        showFragment(mFragments[0].getFragment());

        setViewTreeObserver();

        mSendLayout.setOnClickListener(mDynamicOnClickListener);
        mDeleteAll.setOnClickListener(mDynamicOnClickListener);
    }

    public void showFragment(Fragment f) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(R.id.main_container, f);
        transaction.commitAllowingStateLoss();
    }

    /**
     * 设置软键盘监听
     */
    private void setViewTreeObserver() {
        mRootLayout.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            //之后MainActivity可见，才会对布局的变化做处理
            if (JaythonApplication.getCurrentActivity() != MainActivity.this) return;
            if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > ResHelper.getScreenHeight() / 3)) {
                updateEditTextShowStatus(false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mCommentEditView.getVisibility() == View.VISIBLE) {
            updateEditTextShowStatus(false);
        } else {
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .content(ResHelper.getString(R.string.exit_tip))
                    .leftButton(ResHelper.getString(R.string.dialog_cancel), 0xff343434)
                    .leftBtnClickListener((dialog, view) -> {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    })
                    .rightButton(ResHelper.getString(R.string.dialog_confirm), 0xffdf4d69)
                    .rightBtnClickListener((dialog, view) -> {
                        if (null != dialog && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        JaythonApplication.exit();
                    })
                    .show();
        }
    }


    //更新底部输入框的显示状态
    private void updateEditTextShowStatus(boolean show) {
        if (show) {
            mCommentEditView.setVisibility(View.VISIBLE);
            mCommentEditView.setEnabled(true);
            mCommentEditText.requestFocus();
            UIHelper.showSoftInput(mCommentEditText);
        } else {
            //1.第二个fragment在键盘收起 editLayout消失的时候会向下滑动，规避这个问题
            DynamicFragment dynamicFragment = (DynamicFragment) mFragments[2].getFragment();
            dynamicFragment.setFrozen(true);
            mCommentEditView.setVisibility(View.INVISIBLE);
            mCommentEditView.setEnabled(false);
            mCommentEditText.setText("");
            UIHelper.hideSoftInput(mCommentEditText);
            dynamicFragment.setFrozen(false);
        }
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    protected boolean hasBaseLayout() {
        return false;
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }
}
