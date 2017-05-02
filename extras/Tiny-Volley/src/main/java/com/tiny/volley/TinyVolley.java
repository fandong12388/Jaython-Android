package com.tiny.volley;

import android.content.Context;

import com.facebook.stetho.Stetho;
import com.tiny.volley.core.IParametersGenerator;
import com.tiny.volley.core.IPlaceholderParser;

/**
 * time: 2015/10/16
 * description:同步网络请求框架TinyVolley的入口
 *
 * @author sunjianfei
 */
public class TinyVolley {

    public static Context gContext;
    public static boolean sDebug;
    public static IParametersGenerator sParametersGenerator;
    public static IPlaceholderParser sPlaceholderParser;
    public static String sValidateHost;
    public static String sHost;

    /**
     * 同步Volley的初始化
     *
     * @param context      必须是Application的子类
     * @param debug        是否是调试模式
     * @param generator    请求参数的生成器(可能需要添加基本字段和签名字段)
     * @param validateHost https请求模式下需要与服务器进行双向认证的主机名
     */
    public static void init(Context context, boolean debug, IParametersGenerator generator
            , IPlaceholderParser parser, String validateHost, String host) {
        TinyVolley.gContext = context;
        TinyVolley.sDebug = debug;
        TinyVolley.sParametersGenerator = generator;
        TinyVolley.sValidateHost = validateHost;
        TinyVolley.sPlaceholderParser = parser;
        TinyVolley.sHost = host;
        if (debug) {
            //调试信息
            Stetho.initialize(
                    Stetho.newInitializerBuilder(context)
                            .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                            .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                            .build());
        }

    }

    public static void release() {
        TinyVolley.gContext = null;
        TinyVolley.sParametersGenerator = null;
        TinyVolley.sValidateHost = null;
    }
}
