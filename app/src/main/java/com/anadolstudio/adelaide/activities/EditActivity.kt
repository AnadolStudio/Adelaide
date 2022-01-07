package com.anadolstudio.adelaide.activities

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View.GONE
import android.widget.Toast
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.activities.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.animation.AnimateUtil
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.helpers.GlideLoader
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import java.io.IOException

class EditActivity : BaseActivity() {

    private var backPressed = 0L

    companion object {
        val TAG: String = EditActivity::class.java.name
        private const val IMAGE_PATH = "image_path"
        var callback: Callback? = null

        fun start(context: Context, key: String?, path: String?, callback: Callback) {
            val starter = Intent(context, EditActivity::class.java)
            starter.putExtra(EDIT_TYPE, key)
            starter.putExtra(IMAGE_PATH, path)
            context.startActivity(starter)
            this.callback = callback
        }
    }

    interface Callback {
        fun callback()
    }

    private lateinit var path: String

    private lateinit var binding: ActivityEditBinding

    inner class MyRequestListener() : RequestListener<Bitmap> {
        override fun onResourceReady(
            resource: Bitmap?,
            model: Any?,
            target: Target<Bitmap>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            useCallback()
            hideLoadingDialog()
            return false
        }

        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Bitmap>?,
            isFirstResource: Boolean
        ): Boolean {
            useCallback()
            hideLoadingDialog()
            Log.d(TAG, "onLoadFailed: Can\\'t open file")
            Toast.makeText(this@EditActivity, getText(R.string.cant_open_photo), Toast.LENGTH_SHORT)
                .show()
            finish()
            return false
        }

        private fun useCallback() {
            callback?.callback()
            callback = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState != null) {
            //Создаю активити заново ибо сохранять большой bitmap - проблема
            val intent = intent
            finish()
            startActivity(intent)
        }
        binding = ActivityEditBinding.inflate(layoutInflater)
        setSupportActionBar(binding.navigationToolbar)
        setContentView(binding.root)
        init()
        initAd()
    }

    private fun init() {
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null
        binding.saveBtn.setOnClickListener {
            val realPath = getRealPath()
            SaveActivity.start(this, realPath)
        }
        /*currentMode = EditActivity.Mode.MODE_MAIN
        dialogIsShow = false
        lastDialogIsAccept = false
        getEditHelper()

        binding.navigationToolbar.setNavigationOnClickListener { v -> onBackPressed() }
        binding.navigationToolbar.title = null
        val fm = supportFragmentManager
        bottomFragment = fm.findFragmentById(R.id.toolbar_fragment)
        val key = intent.getStringExtra(EDIT_TYPE.name())
        val fragment: MainEditFragment = MainEditFragment.newInstance(key ?: PHOTO_KEY)
        addFragment(fm, fragment)
        currentMode = EditActivity.Mode.MODE_MAIN
        binding.textFlipper.setInAnimation(this, R.anim.in_bottom_to_top)
        binding.textFlipper.setOutAnimation(this, R.anim.out_bottom_to_top)
        binding.applyText.setOnClickListener { v ->
            if (bottomFragment != null && bottomFragment is StateListener
                && (bottomFragment as StateListener).isReadyToApply()
            ) {
                createDialog(true)
            }
        }
        flipperWrapper = ViewFlipperWrapper(
            binding.textFlipper, 0
        ) { v, c ->
            binding.applyText.setEnabled(c === 1)
            binding.saveText.setEnabled(c === 0)
        }
        binding.saveText.setOnClickListener { v ->
            if (PermissionHelper.hasPermission(this, STORAGE_PERMISSION)) {
                getEditHelper().saveImage()
            } else {
                PermissionHelper.requestPermission(
                    this,
                    STORAGE_PERMISSION,
                    REQUEST_STORAGE_PERMISSION
                )
            }
        }
        val photoEditorView: PhotoEditorView = binding.photoEditorView*/
        path = intent.getStringExtra(IMAGE_PATH).toString()
        Log.d(TAG, "init path: $path")
        showLoadingDialog()
        GlideLoader.loadImageWithoutCache(binding.testImage, path, MyRequestListener())
        /*editHelper.initPhotoEditor(photoEditorView)
        multiTouchListener = MyMultiTouchListener(binding.frameContentImageView, true)
        binding.frameContentImageView.setOnTouchListener(multiTouchListener)
        if (dialogIsShow) {
            createDialog(lastDialogIsAccept)
        }*/
    }

    @Deprecated("getRealPath")
    private fun getRealPath(): String {
        val contentUri = Uri.parse(path)
        var cursor: Cursor? = null
        var realPath = ""
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            cursor = contentResolver.query(contentUri, proj, null, null, null)
            cursor?.let {
                val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor.moveToFirst()
                realPath = cursor.getString(columnIndex).toString()
            }

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return realPath
    }

    override fun onBackPressed() {
        if (backPressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed()
        } else {
            Toast.makeText(
                this,
                getString(R.string.double_click_for_exit),
                Toast.LENGTH_SHORT
            ).show()
            backPressed = System.currentTimeMillis()
        }
    }

    private fun initAd() {
        MobileAds.initialize(this) {}
        val adRequest = AdRequest.Builder()
            .build()
        binding.adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                AnimateUtil.showAnimY(binding.adView, -binding.adView.height.toFloat(), 0F)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                binding.adView.visibility = GONE
            }
        }
        binding.adView.loadAd(adRequest)

    }
}