package com.jaython.cc.data.manager;

import android.text.TextUtils;

import com.jaython.cc.bean.User;
import com.jaython.cc.bean.UserProfile;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.utils.SystemUtil;
import com.jaython.cc.utils.TimeUtil;
import com.jaython.cc.utils.ValidateUtil;
import com.tiny.volley.core.IPlaceholderParser;

import java.util.Map;

/**
 * time:2016/5/12
 * description:解析动态Url
 * 题目:{HOST}/xxx/{xxx}?uid={uid}&oid={oid}
 *
 * @author sunjianfei
 */
public class UrlParserManager implements IPlaceholderParser {
    //广告类型(0首页弹窗广告、1个人中心下面的banner广告)
    public static final String METHOD_ADVTYPE = "ADVTYPE";
    //当前资讯的ID
    public static final String METHOD_TASKID = "TASKID";
    //渠道名称，用于分享时候设置分享链接时候，枚举类型，wechat/qq/app
    // app渠道是为了区分是在应用内打开还是普通浏览器
    public static final String METHOD_CHANNEL = "CHANNEL";
    //IP地址
    private static final String METHOD_HOST = "HOST";
    //日期，格式化为yyyyMMdd的日期时间值，例如20160516
    private static final String METHOD_DATE = "DATE";
    //格式化的日期，格式化为yyyy-MM-dd的日期时间值，例如2016-05-16
    private static final String METHOD_ISODATE = "ISODATE";
    //时间HH:mm:ss, 例如13:00:00
    private static final String METHOD_TIME = "TIME";
    //格式化的时间，例如2016-05-16 13:00:00
    private static final String METHOD_DATETIME = "DATETIME";
    private static final Object mLock = new Object();
    private static UrlParserManager instance = new UrlParserManager();
    private Map<String, String> baseParams;

    public static UrlParserManager getInstance() {
        return instance;
    }

    @Override
    public String parsePlaceholderUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }

        if (!ValidateUtil.isValidate(baseParams)) {
            buildBaseParams();
        }

        while (true) {
            int start = url.indexOf("{");
            int end = url.indexOf("}");
            if (start == -1 || end == -1) {
                return url;
            }
            String key = url.substring(start + 1, end);
            String value = baseParams.get(key);
            if (null == value) {
                value = "";
            }
            key = "{" + key + "}";
            url = url.replace(key, value);
        }
    }

    @Override
    public String parsePlaceholderJson(String json) {
        if (TextUtils.isEmpty(json)) {
            return "";
        }

        if (!ValidateUtil.isValidate(baseParams)) {
            buildBaseParams();
        }

        for (Map.Entry<String, String> entry : baseParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            String replaceKey = "{" + key + "}";
            json = json.replace(replaceKey, value);
        }
        return json;
    }

    private void buildBaseParams() {
        User user = LoginManager.getInstance().getLoginUser();
        UserProfile profile = null != user ? user.getUserProfile() : null;
        Map<String, String> userMap = SystemUtil.beanToMap(user);
        Map<String, String> userProfileMap = SystemUtil.beanToMap(profile);
        Map<String, String> baseParams = SystemUtil.beanToMap(AppInitManager.getSdkEntity());

        baseParams.putAll(userMap);
        baseParams.putAll(userProfileMap);

        //手动添加占位函数
        baseParams.put(METHOD_HOST, ApiConstant.HOST);
        baseParams.put(METHOD_DATE, TimeUtil.getTime());
        baseParams.put(METHOD_ISODATE, TimeUtil.getSimpleTime());
        baseParams.put(METHOD_TIME, TimeUtil.getHmsTime());
        baseParams.put(METHOD_DATETIME, TimeUtil.getCurrentTimeStr());
        this.baseParams = baseParams;
    }

    public void updateBaseParams() {
        synchronized (mLock) {
            if (ValidateUtil.isValidate(baseParams)) {
                baseParams.clear();
            }
            buildBaseParams();
        }
    }

    public void addParams(String key, String value) {
        if (!ValidateUtil.isValidate(baseParams)) {
            buildBaseParams();
        }
        baseParams.put(key, value);
    }

    public void release() {
        if (ValidateUtil.isValidate(baseParams)) {
            baseParams.clear();
            baseParams = null;
        }
    }

}
