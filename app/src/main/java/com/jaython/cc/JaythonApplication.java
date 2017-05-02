package com.jaython.cc;

import android.app.Activity;
import android.app.Application;

import com.jaython.cc.data.manager.AppInitManager;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.loader.TinyImageLoader;
import com.tiny.volley.TinyVolley;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class JaythonApplication extends Application {
    private static final List<Activity> ACTIVITIES = new ArrayList<Activity>();

    public static JaythonApplication gContext;

    public static List<Activity> getActivities() {
        return ACTIVITIES;
    }

    public static void addActivity(Activity activity) {
        if (!ACTIVITIES.contains(activity)) {
            ACTIVITIES.add(activity);
        }
    }

    public static void removeActivity(Activity activity) {
        if (ACTIVITIES.contains(activity)) {
            ACTIVITIES.remove(activity);
        }
    }

    public static Activity getTop2Activity() {
        if (ACTIVITIES.size() >= 2) {
            return ACTIVITIES.get(ACTIVITIES.size() - 2);
        }
        return null;
    }

    /**
     * finish掉所有非栈顶的activity
     */
    public static void finishNoTopActivity() {
        if (ValidateUtil.isValidate(ACTIVITIES)) {
            while (ACTIVITIES.size() > 1) {
                Activity activity = ACTIVITIES.get(0);
                ACTIVITIES.remove(activity);
                if (activity != null && !activity.isFinishing()) {
                    activity.finish();
                }
            }
        }

    }

    /**
     * finish掉所有的activity
     */
    public static void finishAllActivity() {
        while (ValidateUtil.isValidate(ACTIVITIES)) {
            Activity activity = ACTIVITIES.get(0);
            ACTIVITIES.remove(activity);
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public static Activity getCurrentActivity() {
        if (ValidateUtil.isValidate(ACTIVITIES)) {
            return ACTIVITIES.get(ACTIVITIES.size() - 1);
        }
        return null;
    }

    public static int getStackActivitiesNum() {
        return ACTIVITIES.size();
    }

    public static void exit() {
        //umeng
        MobclickAgent.onKillProcess(gContext);
        finishAllActivity();
        TinyVolley.release();
        TinyImageLoader.destroy();
        System.exit(0);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gContext = this;

        //初始化
        AppInitManager.getInstance().initializeApp(this);
    }
}
