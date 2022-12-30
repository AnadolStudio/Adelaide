package com.anadolstudio.adelaide.ui.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.databinding.ActivityMainBinding
import com.anadolstudio.adelaide.domain.utils.confetti.WinterConfettiGenerator
import com.anadolstudio.adelaide.ui.screens.gallery.GalleryListActivity
import com.anadolstudio.core.view.animation.AnimateUtil.scaleAnimationOnClick

class MainActivity : AppCompatActivity() {
    companion object {

        fun start(context: Context) = context.startActivity(
            Intent(context, MainActivity::class.java)
        )
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.nightBtn.setOnClickListener { (application as App).changeTheme() }
        binding.photoCardView.scaleAnimationOnClick { startActivity(EditType.PHOTO) }
//        binding.collageCardView.setOnClickListener {  } TODO
        setContentView(binding.root)

        Handler(mainLooper).postDelayed(
            { WinterConfettiGenerator(
                binding.confettiContainer,
                AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
                ).launchConfetti()
            },
            500)
    }

    private fun startActivity(data: EditType) {
        GalleryListActivity.start(this, data)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
