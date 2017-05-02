package com.jaython.cc.ui.dialog;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.utils.PreferenceUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


/**
 * time: 17-5-2
 * description:
 *
 * @author fandong
 */

public class ClockDialog extends DialogFragment {
    @InjectView(R.id.clock_split_time)
    TextView mTimeTv;
    @InjectView(R.id.clock_split_fre)
    TextView mFreTv;

    private OnFinishListener mOnFinishListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //将背景设置为透明
        Window window = getDialog().getWindow();
        if (null != window) {
            window.setBackgroundDrawable(new ColorDrawable(0x00000000));
        }
        return inflater.inflate(R.layout.vw_clock_dlg, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        if (null != window) {
            WindowManager.LayoutParams windowParams = window.getAttributes();
            windowParams.dimAmount = 0.0f;
            window.setAttributes(windowParams);
        }
    }

    public void setOnFinishListener(OnFinishListener listener) {
        this.mOnFinishListener = listener;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.inject(this, view);
        Bundle arg = getArguments();
        if (null != arg) {
            mTimeTv.setText("" + arg.getInt("time", 4));
            mFreTv.setText("" + arg.getInt("fre", 10));
        }
    }

    @OnClick({R.id.dialog_left,
            R.id.dialog_right,
            R.id.time_add_clock,
            R.id.time_sub_clock,
            R.id.fre_add_clock,
            R.id.fre_sub_clock})
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.dialog_left == id) {
            dismissAllowingStateLoss();
        } else if (R.id.time_add_clock == id) {
            String time = mTimeTv.getText().toString();
            Integer value = Integer.valueOf(time);
            value++;
            if (value > 20) {
                JToast.show("最大间隔时间不能大于20秒", getContext());
            } else {
                mTimeTv.setText("" + value);
            }

        } else if (R.id.time_sub_clock == id) {
            String time = mTimeTv.getText().toString();
            Integer value = Integer.valueOf(time);
            if (value <= 1) {
                JToast.show("最小间隔时间不能小于１s", getContext());
            } else {
                value--;
                mTimeTv.setText("" + value);
            }
        } else if (R.id.fre_add_clock == id) {
            String time = mFreTv.getText().toString();
            Integer value = Integer.valueOf(time);
            value++;
            mFreTv.setText("" + value);
        } else if (R.id.fre_sub_clock == id) {
            String time = mFreTv.getText().toString();
            Integer value = Integer.valueOf(time);
            if (value <= 1) {
                JToast.show("最小间隔次数不能小于１次", getContext());
            } else {
                value--;
                mFreTv.setText("" + value);
            }
        } else {
            String time = mTimeTv.getText().toString();
            Integer timeValue = Integer.valueOf(time);


            String fre = mFreTv.getText().toString();
            Integer freValue = Integer.valueOf(fre);

            if (mOnFinishListener != null) {
                PreferenceUtil.putInt(SPConstant.KEY_CLOCK_FRE, freValue);
                PreferenceUtil.putInt(SPConstant.KEY_CLOCK_TIME, timeValue);
                mOnFinishListener.onFinish(timeValue, freValue);
                mOnFinishListener = null;
                dismissAllowingStateLoss();
            }
        }
    }

    public interface OnFinishListener {
        void onFinish(int time, int fre);
    }

}
