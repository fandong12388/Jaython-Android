package com.jaython.cc.bean;

/**
 * time: 17/2/1
 * description:
 *
 * @author fandong
 */
public class DynamicComment {
    private Integer id;
    private Integer dynamicId;
    private String uid;
    private String rUid;
    private Integer type;
    private String content;
    private String created;
    private UserProfile user;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(Integer dynamicId) {
        this.dynamicId = dynamicId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getrUid() {
        return rUid;
    }

    public void setrUid(String rUid) {
        this.rUid = rUid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
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

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }
}
