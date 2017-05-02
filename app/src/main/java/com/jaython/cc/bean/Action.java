package com.jaython.cc.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * time: 2017/1/13
 * description:
 *
 * @author fandong
 */
public class Action implements Parcelable {
    public static final Parcelable.Creator<Action> CREATOR = new Parcelable.Creator<Action>() {
        @Override
        public Action createFromParcel(Parcel source) {
            return new Action(source);
        }

        @Override
        public Action[] newArray(int size) {
            return new Action[size];
        }
    };
    private Integer id;
    private Integer groupId;
    private String title;
    private String icon;
    private String description;
    private String created;
    private Integer isDisplay;
    private String video;
    private String prepare;


    //  setter/getter
    private String detail;

    public Action() {
    }

    protected Action(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.groupId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.title = in.readString();
        this.icon = in.readString();
        this.description = in.readString();
        this.created = in.readString();
        this.isDisplay = (Integer) in.readValue(Integer.class.getClassLoader());
        this.video = in.readString();
        this.prepare = in.readString();
        this.detail = in.readString();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public Integer getIsDisplay() {
        return isDisplay;
    }

    public void setIsDisplay(Integer isDisplay) {
        this.isDisplay = isDisplay;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public String getPrepare() {
        return prepare;
    }

    public void setPrepare(String prepare) {
        this.prepare = prepare;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.groupId);
        dest.writeString(this.title);
        dest.writeString(this.icon);
        dest.writeString(this.description);
        dest.writeString(this.created);
        dest.writeValue(this.isDisplay);
        dest.writeString(this.video);
        dest.writeString(this.prepare);
        dest.writeString(this.detail);
    }
}
