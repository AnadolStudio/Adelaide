package com.anadolstudio.adelaide

import android.content.Intent
import android.os.Bundle
import com.anadolstudio.core.presentation.activity.CoreActivity

class SplashActivity : CoreActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(Intent(this, SingleActivity::class.java))
        finish()
    }
}
