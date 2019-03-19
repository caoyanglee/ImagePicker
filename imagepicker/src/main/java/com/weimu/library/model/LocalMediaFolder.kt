package com.weimu.library.model

import java.io.Serializable
import java.util.ArrayList


internal class LocalMediaFolder : Serializable {
    var name: String? = null
    var path: String? = null
    var firstImagePath: String? = null
    var imageNum: Int = 0
    var images: List<LocalMedia> = ArrayList()
}
