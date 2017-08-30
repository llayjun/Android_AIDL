package com.millet.mydemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/29 0029.
 */

public class User implements Parcelable {

    private String id;
    private String name;
    /**
     * 进程Id
     */
    private String pId;

    public User(String _id, String _name, String _pId) {
        id = _id;
        name = _name;
        pId = _pId;
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        pId = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel _parcel, int _i) {
        _parcel.writeString(id);
        _parcel.writeString(name);
        _parcel.writeString(pId);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", pId='" + pId + '\'' +
                '}';
    }
}
