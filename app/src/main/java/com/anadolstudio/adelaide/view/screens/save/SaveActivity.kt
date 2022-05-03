package com.anadolstudio.adelaide.view.screens.save

import android.animation.ObjectAnimator
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import androidx.core.animation.doOnEnd
import androidx.core.content.FileProvider
import com.anadolstudio.adelaide.BuildConfig
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.view.animation.AnimateUtil.Companion.DURATION_EXTRA_LONG
import com.anadolstudio.adelaide.view.animation.AnimateUtil.Companion.showAnimX
import com.anadolstudio.adelaide.databinding.ActivitySaveBinding
import com.anadolstudio.adelaide.fragments.ImageDialogTouchListener
import com.anadolstudio.adelaide.domain.utils.BitmapHelper.CONTENT
import com.anadolstudio.adelaide.domain.utils.BitmapHelper.decodeSampledBitmapFromContentResolverPath
import com.anadolstudio.adelaide.domain.utils.FirebaseHelper
import com.anadolstudio.adelaide.domain.utils.populateNativeAdView
import com.anadolstudio.adelaide.data.AdKeys
import com.anadolstudio.adelaide.data.SettingsPreference
import com.anadolstudio.core.view.BaseActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView
import java.io.File


class SaveActivity : BaseActivity(), View.OnClickListener {
    companion object {
        const val PATH = "path"
        val TAG = SaveActivity::class.java.name
        private const val VK_PACKAGE = "com.vkontakte.android"
        private const val INSTAGRAM_PACKAGE = "com.instagram.android"
        private const val FACEBOOK_PACKAGE = "com.facebook.katana"
        private const val MESSENGER_PACKAGE = "com.facebook.orca"
        private const val WHATS_APP_PACKAGE = "com.whatsapp"
        private const val TWITTER_PACKAGE = "com.twitter.android"

        fun start(context: Context, path: String?) {
            val starter = Intent(context, SaveActivity::class.java)
            starter.putExtra(PATH, path)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivitySaveBinding
    private lateinit var path: String
    private var nativeAdView: NativeAdView? = null
    private var mInterstitialAd: InterstitialAd? = null
    private val fullScreenContentCallback: FullScreenContentCallback =
        object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() {
                mInterstitialAd = null
                Log.d(TAG, "The ad was shown.")
            }

            override fun onAdDismissedFullScreenContent() {
                onSupportNavigateUp()
                super.onAdDismissedFullScreenContent()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = intent.getStringExtra(PATH).toString()
        binding = ActivitySaveBinding.inflate(layoutInflater)
        FirebaseHelper.get().logEvent(FirebaseHelper.Event.SAVE_PHOTO)
        init()
        initAd()
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        FirebaseHelper.get().logEvent(FirebaseHelper.Event.BACK_TO_PHOTO)
        super.onBackPressed()
    }

    private fun init() {
        setSupportActionBar(binding.navigationToolbar)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null
        binding.homeButton.setOnClickListener {
            FirebaseHelper.get().logEvent(FirebaseHelper.Event.BACK_TO_MAIN)
            if (!SettingsPreference.hasPremium(this)) showInterstitialAd()
        }
        binding.savedImage.setOnClickListener(this) // Не бесполезный, он влияет на анимацию

        binding.savedImage.setOnTouchListener(
            ImageDialogTouchListener(path, this)
        )

        binding.savedImage.setImageBitmap(
            decodeSampledBitmapFromContentResolverPath(this, path, 400, 400)
        )

        binding.share.setOnClickListener(this)
        binding.instagram.setOnClickListener(this)
        binding.vk.setOnClickListener(this)
        binding.facebook.setOnClickListener(this)
        binding.messenger.setOnClickListener(this)
        binding.whatsApp.setOnClickListener(this)
        binding.twitter.setOnClickListener(this)
    }

    private fun updateAd() {
        if (SettingsPreference.hasPremium(this)) {
            binding.adCardView.visibility = View.INVISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        updateAd()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.share -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_OTHERS)
                createShareIntent(path, null)
            }
            R.id.vk -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_VK)
                createShareIntent(path, VK_PACKAGE)
            }
            R.id.instagram -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_INSTAGRAM)
                createShareIntent(path, INSTAGRAM_PACKAGE)
            }
            R.id.facebook -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_FACEBOOK)
                createShareIntent(path, FACEBOOK_PACKAGE)
            }
            R.id.messenger -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_MESSENGER)
                createShareIntent(path, MESSENGER_PACKAGE)
            }
            R.id.whats_app -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_WHATS_APP)
                createShareIntent(path, WHATS_APP_PACKAGE)
            }
            R.id.twitter -> {
                FirebaseHelper.get().logEvent(FirebaseHelper.Event.SHARE_TWITTER)
                createShareIntent(path, TWITTER_PACKAGE)
            }
        }
    }

    private fun createShareIntent(path: String, namePackage: String?) {
        val type = "image/*"
        val photoURI = if (path.contains(CONTENT)) {
            Uri.parse(path)
        } else {
            FileProvider.getUriForFile(
                this,
                applicationContext.packageName + ".provider",
                File(path)
            )
        }

        val share = Intent(Intent.ACTION_SEND)
        share.type = type
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        share.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        share.putExtra(Intent.EXTRA_STREAM, photoURI)

        if (namePackage == null) {
            startActivity(Intent.createChooser(share, getString(R.string.another)))
        } else {
            share.setPackage(namePackage)
            try {
                startActivity(share)
            } catch (e: ActivityNotFoundException) {
                openGooglePlay(namePackage)
            }
        }
    }

    private fun openGooglePlay(namePackage: String) {
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$namePackage")))
        } catch (ex: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$namePackage")
                )
            )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (binding.selfPromotionCardView.visibility != VISIBLE) {
            showAnimX(
                binding.selfPromotionCardView,
                -binding.selfPromotionCardView.width.toFloat(),
                0F,
            )
            if (SettingsPreference.isFirstRunShareActivity(this)) {
                if (!BuildConfig.DEBUG)
                    SettingsPreference.firstRunShareActivity(this)

                Handler(Looper.getMainLooper()).postDelayed({
                    horizontalScrollAnim()
                }, 2000L)
            }
        }
    }

    private fun horizontalScrollAnim() {
        val animator = ObjectAnimator.ofInt(binding.horizontalScrollView, "scrollX", 300)
        animator.duration = DURATION_EXTRA_LONG
        animator.doOnEnd {
            val animatorEnd = ObjectAnimator.ofInt(binding.horizontalScrollView, "scrollX", 0)
            animatorEnd.duration = DURATION_EXTRA_LONG
            animatorEnd.start()
        }
        animator.start()
    }

    private fun initAd() {
        updateAd()
        if (SettingsPreference.hasPremium(this)) return

        MobileAds.initialize(this) { }
        loadNativeAd()
        loadInterstitialAd()
    }

    private fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(this, AdKeys.TestKeys.INTERSTITIAL_AD_ID, adRequest,
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    mInterstitialAd?.fullScreenContentCallback = fullScreenContentCallback
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.i(TAG, loadAdError.message)
                    mInterstitialAd = null
                }
            })
    }

    private fun loadNativeAd() {
        val adLoader = AdLoader.Builder(this, AdKeys.TestKeys.NATIVE_AD_ID)
            .forNativeAd {
                nativeAdView = layoutInflater
                    .inflate(R.layout.native_ad_layout, null) as NativeAdView

                populateNativeAdView(it, nativeAdView ?: return@forNativeAd)
                binding.adContainer.removeAllViews()
                binding.adContainer.addView(nativeAdView)
            }
            .withAdListener(object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    showAnimX(binding.adCardView, binding.adCardView.width.toFloat(), 0F)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    super.onAdFailedToLoad(loadAdError)
                    Log.d(TAG, "onAdFailedToLoad: ")
                    binding.adCardView.visibility = View.INVISIBLE
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                    Log.d(TAG, "onAdClicked: ")
                }
            }).build()

        val adRequest = AdRequest.Builder().build()
        adLoader.loadAd(adRequest)
    }

    private fun showInterstitialAd() {
        mInterstitialAd?.show(this) ?: Log.d(TAG, "The interstitial ad wasn't ready yet.")
    }

    override fun onDestroy() {
        nativeAdView?.destroy()
        super.onDestroy()
    }
}