package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.ui.adapter.CollectPagerAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * time: 2017/2/14
 * description:
 *
 * @author fandong
 */
public class CollectActivity extends BaseActivity {


    @InjectView(R.id.compose_pager_trip)
    TextView mComposePagerTrip;
    @InjectView(R.id.compose_pager_tv)
    TextView mComposePagerTv;

    @InjectView(R.id.news_pager_trip)
    TextView mActionPagerTrip;
    @InjectView(R.id.news_pager_tv)
    TextView mActionPagerTv;


    @InjectView(R.id.collect_vp)
    ViewPager mViewPager;

    ViewPager.OnPageChangeListener mOnPageChangeListener;

    {
        mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }


    public static void launch(Context context) {
        Intent intent = new Intent(context, CollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_collect);
        //初始化 viewpager
        initView();
    }

    public void initView() {
        //1.初始化控件
        ButterKnife.inject(this);
        //2.设置适配器
        CollectPagerAdapter adapter = new CollectPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        //3.显示title
        switchTitle();
    }


    @OnClick({R.id.compose_title_layout, R.id.news_title_layout})
    public void onClick(View view) {
        int id = view.getId();
        int curItem = mViewPager.getCurrentItem();
        if (id == R.id.compose_title_layout
                && curItem != 0) {
            mViewPager.setCurrentItem(0);
            switchTitle();
        } else if (id == R.id.news_title_layout
                && curItem != 1) {
            mViewPager.setCurrentItem(1);
            switchTitle();
        }
    }

    private void switchTitle() {
        if (mViewPager.getCurrentItem() == 1) {
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
    protected boolean isToolbarEnable() {
        return false;
    }
}
