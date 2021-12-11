package io.weimu.www.imagepicker

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.multidex.MultiDex
import com.pmm.ui.ktx.getDrawablePro
import com.pmm.ui.widget.ToolBarPro

/**
 * Author:你需要一台永动机
 * Date:2018/1/17 13:59
 * Description:
 */
class AppData : Application() {

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
            navigationDrawable = applicationContext.getDrawablePro(R.drawable.universal_arrow_back_white)
        }
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }


}
