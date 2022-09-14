package com.anadolstudio.adelaide.view.adcontrollers

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.data.AdKeys
import com.anadolstudio.adelaide.databinding.ActivitySaveBinding
import com.anadolstudio.adelaide.view.animation.AnimateUtil
import com.anadolstudio.adelaide.view.screens.save.NativeAd
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView

class SaveAdController(val binding: ActivitySaveBinding) : AdController.Abstract() {

    private var mInterstitialAd: InterstitialAd? = null
    private var nativeAdView: NativeAdView? = null

    override fun load(activity: AppCompatActivity) {
        super.load(activity)
        val adRequest = AdRequest.Builder().build()
        loadNative(activity, adRequest)
        loadInterstitial(activity, adRequest)
    }

    private fun loadNative(
        activity: AppCompatActivity,
        adRequest: AdRequest
    ) {
        val adLoader = AdLoader.Builder(activity, AdKeys.KeyManager.nativeId)
            .forNativeAd { ad ->
                nativeAdView = activity.layoutInflater
                    .inflate(R.layout.native_ad_layout, null) as NativeAdView

                NativeAd.populateNativeAdView(ad, nativeAdView!!)
                binding.adContainer.removeAllViews()
                binding.adContainer.addView(nativeAdView)
            }.withAdListener(
                NativeAd.NativeAdListener(
                    {
                        AnimateUtil.showAnimX(
                            binding.adCardView,
                            binding.adCardView.width.toFloat(),
                            0F
                        )
                    }, {
                        binding.adCardView.visibility = View.INVISIBLE
                    }
                )
            ).build()

        adLoader.loadAd(adRequest)
    }

    private fun loadInterstitial(
        activity: AppCompatActivity,
        adRequest: AdRequest
    ) {
        val fullScreenContentCallback: FullScreenContentCallback =
            object : FullScreenContentCallback() {

                override fun onAdShowedFullScreenContent() {
                    mInterstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    activity.onSupportNavigateUp()// TODO hardcode, довать гибкость а вместо активити использвать Context
                    super.onAdDismissedFullScreenContent()
                }
            }

        InterstitialAd.load(activity, AdKeys.KeyManager.interstitialId, adRequest,

            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback = fullScreenContentCallback
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    mInterstitialAd = null
                }
            })
    }

    fun showInterstitialAd(activity: AppCompatActivity) {
        mInterstitialAd?.show(activity)
    }

    override fun updateView(showAd: Boolean) {
        binding.adCardView.visibility = if (showAd) View.VISIBLE else View.GONE
    }

    fun destroy() {
        mInterstitialAd = null
        nativeAdView?.destroy()
    }

}