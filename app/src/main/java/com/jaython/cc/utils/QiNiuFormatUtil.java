package com.jaython.cc.utils;

import java.util.Locale;

/**
 * time:2016/11/28
 * description:
 *
 * @author sunjianfei
 */
public class QiNiuFormatUtil {

    /**
     * 获取视频指定帧图片
     *
     * @param url    地址
     * @param time   指定时间
     * @param width  图片宽
     * @param height 图片高
     * @return
     */
    public static String getVideoFrameImage(String url, int time, int width, int height) {
        String format = "%1$s?vframe/jpg/offset/%2$d/w/%3$d/h/%4$d";
        return String.format(Locale.getDefault(), format, url, time, width, height);
    }

    /**
     * 获取视频指定帧图片
     *
     * @param url  地址
     * @param time 指定时间
     * @return
     */
    public static String getVideoFrameImage(String url, int time) {
        String format = "%1$s?vframe/jpg/offset/%2$d";
        return String.format(Locale.getDefault(), format, url, time);
    }
}
