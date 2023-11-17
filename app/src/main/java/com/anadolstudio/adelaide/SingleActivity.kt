package com.anadolstudio.adelaide

import android.os.Bundle
import com.anadolstudio.adelaide.databinding.ActivityMainBinding
import com.anadolstudio.core.presentation.activity.CoreActivity

class SingleActivity : CoreActivity() {

    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}
