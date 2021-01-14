package com.pmm.imagepicker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import by.kirich1409.viewbindingdelegate.viewBinding
import com.pmm.imagepicker.R
import com.pmm.imagepicker.databinding.ListItemFolderBinding
import com.pmm.imagepicker.model.ImageData
import com.pmm.imagepicker.model.LocalMediaFolder
import com.pmm.ui.core.recyclerview.BaseRecyclerAdapter
import com.pmm.ui.core.recyclerview.BaseRecyclerViewHolder
import com.pmm.ui.ktx.click
import com.pmm.ui.ktx.load4CenterCrop
import java.io.File

/**
 * Author:你需要一台永动机
 * Date:2019-05-27 15:48
 * Description:文件夹适配器
 */

internal typealias FolderClickCallBack = ((index: Int, folderName: String?, images: List<ImageData>) -> Unit)?

internal class ImageFolderAdapter(
        mContext: Context,
        private var checkedIndex: Int = -1//选中的位置
) : BaseRecyclerAdapter<Any, LocalMediaFolder>(mContext) {

    override fun getItemLayoutRes(): Int = R.layout.list_item_folder

    var onFolderClickListener: FolderClickCallBack = null

    override fun getViewHolder(itemView: View?): BaseRecyclerViewHolder = ItemViewHolder(itemView)

    @SuppressLint("StringFormatMatches")
    override fun itemViewChange(holder: BaseRecyclerViewHolder, position: Int) {
        val item = getItem(position) ?: return
        (holder as ItemViewHolder).mVB.apply {
            //图片
            this.firstImage.load4CenterCrop(
                    file = File(item.firstImagePath),
                    placeholder = R.drawable.ic_image_24dp
            )
            //文件夹 名称
            this.folderName.text = item.name
            //文件夹的图片数
            this.imageNum.text = mContext.getString(R.string.num_postfix, item.imageNum)
            //是否显示
            this.isSelected.visibility = if (checkedIndex == position) View.VISIBLE else View.GONE
            //点击事件
            container.click {
                checkedIndex = position
                notifyDataSetChanged()
                onFolderClickListener?.invoke(position, item.name, item.images)
            }
        }
    }

    private class ItemViewHolder(itemView: View?) : BaseRecyclerViewHolder(itemView) {
        val mVB by viewBinding(ListItemFolderBinding::bind, R.id.container)
    }
}


