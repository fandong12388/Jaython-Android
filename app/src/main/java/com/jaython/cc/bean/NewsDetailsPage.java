package com.jaython.cc.bean;

import java.util.List;

/**
 * time:2016/11/25
 * description:资讯详情
 *
 * @author sunjianfei
 */
public class NewsDetailsPage {
    String id;
    String title;
    String category;
    int visit;
    int comment;
    int collect;
    List<NewsContent> subNewses;
    List<NewsComment> newsComments;
    private boolean hasCollect;
    private boolean hasPraise;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getVisit() {
        return visit;
    }

    public void setVisit(int visit) {
        this.visit = visit;
    }

    public int getComment() {
        return comment;
    }

    public void setComment(int comment) {
        this.comment = comment;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }

    public List<NewsContent> getSubNewses() {
        return subNewses;
    }

    public void setSubNewses(List<NewsContent> subNewses) {
        this.subNewses = subNewses;
    }

    public List<NewsComment> getNewsComments() {
        return newsComments;
    }

    public void setNewsComments(List<NewsComment> newsComments) {
        this.newsComments = newsComments;
    }

    public boolean isHasCollect() {
        return hasCollect;
    }

    public void setHasCollect(boolean hasCollect) {
        this.hasCollect = hasCollect;
    }

    public boolean isHasPraise() {
        return hasPraise;
    }

    public void setHasPraise(boolean hasPraise) {
        this.hasPraise = hasPraise;
    }
}
