package com.jaython.cc.data.manager;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.bean.Action;
import com.jaython.cc.bean.ActionGroup;
import com.jaython.cc.utils.FileUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.volley.download.DefaultDownLoaderListener;
import com.tiny.volley.download.DownloadResult;
import com.tiny.volley.download.DownloadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * time:2017/1/15
 * description:
 *
 * @author fandong
 */
public class ActionDownloadManager {

    private static ActionDownloadManager sManager;

    private List<ActionGroup> mList;

    private ActionDownloadManager() {
        mList = new ArrayList<>();
    }

    public static ActionDownloadManager getInstance() {
        if (null == sManager) {
            sManager = new ActionDownloadManager();
        }
        return sManager;
    }

    public void addList(List<ActionGroup> list) {
        mList.clear();
        mList.addAll(list);
    }

    public void downLoad() {
        if (ValidateUtil.isValidate(mList)) {
            for (ActionGroup group : mList) {
                if (!ValidateUtil.isValidate(group.getActions())) {
                    continue;

                }
                for (Action action : group.getActions()) {
                    String url = action.getVideo();
                    int index = url.lastIndexOf(File.separator);
                    String fileName = url.substring(index + 1);
                    String path = FileUtil.getPathByType(FileUtil.DIR_TYPE_DOWNLOAD) + fileName;
                    final File file = new File(path);
                    if (!file.exists()) {
                        String temp = FileUtil.getPathByType(FileUtil.DIR_TYPE_TEMP) + fileName;
                        DownloadTask task = new DownloadTask(JaythonApplication.gContext
                                , url
                                , temp
                                , new DefaultDownLoaderListener() {
                            @Override
                            public void onDownloadFinished(DownloadResult result) {
                                if (DownloadResult.CODE_SUCCESS == result.code) {
                                    File tempFile = new File(result.path);
                                    tempFile.renameTo(file);
                                }
                            }
                        });
                        task.execute();
                    }
                }
            }
        }
    }
}
