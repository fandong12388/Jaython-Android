package com.jaython.cc.data.manager;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.Version;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.utils.FileUtil;
import com.jaython.cc.utils.helper.DialogHelper;
import com.jaython.cc.utils.helper.NotifyHelper;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.volley.core.response.HttpResponse;
import com.tiny.volley.download.DownloadResult;
import com.tiny.volley.download.DownloadTask;
import com.tiny.volley.download.IDownloadListener;

import java.io.File;

/**
 * time: 2017/2/9
 * description:版本升级的管理
 *
 * @author fandong
 */
public class VersionManager {
    private static VersionManager sInstance;
    private Dialog mDialog;

    private VersionManager() {

    }

    public static synchronized VersionManager getInstance() {
        if (null == sInstance) {
            sInstance = new VersionManager();
        }
        return sInstance;
    }

    public void onUpdateNext(HttpResponse<Version> resp) {
        dismissVersionDialog();
        if (null == resp.data) {
            JToast.show("已经是最新版本！", JaythonApplication.gContext);
        } else {
            Version version = resp.data;
            if (version.getVersionCode() != null && version.getVersionCode() > BuildConfig.VERSION_CODE) {
                DialogHelper helper = DialogHelper.create(DialogHelper.TYPE_NORMAL)
                        .title("版本升级")
                        .content(version.getDescription());
                //是否强制升级 0：不强制升级 1：强制升级 2：静默安装
                if (version.getUpgrade() == 0) {
                    mDialog = helper.cancelable(true)
                            .canceledOnTouchOutside(true)
                            .leftButton(ResHelper.getString(R.string.dialog_cancel), 0xff9c9c9c)
                            .leftBtnClickListener((__, ___) -> dismissVersionDialog())
                            .rightButton(ResHelper.getString(R.string.dialog_confirm), 0xffdf4d69)
                            .rightBtnClickListener((__, ___) -> downloadApk(version.getUrl()))
                            .show();
                } else if (version.getUpgrade() == 1) {
                    mDialog = helper.cancelable(false)
                            .canceledOnTouchOutside(false)
                            .bottomButton(ResHelper.getString(R.string.dialog_confirm), 0xffdf4d69)
                            .bottomBtnClickListener((__, ___) -> downloadApk(version.getUrl()))
                            .show();
                } else {
                    downloadApk(version.getUrl());
                }
            } else {
                JToast.show("已经是最新版本！", JaythonApplication.gContext);
            }

        }
    }


    public void onUpdateComplete() {

    }

    private void dismissVersionDialog() {
        if (null != mDialog && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    /**
     * 下载新的安装包
     *
     * @param url apk对应的链接
     */
    public void downloadApk(String url) {
        dismissVersionDialog();
        IDownloadListener mDownloadListener = new IDownloadListener() {
            private Context context;


            @Override
            public void onDownloadStarted() {
                context = JaythonApplication.getCurrentActivity();
                NotifyHelper.notifyProgress(context, 0, 0.1f);
            }

            @Override
            public void onDownloadFinished(DownloadResult downloadResult) {
                //1.停止通知
                NotifyHelper.notifyProgress(context, 0, 0);
                //2.安装文件
                String path = downloadResult.path;
                if (!TextUtils.isEmpty(path)) {
                    File file = new File(path);
                    install(file);
                }
            }

            @Override
            public void onProgressUpdate(Float... floats) {
                if (floats[1] - floats[0] < 0.01f) {
                    NotifyHelper.notifyProgress(context, floats[0], floats[1]);
                }
            }
        };
        if (!TextUtils.isEmpty(url)) {
            DownloadTask task = new DownloadTask(JaythonApplication.gContext,
                    url, FileUtil.getApkPath(), mDownloadListener);
            task.execute();
        }
    }

    /**
     * 安装下载下来的APK文件
     *
     * @param file 下载的apk文件
     */
    private void install(File file) {
        Context context = JaythonApplication.getCurrentActivity();
        if (null != context) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            context.startActivity(intent);
        }
    }
}
