package com.alarmapplication.www

import android.app.Application
import com.alarmapplication.www.util.SettingsManager

// 데이터 저장, 초기화
class BaseApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        SettingsManager.init(this)
    }
}