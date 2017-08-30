package com.millet.mydemo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/8/29 0029.
 */

public class Book implements Parcelable {

    public int bookId;

    public String bookName;

    public Book(int _bookId, String _bookName) {
        bookId = _bookId;
        bookName = _bookName;
    }

    protected Book(Parcel in) {
        bookId = in.readInt();
        bookName = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel _parcel, int _i) {
        _parcel.writeInt(bookId);
        _parcel.writeString(bookName);
    }

    @Override public String toString() {
        return "ID: " + bookId + ", BookName: " + bookName;
    }

}
