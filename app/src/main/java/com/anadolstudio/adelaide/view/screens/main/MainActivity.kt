package com.anadolstudio.adelaide.view.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.databinding.ActivityMainBinding
import com.anadolstudio.adelaide.domain.utils.FirebaseHelper
import com.anadolstudio.adelaide.domain.utils.FirebaseHelper.Event
import com.anadolstudio.adelaide.view.screens.gallery.GalleryListActivity

class MainActivity : AppCompatActivity() {
    companion object {
        fun start(context: Context) {
            context.startActivity(
                Intent(
                    context,
                    MainActivity::class.java
                )
            )
        }

        const val EDIT_TYPE = "edit_type"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.nightBtn.setOnClickListener { (application as App).changeTheme() }
        binding.photoCardView.setOnClickListener { startActivity(TypeKey.PHOTO_KEY) }
//        binding.collageCardView.setOnClickListener {  } TODO
        setContentView(binding.root)
    }

    private fun startActivity(data: String) {
        if (data != TypeKey.COLLAGE_KEY && data != TypeKey.PHOTO_KEY) return

        FirebaseHelper.get().logEvent(
            if (data == TypeKey.PHOTO_KEY) Event.PHOTO_EDIT_OPEN else Event.COLLAGE_EDIT_OPEN
        )

        GalleryListActivity.start(this, data)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}