package com.anadolstudio.adelaide.ui.ad_controllers

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds

interface AdController {

    fun load(activity: AppCompatActivity)

    fun updateView(showAd: Boolean)

    abstract class Abstract : AdController {

        override fun load(activity: AppCompatActivity) {
            MobileAds.initialize(activity) {}
        }
    }

}
