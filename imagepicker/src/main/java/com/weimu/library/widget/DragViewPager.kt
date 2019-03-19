package com.weimu.library.widget

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import com.nineoldandroids.view.ViewHelper
import com.shizhefei.view.largeimage.LargeImageView
import com.weimu.universalib.ktx.getScreenHeight

/**
 * Author:你需要一台永动机
 * Date:2019/3/19 15:33
 * Description:
 */
class DragViewPager : ViewPager, View.OnClickListener {

    companion object {
        val STATUS_NORMAL = 0//正常浏览状态
        val STATUS_MOVING = 1//滑动状态
        val STATUS_RESETTING = 2//返回中状态
        val TAG = "DragViewPager"

        val MIN_SCALE_SIZE = 0.3f//最小缩放比例
        val BACK_DURATION = 300//ms
        val DRAG_GAP_PX = 50
    }


    private var currentStatus = STATUS_NORMAL
    private var currentPageStatus: Int = 0

    private var mDownX: Float = 0.toFloat()
    private var mDownY: Float = 0.toFloat()
    private var screenHeight: Float = 0.toFloat()

    //解决返回时，快速点击边缘区域，图片卡主的问题
    //private float oDownX;
    //private float oDownY;

    /**
     * 要缩放的View
     */
    private var currentShowView: View? = null
    /**
     * 滑动速度检测类
     */
    private var mVelocityTracker: VelocityTracker? = null
    private var iAnimClose: IAnimClose? = null

    fun setIAnimClose(iAnimClose: IAnimClose) {
        this.iAnimClose = iAnimClose
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context)
    }

    fun init(context: Context) {
        screenHeight = context.getScreenHeight().toFloat()
        setBackgroundColor(Color.BLACK)
        addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

            override fun onPageSelected(position: Int) {

            }

            override fun onPageScrollStateChanged(state: Int) {
                currentPageStatus = state
            }
        })
        //设置自己为显示视图
        setCurrentShowView(this)
    }

    //重要
    fun setCurrentShowView(currentShowView: View?) {
        this.currentShowView = currentShowView
        this.currentShowView?.setOnClickListener(this)
    }


    //配合SubsamplingScaleImageView使用，根据需要拦截ACTION_MOVE
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (adapter !is DragViewAdapter) return super.onInterceptTouchEvent(ev)
        val mImage: LargeImageView? = (adapter as DragViewAdapter).getImageView(currentItem) //特殊操作
        if (mImage == null) return super.onInterceptTouchEvent(ev)
        val canPullDown = mImage.canScrollVertically(0)//是否可以下拉
        when (ev.action) {
            MotionEvent.ACTION_DOWN ->
                //Log.e("jc", "onInterceptTouchEvent:ACTION_DOWN currentStatus="+currentStatus);
                if (currentStatus == STATUS_NORMAL) {
                    mDownX = ev.rawX
                    mDownY = ev.rawY
                }
            MotionEvent.ACTION_MOVE -> {
                //Log.e("jc", "onInterceptTouchEvent:ACTION_MOVE");
                val centerY = height / 2

                val slot = mImage.height.toFloat() / mImage.scale / 2f

                //todo  重要
                //Log.e("jc", "centerY=" + centerY + " mImage.height=" + mImage.getHeight() + " scale=" + (mImage.getScale() / 2) + " pivotY=" + pivotY + " slot=" + slot);

                if (centerY <= slot) {
                    val deltaX = Math.abs((ev.rawX - mDownX).toInt())
                    val deltaY = (ev.rawY - mDownY).toInt()
                    //Log.e("jc", "deltaY=" + deltaY + " deltaX=" + deltaX);
                    if (DRAG_GAP_PX in deltaX..(deltaY - 1) && !canPullDown) {//往下移动超过临界，左右移动不超过临界时，拦截滑动事件
                        return true
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
            }
        }//Log.e("jc", "onInterceptTouchEvent:ACTION_UP");
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //Log.e("jc", "onTouchEvent1  状态=" + currentStatus + " type=" + ev.getAction());
        if (currentStatus == STATUS_RESETTING)
            return false
        //Log.e("jc", "onTouchEvent2  状态=" + currentStatus);
        when (ev.actionMasked) {

            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.rawX
                mDownY = ev.rawY
                addIntoVelocity(ev)
            }
            MotionEvent.ACTION_MOVE -> {
                addIntoVelocity(ev)
                val deltaY = (ev.rawY - mDownY).toInt()
                //手指往上滑动
                if (deltaY <= DRAG_GAP_PX && currentStatus != STATUS_MOVING)
                    return super.onTouchEvent(ev)
                //viewpager不在切换中，并且手指往下滑动，开始缩放
                if (currentPageStatus != ViewPager.SCROLL_STATE_DRAGGING && (deltaY > DRAG_GAP_PX || currentStatus == STATUS_MOVING)) {
                    moveView(ev.rawX, ev.rawY)
                    return true
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {

                if (currentStatus != STATUS_MOVING)
                    return super.onTouchEvent(ev)

                val mUpX = ev.rawX
                val mUpY = ev.rawY
                val vY = computeYVelocity()//松开时必须释放VelocityTracker资源
                //Log.e("jc", "onTouchEvent vY=" + vY + " mUpY=" + mUpY + " mDownY=" + mDownY + " screenHeight/4=" + (screenHeight / 4));
                if (vY >= 1200 || Math.abs(mUpY - mDownY) > screenHeight / 4) {
                    //下滑速度快，或者下滑距离超过屏幕高度的一半，就关闭
                    if (iAnimClose != null) {
                        iAnimClose!!.onPictureRelease(currentShowView)
                    }
                } else {
                    resetReviewState(mUpX, mUpY)
                }
            }
        }

        return super.onTouchEvent(ev)
    }

    //返回浏览状态
    private fun resetReviewState(mUpX: Float, mUpY: Float) {
        currentStatus = STATUS_RESETTING
        if (mUpY != mDownY) {
            Log.e("jc", "y轴重置准备")
            val valueAnimator = ValueAnimator.ofFloat(mUpY, mDownY)
            valueAnimator.duration = BACK_DURATION.toLong()
            valueAnimator.addUpdateListener { animation ->
                val mY = animation.animatedValue as Float

                val percent = (mY - mDownY) / (mUpY - mDownY)
                val mX = percent * (mUpX - mDownX) + mDownX

                Log.e("jc", "mY=$mY mX=$mX mDownY=$mDownY")
                moveView(mX, mY)
                if (mY == mDownY) {
                    mDownY = 0f
                    mDownX = 0f

                    currentStatus = STATUS_NORMAL
                    Log.e("jc", "y轴重置")
                }
            }
            valueAnimator.start()
        } else if (mUpX != mDownX) {
            Log.e("jc", "x轴重置准备")
            val valueAnimator = ValueAnimator.ofFloat(mUpX, mDownX)
            valueAnimator.duration = BACK_DURATION.toLong()
            valueAnimator.addUpdateListener { animation ->
                val mX = animation.animatedValue as Float
                val percent = (mX - mDownX) / (mUpX - mDownX)
                val mY = percent * (mUpY - mDownY) + mDownY
                moveView(mX, mY)
                if (mX == mDownX) {
                    mDownY = 0f
                    mDownX = 0f
                    currentStatus = STATUS_NORMAL
                    Log.e("jc", "x轴重置")
                }
            }
            valueAnimator.start()
        } else if (iAnimClose != null)
            iAnimClose!!.onPictureClick()
    }


    //移动View
    private fun moveView(movingX: Float, movingY: Float) {
        if (currentShowView == null)
            return
        currentStatus = STATUS_MOVING
        val deltaX = movingX - mDownX
        val deltaY = movingY - mDownY
        var scale = 1f
        var alphaPercent = 1f
        if (deltaY > 0) {
            scale = 1 - Math.abs(deltaY) / screenHeight
            alphaPercent = 1 - Math.abs(deltaY) / (screenHeight / 2)
        }

        ViewHelper.setTranslationX(currentShowView!!, deltaX)
        ViewHelper.setTranslationY(currentShowView!!, deltaY)
        scaleView(scale)
        setBackgroundColor(getBlackAlpha(alphaPercent))
    }

    //缩放View
    private fun scaleView(scale: Float) {
        var scale = scale
        scale = Math.min(Math.max(scale, MIN_SCALE_SIZE), 1f)
        ViewHelper.setScaleX(currentShowView!!, scale)
        ViewHelper.setScaleY(currentShowView!!, scale)
    }


    private fun getBlackAlpha(percent: Float): Int {
        var percent = percent
        percent = Math.min(1f, Math.max(0f, percent))
        val intAlpha = (percent * 255).toInt()
        return Color.argb(intAlpha, 0, 0, 0)
    }

    private fun addIntoVelocity(event: MotionEvent) {
        if (mVelocityTracker == null)
            mVelocityTracker = VelocityTracker.obtain()
        mVelocityTracker!!.addMovement(event)
    }


    private fun computeYVelocity(): Float {
        var result = 0f
        if (mVelocityTracker != null) {
            mVelocityTracker!!.computeCurrentVelocity(1000)
            result = mVelocityTracker!!.yVelocity
            releaseVelocity()
        }
        return result
    }

    private fun releaseVelocity() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.clear()
            mVelocityTracker!!.recycle()
            mVelocityTracker = null
        }
    }

    override fun onClick(v: View) {
        if (iAnimClose != null) {
            iAnimClose!!.onPictureClick()
        }
    }


    interface IAnimClose {
        fun onPictureClick()

        fun onPictureRelease(view: View?)
    }

    interface DragViewAdapter {
        fun getImageView(position: Int): LargeImageView
    }

}
