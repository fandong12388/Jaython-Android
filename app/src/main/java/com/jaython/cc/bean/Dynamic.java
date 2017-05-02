package com.jaython.cc.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * time: 17/1/29
 * description:
 *
 * @author fandong
 */
public class Dynamic {
    private Integer id;
    private String content;
    private String address;
    private ArrayList<String> images;
    private String uid;
    private int platform;
    private String build;
    private String created;
    //评论
    private List<DynamicComment> comments;
    //点赞
    private List<UserProfile> praises;
    //人
    private UserProfile userProfile;
    //是否赞过
    private boolean isPraised;

    //城市
    private String city;
    private String district;
    //评论的数量
    private Integer comment = 0;
    //点赞的数量
    private Integer praise = 0;

    public void addDynamicComment(DynamicComment dynamicComment) {
        if (comments == null) {
            comments = new ArrayList<>();
        }
        comments.add(0, dynamicComment);
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public Integer getComment() {
        return comment;
    }

    public void setComment(Integer comment) {
        this.comment = comment;
    }

    public Integer getPraise() {
        return praise;
    }

    public void setPraise(Integer praise) {
        this.praise = praise;
    }

    public boolean isPraised() {
        return isPraised;
    }

    public void setPraised(boolean praised) {
        isPraised = praised;
    }

    public List<DynamicComment> getComments() {
        return comments;
    }

    public void setComments(List<DynamicComment> comments) {
        this.comments = comments;
    }

    public List<UserProfile> getPraises() {
        return praises;
    }

    public void setPraises(List<UserProfile> praises) {
        this.praises = praises;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dynamic dynamic = (Dynamic) o;

        return id != null ? id.equals(dynamic.id) : dynamic.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
