package com.weimu.library.model

import java.io.Serializable


internal class LocalMedia(
        var path: String,
        var duration: Long = 0,
        var lastUpdateAt: Long = 0) : Serializable, Comparable<LocalMedia> {

    override fun compareTo(another: LocalMedia): Int = -path.compareTo(another.path)
}
