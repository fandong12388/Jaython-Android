package com.jaython.cc.bean.parser;

import com.jaython.cc.bean.Dynamic;
import com.tiny.volley.bean.parser.BaseParser;
import com.tiny.volley.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/11/25
 * description:动态列表解析器
 *
 * @author fandong
 */
public class DynamicParser extends BaseParser<List<Dynamic>> {

    @Override
    public ArrayList<Dynamic> parser(String json) {
        ArrayList<Dynamic> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                Dynamic dynamic = GsonUtil.fromJson(jsonString, Dynamic.class);
                lists.add(dynamic);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
