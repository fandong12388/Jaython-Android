package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jaython.cc.R;
import com.jaython.cc.data.model.AppModel;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.handler.WeakHandler;
import com.jaython.cc.utils.helper.DialogHelper;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.volley.utils.NetworkUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time: 2017/2/9
 * description:
 *
 * @author fandong
 */
public class AdviceActivity extends BaseActivity<AppModel> {
    @InjectView(R.id.advice_edit)
    EditText mAdviceContent;

    @InjectView(R.id.advice_contact_edit)
    EditText mAdviceContact;

    private WeakHandler mHandler;

    {
        mHandler = new WeakHandler(msg -> {
            dismissProgressDialog();
            DialogHelper.create(DialogHelper.TYPE_NORMAL)
                    .content("您的反馈信息我们已经收到，谢谢您的反馈！")
                    .bottomButton(ResHelper.getString(R.string.i_know), 0xffdf4d69)
                    .bottomBtnClickListener((dialog, view) -> finish())
                    .show();
            return false;
        });
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, AdviceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_advice);
        ButterKnife.inject(this);
        //标题
        mToolbarLayout.setTitle(R.string.advice_title);
    }

    @OnClick(R.id.advice_commit_btn)
    public void OnClick(View view) {
        if (NetworkUtil.isAvailable(this)) {
            //2.请求
            String content = null;
            String contact = null;
            if (mAdviceContact.getEditableText() != null
                    && ValidateUtil.isValidate(mAdviceContent.getEditableText().toString())) {
                content = mAdviceContent.getEditableText().toString();
            } else {
                JToast.show("反馈的内容不能为空！", this);
                return;
            }
            if (mAdviceContact.getEditableText() != null
                    && ValidateUtil.isValidate(mAdviceContact.getEditableText().toString())) {
                contact = mAdviceContact.getEditableText().toString();
            } else {
                JToast.show("联系方式不能为空！", this);
                return;
            }
            //1.显示progress
            mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                    .activity(this)
                    .canceledOnTouchOutside(false)
                    .cancelable(false)
                    .content("正在反馈您的意见，请稍等！")
                    .show();
            mViewModel.request(content, contact);
            mHandler.sendEmptyMessageDelayed(1, 2000);
        } else {
            JToast.show("您尚未连接到网络！", this);
        }
    }

}
