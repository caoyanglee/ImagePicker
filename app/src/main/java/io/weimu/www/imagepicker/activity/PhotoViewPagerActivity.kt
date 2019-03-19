package io.weimu.www.imagepicker.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.WindowManager
import com.weimu.library.view.SelectorBaseActivity
import com.weimu.library.widget.PreviewViewPager
import com.weimu.universalib.ktx.getColorPro
import com.weimu.universalview.core.fragment.BaseFragment
import com.weimu.universalview.core.pager.BaseFragmentPagerAdapter
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.core.toolbar.ToolBarManager
import io.weimu.www.imagepicker.R
import io.weimu.www.imagepicker.fragment.ImagePreviewFragment
import java.util.*

class PhotoViewPagerActivity : SelectorBaseActivity() {
    private var mViewPager: PreviewViewPager? = null


    private var isShowBar = true

    private var imagList: List<String> = ArrayList()
    private val fragments = ArrayList<BaseFragment>()
    private var position = 0
    private lateinit var mAdapter: BaseFragmentPagerAdapter

    private lateinit var toolBarManager: ToolBarManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_view_pager)

        initData()
        initToolBar()
        initViewPager()
    }


    private fun initData() {
        position = intent.getIntExtra(POSITION, 0)
        imagList = intent.getStringArrayListExtra(PICLIST)
    }


    private fun initToolBar() {
        StatusBarManager.setColor(window, getColorPro(R.color.white))
        StatusBarManager.setLightMode(window)
        toolBarManager = ToolBarManager.with(this, contentView)
                .bg {
                    this.setBackgroundResource(com.weimu.library.R.color.white)
                }
                .leftMenuIcon {
                    this.setImageResource(com.weimu.library.R.drawable.toolbar_arrow_back_black)
                }
                .title {
                    this.text = "${(position + 1).toString() + "/" + imagList.size}"
                }
    }


    private fun initViewPager() {
        mViewPager = findViewById(R.id.id_vp)

        for (i in imagList.indices) {
            fragments.add(ImagePreviewFragment.newInstance(imagList[i]))
        }
        mAdapter = BaseFragmentPagerAdapter(supportFragmentManager)
        mAdapter.setFragments(fragments)
        mViewPager!!.adapter = mAdapter
        mViewPager!!.currentItem = position
        mViewPager!!.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                toolBarManager.title {
                    this.text = (position + 1).toString() + "/" + imagList.size
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

            }
        })


    }


    private fun hideStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.attributes = attrs
    }

    private fun showStatusBar() {
        val attrs = window.attributes
        attrs.flags = attrs.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        window.attributes = attrs
    }

    fun switchBarVisibility() {
        toolBarManager.bg {
            this.visibility = if (isShowBar) View.GONE else View.VISIBLE
        }
        if (isShowBar) {
            hideStatusBar()
        } else {
            showStatusBar()
        }
        isShowBar = !isShowBar
    }

    companion object {

        val POSITION = "position"
        val PICLIST = "piclist"

        fun newInstance(context: Context, position: Int, imagList: ArrayList<String>): Intent {
            val i = Intent(context, PhotoViewPagerActivity::class.java)
            i.putExtra(POSITION, position)
            i.putStringArrayListExtra(PICLIST, imagList)
            return i
        }
    }
}
