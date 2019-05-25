package com.pmm.imagepicker.model

import java.io.Serializable


internal class LocalMedia(
        var path: String,
        var duration: Long = 0,
        var lastUpdateAt: Long = 0) : Serializable, Comparable<LocalMedia> {

    override fun compareTo(other: LocalMedia): Int = -path.compareTo(other.path)
}
