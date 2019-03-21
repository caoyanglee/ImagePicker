package com.weimu.imagepicker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.weimu.imagepicker.R
import com.weimu.imagepicker.model.LocalMedia
import com.weimu.imagepicker.model.LocalMediaFolder

import java.io.File
import java.util.ArrayList


internal class ImageFolderAdapter(private val context: Context) : RecyclerView.Adapter<ImageFolderAdapter.ViewHolder>() {
    private var folders: List<LocalMediaFolder> = ArrayList()
    private var checkedIndex = 0

    private var onItemClickListener: OnItemClickListener? = null

    fun bindFolder(folders: List<LocalMediaFolder>) {
        this.folders = folders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_folder, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("StringFormatMatches")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folders[position]

        Glide.with(context)
                .asBitmap()
                .load(File(folder.firstImagePath))
                .apply(RequestOptions().centerCrop())
                .transition(BitmapTransitionOptions.withCrossFade())
                .into(holder.firstImage)

        holder.folderName.text = folder.name
        holder.imageNum.text = context.getString(R.string.num_postfix, folder.imageNum)

        holder.isSelected.visibility = if (checkedIndex == position) View.VISIBLE else View.GONE

        holder.contentView.setOnClickListener {
            if (onItemClickListener != null) {
                checkedIndex = position
                notifyDataSetChanged()
                onItemClickListener!!.onItemClick(folder.name, folder.images)
            }
        }
    }

    override fun getItemCount(): Int {
        return folders.size
    }

    inner class ViewHolder(var contentView: View) : RecyclerView.ViewHolder(contentView) {
        var firstImage: ImageView
        var folderName: TextView
        var imageNum: TextView
        var isSelected: ImageView

        init {
            firstImage = contentView.findViewById<View>(R.id.first_image) as ImageView
            folderName = contentView.findViewById<View>(R.id.folder_name) as TextView
            imageNum = contentView.findViewById<View>(R.id.image_num) as TextView
            isSelected = contentView.findViewById<View>(R.id.is_selected) as ImageView
        }
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(folderName: String?, images: List<LocalMedia>)
    }
}
