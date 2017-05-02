package com.jaython.cc.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.Action;
import com.jaython.cc.ui.widget.SwipeBackLayout;
import com.jaython.cc.utils.FileUtil;
import com.jaython.cc.utils.handler.WeakHandler;
import com.jaython.cc.utils.helper.DialogHelper;
import com.jaython.cc.utils.helper.ResHelper;
import com.jaython.cc.utils.helper.ViewDragHelper;
import com.tiny.loader.TinyImageLoader;
import com.tiny.volley.download.DownloadResult;
import com.tiny.volley.download.SyncDownloadTask;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * time:2017/1/15
 * description:动作详情
 *
 * @author fandong
 */
public class ActionDetailActivity extends BaseActivity {

    public static final int WHAT_HIDE_IV = 1;
    public static final int WHAT_INIT_VIDEO = 2;
    public static final int WHAT_SHOW_IV = 3;

    @InjectView(R.id.action_video_layout)
    FrameLayout mVideoViewLayout;
    @InjectView(R.id.action_video_iv)
    ImageView mVideoIv;

    @InjectView(R.id.action_detail_prepare_title)
    RelativeLayout mPrepareLayout;
    @InjectView(R.id.action_detail_prepare_tv)
    TextView mPrepareTv;
    @InjectView(R.id.action_detail_notice_title)
    RelativeLayout mNoticeLayout;
    @InjectView(R.id.action_detail_notice_tv)
    TextView mNoticeTv;
    @InjectView(R.id.action_detail_description_title)
    RelativeLayout mDescriptionLayout;
    @InjectView(R.id.action_detail_description_tv)
    TextView mDescriptionTv;

    private VideoView mVideoView;
    private String mVideoPath;

    private TinyImageLoader.DefaultLoadingListener mDefaultLoadingListener;
    private Action mAction;

    private WeakHandler mHandler;

    private SwipeBackLayout.SwipeListener mSwipeListener;

    {
        this.mDefaultLoadingListener = new TinyImageLoader.DefaultLoadingListener() {
            private boolean hasInit;

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (hasInit) return;
                hasInit = true;
                //1.初始化mVideoIv
                mVideoIv.setImageBitmap(bitmap);
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mVideoViewLayout.getLayoutParams();
                params.width = ResHelper.getScreenWidth();
                params.height = (int) (height / (float) width * ResHelper.getScreenWidth());
                mVideoViewLayout.setLayoutParams(params);
                //2.初始化videoView
                initVideoView();
            }
        };

        this.mHandler = new WeakHandler(msg -> {
            switch (msg.what) {
                case WHAT_HIDE_IV:
                    mVideoIv.setVisibility(View.INVISIBLE);
                    break;
                case WHAT_SHOW_IV:
                    mVideoIv.setVisibility(View.VISIBLE);
                    break;
                case WHAT_INIT_VIDEO:
                    initView();
                    break;
                default:
                    break;
            }
            return false;
        });

        this.mSwipeListener = new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {
                if (state == ViewDragHelper.STATE_IDLE) {
                    startVideo();
                    mVideoIv.setVisibility(View.INVISIBLE);
                } else {
                    stopVideo();
                    mVideoIv.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
            }

            @Override
            public void onScrollOverThreshold() {
            }
        };
    }

    public static void launch(Context context, Action action) {
        Intent intent = new Intent(context, ActionDetailActivity.class);
        intent.putExtra("action", action);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        setContentView(R.layout.act_action_detail);
        ButterKnife.inject(this);
        //1.得到宽高初始化videoView
        initView();
    }

    private void initView() {
        Intent intent = getIntent();
        mAction = intent.getParcelableExtra("action");
        //1.得到宽高信息
        TinyImageLoader.create(mAction.getIcon())
                .setImageLoadinglistener(mDefaultLoadingListener)
                .load();

        mToolbarLayout.setTitleTxt(mAction.getTitle());

        //2.初始化界面
        if (TextUtils.isEmpty(mAction.getPrepare())) {
            mPrepareLayout.setVisibility(View.GONE);
            mPrepareTv.setVisibility(View.GONE);

        } else {
            mPrepareLayout.setVisibility(View.VISIBLE);
            mPrepareTv.setVisibility(View.VISIBLE);
            mPrepareTv.setText(mAction.getPrepare());
        }

        if (TextUtils.isEmpty(mAction.getDetail())) {
            mNoticeLayout.setVisibility(View.GONE);
            mNoticeTv.setVisibility(View.GONE);
        } else {
            mNoticeLayout.setVisibility(View.VISIBLE);
            mNoticeTv.setVisibility(View.VISIBLE);
            mNoticeTv.setText(mAction.getDetail());
        }

        if (TextUtils.isEmpty(mAction.getDescription())) {
            mDescriptionLayout.setVisibility(View.GONE);
            mDescriptionTv.setVisibility(View.GONE);
        } else {
            mDescriptionLayout.setVisibility(View.VISIBLE);
            mDescriptionTv.setVisibility(View.VISIBLE);
            mDescriptionTv.setText(mAction.getDescription());
        }


    }

    //构建一个
    private void initVideoView() {
        int index = mAction.getVideo().lastIndexOf(File.separator);
        String fileName = mAction.getVideo().substring(index + 1);
        mVideoPath = FileUtil.getPathByType(FileUtil.DIR_TYPE_DOWNLOAD) + fileName;
        File file = new File(mVideoPath);
        if (file.exists() && file.length() > 100) {
            if (null == mVideoView) {
                //1.添加videoview
                mVideoView = new VideoView(this);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
                mVideoViewLayout.addView(mVideoView, 0, params);
                //2.滑动的时候需要pause
                mSwipeBackLayout.addSwipeListener(mSwipeListener);
                //3.开始播放
                mVideoView.setVideoPath(mVideoPath);
                //循环
                mVideoView.setOnCompletionListener(MediaPlayer::start);
                mVideoView.setOnPreparedListener(mp -> {
                    mp.start();
                    mHandler.sendEmptyMessageDelayed(WHAT_HIDE_IV, 200);
                });
                mVideoView.setOnErrorListener((mp, what, extra) -> {
                    mProgressDialog = DialogHelper.create(DialogHelper.TYPE_NORMAL)
                            .content("播放失败，请重试！")
                            .bottomButton(ResHelper.getString(R.string.dialog_confirm), 0xffdf4d69)
                            .bottomBtnClickListener((dialog, view) -> {
                                dismissProgressDialog();
                                File target = new File(mVideoPath);
                                target.delete();
                                mHandler.sendEmptyMessageDelayed(WHAT_SHOW_IV, 10);
                                downloadActionVideo();
                            })
                            .show();
                    return true;
                });
                //暂停之类的
                mVideoViewLayout.setOnClickListener(__ -> {
                    if (mVideoView.isPlaying()) {
                        mVideoView.pause();
                    } else {
                        mVideoView.start();
                    }
                });
            } else {
                startVideo();
            }
        } else {
            downloadActionVideo();
        }
    }

    private void downloadActionVideo() {
        String url = mAction.getVideo();
        int index = url.lastIndexOf(File.separator);
        String fileName = url.substring(index + 1);
        String path = FileUtil.getPathByType(FileUtil.DIR_TYPE_DOWNLOAD) + fileName;
        final File file = new File(path);
        if (!file.exists()) {
            new Thread(() -> {
                String temp = FileUtil.getPathByType(FileUtil.DIR_TYPE_TEMP) + fileName;
                SyncDownloadTask task = new SyncDownloadTask(JaythonApplication.gContext
                        , url
                        , temp);
                DownloadResult result = task.download();
                if (DownloadResult.CODE_SUCCESS == result.code) {
                    File tempFile = new File(result.path);
                    tempFile.renameTo(file);
                    if (!mDestroyed) {
                        runOnUiThread(ActionDetailActivity.this::initVideoView);
                    }

                }
            }).start();


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(WHAT_INIT_VIDEO, 600);
    }

    private void stopVideo() {
        mVideoIv.setVisibility(View.VISIBLE);
        if (null != mVideoView && mVideoView.isPlaying()) {
            mVideoView.pause();
        }
    }

    private void startVideo() {
        if (null != mVideoView && !mVideoView.isPlaying()) {
            mVideoView.start();
        }
        mVideoIv.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        stopVideo();
        super.onPause();
    }

    @Override
    protected boolean hasBaseLayout() {
        return false;
    }
}
