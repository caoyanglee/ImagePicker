package io.weimu.www.imagepicker

import android.graphics.Color
import com.pmm.ui.OriginAppData
import com.pmm.ui.ktx.getDrawablePro
import com.pmm.ui.widget.ToolBarPro

/**
 * Author:你需要一台永动机
 * Date:2018/1/17 13:59
 * Description:
 */
class AppData : OriginAppData() {
    override fun isDebug(): Boolean = BuildConfig.DEBUG

    override fun onCreate() {
        super.onCreate()
        initToolBar()
    }

    private fun initToolBar() {
        ToolBarPro.GlobalConfig.apply {
            //centerTitle
            centerTitleColor = Color.WHITE
            centerTitleSize = 17f

            //navigation
            navigationDrawable = context.getDrawablePro(R.drawable.universal_arrow_back_white)
        }
    }


}
