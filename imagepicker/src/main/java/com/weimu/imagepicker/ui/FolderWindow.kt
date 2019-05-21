package com.weimu.imagepicker.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import com.weimu.imagepicker.R
import com.weimu.imagepicker.adapter.ImageFolderAdapter
import com.weimu.imagepicker.model.LocalMediaFolder
import com.weimu.universalview.ktx.dip2px
import com.weimu.universalview.ktx.getScreenHeight
import com.weimu.universalview.ktx.getScreenWidth
import java.lang.reflect.Method


internal class FolderWindow(private val context: Context) : PopupWindow() {
    private val window: View
    private var recyclerView: RecyclerView? = null
    private var adapter: ImageFolderAdapter? = null

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
        registerListener()
        setPopupWindowTouchModal(this, false)
    }

    fun initView() {
        adapter = ImageFolderAdapter(context)

        recyclerView = window.findViewById<View>(R.id.folder_list) as RecyclerView
        recyclerView!!.addItemDecoration(ItemDivider())
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        recyclerView!!.adapter = adapter

        recyclerView!!.visibility = View.GONE
    }

    fun registerListener() {

    }

    fun bindFolder(folders: List<LocalMediaFolder>) {
        adapter!!.bindFolder(folders)
    }

    override fun showAsDropDown(anchor: View) {
        super.showAsDropDown(anchor)
        Handler().postDelayed({
            recyclerView!!.visibility = View.VISIBLE
            val animation = AnimationUtils.loadAnimation(context, R.anim.up_in)
            recyclerView!!.startAnimation(animation)
        }, 300)

    }

    fun setOnItemClickListener(onItemClickListener: ImageFolderAdapter.OnItemClickListener) {
        adapter!!.setOnItemClickListener(onItemClickListener)
    }

    override fun dismiss() {
        if (isDismiss) {
            return
        }
        isDismiss = true
        val animation = AnimationUtils.loadAnimation(context, R.anim.down_out)
        recyclerView!!.startAnimation(animation)
        dismiss()
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {

            }

            override fun onAnimationEnd(animation: Animation) {
                isDismiss = false
                recyclerView!!.visibility = View.GONE
                super@FolderWindow.dismiss()
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
    }

    inner class ItemDivider : RecyclerView.ItemDecoration() {
        private val mDrawable: Drawable

        init {
            mDrawable = context.resources.getDrawable(R.drawable.item_divider)
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView) {
            val left = context.dip2px(16f)
            val right = parent.width - left

            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + mDrawable.intrinsicHeight
                mDrawable.setBounds(left, top, right, bottom)
                mDrawable.draw(c)
            }
        }

        override fun getItemOffsets(outRect: Rect, position: Int, parent: RecyclerView) {
            outRect.set(0, 0, 0, mDrawable.intrinsicWidth)
        }

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
