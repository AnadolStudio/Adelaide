package com.anadolstudio.adelaide.feature.start

import android.annotation.SuppressLint
import android.os.Bundle
import com.anadolstudio.adelaide.feature.start.single.SingleActivity
import com.anadolstudio.ui.activity.CoreActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SingleActivity.start(this)
        finish()
    }
}
