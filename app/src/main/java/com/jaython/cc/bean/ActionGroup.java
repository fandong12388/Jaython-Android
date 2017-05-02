package com.jaython.cc.bean;

import java.util.List;

/**
 * time: 2017/1/13
 * description:
 *
 * @author fandong
 */
public class ActionGroup {
    private Integer id;
    private String title;
    private String icon;
    private List<Action> actions;
    //------setter/getter

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }
}
