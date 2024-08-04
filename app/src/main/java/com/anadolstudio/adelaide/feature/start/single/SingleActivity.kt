package com.anadolstudio.adelaide.feature.start.single

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.base.BaseActionActivity
import com.anadolstudio.adelaide.databinding.ActivityMainBinding
import com.anadolstudio.ui.viewmodel.livedata.SingleEvent

class SingleActivity : BaseActionActivity<SingleViewModel, SingleController>(R.id.nav_host_fragment_content_main) {

    companion object {
        fun start(context: Context) = context.startActivity(Intent(context, SingleActivity::class.java))
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivityMainBinding.inflate(layoutInflater) }

    override fun createViewModelLazy(): Lazy<SingleViewModel> = viewModels { viewModelFactory }

    override fun provideRootView(): View = binding.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun handleEvent(event: SingleEvent) = when (event) {
        is SingleEvents.ChangeNightModeEvent -> AppCompatDelegate.setDefaultNightMode(event.mode)
        else -> super.handleEvent(event)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
