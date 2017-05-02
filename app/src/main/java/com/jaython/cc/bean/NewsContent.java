package com.jaython.cc.bean;

/**
 * time:2016/11/25
 * description:资讯内容
 *
 * @author sunjianfei
 */
public class NewsContent {
    //标题
    public static final int TYPE_TITLE = 0;
    //图片
    public static final int TYPE_IMAGE = 1;
    //视频
    public static final int TYPE_VIDEO = 2;
    //文字
    public static final int TYPE_TEXT = 3;

    String id;
    int type;
    String content;
    String created;
    String newsId;
    String sort;
    int width;
    int height;
    String thumb;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }
}
