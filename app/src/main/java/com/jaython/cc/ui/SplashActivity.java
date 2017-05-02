package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.R;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.data.db.DBService;
import com.jaython.cc.data.manager.AppInitManager;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.model.SplashModel;
import com.jaython.cc.utils.PreferenceUtil;
import com.jaython.cc.utils.handler.WeakHandler;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.loader.TinyImageLoader;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.jaython.cc.JaythonApplication.gContext;

/**
 * time:2016/11/24
 * description:
 *
 * @author sunjianfei
 */
public class SplashActivity extends BaseActivity<SplashModel> {
    private static final int WHAT_CANCEL_SCREEN = 1;
    private static final int WHAT_TO_MAIN = 2;
    private static final int WHAT_FINISH = 3;
    private static final long MS_DURATION = 3000;
    @InjectView(R.id.splash_target_iv)
    ImageView mSplashImage;
    private WeakHandler mHandler;
    private boolean mIsLoginValidate;
    private long mExecuteTime;
    private String mSplashPic;

    {
        this.mHandler = new WeakHandler(msg -> {
            int what = msg.what;
            switch (what) {
                case WHAT_CANCEL_SCREEN:
                    cancelFullScreen();
                    mHandler.sendEmptyMessageDelayed(WHAT_TO_MAIN, 1000);
                    break;
                case WHAT_TO_MAIN:
                    MainActivity.launch(SplashActivity.this);
                    mHandler.sendEmptyMessageDelayed(WHAT_FINISH, 3000);
                    break;
                case WHAT_FINISH:
                    finish();
                    break;
                default:
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSplashPic = PreferenceUtil.getString(SPConstant.KEY_SPLASH_PIC);
        if (TextUtils.isEmpty(mSplashPic)) {
            setContentView(R.layout.act_splash_plus);
            ImageView iv = (ImageView) findViewById(R.id.splash_logo_layout);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv.getLayoutParams();
            params.bottomMargin = (int) (ResHelper.getScreenHeight() * 0.568f);
        } else {
            setContentView(R.layout.act_splash);
            ButterKnife.inject(this);
        }
        //1.解决安装后直接打开，home键切换到后台再启动重复出现闪屏页面的问题
        // http://stackoverflow.com/questions/2280361/app-always-starts-fresh-from-root-activity-instead-of-resuming-background-state
        if (!isTaskRoot()) {
            if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
                finish();
                return;
            }
        }
        //2.初始化
        initialize(this);
        //3.显示广告图片
        initView();
        //5.跳转
        toMainPage();
    }

    @Override
    protected void onLazyLoad() {
        mViewModel.getSplashData();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //闪屏页面屏蔽掉返回按钮事件
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void initialize(Context context) {
        //1.防止application没有初始化完毕,再次初始化
        AppInitManager.getInstance().initializeApp(context);
        //3.创建快捷方式
        if (PreferenceUtil.getBoolean(SPConstant.KEY_FIRST_USE) && !"xiaomi".equals(BuildConfig.FLAVOR)) {
            shortcut(this);
        }
        //4. 初始化数据库
        mExecuteTime = System.currentTimeMillis();
        mIsLoginValidate = isLoginValidate();
        if (mIsLoginValidate) {
            //初始化数据库
            DBService.init(gContext);
        }

    }

    private void initView() {
        String filePath = TinyImageLoader.getQiniuDiskCachePath(mSplashPic);
        if (!TextUtils.isEmpty(filePath) && mSplashImage != null) {
            TinyImageLoader.create(mSplashPic)
                    .into(mSplashImage);
        }
    }

    /**
     * 在桌面创建快捷方式
     *
     * @param context 应用上下文
     */
    private void shortcut(Context context) {
        Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        shortcut.putExtra("duplicate", false);
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, ResHelper.getString(R.string.app_name));
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.mipmap.ic_launcher));
        Intent intent = new Intent();
        intent.setClass(context.getApplicationContext(), SplashActivity.class);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcut);
    }

    /**
     * 判断是否登录有效
     *
     * @return
     */
    private boolean isLoginValidate() {
        return null != LoginManager.getInstance().getLoginUser();
    }

    private void toMainPage() {
        long time = MS_DURATION - (System.currentTimeMillis() - mExecuteTime);
        mHandler.sendEmptyMessageDelayed(WHAT_CANCEL_SCREEN, time > 0 ? time : 10);
    }

    private void cancelFullScreen() {
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(params);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    @Override
    public boolean isSwipeBackEnabled() {
        return false;
    }

}
