package com.anadolstudio.adelaide.ui.ad_controllers

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.core.view.animation.AnimateUtil
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError

class EditAdController(val binding: ActivityEditBinding) : AdController.Abstract() {

    override fun load(activity: AppCompatActivity) {
        super.load(activity)
        val adRequest = AdRequest.Builder()
            .build()

        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                AnimateUtil.showAnimY(binding.adView, -binding.adView.height.toFloat(), 0F)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                binding.adView.visibility = View.VISIBLE
            }
        }

        binding.adView.loadAd(adRequest)
    }

    override fun updateView(showAd: Boolean) {
        binding.adView.visibility = if (showAd) View.VISIBLE else View.GONE
    }
}