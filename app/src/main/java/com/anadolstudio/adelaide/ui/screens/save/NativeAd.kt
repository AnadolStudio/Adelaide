package com.anadolstudio.adelaide.ui.screens.save

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.anadolstudio.adelaide.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

object NativeAd {
    fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view.
        adView.mediaView = adView.findViewById(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView?.setMediaContent(nativeAd.mediaContent!!)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.

        adView.bodyView?.visibility = getVisibleMode(nativeAd.body)
        nativeAd.body?.let { (adView.bodyView as TextView).text = it }

        adView.callToActionView?.visibility = getVisibleMode(nativeAd.callToAction)
        nativeAd.callToAction?.let { (adView.callToActionView as Button).text = it }

        adView.iconView?.visibility = getVisibleMode(nativeAd.icon)
        nativeAd.icon?.let { (adView.iconView as ImageView).setImageDrawable(it.drawable) }

        adView.priceView?.visibility = getVisibleMode(nativeAd.price)
        nativeAd.price?.let { (adView.priceView as TextView).text = it }

        adView.storeView?.visibility = getVisibleMode(nativeAd.store)
        nativeAd.store?.let { (adView.storeView as TextView).text = it }

        /*adView.starRatingView?.visibility = getVisibleMode(nativeAd.starRating)
    nativeAd.starRating?.let { (adView.starRatingView as RatingBar).rating = it.toFloat() }*/

        adView.advertiserView?.visibility = getVisibleMode(nativeAd.advertiser)
        nativeAd.advertiser?.let { (adView.advertiserView as TextView).text = it }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        vc?.let {
            if (it.hasVideoContent()) {
                // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
                // VideoController will call methods on this object when events occur in the video
                // lifecycle.
                vc.videoLifecycleCallbacks = object : VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                        super.onVideoEnd()
                    }
                }
            }
        }
    }

    private fun getVisibleMode(item: Any?) = item?.let { return View.VISIBLE } ?: View.GONE

    class NativeAdListener(
            val adLoaded: () -> Unit,
            val adFailLoaded: () -> Unit
    ) : AdListener() {

        override fun onAdLoaded() {
            super.onAdLoaded()
            adLoaded.invoke()
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            super.onAdFailedToLoad(loadAdError)
            adFailLoaded.invoke()
        }
    }
}
