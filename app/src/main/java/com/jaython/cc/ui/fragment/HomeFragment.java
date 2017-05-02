package com.jaython.cc.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.data.model.HomeModel;
import com.jaython.cc.ui.ClockActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time:2016/11/24
 * description:
 *
 * @author sunjianfei
 */
public class HomeFragment extends BaseFragment<HomeModel> {
    @InjectView(R.id.action_pager_trip)
    TextView mActionPagerTrip;
    @InjectView(R.id.action_pager_tv)
    TextView mActionPagerTv;
    @InjectView(R.id.compose_pager_trip)
    TextView mComposePagerTrip;
    @InjectView(R.id.compose_pager_tv)
    TextView mComposePagerTv;


    private ActionFragment mActionFragment;
    private ComposeFragment mComposeFragment;

    //当前页面
    private Object mCurPage;


    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_home, container, false);
    }


    public void showFragment(Fragment f) {

        mCurPage = f;
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.home_container, f);
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void initView(View view) {
        //1.初始化控件
        ButterKnife.inject(this, view);
        //4.初始化界面
        mActionFragment = new ActionFragment();
        mComposeFragment = new ComposeFragment();
    }

    @Override
    public void onStart() {
        super.onStart();
        //显示第一个界面
        showFragment(mActionFragment);
        mCurPage = mActionFragment;
        switchTitle();
    }

    @OnClick({R.id.action_title_layout, R.id.compose_title_layout, R.id.clock})
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.action_title_layout) {
            if (mCurPage != mActionFragment) {
                showFragment(mActionFragment);
                switchTitle();
            }
        } else if (id == R.id.clock) {
            ClockActivity.launch(mActivity);
        } else {
            if (mCurPage != mComposeFragment) {
                showFragment(mComposeFragment);
                switchTitle();
            }
        }
    }

    private void switchTitle() {
        if (mCurPage == mActionFragment) {
            mActionPagerTrip.setEnabled(true);
            mActionPagerTv.setEnabled(true);
            mComposePagerTrip.setEnabled(false);
            mComposePagerTv.setEnabled(false);
        } else {
            mActionPagerTrip.setEnabled(false);
            mActionPagerTv.setEnabled(false);
            mComposePagerTrip.setEnabled(true);
            mComposePagerTv.setEnabled(true);
        }
    }

    @Override
    protected boolean hasBaseLayout() {
        return false;
    }
}
