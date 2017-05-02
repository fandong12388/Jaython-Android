package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.ui.dialog.ClockDialog;
import com.jaython.cc.utils.PreferenceUtil;
import com.jaython.cc.utils.handler.WeakHandler;
import com.jaython.cc.utils.helper.ResHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import me.xiaopan.switchbutton.SwitchButton;

/**
 * time:2017/5/1
 * description:
 *
 * @author fandong
 */
public class ClockActivity extends BaseActivity {

    @InjectView(R.id.settings)
    ImageView mIv;

    @InjectView(R.id.click_text)
    TextView mTextView;
    @InjectView(R.id.clock_continue)
    TextView mContinue;
    @InjectView(R.id.switch_main_1)
    SwitchButton mSwitchButton;


    private MediaPlayer mSingleMp;
    private MediaPlayer mMatchMp;

    private TelephonyManager mTelephonyManager;
    private PhoneStateListener mPhoneStateListener;

    private WeakHandler mHandler;

    private Handler.Callback mCallback;

    private ClockDialog.OnFinishListener mOnFinishListener;
    private CompoundButton.OnCheckedChangeListener mOnCheckedChangeListener;

    //间隔时间
    private int mSplitTime;
    //间隔次数
    private int mSplitFre;

    //是否停止
    private boolean mIsPause;

    {
        this.mIsPause = true;
        this.mSplitTime = PreferenceUtil.getInt(SPConstant.KEY_CLOCK_TIME, 2) * 1000;
        this.mSplitFre = PreferenceUtil.getInt(SPConstant.KEY_CLOCK_FRE, 10);
        this.mPhoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        //pause();// 音乐播放器暂停
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // pause();// 重新播放音乐
                        break;
                }
            }

        };
        this.mCallback = msg -> {
            if (!mIsPause) {
                clock();
                mHandler.sendEmptyMessageDelayed(1, mSplitTime);
            }
            return false;
        };
        this.mHandler = new WeakHandler(mCallback);
        this.mOnFinishListener = (time, fre) -> {
            mSplitTime = time * 1000;
            mSplitFre = fre;
        };
        this.mOnCheckedChangeListener = (buttonView, isChecked) -> {
            if (isChecked) {
                mContinue.setBackgroundResource(R.drawable.selector_btn);
                mContinue.setEnabled(true);
                mIv.setVisibility(View.VISIBLE);
            } else {
                mContinue.setBackgroundResource(R.drawable.shape_gray_bgn);
                mContinue.setEnabled(false);
                mIv.setVisibility(View.GONE);
                pause();
            }
        };
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, ClockActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_train);
        ButterKnife.inject(this);

        mTelephonyManager = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        mSingleMp = MediaPlayer.create(this, R.raw.single);
        mMatchMp = MediaPlayer.create(this, R.raw.match);

        initSwitchButton();
    }

    private void initSwitchButton() {
        if (mSwitchButton.isChecked()) {
            mIv.setVisibility(View.VISIBLE);
        } else {
            mIv.setVisibility(View.GONE);
        }

        mSwitchButton.setOnCheckedChangeListener(mOnCheckedChangeListener);
    }

    @OnClick({R.id.settings, R.id.click_layout, R.id.back, R.id.clock_continue, R.id.clock_finish})
    public void onclick(View view) {
        int id = view.getId();
        if (R.id.click_layout == id) {
            if (!mSwitchButton.isChecked()) {
                clock();
            }
        } else if (R.id.back == id) {
            finish();
        } else if (R.id.settings == id) {
            ClockDialog clockDialog = new ClockDialog();
            Bundle bundle = new Bundle();
            bundle.putInt("time", mSplitTime / 1000);
            bundle.putInt("fre", mSplitFre);
            clockDialog.setArguments(bundle);
            clockDialog.setOnFinishListener(mOnFinishListener);
            clockDialog.show(getSupportFragmentManager(), "clock");
            pause();
        } else if (R.id.clock_continue == id) {
            String txt = mContinue.getText().toString();
            if (txt.equals(ResHelper.getString(R.string.clock_continue))) {
                start();
            } else {
                pause();
            }
        } else if (R.id.clock_finish == id) {
            mIsPause = true;
            mHandler.removeMessages(1);
            mContinue.setText(R.string.clock_continue);
            mTextView.setText(0 + "");
        }
    }


    public void start() {
        if (mIsPause) {
            mIsPause = false;
            mHandler.sendEmptyMessageDelayed(1, 1000);
            mContinue.setText(R.string.clock_stop);
        }
    }

    public void pause() {
        if (!mIsPause) {
            mIsPause = true;
            mHandler.removeMessages(1);
            mContinue.setText(R.string.clock_continue);
        }
    }

    private void clock() {
        //1.显示秒数
        String txt = mTextView.getText().toString();
        Integer value = Integer.valueOf(txt);
        value++;
        if (value > 99) {
            mTextView.setTextSize(86.f);
            mTextView.invalidate();
        }
        mTextView.setText("" + value);
        //2.播放声音
        if (value % mSplitFre == 0) {
            mMatchMp.start();
        } else {
            mSingleMp.start();
        }
    }

    @Override
    protected void onDestroy() {
        mIsPause = true;
        mHandler.removeMessages(0);
        if (null != mSingleMp) {
            if (mSingleMp.isPlaying()) {
                mSingleMp.stop();
            }
            mSingleMp.release();
        }
        if (null != mMatchMp) {
            if (mMatchMp.isPlaying()) {
                mMatchMp.stop();
            }
            mMatchMp.release();
        }
        super.onDestroy();

    }

    @Override
    protected boolean hasBaseLayout() {
        return false;
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }


}
