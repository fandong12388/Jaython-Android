package com.jaython.cc.bean;

/**
 * time: 2016/11/21
 * description:
 *
 * @author sunjianfei
 */
public class User {
    private String uid;
    private String platform;
    private String created;
    private UserProfile userProfile;
    private UserThird userThird;

    public UserThird getUserThird() {
        return userThird;
    }

    public void setUserThird(UserThird userThird) {
        this.userThird = userThird;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }
}
