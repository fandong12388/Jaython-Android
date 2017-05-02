package com.jaython.cc.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.PopupWindow;

import com.jaython.cc.R;
import com.jaython.cc.bean.Gallery;
import com.jaython.cc.data.manager.LocalPhotoManager;
import com.jaython.cc.ui.adapter.GalleryAdapter;
import com.jaython.cc.ui.adapter.GalleryNameAdapter;
import com.jaython.cc.ui.view.GalleryTitleBar;
import com.jaython.cc.ui.view.divider.GridRecyclerDecoration;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.PictureUtil;
import com.jaython.cc.utils.PixelUtil;
import com.jaython.cc.utils.helper.ResHelper;
import com.jaython.cc.utils.helper.UIHelper;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tiny.loader.core.listener.RecyclerPauseOnScrollListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * time:2016/4/18
 * description:
 *
 * @author sunjianfei
 */
public class GalleryActivity extends BaseActivity implements GalleryAdapter.OnRecyclerItemClickListener {
    public static final String KEY_NEED_CAMERA = "need_camera";
    public static final String KEY_MULTI_SELECT = "multi_select";

    @InjectView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    @InjectView(R.id.gallery_title)
    GalleryTitleBar mTitleBar;
    boolean isMultiSelected;
    boolean isOpenCamera;
    private PopupWindow mPopupWindow;
    private GalleryAdapter mGalleryAdapter;

    public static void launch(Activity activity, int requestCode) {
        launch(activity, requestCode, true);
    }

    public static void launch(Activity activity, int requestCode, boolean needCamera) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra(KEY_NEED_CAMERA, needCamera);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launch(Activity activity, int requestCode, boolean needCamera, boolean multiSelect) {
        Intent intent = new Intent(activity, GalleryActivity.class);
        intent.putExtra(KEY_NEED_CAMERA, needCamera);
        intent.putExtra(KEY_MULTI_SELECT, multiSelect);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_gallery);
        ButterKnife.inject(this);
        initView();
    }

    @Override
    protected boolean isToolbarEnable() {
        return false;
    }

    private void initView() {
        isOpenCamera = getIntent().getBooleanExtra(KEY_NEED_CAMERA, true);
        isMultiSelected = getIntent().getBooleanExtra(KEY_MULTI_SELECT, false);
        //设置溢出菜单显示状态
        mTitleBar.setRightButtonStatus(isMultiSelected);

        mGalleryAdapter = new GalleryAdapter(this, isOpenCamera);
        //1.得到LayoutManager
        GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        //2.添加边距
        mRecyclerView.addItemDecoration(new GridRecyclerDecoration((int) PixelUtil.dp2px(2.f), 0x242424));
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mGalleryAdapter);
        mRecyclerView.setItemViewCacheSize(0);
        mRecyclerView.setDrawingCacheEnabled(false);
        //3. 当recyclerView滑动停止时候，ScrollLinearLayout需要重置状态
        /*如果为idle，那么ImageLoader应该停止加载图片和保存图片*/
        mRecyclerView.addOnScrollListener(new RecyclerPauseOnScrollListener(true, true));
        //4.添加点击事件
        mGalleryAdapter.setOnRecyclerItemClickListener(this);
        //设置标题栏
        mTitleBar.setOnBackClickListener(v -> finish());
        //设置右边按钮的点击事件
        mTitleBar.setOnAheadClickListener(v -> onPictureSelectComplete());
        //设置选择相册按钮的点击事件
        mTitleBar.setOnGalleryClickListener(v -> {
            showGalleryPopupWindow();
        });
    }

    @Override
    public void onItemClick(int position, String url, View view) {
        if (0 == position && isOpenCamera) {
            openCamera();
        } else {
            if (!isMultiSelected) {
                onPictureSelectComplete();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            if (LocalPhotoManager.getInstance().isGalleryValidate()) {
                showGalleryPopupWindow();
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mPopupWindow != null && mPopupWindow.isShowing()) {
                mPopupWindow.dismiss();
                mPopupWindow = null;
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onPictureSelectComplete() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(PictureUtil.REQUEST_DATA, mGalleryAdapter.getSelectPhotos());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    /**
     * 当点击Menu键，或者点击标题栏，都会触发此方法，会呼起相册选择界面
     */
    public void showGalleryPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            //1.获取到数据
            List<Gallery> galleryList = LocalPhotoManager.getInstance().getLocalGallery();
            //2.
            RecyclerView recyclerView = (RecyclerView) View.inflate(this, R.layout.vw_gallery_pop, null);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            GalleryNameAdapter adapter = new GalleryNameAdapter(this, galleryList);
            recyclerView.setAdapter(adapter);
            adapter.setOnRecyclerItemClickListener((position, __) -> {
                //1.修改标题栏的相册名称
                String galleryName = galleryList.get(position).getGalleryName();
                mTitleBar.setTitle(galleryName);
                //2.将popWindow消失
                if (mPopupWindow != null && mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }
                //3.相册内容改变
                mGalleryAdapter.refresh(galleryList.get(position).getPictures());
                mGalleryAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(0);
            });

            int width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.5f);
            //确定显示的高度
            int height = (int) (ResHelper.getDimen(R.dimen.gallery_pop_item_height) * galleryList.size());
            int limitHeight = (int) (this.getResources().getDisplayMetrics().heightPixels * 0.6f);
            if (height > limitHeight) {
                height = limitHeight;
            }
            mPopupWindow = new PopupWindow(recyclerView, width, height);
            Drawable drawable = new ColorDrawable(0xf1131313);
            mPopupWindow.setBackgroundDrawable(drawable);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.showAsDropDown(mTitleBar, 0, 0);
        }
    }

    /**
     * 当从拍照界面拍完照片之后，会调用该方法更新相册界面
     */
    public void refresh() {
        //1.将popwindow消失
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
        //2.相册内容改变
        mGalleryAdapter.refresh();
        mGalleryAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    /**
     * 打开相机
     */
    private void openCamera() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.CAMERA)
                .subscribe(granted -> {
                    if (granted) {
                        //CameraActivity.launch(this, PictureUtil.REQUEST_CODE_FROM_CAMERA);
                    } else {
                        UIHelper.shortToast(R.string.camera_no_permission_content);
                    }
                }, Logger::e);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //如果取消拍照,删除uri
        if (resultCode == RESULT_CANCELED) {
            Logger.e("result canceled!");
        } else if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PictureUtil.REQUEST_CODE_FROM_CAMERA:
                    if (null != data) {
                        Intent intent = new Intent();
                        ArrayList<String> list = new ArrayList<>();
                        list.add(data.getStringExtra(PictureUtil.REQUEST_DATA));
                        intent.putStringArrayListExtra(PictureUtil.REQUEST_DATA, list);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                    break;
            }
        }
    }
}
