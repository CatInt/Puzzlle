package com.whalespool.puzzlle.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobDate;

public class User extends BmobObject implements Parcelable {

    //    Integer user_id;
    String username = "";
    String pwd = "";
    BmobDate createTime = new BmobDate(new Date());
    String imgUrl = "";

    public User() {
    }

    protected User(Parcel in) {
        username = in.readString();
        pwd = in.readString();
        imgUrl = in.readString();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BmobDate getCreateTime() {
        return createTime;
    }

    public void setCreateTime(BmobDate createTime) {
        this.createTime = createTime;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "User{" +
                ", username='" + username + '\'' +
                ", pwd='" + pwd + '\'' +
                ", createTime=" + createTime +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(pwd);
        dest.writeString(imgUrl);
    }
}
