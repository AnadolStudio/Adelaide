package com.anadolstudio.adelaide.ui.screens.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.ui.screens.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivity.start(this)
        finish()
    }
}
