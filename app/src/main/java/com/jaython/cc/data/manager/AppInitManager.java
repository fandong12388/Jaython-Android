package com.jaython.cc.data.manager;

import android.Manifest;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.webkit.WebView;

import com.jaython.cc.BuildConfig;
import com.jaython.cc.bean.BaseParameterGenerator;
import com.jaython.cc.bean.Device;
import com.jaython.cc.bean.SdkEntity;
import com.jaython.cc.bean.User;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.data.pool.RequestPool;
import com.jaython.cc.utils.DebugUtil;
import com.jaython.cc.utils.FileUtil;
import com.jaython.cc.utils.Logger;
import com.jaython.cc.utils.PreferenceUtil;
import com.jaython.cc.utils.SystemUtil;
import com.jaython.cc.utils.UEHandler;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.tiny.loader.TinyImageLoader;
import com.tiny.loader.internal.utils.L;
import com.tiny.volley.TinyVolley;
import com.tiny.volley.utils.NetworkUtil;
import com.umeng.analytics.MobclickAgent;

import static com.jaython.cc.JaythonApplication.gContext;

/**
 * Created by sunjianfei on 16-2-26.
 * 初始化基本参数
 */
public class AppInitManager {
    private static AppInitManager sInstance;
    private static SdkEntity mSdkEntity = new SdkEntity();

    //是否初始化
    private boolean mIsInitialized;

    public static AppInitManager getInstance() {
        if (sInstance == null) {
            sInstance = new AppInitManager();
        }
        return sInstance;
    }

    public static void destroy() {
        if (null != sInstance) {
            sInstance = null;
        }
    }

    public static SdkEntity getSdkEntity() {
        return mSdkEntity;
    }

    public static void loginOut() {
        if (null != mSdkEntity) {
            mSdkEntity.setUid(null);
        }
    }

    public void initializeApp(Context context) {
        if (mIsInitialized) return;
        //1.标志位
        mIsInitialized = true;
        //1.初始化 Logger（Logger的初始化要放在ZJB-volley的前面)
        Logger.initLog(BuildConfig.DEBUG_ENABLE, FileUtil.getPathByType(FileUtil.DIR_TYPE_LOG));
        //2.初始化ImageLoader
        TinyImageLoader.init(context.getApplicationContext(), FileUtil.getPathByType(FileUtil.DIR_TYPE_CACHE));
        //3.初始化ZJB-image-loader当中的日志系统
        L.writeDebugLogs(DebugUtil.isDebug());
        //4.初始化Volley
        TinyVolley.init(gContext, BuildConfig.DEBUG_ENABLE, new BaseParameterGenerator(), UrlParserManager.getInstance(), null, ApiConstant.HOST);
        //5.http request 连接池初始化
        RequestPool.gRequestPool.init();
        //6.记录崩溃日志
        UEHandler handler = new UEHandler(context.getApplicationContext());
        Thread.setDefaultUncaughtExceptionHandler(handler);
        //6.初始化高德定位
        LocationManager.getInstance().init();
        //申请权限.获取定位信息
        requestPermission(context);
        //7.初始化SDK相关
        initializeSDK();
        //8.避免目录下的文件被扫描进入系统文件
        FileUtil.hideMediaFile();
        //9.本地图片扫描
        LocalPhotoManager.getInstance().initialize();
        //umeng
        MobclickAgent.setScenarioType(context, MobclickAgent.EScenarioType.E_UM_NORMAL);
        MobclickAgent.setSessionContinueMillis(60000);
    }

    public void initializeSDK() {
        //1.处理eid
        String snb = SystemUtil.getSerialNumber();
        mSdkEntity.setDevice(TextUtils.isEmpty(snb) ? "snb" : snb);
        mSdkEntity.setVersionName(BuildConfig.VERSION_NAME);
        mSdkEntity.setCid(PreferenceUtil.getString(SPConstant.KEY_PUSH_CLIENT_ID));
        mSdkEntity.setPlatform(0);
        mSdkEntity.setSystemVersion(Build.VERSION.RELEASE);
        mSdkEntity.setBuild(Build.MODEL);
        mSdkEntity.setVersionCode(BuildConfig.VERSION_CODE);
        if (LoginManager.getInstance().isLoginValidate()) {
            User user = LoginManager.getInstance().getLoginUser();
            mSdkEntity.setUid(user.getUid());
        }
    }

    public Device initializeDevice(Context context) {
        Device device = new Device();
        try {
            device.setDeviceId(Settings.Secure.getString(context.getContentResolver(), "android_id"));
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            device.setImei(manager.getDeviceId());
            device.setImsi(manager.getSubscriberId());
            device.setIccid(manager.getSimSerialNumber());
            device.setNetworkOperator(manager.getNetworkOperatorName());
            device.setSim(manager.getSimState());
            String operatorString = manager.getSimOperator();
            //
            if (operatorString.equals("46000") || operatorString.equals("46002")) {
                device.setCarrier("中国移动");
            } else if (operatorString.equals("46001")) {
                device.setCarrier("中国联通");
            } else if (operatorString.equals("46003")) {
                device.setCarrier("中国电信");
            }

            if (NetworkUtil.isWifiAvailable(context)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                device.setBssid(wifiInfo.getBSSID());
                device.setNet("wifi");
                device.setMacAddress(wifiInfo.getMacAddress());
            } else {
                device.setNet("cmnet");
                device.setBssid("");
                device.setMacAddress("02:00:00:00:00:00");
            }
            WebView webView = new WebView(context);
            device.setUa(webView.getSettings().getUserAgentString());

            device.setMacAddress(SystemUtil.getMacAddress());
            device.setIp(SystemUtil.getIP());
            device.setSimSerialNumber(manager.getSimSerialNumber());
            device.setPhone(manager.getLine1Number());
            device.setModel(Build.MODEL);
            device.setManufacturer(Build.MANUFACTURER);
            device.setVersion(Build.VERSION.RELEASE);
            device.setSdkVersion(Build.VERSION.SDK_INT);

            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            device.setScreen(displayMetrics.widthPixels + "x" + displayMetrics.heightPixels);

            device.setAppVersion(BuildConfig.VERSION_NAME);
            device.setAppVersionInt(BuildConfig.VERSION_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return device;
    }

    private void setLocationData() {
        long time = PreferenceUtil.getLong(SPConstant.KEY_LOCATION_TIME);
        if (time == 0) {
            //如果没有定位过,开启定位
            LocationManager.getInstance().startLocation();
        } else {
            String lon = PreferenceUtil.getString(SPConstant.KEY_LON);
            String lat = PreferenceUtil.getString(SPConstant.KEY_LAT);

            long currentTime = System.currentTimeMillis();
            int hour = (int) ((currentTime - time) / (1000 * 60 * 60));
            Logger.e("间隔时间 = " + hour);
            if (hour >= 2) {
                //如果离上次定位超过2个小时,则重新定位
                LocationManager.getInstance().startLocation();
            } else {
                //如果没有拿到文件中存储的经纬度,则重新定位
                if (TextUtils.isEmpty(lon) || TextUtils.isEmpty(lat)) {
                    LocationManager.getInstance().startLocation();
                }
            }
        }
    }

    /**
     * 申请定位权限
     */
    private void openLocationPermission(Context context) {
        RxPermissions.getInstance(context)
                .request(Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .subscribe(granted -> {
                    if (granted) {
                        setLocationData();
                    } else {
                        Logger.e("permission has refused!");
                    }
                }, Logger::e);
    }

    private void requestPermission(Context context) {
        openLocationPermission(context);
    }

}
