package com.anadolstudio.adelaide.view.screens.save

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import com.anadolstudio.adelaide.BuildConfig
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.data.AdKeys
import com.anadolstudio.adelaide.data.SettingsPreference
import com.anadolstudio.adelaide.databinding.ActivitySaveBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.share_action.SharedAction
import com.anadolstudio.adelaide.domain.editphotoprocessor.share_action.SharedActionFactory
import com.anadolstudio.adelaide.domain.utils.BitmapHelper.decodeSampledBitmapFromContentResolverPath
import com.anadolstudio.adelaide.domain.utils.FirebaseHelper
import com.anadolstudio.adelaide.view.animation.AnimateUtil.Companion.DURATION_EXTRA_LONG
import com.anadolstudio.adelaide.view.animation.AnimateUtil.Companion.showAnimX
import com.anadolstudio.adelaide.view.screens.dialogs.ImageDialogTouchListener
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.view.BaseActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdView

class SaveActivity : BaseActivity(), IDetailable<SharedAction.SharedItem> {

    companion object {
        const val PATH = "path"
        const val DELTA = 70
        val TAG = SaveActivity::class.java.name

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

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        setSupportActionBar(binding.navigationToolbar)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null

        binding.homeButton.setOnClickListener {
            FirebaseHelper.get().logEvent(FirebaseHelper.Event.BACK_TO_MAIN)
            if (!SettingsPreference.hasPremium(this)) showInterstitialAd()
        }

        binding.savedImage.setOnClickListener {} // Не бесполезный, он влияет на анимацию
        binding.savedImage.setOnTouchListener(ImageDialogTouchListener(path, this))

        RxTask.Base {
            decodeSampledBitmapFromContentResolverPath(this, path, 400, 400)
        }.onSuccess { binding.savedImage.setImageBitmap(it) }

        binding.recyclerView.adapter = SharedAdapter(SharedActionFactory.instance(), this@SaveActivity)
    }

    override fun toDetail(data: SharedAction.SharedItem) {

        val fbEvent = when (data) {
            is SharedActionFactory.Empty -> FirebaseHelper.Event.SHARE_OTHERS
            is SharedActionFactory.VK -> FirebaseHelper.Event.SHARE_VK
            is SharedActionFactory.Instagram -> FirebaseHelper.Event.SHARE_INSTAGRAM
            is SharedActionFactory.Facebook -> FirebaseHelper.Event.SHARE_FACEBOOK
            is SharedActionFactory.Messenger -> FirebaseHelper.Event.SHARE_MESSENGER
            is SharedActionFactory.WhatsApp -> FirebaseHelper.Event.SHARE_WHATS_APP
            is SharedActionFactory.Twitter -> FirebaseHelper.Event.SHARE_TWITTER
        }

        FirebaseHelper.get().logEvent(fbEvent)
        data.createShareIntent(path, this)
    }

    private fun updateAd(): Boolean = SettingsPreference.hasPremium(this).also { hasPremium ->
        if (hasPremium) binding.adCardView.visibility = View.INVISIBLE
    }

    override fun onResume() {
        super.onResume()
        updateAd()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        with(binding.selfPromotionCardView) {
            if (visibility != VISIBLE) {
                showAnimX(view = this, -width.toFloat(), 0F)

                if (SettingsPreference.isFirstRunShareActivity(this@SaveActivity))
                    return

                if (!BuildConfig.DEBUG) SettingsPreference.firstRunShareActivity(this@SaveActivity)

                Handler(Looper.getMainLooper()).postDelayed({
                    horizontalScrollAnim()
                }, 2000L)

            }
        }
    }

    private fun horizontalScrollAnim() {
        binding.recyclerView.apply {
            smoothScrollBy(DELTA, 0, null, DURATION_EXTRA_LONG.toInt())
            postDelayed(
                { smoothScrollBy(-DELTA, 0, null, DURATION_EXTRA_LONG.toInt()) },
                DURATION_EXTRA_LONG + 200
            )
        }
    }

    private fun initAd() {
        if (updateAd()) return

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
            .forNativeAd { ad ->
                nativeAdView = layoutInflater.inflate(R.layout.native_ad_layout, null) as NativeAdView

                NativeAd.populateNativeAdView(ad, nativeAdView ?: return@forNativeAd)
                binding.adContainer.removeAllViews()
                binding.adContainer.addView(nativeAdView)
            }
            .withAdListener(
                NativeAd.NativeAdListener(
                    { showAnimX(binding.adCardView, binding.adCardView.width.toFloat(), 0F) },
                    { binding.adCardView.visibility = View.INVISIBLE }
                )
            )
            .build()

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