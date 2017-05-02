package com.jaython.cc.bean.parser;

import com.google.gson.Gson;
import com.jaython.cc.bean.ActionGroup;
import com.tiny.volley.bean.parser.BaseParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/11/25
 * description:动作列表
 *
 * @author fandong
 */
public class ActionGroupParser extends BaseParser<List<ActionGroup>> {

    @Override
    public List<ActionGroup> parser(String json) {
        List<ActionGroup> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                ActionGroup group = gson.fromJson(jsonString, ActionGroup.class);
                lists.add(group);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
