package com.weimu.library.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.weimu.library.ImageStaticHolder
import com.weimu.library.R
import com.weimu.library.model.LocalMedia
import com.weimu.library.ui.ImageSelectorActivity
import com.weimu.universalview.ktx.load4CenterCrop
import java.io.File
import java.util.*


internal class ImageListAdapter(
        private var context: Context,
        val maxSelectNum: Int,
        var selectMode: Int = ImageSelectorActivity.MODE_MULTIPLE,
        var showCamera: Boolean = true,
        var enablePreview: Boolean = true
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var images: ArrayList<LocalMedia> = ArrayList()
        private set
    private var selectImages: ArrayList<LocalMedia> = ArrayList()

    private var imageSelectChangedListener: OnImageSelectChangedListener? = null

    val selectedImages: List<LocalMedia>
        get() = selectImages


    fun bindImages(images: ArrayList<LocalMedia>) {
        //讲选中的集合引用 给到appData
        ImageStaticHolder.setChooseImages(images)
        this.images = images
        notifyDataSetChanged()
    }

    fun bindSelectImages(images: ArrayList<LocalMedia>) {
        this.selectImages = images
        notifyDataSetChanged()
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener!!.onChange(selectImages)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (showCamera && position == 0) {
            TYPE_CAMERA
        } else {
            TYPE_PICTURE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == TYPE_CAMERA) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_camera, parent, false)
            return HeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_picture, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_CAMERA) {
            val headerHolder = holder as HeaderViewHolder
            headerHolder.headerView.setOnClickListener {
                if (imageSelectChangedListener != null) {
                    imageSelectChangedListener!!.onTakePhoto()
                }
            }
        } else {
            val contentHolder = holder as ViewHolder
            val image = images[if (showCamera) position - 1 else position]

            contentHolder.picture.load4CenterCrop(
                    file = File(image.path),
                    placeholder = R.drawable.image_placeholder
            )

            if (selectMode == ImageSelectorActivity.MODE_SINGLE) {
                contentHolder.ivCheckCircle.visibility = View.GONE
            }

            selectImage(contentHolder, isSelected(image))

            if (enablePreview) {
                contentHolder.ivCheckCircle.setOnClickListener { changeCheckboxState(contentHolder, image) }
            }

            contentHolder.contentView.setOnClickListener {
                if ((selectMode == ImageSelectorActivity.MODE_SINGLE || enablePreview) && imageSelectChangedListener != null) {
                    imageSelectChangedListener!!.onPictureClick(image, if (showCamera) position - 1 else position, holder.picture)
                } else {
                    changeCheckboxState(contentHolder, image)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (showCamera) images.size + 1 else images.size
    }

    @SuppressLint("StringFormatMatches")
    private fun changeCheckboxState(contentHolder: ViewHolder, image: LocalMedia) {
        val isChecked = contentHolder.ivCheckCircle.isSelected
        if (selectImages.size >= maxSelectNum && !isChecked) {
            Toast.makeText(context, context.getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show()
            return
        }
        if (isChecked) {
            for (media in selectImages) {
                if (media.path == image.path) {
                    selectImages.remove(media)
                    break
                }
            }
        } else {
            selectImages.add(image)
        }
        selectImage(contentHolder, !isChecked)
        if (imageSelectChangedListener != null) {
            imageSelectChangedListener!!.onChange(selectImages)
        }
    }

    fun isSelected(image: LocalMedia): Boolean {
        for (media in selectImages) {
            if (media.path == image.path) {
                return true
            }
        }
        return false
    }

    fun selectImage(holder: ViewHolder, isChecked: Boolean) {
        holder.ivCheckCircle.isSelected = isChecked
        if (isChecked) {

            holder.picture.setColorFilter(ContextCompat.getColor(context, R.color.image_overlay2), PorterDuff.Mode.SRC_ATOP)
        } else {
            holder.picture.setColorFilter(ContextCompat.getColor(context, R.color.image_overlay), PorterDuff.Mode.SRC_ATOP)
        }
    }

    internal class HeaderViewHolder(var headerView: View) : RecyclerView.ViewHolder(headerView)

    inner class ViewHolder(var contentView: View) : RecyclerView.ViewHolder(contentView) {
        var picture: ImageView
        var ivCheckCircle: ImageView

        init {
            picture = contentView.findViewById(R.id.picture)
            ivCheckCircle = contentView.findViewById(R.id.iv_check_circle)
        }

    }

    interface OnImageSelectChangedListener {
        fun onChange(selectImages: List<LocalMedia>)

        fun onTakePhoto()

        fun onPictureClick(media: LocalMedia, position: Int, view: View)
    }

    fun setOnImageSelectChangedListener(imageSelectChangedListener: OnImageSelectChangedListener) {
        this.imageSelectChangedListener = imageSelectChangedListener
    }

    companion object {
        val TYPE_CAMERA = 1
        val TYPE_PICTURE = 2
    }
}
