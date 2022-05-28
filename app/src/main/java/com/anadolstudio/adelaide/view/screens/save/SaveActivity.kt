package com.anadolstudio.adelaide.view.screens.save

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View.VISIBLE
import com.anadolstudio.adelaide.BuildConfig
import com.anadolstudio.adelaide.data.SettingsPreference
import com.anadolstudio.adelaide.databinding.ActivitySaveBinding
import com.anadolstudio.adelaide.domain.shareaction.SharedAction.SharedItem
import com.anadolstudio.adelaide.domain.shareaction.SharedActionFactory
import com.anadolstudio.adelaide.domain.utils.FirebaseHelper
import com.anadolstudio.adelaide.view.adcontrollers.SaveAdController
import com.anadolstudio.adelaide.view.animation.AnimateUtil.Companion.DURATION_EXTRA_LONG
import com.anadolstudio.adelaide.view.animation.AnimateUtil.Companion.showAnimX
import com.anadolstudio.adelaide.view.screens.dialogs.ImageDialogTouchListener
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.tasks.RxTask
import com.anadolstudio.core.util.BitmapDecoder
import com.anadolstudio.core.view.BaseActivity

class SaveActivity : BaseActivity(), ActionClick<SharedItem> {

    companion object {
        private const val PATH = "path"
        const val ANIM_DELTA_X = 70

        fun start(context: Context, path: String) {
            val starter = Intent(context, SaveActivity::class.java)
            starter.putExtra(PATH, path)
            context.startActivity(starter)
        }
    }

    private lateinit var adController: SaveAdController
    private lateinit var binding: ActivitySaveBinding
    private lateinit var path: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = intent.getStringExtra(PATH).toString()
        binding = ActivitySaveBinding.inflate(layoutInflater)
        FirebaseHelper.get().logEvent(FirebaseHelper.Event.SAVE_PHOTO)
        setupView()
        setContentView(binding.root)
    }

    override fun onBackPressed() {
        FirebaseHelper.get().logEvent(FirebaseHelper.Event.BACK_TO_PHOTO)
        super.onBackPressed()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupView() {
        setSupportActionBar(binding.navigationToolbar)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null

        binding.homeButton.setOnClickListener {
            FirebaseHelper.get().logEvent(FirebaseHelper.Event.BACK_TO_MAIN)
            if (!SettingsPreference.hasPremium(this)) adController.showInterstitialAd(this)
        }

        binding.savedImage.setOnClickListener {} // Не бесполезный, он влияет на анимацию
        binding.savedImage.setOnTouchListener(ImageDialogTouchListener(path, this))

        // TODO ВЫнести во ViewModel
        RxTask.Base.Quick { BitmapDecoder.Manager.decodeBitmapFromPath(this, path, 400, 400) }
            .onSuccess { binding.savedImage.setImageBitmap(it) }
            .onError { it.printStackTrace() }

        binding.recyclerView.adapter =
            SharedAdapter(SharedActionFactory.instance(), this@SaveActivity)

        adController = SaveAdController(binding).apply {
            if (!SettingsPreference.hasPremium(this@SaveActivity))
                load(this@SaveActivity)
        }
    }

    override fun action(data: SharedItem) {

        //TODO FB listener
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
        data.runShareIntent(path, this)
    }

    private fun updateAd() {
        SettingsPreference.hasPremium(this)
            .also { hasPremium ->
                adController.updateView(!hasPremium)
            }
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
            smoothScrollBy(ANIM_DELTA_X, 0, null, DURATION_EXTRA_LONG.toInt())
            postDelayed(
                { smoothScrollBy(-ANIM_DELTA_X, 0, null, DURATION_EXTRA_LONG.toInt()) },
                DURATION_EXTRA_LONG + 200
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        adController.destroy()
    }

}