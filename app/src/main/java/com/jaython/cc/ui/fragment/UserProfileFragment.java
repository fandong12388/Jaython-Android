package com.jaython.cc.ui.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.User;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.manager.RxBusManager;
import com.jaython.cc.data.manager.VersionManager;
import com.jaython.cc.data.model.AppModel;
import com.jaython.cc.ui.AboutActivity;
import com.jaython.cc.ui.AdviceActivity;
import com.jaython.cc.ui.CollectActivity;
import com.jaython.cc.ui.MainActivity;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.MasterItemView;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.SystemUtil;
import com.jaython.cc.utils.helper.DialogHelper;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.core.assist.ImageScaleType;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time:2016/12/15
 * description:
 *
 * @author sunjianfei
 */
public class UserProfileFragment extends BaseFragment<AppModel> {


    @InjectView(R.id.item_feed_back)
    MasterItemView mFeedBackView;
    @InjectView(R.id.item_check_update)
    MasterItemView mCheckUpdateView;
    @InjectView(R.id.item_evaluate_us)
    MasterItemView mEvaluateView;
    @InjectView(R.id.item_about_us)
    MasterItemView mAboutUsView;
    @InjectView(R.id.item_contact_us)
    MasterItemView mContactUsView;
    @InjectView(R.id.profile_unlogin_layout)
    FrameLayout mUnLoginLayout;

    @InjectView(R.id.profile_login_out)
    TextView mLoginOut;

    //登录之后需要显示的
    @InjectView(R.id.profile_login_layout)
    FrameLayout mLoginLayout;
    @InjectView(R.id.item_collect_layout)
    LinearLayout mCollectLayout;
    @InjectView(R.id.item_collect)
    MasterItemView mCollect;
    @InjectView(R.id.profile_header_login)
    ImageView mHeaderLoginIv;
    @InjectView(R.id.profile_user_name)
    TextView mUserName;
    @InjectView(R.id.header_login_bg_iv)
    ImageView mHeaderLoginBgIv;
    //登录之前
    @InjectView(R.id.profile_unlogin_layout)
    FrameLayout mUnloginLayout;
    @InjectView(R.id.header_unlogin_bg_iv)
    ImageView mHeaderUnloginBgIv;
    @InjectView(R.id.profile_unlogin_header_iv)
    ImageView mHeaderUnloginIv;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_mine, container, false);
    }


    @Override
    public void initView(View view) {
        //1.得到控件
        ButterKnife.inject(this, view);
        //2.处理头像
        initTitle();
        //意见反馈
        mFeedBackView.setText(R.string.item_feed_back);
        mFeedBackView.setLeftDrawableRes(R.drawable.icon_feed_back);

        //检查更新
        mCheckUpdateView.setText(R.string.item_check_update);
        mCheckUpdateView.setLeftDrawableRes(R.drawable.icon_check_update);

        //评价我们
        mEvaluateView.setText(R.string.item_evaluate_us);
        mEvaluateView.setLeftDrawableRes(R.drawable.icon_evaluate);

        //关于我们
        mAboutUsView.setText(R.string.item_about_us);
        mAboutUsView.setLeftDrawableRes(R.drawable.icon_about_us);

        //联系我们
        mContactUsView.setText(R.string.item_contact_us);
        mContactUsView.setLeftDrawableRes(R.drawable.icon_contact_us);
        mContactUsView.setLineVisible(View.GONE);

        RxBusManager.register(this, EventConstant.KEY_LOGIN, User.class)
                .delay(100, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(__ -> initTitle(), Logger::e);

    }


    private void initTitle() {
        if (LoginManager.getInstance().isLoginValidate()) {
            mLoginLayout.setVisibility(View.VISIBLE);
            mUnLoginLayout.setVisibility(View.GONE);
            String url = LoginManager.getInstance().getLoginUser().getUserProfile().getHeadimgurl();
            TinyImageLoader.create(url)
                    .setImageScaleType(ImageScaleType.EXACTLY)
                    .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                    .setFailRes(R.drawable.icon_user_avatar_default_92)
                    .setEmptyRes(R.drawable.icon_user_avatar_default_92)
                    .setStrokeWidth(5.f)
                    .setRingColor(0xffffffff)
                    .setRingPadding(3.f)
                    .setDisplayType(TinyImageLoader.DISPLAY_CIRCLE_RING)
                    .into(mHeaderLoginIv);
            TinyImageLoader.create(url)
                    .setImageScaleType(ImageScaleType.EXACTLY)
                    .setBlurDepth(22)
                    .setDisplayType(TinyImageLoader.DISPLAY_BLUR)
                    .into(mHeaderLoginBgIv);

            String name = LoginManager.getInstance().getLoginUser().getUserProfile().getNickname();
            mUserName.setText(name);
            mLoginOut.setVisibility(View.VISIBLE);
            mCollectLayout.setVisibility(View.VISIBLE);
            mCollect.setText("我的收藏");
            mCollect.setLeftDrawableRes(R.drawable.icon_profile_collect);
        } else {
            mLoginLayout.setVisibility(View.GONE);
            mUnLoginLayout.setVisibility(View.VISIBLE);
            TinyImageLoader.create("drawable://" + R.drawable.icon_user_avatar_default_92)
                    .setImageScaleType(ImageScaleType.EXACTLY)
                    .setDefaultRes(R.drawable.icon_user_avatar_default_92)
                    .setFailRes(R.drawable.icon_user_avatar_default_92)
                    .setEmptyRes(R.drawable.icon_user_avatar_default_92)
                    .setStrokeWidth(5.f)
                    .setRingColor(0xffffffff)
                    .setRingPadding(3.f)
                    .setDisplayType(TinyImageLoader.DISPLAY_CIRCLE_RING)
                    .into(mHeaderUnloginIv);
            mHeaderUnloginBgIv.setImageResource(R.drawable.profile_user_bg_default);
            mLoginOut.setVisibility(View.GONE);
            mCollectLayout.setVisibility(View.GONE);

        }
    }

    @OnClick({R.id.weibo_login
            , R.id.item_collect
            , R.id.qq_login
            , R.id.profile_login_out
            , R.id.item_feed_back
            , R.id.item_check_update
            , R.id.item_evaluate_us
            , R.id.item_about_us
            , R.id.item_contact_us})
    public void onClick(View v) {
        String url = null;
        switch (v.getId()) {
            case R.id.item_about_us:
                AboutActivity.launch(mActivity);
                /*
                if (SystemUtil.checkMobileQQ(JaythonApplication.gContext)) {
                    url = "mqqwpa://im/chat?chat_type=group&uin=344548272&version=1";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else {
                    JToast.show("您尚未安装QQ客户端!", mActivity);
                }*/
                break;
            case R.id.weibo_login:
                ((MainActivity) mActivity).loginWeibo();
                break;
            case R.id.item_collect:
                CollectActivity.launch(mActivity);
                break;
            case R.id.item_feed_back:
                AdviceActivity.launch(mActivity);
                break;
            case R.id.qq_login:
                if (SystemUtil.checkMobileQQ(JaythonApplication.gContext)) {
                    ((MainActivity) mActivity).loginQQ();
                } else {
                    JToast.show("您尚未安装QQ客户端!", mActivity);
                }
                break;
            case R.id.profile_login_out:
                DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .content(ResHelper.getString(R.string.profile_logout))
                        .leftButton(ResHelper.getString(R.string.dialog_cancel), 0xff212121)
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
                            LoginManager.getInstance().logout();
                            initTitle();
                            mLoginOut.setVisibility(View.GONE);
                            RxBusManager.post(EventConstant.KEY_LOGIN_OUT, "");
                        })
                        .show();


                break;
            case R.id.item_check_update:
                Subscription subscription = mViewModel.requestVersionUpdate()
                        .subscribe(VersionManager.getInstance()::onUpdateNext, Logger::e, VersionManager.getInstance()::onUpdateComplete);
                addSubscription(subscription);
                break;
            case R.id.item_evaluate_us:
                try {
                    Uri uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);
                } catch (Exception e) {
                    //尚未安装客户端
                    Logger.e("Jaython", "您尚未安装应用商店！");
                    e.printStackTrace();
                }
                break;
            case R.id.item_contact_us:
                if (SystemUtil.checkMobileQQ(JaythonApplication.gContext)) {
                    url = "mqqwpa://im/chat?chat_type=wpa&uin=2098477734";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                } else {
                    JToast.show("您尚未安装QQ客户端!", mActivity);
                }
                break;
            default:
                break;
        }
    }
}
