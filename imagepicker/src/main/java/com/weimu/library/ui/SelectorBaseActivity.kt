package com.weimu.library.ui

import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup


open class SelectorBaseActivity : AppCompatActivity() {
    //获取内容视图
    lateinit var contentView: ViewGroup
        private set

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        contentView = window.decorView.findViewById(android.R.id.content)
    }
}
