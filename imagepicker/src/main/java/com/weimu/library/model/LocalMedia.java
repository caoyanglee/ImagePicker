package com.weimu.library.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class LocalMedia implements Serializable,Comparable<LocalMedia>,Parcelable {
    private String path;
    private long duration;
    private long lastUpdateAt;


    public LocalMedia(String path, long lastUpdateAt, long duration) {
        this.path = path;
        this.duration = duration;
        this.lastUpdateAt = lastUpdateAt;
    }

    public LocalMedia(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLastUpdateAt() {
        return lastUpdateAt;
    }

    public void setLastUpdateAt(long lastUpdateAt) {
        this.lastUpdateAt = lastUpdateAt;
    }

    public long getDuration() {
        return duration;
    }
    public void setDuration(long duration) {
        this.duration = duration;
    }


    @Override
    public int compareTo(LocalMedia another) {

        return -(path.compareTo(another.path));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeLong(duration);
        dest.writeLong(lastUpdateAt);
    }


    public LocalMedia(Parcel source) {
        path = source.readString();
        duration=source.readLong();
        lastUpdateAt = source.readLong();
    }

    public static final Creator<LocalMedia> CREATOR = new Creator<LocalMedia>() {

        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public LocalMedia[] newArray(int size) {
            return new LocalMedia[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public LocalMedia createFromParcel(Parcel source) {
            return new LocalMedia(source);
        }
    };
}
