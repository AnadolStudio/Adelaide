package com.anadolstudio.adelaide.view.screens.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.databinding.ActivityMainBinding
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

    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.nightBtn.setOnClickListener { (application as App).changeTheme() }
        binding.photoCardView.setOnClickListener { startActivity(EditType.PHOTO) }
//        binding.collageCardView.setOnClickListener {  } TODO
        setContentView(binding.root)
    }

    private fun startActivity(data: EditType) {
        GalleryListActivity.start(this, data.name)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)

        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
