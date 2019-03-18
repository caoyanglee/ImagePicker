package com.weimu.library.model

import java.io.Serializable


class LocalMedia : Serializable, Comparable<LocalMedia> {
    var path: String? = null
    var duration: Long = 0
    var lastUpdateAt: Long = 0


    constructor(path: String, lastUpdateAt: Long, duration: Long) {
        this.path = path
        this.duration = duration
        this.lastUpdateAt = lastUpdateAt
    }

    constructor(path: String) {
        this.path = path
    }


    override fun compareTo(another: LocalMedia): Int = -path!!.compareTo(another.path!!)


}
