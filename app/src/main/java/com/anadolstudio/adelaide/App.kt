package com.anadolstudio.adelaide

import android.app.Application
import com.anadolstudio.adelaide.di.DI

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        DI.init(applicationContext)
    }

}
