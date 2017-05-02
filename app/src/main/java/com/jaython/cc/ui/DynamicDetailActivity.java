package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.jaython.cc.R;
import com.jaython.cc.ui.adapter.DynamicDetailAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2017/2/10
 * description:动态
 *
 * @author fandong
 */
public class DynamicDetailActivity extends BaseActivity {
    @InjectView(R.id.dynamic_detail_vp)
    ViewPager mViewPager;

    @InjectView(R.id.dynamic_detail_tv)
    TextView mTextView;

    private String mDynamic;
    private ArrayList<String> mImages;
    private int mPosition;


    public static void launch(Context context, ArrayList<String> images, String dynamic, int position) {
        Intent intent = new Intent(context, DynamicDetailActivity.class);
        intent.putExtra("images", images);
        intent.putExtra("dynamic", dynamic);
        intent.putExtra("position", position);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dynamic_detail);
        //1.解析intent
        Intent intent = getIntent();
        mDynamic = intent.getStringExtra("dynamic");
        mImages = intent.getStringArrayListExtra("images");
        mPosition = intent.getIntExtra("position", 0);
        //2.初始化视图
        initView();

    }

    private void initView() {
        ButterKnife.inject(this);
        //viewPager
        DynamicDetailAdapter adapter = new DynamicDetailAdapter(this, mImages);
        mViewPager.setAdapter(adapter);

        mTextView.setText(mDynamic);

        if (0 != mPosition) {
            mViewPager.setCurrentItem(mPosition);
        }
        //title
        mToolbarLayout.setTitleTxt("浏览");

    }
}
