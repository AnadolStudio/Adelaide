package com.anadolstudio.adelaide.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.App
import com.anadolstudio.adelaide.databinding.ActivityMainBinding
import com.anadolstudio.adelaide.helpers.FirebaseHelper


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

        private val TAG: String = MainActivity::class.java.name
        const val PHOTO_TYPE = "type_photo"
        const val COLLAGE_TYPE = "type_collage"
        const val EDIT_TYPE = "edit_type"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        binding.nightBtn.setOnClickListener { (application as App).changeTheme() }
        binding.photoCardView.setOnClickListener { startActivity(PHOTO_TYPE) }
//        binding.collageCardView.setOnClickListener {  } TODO
        setContentView(binding.root)
    }

    private fun startActivity(type: String) {
        if (type != COLLAGE_TYPE && type != PHOTO_TYPE) return

        if (type == PHOTO_TYPE)
            FirebaseHelper.get().logEvent(FirebaseHelper.Event.PHOTO_EDIT_OPEN)
        if (type == COLLAGE_TYPE)
            FirebaseHelper.get().logEvent(FirebaseHelper.Event.COLLAGE_EDIT_OPEN)

        GalleryActivity.start(this, type)
    }

    override fun recreate() {
        finish()
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
        startActivity(intent)
        overridePendingTransition(
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )
    }
}