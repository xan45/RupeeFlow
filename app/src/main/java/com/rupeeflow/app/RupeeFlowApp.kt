package com.rupeeflow.app

import android.app.Application
import com.rupeeflow.app.di.AppContainer
import com.rupeeflow.app.di.DefaultAppContainer

class RupeeFlowApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
