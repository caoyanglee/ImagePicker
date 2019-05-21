package io.weimu.www.imagepicker.fragment.adapter


import android.content.Context
import com.bumptech.glide.Glide
import com.weimu.universalview.core.BaseB
import com.weimu.universalview.core.recyclerview.BaseRecyclerAdapter
import com.weimu.universalview.core.recyclerview.BaseRecyclerViewHolder
import io.weimu.www.imagepicker.R
import kotlinx.android.synthetic.main.grid_item_image.view.*


class ImageGridAdapter(mContext: Context, var maxImageNumber: Int = 9) : BaseRecyclerAdapter<BaseB, String>(mContext) {

    var imageActionListener: ImageActionListener? = null


    override fun getItemLayoutRes(): Int = R.layout.grid_item_image

    override fun itemViewChange(holder: BaseRecyclerViewHolder, position: Int) {
        val item = getItem(position)
        holder.itemView.apply {
            Glide.with(mContext).asBitmap().load(item).into(this.iv_cover)
            //点击事件
            this.iv_cover.setOnClickListener { imageActionListener?.onItemClick(position) }
            this.iv_cover_delete.setOnClickListener { imageActionListener?.onItemDeleteClick(position) }
        }

    }

    override fun getFooterLayoutRes(): Int = R.layout.grid_item_image_add


    interface ImageActionListener {

        fun onItemClick(position: Int)

        fun onItemDeleteClick(position: Int)
    }
}