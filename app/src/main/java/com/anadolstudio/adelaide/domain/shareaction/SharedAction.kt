package com.anadolstudio.adelaide.domain.shareaction

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.anadolstudio.adelaide.R
import com.anadolstudio.core.util.BitmapDecoder
import java.io.File

interface SharedAction {

    fun runShareIntent(path: String, activity: AppCompatActivity)

    open class Base(val appPackage: AppPackages?) : SharedAction {

        override fun runShareIntent(path: String, activity: AppCompatActivity) {
            val type = "image/*"
            val photoURI =
                if (path.contains(BitmapDecoder.Manager.CONTENT)) {
                    Uri.parse(path)
                } else {
                    FileProvider.getUriForFile(
                        activity,
                        activity.applicationContext.packageName + ".provider",
                        File(path)
                    )
                }

            val share = Intent(Intent.ACTION_SEND)
            share.type = type
            share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            share.putExtra(Intent.EXTRA_STREAM, photoURI)

            if (appPackage == null) {
                activity.startActivity(
                    Intent.createChooser(share, activity.getString(R.string.save_func_another))
                )
            } else {
                share.setPackage(appPackage.appPackage)

                try {
                    activity.startActivity(share)
                } catch (e: ActivityNotFoundException) {
                    openGooglePlay(appPackage.appPackage, activity)
                }
            }
        }

        protected fun openGooglePlay(namePackage: String, activity: AppCompatActivity) {
            try {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=$namePackage")
                    )
                )
            } catch (ex: ActivityNotFoundException) {
                activity.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=$namePackage")
                    )
                )
            }
        }
    }

    sealed class SharedItem(
        @DrawableRes val drawable: Int,
        appPackage: AppPackages?
    ) : Base(appPackage)
}