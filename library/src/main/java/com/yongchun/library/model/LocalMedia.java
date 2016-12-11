package com.yongchun.library.model;

import java.io.Serializable;


public class LocalMedia implements Serializable,Comparable<LocalMedia> {
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
}
