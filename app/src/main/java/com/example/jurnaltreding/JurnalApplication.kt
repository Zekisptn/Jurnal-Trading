package com.example.jurnaltreding

import android.app.Application
import com.example.jurnaltreding.data.AppContainer
import com.example.jurnaltreding.data.AppDataContainer

class JurnalApplication : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
