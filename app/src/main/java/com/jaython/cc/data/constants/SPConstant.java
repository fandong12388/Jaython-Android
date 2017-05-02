package com.jaython.cc.data.constants;

/**
 * time: 15/7/17
 * description:存放SharedPreference当中的key值
 *
 * @author sunjianfei
 */
public interface SPConstant {
    // sharepreference文件名
    String SP_NAME = "jerthon";
    /* 存储唯一标识 串号*/
    String KEY_SERIAL = "serial";
    /* 推送的clientId */
    String KEY_PUSH_CLIENT_ID = "push_client_id";
    /* 用户相关 */
    String KEY_USER = "user";

    //存储本地定位时间戳的key
    String KEY_LOCATION_TIME = "location_time";
    //经度
    String KEY_LON = "lon";
    //纬度
    String KEY_LAT = "lat";
    //定位信息
    String KEY_LOCATION = "location_info";
    //定位详细信息
    String KEY_LOCATION_DETAIL = "location_detail_info";
    //定位城市名称
    String KEY_CITY = "location_city";
    //定位区域名称
    String KEY_DISTRICT = "location_district";

    //是否第一次启动助驾帮,用来显示引导页
    String KEY_FIRST_USE = "first_use";
    //闪屏页广告数据
    String KEY_SPLASH = "splash_data";
    //闪屏页图片
    String KEY_SPLASH_PIC = "splash_pic";

    String KEY_SINA_LOGIN_INFO = "sina_login_info";

    String KEY_CLOCK_TIME = "clock_time";

    String KEY_CLOCK_FRE = "clock_fre";
}