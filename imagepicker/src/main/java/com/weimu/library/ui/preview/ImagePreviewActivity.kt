package com.weimu.library.ui.preview

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.RelativeLayout
import android.widget.Toast
import com.shizhefei.view.largeimage.LargeImageView
import com.weimu.library.ImageStaticHolder
import com.weimu.library.R
import com.weimu.library.model.LocalMedia
import com.weimu.library.widget.DragViewPager
import com.weimu.universalview.core.activity.BaseActivity
import com.weimu.universalview.core.pager.BaseFragmentStatePagerAdapter
import com.weimu.universalview.core.toolbar.StatusBarManager
import com.weimu.universalview.core.toolbar.ToolBarManager
import com.weimu.universalview.ktx.setOnClickListenerPro
import java.util.*
import kotlin.properties.Delegates


internal class ImagePreviewActivity : BaseActivity() {

    companion object {
        val REQUEST_PREVIEW = 68
        val EXTRA_PREVIEW_SELECT_LIST = "previewSelectList"
        val EXTRA_MAX_SELECT_NUM = "maxSelectNum"
        val EXTRA_POSITION = "position"

        val OUTPUT_LIST = "outputList"
        val OUTPUT_ISDONE = "isDone"


        fun startPreview(context: Activity, selectImages: List<LocalMedia>, maxSelectNum: Int, position: Int) {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, selectImages as ArrayList<LocalMedia>)

            intent.putExtra(EXTRA_POSITION, position)
            intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum)
            context.startActivityForResult(intent, REQUEST_PREVIEW)
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        fun startPreviewWithAnim(context: Activity, selectImages: List<LocalMedia>, maxSelectNum: Int, position: Int, view: View) {
            val intent = Intent(context, ImagePreviewActivity::class.java)
            intent.putExtra(EXTRA_PREVIEW_SELECT_LIST, selectImages as ArrayList<LocalMedia>)

            intent.putExtra(EXTRA_POSITION, position)
            intent.putExtra(EXTRA_MAX_SELECT_NUM, maxSelectNum)
            context.startActivityForResult(intent, REQUEST_PREVIEW, ActivityOptions.makeSceneTransitionAnimation(context, view, "share_image").toBundle())
        }
    }

    private val selectBarLayout: RelativeLayout by lazy { findViewById<RelativeLayout>(R.id.select_bar_layout) }
    private var checkboxSelect: CheckBox? = null
    private val viewPager: DragViewPager by lazy { findViewById<DragViewPager>(R.id.drag_viewPager) }

    private var position: Int = 0
    private var maxSelectNum = 1
    private var images: List<LocalMedia> = ArrayList()//所有图片
    private var selectImages: ArrayList<LocalMedia> = ArrayList()//选择的图片


    var isShowBar by Delegates.observable(false) { property, oldValue, newValue ->
        //todo是否显示
        if (newValue) {
            showStatusBar()
        } else {
            hideStatusBar()
        }
        if (newValue) {
            toolBarManager.showToolBar()
        } else {
            toolBarManager.hideToolBar()
        }
        selectBarLayout.visibility = if (newValue) View.VISIBLE else View.GONE

    }

    private lateinit var toolBarManager: ToolBarManager

    override fun getLayoutResID(): Int = R.layout.activity_image_preview

    override fun afterViewAttach(savedInstanceState: Bundle?) {
        initData()
        initView()
        registerListener()
    }

    private fun initData() {
        //images = getIntent().getParcelableArrayListExtra(EXTRA_PREVIEW_LIST);
        images = ImageStaticHolder.getChooseImages()
        selectImages = intent.getSerializableExtra(EXTRA_PREVIEW_SELECT_LIST) as ArrayList<LocalMedia>
        maxSelectNum = intent.getIntExtra(EXTRA_MAX_SELECT_NUM, 9)
        position = intent.getIntExtra(EXTRA_POSITION, 1)
    }

    private fun initView() {
        //状态栏和Toolbar
        StatusBarManager.setColor(this.window, ContextCompat.getColor(this, R.color.white))
        StatusBarManager.setLightMode(this.window, false)
        toolBarManager = ToolBarManager.with(this, getContentView())
                .bg {
                    this.setBackgroundResource(R.color.white)
                }
                .leftMenuIcon {
                    this.setImageResource(R.drawable.toolbar_arrow_back_black)
                }
                .title {
                    this.text = "${(position + 1).toString() + "/" + images.size}"
                }
                .rightMenuText {
                    this.text = getString(R.string.done)
                    this.setTextColor(ContextCompat.getColorStateList(context, R.color.black_text_selector))
                    this.isEnabled = false
                    this.setOnClickListenerPro {
                        //点击完成
                        onDoneClick(true)
                    }
                }

        onSelectNumChange()

        checkboxSelect = findViewById<View>(R.id.checkbox_select) as CheckBox
        onImageSwitch(position)


        viewPager.adapter = SimpleFragmentAdapter(supportFragmentManager)
        viewPager.currentItem = position

    }

    @SuppressLint("StringFormatMatches")
    fun registerListener() {
        viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {
                toolBarManager.title { this.text = ((position + 1).toString() + "/" + images.size) }
                onImageSwitch(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        checkboxSelect!!.setOnClickListener(View.OnClickListener {
            val isChecked = checkboxSelect!!.isChecked
            if (selectImages.size >= maxSelectNum && isChecked) {
                Toast.makeText(this@ImagePreviewActivity, getString(R.string.message_max_num, maxSelectNum), Toast.LENGTH_LONG).show()
                checkboxSelect!!.isChecked = false
                return@OnClickListener
            }
            val image = images[viewPager!!.currentItem]
            if (isChecked) {
                selectImages.add(image)
            } else {
                for (media in selectImages) {
                    if (media.path == image.path) {
                        selectImages.remove(media)
                        break
                    }
                }
            }
            onSelectNumChange()
        })
    }

    inner class SimpleFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getItem(position: Int): Fragment = ImagePreviewFragment.newInstance(images[position].path!!)
        override fun getCount(): Int = images.size
    }


    @SuppressLint("SetTextI18n")
    fun onSelectNumChange() {
        val enable = selectImages.size != 0
        toolBarManager.rightMenuText { this.isEnabled = enable }

        if (enable) {
            toolBarManager.rightMenuText {
                this.text = "${getString(R.string.done_num)}(${selectImages.size}/${maxSelectNum})"
            }
        } else {
            toolBarManager.rightMenuText {
                this.text = getString(R.string.done)
            }
        }
    }

    fun onImageSwitch(position: Int) {
        checkboxSelect!!.isChecked = isSelected(images[position])
    }

    fun isSelected(image: LocalMedia): Boolean {
        for (media in selectImages) {
            if (media.path == image.path) {
                return true
            }
        }
        return false
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


    fun onDoneClick(isDone: Boolean) {
        val intent = Intent()
        intent.putExtra(OUTPUT_LIST, selectImages as ArrayList<*>)
        intent.putExtra(OUTPUT_ISDONE, isDone)
        setResult(Activity.RESULT_OK, intent)
        super.onBackPressed()
        overridePendingTransition(-1, R.anim.fade_out)
    }

    override fun onBackPressed() {
        onDoneClick(false)
    }

}
