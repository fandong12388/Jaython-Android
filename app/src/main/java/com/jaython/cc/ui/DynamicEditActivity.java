package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.jaython.cc.R;
import com.jaython.cc.bean.Dynamic;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.data.db.TDSystemGallery;
import com.jaython.cc.data.manager.DynamicManager;
import com.jaython.cc.data.manager.LocationManager;
import com.jaython.cc.data.manager.LoginManager;
import com.jaython.cc.data.manager.RxBusManager;
import com.jaython.cc.data.model.DynamicModel;
import com.jaython.cc.ui.adapter.DynamicEditAdapter;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.ui.view.divider.GridRecyclerDecoration;
import com.jaython.cc.utils.PictureUtil;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.PreferenceUtil;
import com.jaython.cc.utils.TimeUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.jaython.cc.utils.handler.WeakHandler;
import com.jaython.cc.utils.helper.DialogHelper;
import com.tiny.volley.utils.NetworkUtil;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time: 17/1/29
 * description:
 *
 * @author fandong
 */
public class DynamicEditActivity extends BaseActivity<DynamicModel> {
    @InjectView(R.id.dynamic_edit_text)
    EditText mEditText;

    @InjectView(R.id.dynamic_recycler_view)
    RecyclerView mRecyclerView;

    @InjectView(R.id.dynamic_location)
    TextView mLocation;

    private DynamicEditAdapter mDynamicEditAdapter;

    private DynamicEditAdapter.OnItemClickListener mOnItemClickListener;

    private String mLoc;

    private WeakHandler mHandler;

    {
        this.mOnItemClickListener = new DynamicEditAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position == mDynamicEditAdapter.getItemCount() - 1
                        && !mDynamicEditAdapter.isDeleteStatus()) {
                    //最后一个的处理
                    GalleryActivity.launch(DynamicEditActivity.this, 1, false, true);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                mDynamicEditAdapter.setDeleteStatus(true);
                mDynamicEditAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemDeleteClick(View v) {
                DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .content("您确定删除吗？")
                        .leftButton("取消", 0xec232323)
                        .leftBtnClickListener((dialog, view) -> {
                            if (null != dialog && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                        })
                        .rightButton("确定", 0xff111111)
                        .rightBtnClickListener((dialog, view) -> {
                            if (null != dialog && dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            //视图
                            int pos = (Integer) v.getTag();
                            //获取recyclerView所有的子视图
                            int count = mRecyclerView.getChildCount();
                            for (int i = 0; i < count; i++) {
                                View child = mRecyclerView.getChildAt(i).findViewById(R.id.dynamic_image_delete);
                                if (child.getTag() != null && ((Integer) child.getTag() > pos)) {
                                    child.setTag(((Integer) child.getTag() - 1));
                                }
                            }
                            mDynamicEditAdapter.removeItem(pos);
                        })
                        .show();

            }
        };
        this.mHandler = new WeakHandler(msg -> {
            dismissProgressDialog();
            finish();
            return false;
        });
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, DynamicEditActivity.class);
        context.startActivity(intent);
    }

    @OnClick(R.id.publish_dynamic)
    public void onClick(View view) {

        String content = mEditText.getText().toString();
        ArrayList<String> paths = mDynamicEditAdapter.getPaths();
        if (TextUtils.isEmpty(content) && !ValidateUtil.isValidate(paths)) {
            JToast.show("发布内容不能为空！", this);
            return;
        }
        //1.构建一个dynamic
        Dynamic dynamic = new Dynamic();
        dynamic.setId(-1);
        dynamic.setContent(content);
        dynamic.setImages(paths);
        dynamic.setPlatform(0);
        dynamic.setUid(LoginManager.getInstance().getUid());
        dynamic.setBuild(Build.MODEL);
        dynamic.setAddress(mLocation.getText().toString());

        dynamic.setCity(PreferenceUtil.getString(SPConstant.KEY_CITY));
        dynamic.setDistrict(PreferenceUtil.getString(SPConstant.KEY_DISTRICT));
        dynamic.setCreated(TimeUtil.getCurrentTimeStr());
        dynamic.setUserProfile(LoginManager.getInstance().getLoginUser().getUserProfile());
        if (TextUtils.isEmpty(dynamic.getContent()) && !ValidateUtil.isValidate(dynamic.getImages())) {
            JToast.show("请输入要发布的内容！", this);
            return;
        }

        if (NetworkUtil.isAvailable(this)) {
            //1.提示正在发布
            mProgressDialog = DialogHelper.create(DialogHelper.TYPE_PROGRESS)
                    .content("正在发布一条动态...")
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .show();
            //2.开始发布
            DynamicManager.getInstance().publish(dynamic);
            //3.finish掉自己
            mHandler.sendEmptyMessageDelayed(1, 1200);
        } else {
            JToast.show(R.string.network_error, this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dynamic);
        //1.得到相应的控件
        ButterKnife.inject(this);
        //2.初始化recyclerview
        initRecyclerView();
        //3.位置信息
        mLoc = PreferenceUtil.getString(SPConstant.KEY_LOCATION_DETAIL);
        if (ValidateUtil.isValidate(mLoc)) {
            mLocation.setText(mLoc);
        } else {
            RxBusManager.register(this, EventConstant.KEY_LOCATION, AMapLocation.class)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(l -> {
                        mLoc = LocationManager.getInstance().getDetailAddr(l);
                        mLocation.setText(mLoc);
                    });
            LocationManager.getInstance().startLocation();
        }
        //4.标题
        mToolbarLayout.setTitleTxt(R.string.dynamic_send_title);

    }

    private void initRecyclerView() {
        //1.设置显示方式为grid，每个空格之间间隙 8dp,则每个空格的尺寸为80dp
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        //2.设置显示方式
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //3.设置间隔
        GridRecyclerDecoration decoration = new GridRecyclerDecoration(
                (int) PixelUtil.dp2px(4.f), 0xffffffff
        );
        mRecyclerView.addItemDecoration(decoration);
        //4.设置适配器
        mDynamicEditAdapter = new DynamicEditAdapter(this);
        mRecyclerView.setAdapter(mDynamicEditAdapter);
        //5.点击事件
        mDynamicEditAdapter.setOnItemClickListener(mOnItemClickListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (null != data) {
            ArrayList<String> list = data.getStringArrayListExtra(PictureUtil.REQUEST_DATA);
            if (ValidateUtil.isValidate(list)) {
                for (int i = 0; i < list.size(); i++) {
                    String contentPath = list.get(i);
                    if (ValidateUtil.isValidate(contentPath)) {
                        Integer index = contentPath.lastIndexOf("/");
                        Integer id = Integer.valueOf(contentPath.substring(index + 1));
                        String path = TDSystemGallery.findImagePathByIndex(id);
                        if (ValidateUtil.isValidate(path)) {
                            mDynamicEditAdapter.addPath(path);
                        }
                    }
                }
                mDynamicEditAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDynamicEditAdapter.isDeleteStatus()) {
            mDynamicEditAdapter.setDeleteStatus(false);
            mDynamicEditAdapter.notifyDataSetChanged();
        } else {
            super.onBackPressed();
        }
    }
}
