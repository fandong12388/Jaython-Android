package com.jaython.cc.bean.parser;

import com.jaython.cc.bean.NewsItem;
import com.tiny.volley.bean.parser.BaseParser;
import com.tiny.volley.utils.GsonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/11/25
 * description:资讯列表解析器
 *
 * @author sunjianfei
 */
public class NewsParser extends BaseParser<List<NewsItem>> {

    @Override
    public List<NewsItem> parser(String json) {
        List<NewsItem> lists = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String jsonString = jsonObject.toString();
                NewsItem album = GsonUtil.fromJson(jsonString, NewsItem.class);
                lists.add(album);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lists;
    }
}
