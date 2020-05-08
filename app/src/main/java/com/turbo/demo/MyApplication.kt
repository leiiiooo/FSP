package com.turbo.demo

import android.app.Application
import com.turbo.fsp.FSP

/**
 * @author leiiiooo
 * @date 2020/5/8
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FSP.init(applicationContext)
    }
}