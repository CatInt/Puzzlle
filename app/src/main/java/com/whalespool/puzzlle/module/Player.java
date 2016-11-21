package com.whalespool.puzzlle.module;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by yodazone on 2016/11/21.
 */

public class Player extends BmobObject implements Parcelable{
    public String name;
    public boolean isOnline;
    public ArrayList<Record> records;
    public Record bestRecord;
    public int avatarRes;

    public Player(){}

    protected Player(Parcel in) {
        name = in.readString();
        isOnline = in.readByte() != 0;
        records = in.createTypedArrayList(Record.CREATOR);
        bestRecord = in.readParcelable(Record.class.getClassLoader());
        avatarRes = in.readInt();
    }

    public static final Creator<Player> CREATOR = new Creator<Player>() {
        @Override
        public Player createFromParcel(Parcel in) {
            return new Player(in);
        }

        @Override
        public Player[] newArray(int size) {
            return new Player[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeByte((byte) (isOnline ? 1 : 0));
        dest.writeTypedList(records);
        dest.writeParcelable(bestRecord, flags);
        dest.writeInt(avatarRes);
    }
}
