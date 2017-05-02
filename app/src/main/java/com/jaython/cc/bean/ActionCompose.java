package com.jaython.cc.bean;

import java.util.List;

/**
 * time:2017/1/14
 * description:
 *
 * @author fandong
 */
public class ActionCompose {

    private Integer id;
    private String title;
    private String description;
    private Integer num;
    private Integer restSec;
    private Integer collects;
    private String showd;
    private String created;
    private String imageUrl1;
    private String imageUrl2;

    private List<ComposeAction> actions;
    //是否收藏
    private boolean isCollected;

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public List<ComposeAction> getActions() {
        return actions;
    }

    public void setActions(List<ComposeAction> actions) {
        this.actions = actions;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getRestSec() {
        return restSec;
    }

    public void setRestSec(Integer restSec) {
        this.restSec = restSec;
    }

    public Integer getCollects() {
        return collects;
    }

    public void setCollects(Integer collects) {
        this.collects = collects;
    }

    public String getShowd() {
        return showd;
    }

    public void setShowd(String showd) {
        this.showd = showd;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
