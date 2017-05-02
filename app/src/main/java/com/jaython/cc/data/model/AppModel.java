package com.jaython.cc.data.model;

import com.jaython.cc.bean.Device;
import com.jaython.cc.bean.Version;
import com.jaython.cc.data.constants.ApiConstant;
import com.jaython.cc.data.pool.RequestPool;
import com.tiny.volley.core.request.HttpGsonRequest;
import com.tiny.volley.core.request.Request;
import com.tiny.volley.core.request.RequestBuilder;
import com.tiny.volley.core.response.HttpResponse;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * time: 2017/2/9
 * description:一些通用的请求
 *
 * @author fandong
 */
public class AppModel {
    //意见反馈
    public void request(String content, String contact) {
        HttpGsonRequest<Boolean> request = RequestBuilder.<Boolean>create(Boolean.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_ADVICE)
                .put("content", content)
                .put("contact", contact)
                .build();
        RequestPool.gRequestPool.request(request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    //检查更新
    public Observable<HttpResponse<Version>> requestVersionUpdate() {
        HttpGsonRequest<Version> request = RequestBuilder.<Version>create(Version.class)
                .requestMethod(Request.Method.POST)
                .url(ApiConstant.API_VERSION_UPDATE)
                .build();
        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .observeOn(AndroidSchedulers.mainThread());
    }

    //上报设备信息
    public Observable<Boolean> requestDevice(Device device) {
        HttpGsonRequest<Boolean> request = RequestBuilder.<Boolean>create(Boolean.class)
                .requestMethod(Request.Method.POST)
                .paramsType(RequestBuilder.TYPE_NO_NEED_BASE)
                .url(ApiConstant.API_DEVICE)
                .put("deviceId", device.getDeviceId())
                .put("phone", device.getPhone())
                .put("imei", device.getImei())
                .put("macAddress", device.getMacAddress())
                .put("simSerialNumber", device.getSimSerialNumber())
                .put("model", device.getModel())
                .put("manufacturer", device.getManufacturer())
                .put("version", device.getVersion())
                .put("sdkVersion", device.getSdkVersion())
                .put("ip", device.getIp())
                .put("imsi", device.getImsi())
                .put("appVersion", device.getAppVersion())
                .put("appVersionInt", device.getAppVersionInt())
                .put("sim", device.getSim())
                .put("carrier", device.getCarrier())
                .put("net", device.getNet())
                .put("networkOperator", device.getNetworkOperator())
                .put("screen", device.getScreen())
                .put("bssid", device.getBssid())
                .put("iccid", device.getIccid())
                .put("ua", device.getUa())
                .build();
        return RequestPool.gRequestPool.request(request)
                .filter(resp -> resp != null)
                .map(HttpResponse::getData)
                .observeOn(AndroidSchedulers.mainThread());
    }
}
