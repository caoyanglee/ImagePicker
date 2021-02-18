package com.pmm.imagepicker.model

import android.net.Uri
import java.io.Serializable
import java.util.ArrayList


internal class MediaFolder(
        var name: String?,
        var path: String = "/"
) : Serializable {

    var firstImagePath: String? = null
    var firstImageUri: Uri? = null
    var imageNum: Int = 0
    var images: ArrayList<MedialFile> = ArrayList()
}
