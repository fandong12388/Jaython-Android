package com.jaython.cc.bean;

/**
 * time:2017/1/15
 * description:
 *
 * @author fandong
 */
public class ComposeAction {
    private Integer id;
    private Integer composeId;
    private Integer actionId;
    private Integer num;
    private Integer sum;
    private String created;
    private Action action;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getComposeId() {
        return composeId;
    }

    public void setComposeId(Integer composeId) {
        this.composeId = composeId;
    }

    public Integer getActionId() {
        return actionId;
    }

    public void setActionId(Integer actionId) {
        this.actionId = actionId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public Integer getSum() {
        return sum;
    }

    public void setSum(Integer sum) {
        this.sum = sum;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
