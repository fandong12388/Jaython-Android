package com.jaython.cc.data.constants;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.utils.DebugUtil;
import com.qiniu.util.Auth;

/**
 * time: 2016/11/21 0021
 * description:
 *
 * @author sunjianfei
 */
public interface ApiConstant {
    // 七牛上传的认证机制
    Auth QINIUAUTH = Auth.create("x2KJmdepFhAJpgGOVMipefEH6n2dOz_akFo4wQ9N",
            "_hVm15zwG4DSQFXzGWHQbRBSrv7-35an9uzy-QC8");
    // 七牛上传的空间名称
    String QINIU_BUCKET = BuildConfig.DEBUG_ENABLE ? "photo-test" : "photo";
    // 七牛的链接前缀
    String QINIU_PREFIX = BuildConfig.DEBUG_ENABLE ? "http://7xrj6u.com2.z0.glb.qiniucdn.com/" : "http://7xjo4p.com2.z0.glb.qiniucdn.com/";


    String HOST_DEVELOP_TEST = "http://test.jaython.com";
    String HOST_RELEASE = "http://cc.jaython.com";
    String HOST = DebugUtil.isDebug() ? HOST_DEVELOP_TEST : HOST_RELEASE;


    //闪屏页的广告
    String API_SPLASH_ADV = HOST + "/ad/splash.json";
    //资讯列表
    String API_NEWS_LIST = HOST + "/news/list.json";
    //资讯详情
    String API_NEWS_DETAILS = HOST + "/news/detail.json";
    //提交评论
    String API_SUBMIT_COMMENT = HOST + "/news/comment.json";
    //首页获取所有的动作
    String API_ACTIONS = HOST + "/action/group.json";
    //首页获取所有的组合
    String API_COMPOSE = HOST + "/action/compose.json";
    //用户登录
    String API_USER_LOGIN = HOST + "/center/login.json";
    //收藏组合
    String API_COLLECT_COMPOSE = HOST + "/action/collect.json";
    //获取
    String API_COMPOSE_DETAIL = HOST + "/action/compose/detail.json";
    //发布一条动态
    String API_DYNAMIC_PUBLISH = HOST + "/dynamic/publish.json";
    //动态列表
    String API_DYNAMIC_LIST = HOST + "/dynamic/fetch.json";
    //意见反馈
    String API_ADVICE = HOST + "/app/advice.json";
    //版本升级
    String API_VERSION_UPDATE = HOST + "/app/version/upgrade.json";
    //设备信息
    String API_DEVICE = HOST + "/app/device.json";
    //评论
    String API_NEWS_COMMENT = HOST + "/news/comments.json";
    //收藏/点赞
    String API_NEWS_ACTION = HOST + "/news/action.json";
    //收藏/点赞
    String API_DYNAMIC_PRAISE = HOST + "/dynamic/praise.json";
    //收藏/点赞
    String API_DYNAMIC_COMMENT = HOST + "/dynamic/comment.json";
    //获取所有收藏的组合
    String API_COMPOSE_COLLECT = HOST + "/action/compose/collects.json";
    //获取所有收藏的资讯
    String API_NEWS_COLLECT_LIST = HOST + "/news/action/list.json";

}
