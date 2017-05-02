package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.utils.SystemUtil;
import com.jaython.cc.utils.helper.ResHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time: 2017/2/17
 * description:
 *
 * @author fandong
 */
public class AboutActivity extends BaseActivity {

    @InjectView(R.id.about_qq_group)
    TextView mQQGroup;
    @InjectView(R.id.about_version)
    TextView mVersion;

    public static void launch(Context context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_about);
        ButterKnife.inject(this);
        //设置标题
        mToolbarLayout.setTitleTxt("关于我们");
        //1.联系方式
        mQQGroup.setText(Html.fromHtml("<u>344548272</u>"));
        mVersion.setText(String.format(ResHelper.getString(R.string.about_version), BuildConfig.VERSION_NAME));

    }

    @OnClick(R.id.about_qq_group)
    public void onClick(View view) {
        if (SystemUtil.checkMobileQQ(JaythonApplication.gContext)) {
//            String url = "mqqwpa://im/chat?chat_type=group&uin=344548272&version=1";
//            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            joinQQGroup("ytSO9HTXCNMnFvuxFAxJqrdE8cYoIUwK");
        } else {
            JToast.show("您尚未安装QQ客户端!", this);
        }
    }

    /****************
     * 发起添加群流程。群号：健身圈(344548272) 的 key 为： ytSO9HTXCNMnFvuxFAxJqrdE8cYoIUwK
     * 调用 joinQQGroup(ytSO9HTXCNMnFvuxFAxJqrdE8cYoIUwK) 即可发起手Q客户端申请加群 健身圈(344548272)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

}
