package io.weimu.www.imagepicker.fragment.adapter


import android.content.Context
import com.pmm.ui.core.recyclerview.BaseRecyclerAdapter
import com.pmm.ui.core.recyclerview.BaseRecyclerViewHolder
import com.pmm.ui.ktx.load
import com.pmm.ui.ktx.load4CenterCrop
import io.weimu.www.imagepicker.R
import kotlinx.android.synthetic.main.grid_item_image.view.*


class ImageGridAdapter(mContext: Context, var maxImageNumber: Int = 9) : BaseRecyclerAdapter<Any, String>(mContext) {


    var imageActionListener: ImageActionListener? = null

    fun getSelectNum() = maxImageNumber - dataList.size

    override fun getItemLayoutRes(): Int = R.layout.grid_item_image

    override fun itemViewChange(holder: BaseRecyclerViewHolder, position: Int) {
        val item = getItem(position)?:return
        holder.itemView.apply {
            this.iv_cover.load4CenterCrop(item)
            //点击事件
            this.iv_cover.setOnClickListener { imageActionListener?.onItemClick(position) }
            this.iv_cover_delete.setOnClickListener { imageActionListener?.onItemDeleteClick(position) }
        }

    }

    override fun getFooterLayoutRes(): Int = R.layout.grid_item_image_add


    override fun footerViewChange(holder: BaseRecyclerViewHolder) {
        holder.itemView.setOnClickListener {
            imageActionListener?.onItemAddClick()
        }
    }


    override fun addData(data: List<String>?) {
        super.addData(data)
        if (dataList.size == maxImageNumber) hideFooter()
    }

    override fun addData(item: String) {
        super.addData(item)
        if (dataList.size == maxImageNumber) hideFooter()

    }

    override fun addData(position: Int, item: String) {
        super.addData(position, item)
        if (dataList.size == maxImageNumber) hideFooter()
    }

    override fun removeItem(position: Int) {
        super.removeItem(position)
        //防止尾部添加按钮消失
        if (dataList.size == maxImageNumber - 1) showFooter()
    }

    interface ImageActionListener {

        fun onItemClick(position: Int)

        fun onItemDeleteClick(position: Int)

        fun onItemAddClick()
    }


}