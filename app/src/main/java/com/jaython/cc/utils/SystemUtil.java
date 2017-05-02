package com.jaython.cc.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Browser;
import android.telephony.TelephonyManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;

import com.jaython.cc.data.constants.SPConstant;
import com.jaython.cc.ui.view.spannable.SpannableClickable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.jaython.cc.JaythonApplication.gContext;

/**
 * time: 15/7/18
 * description:
 *
 * @author sunjianfei
 */
public class SystemUtil {
    /**
     * 检查是否是正确的email格式
     *
     * @param email
     * @return
     */
    public static boolean checkEmailFormat(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        String reg = "^[0-9a-z_-][_.0-9a-z-]{0,31}@([0-9a-z][0-9a-z-]{0,30}\\.){1,4}[a-z]{2,4}$";
        Pattern pattern = Pattern.compile(reg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 判断字符串是否含有中文
     */
    public static boolean isContainsChinese(String str) {
        String regEx = "[\u4e00-\u9fa5]";
        Pattern pat = Pattern.compile(regEx);
        Matcher matcher = pat.matcher(str);
        boolean flg = false;
        if (matcher.find()) {
            flg = true;
        }
        return flg;
    }

    /**
     * 检查是否是正确手机号
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        String reg = "^[0-9]{11}$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    public static String getLocationInfo() {
        String language = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        return language + "-" + country;
    }

    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) gContext.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = tm.getDeviceId();
        if (TextUtils.isEmpty(imei)) {
            imei = "";
        }
        return imei;
    }

    /**
     * 获取手机唯一标识
     *
     * @return
     */
    public static String getSerialNumber() {
        String snb = PreferenceUtil.getString(SPConstant.KEY_SERIAL);
        if (TextUtils.isEmpty(snb)) {
            snb = Build.SERIAL;
        }

        if (TextUtils.isEmpty(snb)) {
            snb = UUID.randomUUID().toString();
        } else {
            PreferenceUtil.putString(SPConstant.KEY_SERIAL, snb);
        }
        return snb;
    }

    public static String getMacAddress() {
        WifiManager wifiManager = (WifiManager) gContext.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return (wifiInfo == null) ? "" : wifiInfo.getMacAddress();
    }

    public static String getIP() {
        String hostIp = null;
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            InetAddress ia = null;
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    ia = ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    String ip = ia.getHostAddress();
                    if (!"127.0.0.1".equals(ip)) {
                        hostIp = ia.getHostAddress();
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return hostIp;

    }

    public static String getMetaData(String key) {
        try {
            Context context = gContext.getApplicationContext();
            //1.得到PackageManager
            PackageManager packageManager = context.getPackageManager();
            String packageName = context.getPackageName();
            //2.得到PackageInfo
//            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            //3.得到ApplicationInfo
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(
                    packageName, PackageManager.GET_META_DATA);
            Bundle metaData = applicationInfo.metaData;
            return metaData.getString(key);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMD5Str(String password) {
        String strResult = "";
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.update(password.getBytes("UTF-8"));
            byte[] bzpassword_1 = md5.digest();
            StringBuffer sb = new StringBuffer();
            for (int i = 0;
                 i < bzpassword_1.length;
                 ++i) {
                sb.append(String.format("%02x", bzpassword_1[i]));
            }
            md5.update(sb.toString().getBytes("UTF-8"));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getCpuInfo() {
        String str1 = "/proc/cpuinfo";
        String str2 = "";
        String[] cpuInfo = {"", ""};
        String[] arrayOfString;
        try {
            FileReader fr = new FileReader(str1);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            for (int i = 2; i < arrayOfString.length; i++) {
                cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
            }
            str2 = localBufferedReader.readLine();
            arrayOfString = str2.split("\\s+");
            cpuInfo[1] += arrayOfString[2];
            localBufferedReader.close();
        } catch (IOException e) {
        }

        StringBuilder builder = new StringBuilder();
        for (String s : cpuInfo) {
            builder.append(s.toLowerCase() + ",");
        }
        return builder.toString();
    }

    public static boolean isX86CPU() {
        String cpu = getCpuInfo();
        if (!TextUtils.isEmpty(cpu)) {
            if (cpu.contains("x86")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 把一个double类型转化为对应的string类型
     */
    public static String doubleParseToString(double d1) {
        Double d = new Double(d1);
        BigDecimal bd = new BigDecimal(d.toString());
        return bd.toPlainString();
    }

    public static String getZhCN() {
        String language = Locale.CHINA.getLanguage().toLowerCase();
        String county = Locale.CHINA.getCountry();
        return language + "_" + county;
    }

    public static boolean isAppRunningForeground(Context context) {
        ActivityManager var1 = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List list = var1.getRunningTasks(1);
        return context.getPackageName().equalsIgnoreCase(((ActivityManager.RunningTaskInfo) list.get(0)).baseActivity.getPackageName());
    }

    /**
     * java实体转换成map
     */
    public static Map<String, String> beanToMap(Object object) {
        Map<String, String> params = new HashMap<>();
        if (null == object) {
            return params;
        }
        try {
            Field[] fields = object.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldObj = field.get(object);
                if (fieldObj != null && fieldObj.getClass() == String.class) {
                    String fieldValue = (String) fieldObj;
                    if (!TextUtils.isEmpty(fieldValue)) {
                        if (!TextUtils.isEmpty(fieldValue)) {
                            fieldValue = fieldValue.replaceAll("\"", "%22");
                        }
                        params.put(fieldName, fieldValue);
                    }
                }
            }
            return params;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return params;
    }

    public static SpannableStringBuilder formatUrlString(String contentStr) {

        SpannableStringBuilder sp;
        if (!TextUtils.isEmpty(contentStr)) {

            sp = new SpannableStringBuilder(contentStr);
            try {
                //处理url匹配
                Pattern urlPattern = Pattern.compile("(http|https|ftp|svn)://([a-zA-Z0-9]+[/?.?])" +
                        "+[a-zA-Z0-9]*\\??([a-zA-Z0-9]*=[a-zA-Z0-9]*&?)*");
                Matcher urlMatcher = urlPattern.matcher(contentStr);

                while (urlMatcher.find()) {
                    final String url = urlMatcher.group();
                    if (!TextUtils.isEmpty(url)) {
                        sp.setSpan(new SpannableClickable() {
                            @Override
                            public void onClick(View widget) {
                                Uri uri = Uri.parse(url);
                                Context context = widget.getContext();
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.putExtra(Browser.EXTRA_APPLICATION_ID, context.getPackageName());
                                context.startActivity(intent);
                            }
                        }, urlMatcher.start(), urlMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }

                //处理电话匹配
                Pattern phonePattern = Pattern.compile("[1][34578][0-9]{9}");
                Matcher phoneMatcher = phonePattern.matcher(contentStr);
                while (phoneMatcher.find()) {
                    final String phone = phoneMatcher.group();
                    if (!TextUtils.isEmpty(phone)) {
                        sp.setSpan(new SpannableClickable() {
                            @Override
                            public void onClick(View widget) {
                                Context context = widget.getContext();
                                //用intent启动拨打电话
                                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }, phoneMatcher.start(), phoneMatcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            sp = new SpannableStringBuilder();
        }
        return sp;
    }

    public static boolean checkMobileQQ(Context context) {
        PackageManager pm = context.getPackageManager();
        PackageInfo pi = null;

        try {
            pi = pm.getPackageInfo("com.tencent.mobileqq", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (pi != null) {
            String versionName = pi.versionName;
            try {
                String[] versionNames = versionName.split("\\.");
                int var5 = Integer.parseInt(versionNames[0]);
                int var6 = Integer.parseInt(versionNames[1]);
                return var5 > 4 || var5 == 4 && var6 >= 1;
            } catch (Exception var8) {
                var8.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
