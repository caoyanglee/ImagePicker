package com.weimu.library.core;

import android.app.Activity;
import android.os.Build;

import com.weimu.library.R;
import com.weimu.library.utils.StatusBarUtils;

/**
 * Author:你需要一台永动机
 * Date:2018/5/11 18:04
 * Description:
 */
public class StatusManager {
    private static final StatusManager ourInstance = new StatusManager();

    public static StatusManager getInstance() {
        return ourInstance;
    }

    private StatusManager() {
    }


    public void setColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtils.setColor(activity, color);
        } else {
            StatusBarUtils.setColor(activity, R.color.black_alpha50);
        }
        //亮色模式
        if (color == R.color.white) {
            StatusBarUtils.StatusBarLightMode(activity);
        } else {
            StatusBarUtils.StatusBarDarkMode(activity);
        }
    }

    public void transparent(Activity activity) {
        StatusBarUtils.transparencyBar(activity);
    }

}
