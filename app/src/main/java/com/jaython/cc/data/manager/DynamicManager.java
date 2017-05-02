package com.jaython.cc.data.manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.jaython.cc.JaythonApplication;
import com.jaython.cc.R;
import com.jaython.cc.bean.Dynamic;
import com.jaython.cc.data.constants.EventConstant;
import com.jaython.cc.data.model.DynamicModel;
import com.jaython.cc.ui.view.JToast;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.helper.ResHelper;
import com.tiny.volley.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 17/2/1
 * description:
 *
 * @author fandong
 */
public class DynamicManager {
    private static DynamicManager sInstance;

    private DynamicModel mDynamicModel;
    private List<Dynamic> mDynamics;

    private DynamicManager() {
        mDynamicModel = new DynamicModel();
        mDynamics = new ArrayList<>();
    }

    public static DynamicManager getInstance() {
        if (sInstance == null) {
            sInstance = new DynamicManager();
        }
        return sInstance;
    }

    //发布一条动态
    public void publish(Dynamic dynamic) {
        Context context = JaythonApplication.gContext;
        //1.假数据
        RxBusManager.post(EventConstant.PUBLISH_DYNAMIC, dynamic);
        //2.网络请求
        if (NetworkUtil.isAvailable(context)) {
            //3.通知
            Notification notification = new NotificationCompat.Builder(context)
                    .setAutoCancel(true)
                    .setContentText(dynamic.getContent())
                    .setContentTitle(ResHelper.getString(R.string.dynamic_send_ing))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0, notification);
            mDynamicModel.publish(dynamic)
                    .subscribe(Logger::e, Logger::e, mNotificationManager::cancelAll);
        } else {
            JToast.show(R.string.network_error, context);
        }


    }
}
