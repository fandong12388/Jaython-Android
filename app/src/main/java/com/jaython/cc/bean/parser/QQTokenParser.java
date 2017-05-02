package com.jaython.cc.bean.parser;

import com.jaython.cc.data.manager.QQManager;
import com.tencent.connect.auth.QQToken;
import com.tiny.volley.bean.parser.BaseParser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class QQTokenParser extends BaseParser<QQToken> {

    @Override
    public QQToken parser(String json) {
        QQToken token = new QQToken(QQManager.QQ_APPID);
        try {
            JSONObject obj = new JSONObject(json);
            token.setAccessToken(obj.optString("access_token"), obj.optInt("expires_in") + "");
            token.setOpenId(obj.optString("openid"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return token;
    }
}
