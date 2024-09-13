package com.imcys.shiqueditinfodemo.base

import android.app.Application
import com.kongzue.dialogx.DialogX

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DialogX.init(this);
    }
}