package com.zzz.opengldemo

import android.app.Application
import android.content.Context

/**
 *  author : chentao
 *  date : 2020/12/21
 *  email: chentao3@yy.com
 */
class LocalApplication:Application() {
    companion object{
        lateinit var context:Context;
    }

    override fun onCreate() {
        super.onCreate()
        context=applicationContext
    }
}