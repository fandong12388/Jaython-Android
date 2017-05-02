package com.jaython.cc.data.constants;

/**
 * time: 2015/8/21
 * description:RxBus上面传递的事件类型
 *
 * @author sunjianfei
 */
public interface EventConstant {
    /*微信获取OAuth_code成功*/
    String KEY_WX_OAUTH_CODE = "wx_oauth_code";
    //新的推送消息
    String KEY_NEW_PUSH_MESSAGE = "new_push_message";
    //分享完成的通知
    String KEY_SHARE_COMPLETE = "share_complete";

    /*高德定位*/
    String KEY_LOCATION = "location";
    //登录
    String KEY_LOGIN = "login";
    //登录出去
    String KEY_LOGIN_OUT = "login_out";
    //发布一条动态
    String PUBLISH_DYNAMIC = "publish_dynamic";
    //发布一条动态
    String COMMENT_DYNAMIC = "comment_dynamic";
    //发布一条动态评论
    String COMMENT_DYNAMIC_PUBLISH = "comment_dynamic_publish";
}
