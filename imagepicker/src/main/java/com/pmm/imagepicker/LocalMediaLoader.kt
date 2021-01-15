package com.pmm.imagepicker

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import com.pmm.imagepicker.model.MedialFile
import com.pmm.imagepicker.model.MediaFolder
import com.pmm.ui.ktx.formatDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.lang.Exception
import java.util.*

/**
 * Author:你需要一台永动机
 * Date:1/15/21 4:09 PM
 * Description:
 * 图片流 => 一一放入对应的文件夹中 =>进行最后排序
 */
internal class LocalMediaLoader(
        private val activity: FragmentActivity,
        private var type: Int = TYPE_IMAGE
) {
    private val TAG = "LocalMediaLoader"

    fun loadAllImage(loadComplete: ((folders: List<MediaFolder>) -> Unit)) {
        LoaderManager.getInstance(activity).initLoader(type, null, object : LoaderManager.LoaderCallbacks<Cursor> {

            override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {

                var cursorLoader: CursorLoader? = null
                when (id) {
                    TYPE_IMAGE -> {
                        cursorLoader = CursorLoader(
                                activity,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                IMAGE_PROJECTION,
                                MediaStore.Images.Media.MIME_TYPE + "=? or "
                                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                        + MediaStore.Images.Media.MIME_TYPE + "=? or "
                                        + MediaStore.Images.Media.MIME_TYPE + "=?",
                                arrayOf("image/jpeg", "image/png", "image/gif", "image/webp"),
                                IMAGE_PROJECTION[2] + " DESC")
                    }
                    TYPE_VIDEO -> {
                        cursorLoader = CursorLoader(
                                activity, MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                VIDEO_PROJECTION, null, null, VIDEO_PROJECTION[2] + " DESC")
                    }
                }
                return cursorLoader as Loader<Cursor>
            }

            override fun onLoadFinished(loader: Loader<Cursor>, data: Cursor?) {
                MainScope().launch(Dispatchers.IO) {
                    try {
                        if (data == null || data.isClosed) return@launch
                        if (!data.moveToFirst()) return@launch //issue链接：https://github.com/jeasonlzy/ImagePicker/issues/243#issuecomment-380353956

                        val imageFolder4All = MediaFolder(
                                name = activity.getString(R.string.all_image),
                        )//全部图片-文件夹
                        val imageFoldersMap = hashMapOf<String, MediaFolder>()

                        //while循环 必须先do，否则会缺失一张照片
                        do {
                            Log.d(TAG, "=========================================")
                            val path = data.getString(data.getColumnIndex(MediaStore.Images.Media.DATA))// 图片的路径
                            val id = data.getInt(data.getColumnIndex(MediaStore.MediaColumns._ID))
                            val name = data.getString(data.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME))
                            val size = data.getInt(data.getColumnIndex(MediaStore.MediaColumns.SIZE))
                            val createTime = data.getLong(data.getColumnIndex(MediaStore.MediaColumns.DATE_ADDED)) * 1000
                            val baseUri = Uri.parse("content://media/external/images/media")
                            val uri = Uri.withAppendedPath(baseUri, "" + id)
                            Log.d(TAG, "path = $path")
                            Log.d(TAG, "name = $name")
                            Log.d(TAG, "size = $size")
                            Log.d(TAG, "createTime = ${createTime.formatDate()}")
                            Log.d(TAG, "id = $id")
                            Log.d(TAG, "uri = $uri")

                            // 获取该图片的目录路径名
                            val nativeFile = File(path)
                            if (!nativeFile.exists()) continue

                            val imageFile = MedialFile(path, uri, name, size, createTime)
                            imageFolder4All.images.add(imageFile)

                            //图片对应的文件夹
                            val parentFile = nativeFile.parentFile
                            if (parentFile == null || !parentFile.exists()) continue
                            val dirPath = parentFile.absolutePath//文件夹路径
                            val dirName = parentFile.name//文件夹路径
                            Log.d(TAG, "parentName = $dirName")
                            Log.d(TAG, "parentPath = $dirPath")

                            var parentFolder = imageFoldersMap[dirPath]
                            if (parentFolder == null) {
                                parentFolder = MediaFolder(
                                        name = dirName,
                                        path = dirPath
                                )
                                imageFoldersMap[dirPath] = parentFolder
                            }
                            parentFolder.images.add(imageFile)


                        } while (!data.isClosed && data.moveToNext())

                        //开始排序
                        fun resolveFolder(folder: MediaFolder): MediaFolder {
                            folder.images.sort()
                            folder.firstImagePath = folder.images[0].path
                            folder.firstImageUri = folder.images[0].uri
                            folder.imageNum = folder.images.size
                            return folder
                        }

                        val folders = arrayListOf<MediaFolder>()

                        //处理全部图片的文件夹
                        folders.add(resolveFolder(imageFolder4All))
                        //处理全部图片的文件夹
                        for (item in imageFoldersMap) {
                            folders.add(resolveFolder(item.value))
                        }
                        //排序
                        sortFolder(folders)

                        //加载所有文件夹
                        withContext(Dispatchers.Main) {
                            loadComplete.invoke(folders)
                        }

                        //data.close()// 不用手动关闭
                    } catch (e: Exception) {
                        //nothing
                    }
                }

            }

            override fun onLoaderReset(loader: Loader<Cursor>) {}
        })
    }

    private fun sortFolder(imageFolders: List<MediaFolder>) {
        // 文件夹按图片数量排序
        Collections.sort(imageFolders, Comparator { lhs, rhs ->
            //默认升序
            if (lhs.name == activity.getString(R.string.all_image)) return@Comparator -1
            val lsize = lhs.imageNum
            val rsize = rhs.imageNum
            if (lsize == rsize) 0 else if (lsize < rsize) 1 else -1
        })
    }

    companion object {
        // load type
        val TYPE_CONTRACT = 0
        val TYPE_IMAGE = 1
        val TYPE_VIDEO = 2

        private val IMAGE_PROJECTION = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_ADDED,
        )

        private val VIDEO_PROJECTION = arrayOf(
                MediaStore.Video.Media.DATA, MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATE_ADDED, MediaStore.Video.Media._ID, MediaStore.Video.Media.DURATION, MediaStore.Images.Media.SIZE,
        )
    }

}
