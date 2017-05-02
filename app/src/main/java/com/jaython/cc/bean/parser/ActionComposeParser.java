package com.jaython.cc.bean.parser;

import com.jaython.cc.bean.ActionCompose;
import com.tiny.volley.bean.parser.BaseParser;
import com.tiny.volley.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class ActionComposeParser extends BaseParser<List<ActionCompose>> {
    @Override
    public List<ActionCompose> parser(String json) {
        List<ActionCompose> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                ActionCompose compose = GsonUtil.fromJson(jsonString, ActionCompose.class);
                lists.add(compose);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
