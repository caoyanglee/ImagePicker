package com.pmm.imagepicker.ui

import android.content.ContextWrapper
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.pmm.imagepicker.R
import com.pmm.imagepicker.adapter.ImageFolderAdapter
import com.pmm.imagepicker.databinding.DialogFolderBinding
import com.pmm.imagepicker.model.MedialFile
import com.pmm.imagepicker.model.MediaFolder
import com.pmm.ui.core.StatusNavigationBar
import com.pmm.ui.ktx.dip2px
import com.pmm.ui.ktx.init
import kotlin.properties.Delegates

/**
 * Author:你需要一台永动机
 * Date:2020/7/31 11:21
 * Description:文件夹弹窗，底部弹出
 */
internal class FolderDialog(
        context: ContextWrapper,
        private var folderIndex: Int = 0//文件夹的指针
) : BottomSheetDialog(context) {


    var folders: List<MediaFolder> by Delegates.observable(arrayListOf()) { _, _, newValue ->
        mAdapter.setDataToAdapter(newValue)
    }

    fun getFolderIndex() = folderIndex//文件夹指针

    var onFolderClickListener: ((folderName: String?, images: List<MedialFile>) -> Unit)? = null

    private val mAdapter by lazy { ImageFolderAdapter(getContext(), folderIndex) }

    init {
        val viewBinding = DialogFolderBinding.inflate(LayoutInflater.from(context))
        this.setContentView(viewBinding.root)

        //初始化列表
        with(viewBinding.recyFolder) {
            this.init()
            this.setPadding(0, context.dip2px(8f), 0, context.dip2px(8f))
            this.adapter = mAdapter
            mAdapter.onFolderClickListener = { index, folderName, images ->
                folderIndex = index
                onFolderClickListener?.invoke(folderName, images)
                dismiss()
            }
        }

        //设置布局背景透明，才能显示的出圆角
        this.window?.apply {
            StatusNavigationBar.setStatusNavigationBarTransparent(window)
            StatusNavigationBar.change2DarkStatusBar(window)
            this.findViewById<View>(R.id.design_bottom_sheet).apply {
                this.setBackgroundResource(android.R.color.transparent)

                //扩展开
//                val orginLayoutParams = this.layoutParams
//                orginLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
//                this.layoutParams = orginLayoutParams
                //解决平板模式没有显示完全的BUG
                val mDialogBehavior = BottomSheetBehavior.from(this)
                mDialogBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
                //mDialogBehavior.setPeekHeight(0)
            }

            //背景不透明度
            this.setDimAmount(0.5f)
            //自定义显示和隐藏动画
            //this.window.setWindowAnimations(R.style.bottomMenuAnim)

        }
    }


}