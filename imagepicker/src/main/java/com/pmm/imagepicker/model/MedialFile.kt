package com.pmm.imagepicker.model

import android.content.ContextWrapper
import android.net.Uri
import android.os.Environment
import android.os.Parcel
import android.os.ParcelFileDescriptor
import android.os.Parcelable
import com.pmm.ui.helper.FileHelper
import java.io.File
import java.io.FileInputStream

/**
 * Author:你需要一台永动机
 * Date:1/14/21 3:37 PM
 * Description:
 */
class MedialFile(
    @Deprecated("Android10后开始弃用")
    val path: String?,
    val uri: Uri?,
    val name: String? = "",
    val size: Int = 0,
    val createTime: Long = 0L
) : Parcelable, Comparable<MedialFile> {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readInt(),
        parcel.readLong(),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(path)
        parcel.writeParcelable(uri, flags)
        parcel.writeString(name)
        parcel.writeInt(size)
        parcel.writeLong(createTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MedialFile> {
        override fun createFromParcel(parcel: Parcel): MedialFile {
            return MedialFile(parcel)
        }

        override fun newArray(size: Int): Array<MedialFile?> {
            return arrayOfNulls(size)
        }
    }

    override fun compareTo(other: MedialFile): Int = -((createTime).compareTo(other.createTime))

    //获取图片大小
    fun getSizeStr(): String {
        val GB: Long = 1073741824 // 1024 * 1024 * 1024
        val MB: Long = 1048576 // 1024 * 1024
        val KB: Long = 1024

        return if (size >= GB) {
            String.format("%.2f GB", size * 1.0 / GB)
        } else if (size >= MB) {
            String.format("%.2f MB", size * 1.0 / MB)
        } else {
            String.format("%.2f KB", size * 1.0 / KB)
        }
    }

}



