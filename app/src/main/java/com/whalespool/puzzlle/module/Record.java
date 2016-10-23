package com.whalespool.puzzlle.module;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;

public class Record extends BmobObject implements Parcelable {
    String picId;
    int level;
    int finishTime;
    String createTime;
    String username;

    public Record() {
    }

    protected Record(Parcel in) {
        picId = in.readString();
        finishTime = in.readInt();
        createTime = in.readString();
        level = in.readInt();
        username = in.readString();
    }

    public static final Creator<Record> CREATOR = new Creator<Record>() {
        @Override
        public Record createFromParcel(Parcel in) {
            return new Record(in);
        }

        @Override
        public Record[] newArray(int size) {
            return new Record[size];
        }
    };

    public String getPicId() {
        return picId;
    }

    public void setPicId(String picId) {
        this.picId = picId;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public String getFinishTimeInFormat() {
        return String.format("%ds", finishTime);
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicIdInFormat(){
        int no = picId.indexOf(" ");
        String sub = picId.substring(0, 3);
        return String.format("%s Lv%d", sub, level);
    }

    @Override
    public String toString() {
        return "Record{" +
                "picId='" + picId + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", level='" + level + '\'' +
                ", username='" + username + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(picId);
        dest.writeInt(finishTime);
        dest.writeString(createTime);
        dest.writeInt(level);
        dest.writeString(username);
    }
}
