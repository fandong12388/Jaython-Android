package com.jaython.cc.bean;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * time:2016/11/24
 * description:资讯实体
 *
 * @author sunjianfei
 */
public class NewsItem {
    //只有一张图的类型
    public static final int NEWS_ITEM_TYPE_1 = 0x000;
    //有3张图的类型
    public static final int NEWS_ITEM_TYPE_2 = 0x001;
    int id;
    String title;
    String category;
    String abstracts;
    int visit;
    int comment;
    int collect;
    String shelvesTime;
    String imageUrl1;
    String imageUrl2;
    String imageUrl3;
    String author;
    boolean hasCollect;
    boolean hasPraise;

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getAbstracts() {
        return abstracts;
    }

    public void setAbstracts(String abstracts) {
        this.abstracts = abstracts;
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

    public String getShelvesTime() {
        return shelvesTime;
    }

    public void setShelvesTime(String shelvesTime) {
        this.shelvesTime = shelvesTime;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getItemType() {
        if (TextUtils.isEmpty(imageUrl1)
                || TextUtils.isEmpty(imageUrl2)
                || TextUtils.isEmpty(imageUrl3)) {
            return NEWS_ITEM_TYPE_1;
        }
        return NEWS_ITEM_TYPE_2;
    }

    public List<String> getAllImage() {
        List<String> images = new ArrayList<>();
        images.add(imageUrl1);
        images.add(imageUrl2);
        images.add(imageUrl3);
        return images;
    }
}
