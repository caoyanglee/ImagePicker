package com.weimu.imagepicker.utils

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import java.io.*

/*
 * Modified from original in AOSP.
 */
internal object CropUtil {

    private val SCHEME_FILE = "file"
    private val SCHEME_CONTENT = "content"

    fun closeSilently(c: Closeable?) {
        if (c == null) return
        try {
            c.close()
        } catch (t: Throwable) {
            // Do nothing
        }

    }

    fun getExifRotation(imageFile: File?): Int {
        if (imageFile == null) return 0
        try {
            val exif = ExifInterface(imageFile.absolutePath)
            // We only recognize a subset of orientation tag values
            when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)) {
                ExifInterface.ORIENTATION_ROTATE_90 -> return 90
                ExifInterface.ORIENTATION_ROTATE_180 -> return 180
                ExifInterface.ORIENTATION_ROTATE_270 -> return 270
                else -> return ExifInterface.ORIENTATION_UNDEFINED
            }
        } catch (e: IOException) {
            return 0
        }

    }

    fun copyExifRotation(sourceFile: File?, destFile: File?): Boolean {
        if (sourceFile == null || destFile == null) return false
        try {
            val exifSource = ExifInterface(sourceFile.absolutePath)
            val exifDest = ExifInterface(destFile.absolutePath)
            exifDest.setAttribute(ExifInterface.TAG_ORIENTATION, exifSource.getAttribute(ExifInterface.TAG_ORIENTATION))
            exifDest.saveAttributes()
            return true
        } catch (e: IOException) {
            return false
        }

    }

    fun getFromMediaUri(context: Context, resolver: ContentResolver, uri: Uri?): File? {
        if (uri == null) return null

        if (SCHEME_FILE == uri.scheme) {
            return File(uri.path)
        } else if (SCHEME_CONTENT == uri.scheme) {
            val filePathColumn = arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
            var cursor: Cursor? = null
            try {
                cursor = resolver.query(uri, filePathColumn, null, null, null)
                if (cursor != null && cursor.moveToFirst()) {
                    val columnIndex = if (uri.toString().startsWith("content://com.google.android.gallery3d"))
                        cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                    else
                        cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
                    // Picasa images on API 13+
                    if (columnIndex != -1) {
                        val filePath = cursor.getString(columnIndex)
                        if (!TextUtils.isEmpty(filePath)) {
                            return File(filePath)
                        }
                    }
                }
            } catch (e: IllegalArgumentException) {
                // Google Drive images
                return getFromMediaUriPfd(context, resolver, uri)
            } catch (ignored: SecurityException) {
                // Nothing we can do
            } finally {
                cursor?.close()
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun getTempFilename(context: Context): String {
        val outputDir = context.cacheDir
        val outputFile = File.createTempFile("image", "tmp", outputDir)
        return outputFile.absolutePath
    }

    private fun getFromMediaUriPfd(context: Context, resolver: ContentResolver, uri: Uri?): File? {
        if (uri == null) return null

        var input: FileInputStream? = null
        var output: FileOutputStream? = null
        try {
            val pfd = resolver.openFileDescriptor(uri, "r")
            val fd = pfd!!.fileDescriptor
            input = FileInputStream(fd)

            val tempFilename = getTempFilename(context)
            output = FileOutputStream(tempFilename)

            val bytes = ByteArray(4096)
            val read = input.read(bytes)
            while ((read) != -1) {
                output.write(bytes, 0, read)
            }
            return File(tempFilename)
        } catch (ignored: IOException) {
            // Nothing we can do
        } finally {
            closeSilently(input)
            closeSilently(output)
        }
        return null
    }


}
