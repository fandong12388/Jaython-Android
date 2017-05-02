package com.jaython.cc.data.manager;

import com.jaython.cc.JaythonApplication;
import com.tencent.tauth.Tencent;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class QQManager {
    //qq appId 1105949604
    public static final String QQ_APPID = "1106028396";
    //get_simple_userinfo,add_topic
    public static final String QQ_SCOPE = "get_simple_userinfo,get_user_info,add_t";
    private static QQManager sManager;
    private Tencent mTencent;

    private QQManager() {
        mTencent = Tencent.createInstance(QQ_APPID, JaythonApplication.gContext);
    }

    public synchronized static QQManager getInstance() {
        if (null == sManager) {
            sManager = new QQManager();
        }
        return sManager;
    }

    public Tencent getTencent() {
        return mTencent;
    }


}
