package com.pmm.imagepicker.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import com.pmm.imagepicker.R
import com.pmm.imagepicker.adapter.ImageFolderAdapter
import com.pmm.imagepicker.model.LocalMediaFolder
import com.weimu.universalview.core.recyclerview.decoration.LinearItemDecoration
import com.weimu.universalview.ktx.dip2px
import com.weimu.universalview.ktx.getScreenHeight
import com.weimu.universalview.ktx.getScreenWidth
import java.lang.reflect.Method

/**
 * Author:你需要一台永动机
 * Date:2019-05-21 15:25
 * Description:文件夹列表 popWindow
 */
internal class FolderWindow(private val context: Context) : PopupWindow() {
    private val window: View
    private lateinit var recyclerView: RecyclerView
    private val mAdapter: ImageFolderAdapter by lazy { ImageFolderAdapter(context) }

    private var isDismiss = false

    init {
        window = LayoutInflater.from(context).inflate(R.layout.window_folder, null)
        val view = window.findViewById<View>(R.id.lin_parent)
        view.setOnClickListener { dismiss() }
        this.contentView = window
        this.width = context.getScreenWidth()
        this.height = context.getScreenHeight() - context.dip2px(96f)
        this.animationStyle = R.style.WindowStyle
        this.isFocusable = true
        this.isOutsideTouchable = true
        this.isClippingEnabled = false
        this.update()
        this.setBackgroundDrawable(ColorDrawable(Color.argb(153, 0, 0, 0)))

        initView()
        setPopupWindowTouchModal(this, false)
    }

    private fun initView() {
        recyclerView = (window.findViewById<View>(R.id.folder_list) as RecyclerView).apply {
            this.addItemDecoration(LinearItemDecoration(
                    context = context,
                    dividerSize = 1,
                    dividerDrawable = ColorDrawable(Color.rgb(220, 220, 220))))
            this.layoutManager = LinearLayoutManager(context)
            this.adapter = mAdapter

            this.visibility = View.GONE
        }

    }

    fun bindFolder(folders: List<LocalMediaFolder>) {
        mAdapter.bindFolder(folders)
    }

    override fun showAsDropDown(anchor: View) {
        super.showAsDropDown(anchor)
        Handler().postDelayed({
            recyclerView.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(context, R.anim.up_in)
            recyclerView.startAnimation(animation)
        }, 300)

    }

    fun setOnItemClickListener(onItemClickListener: ImageFolderAdapter.OnItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener)
    }

    override fun dismiss() {
        if (isDismiss) return
        isDismiss = true
        val animation = AnimationUtils.loadAnimation(context, R.anim.down_out)
        recyclerView.startAnimation(animation)
        dismiss()
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                isDismiss = false
                recyclerView.visibility = View.GONE
                super@FolderWindow.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }


    companion object {

        fun setPopupWindowTouchModal(popupWindow: PopupWindow?, touchModal: Boolean) {
            if (null == popupWindow) {
                return
            }
            val method: Method
            try {
                method = PopupWindow::class.java.getDeclaredMethod("setTouchModal", Boolean::class.javaPrimitiveType!!)
                method.isAccessible = true
                method.invoke(popupWindow, touchModal)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

}
