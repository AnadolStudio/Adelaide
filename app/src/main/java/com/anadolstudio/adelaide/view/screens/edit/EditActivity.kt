package com.anadolstudio.adelaide.view.screens.edit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.EditListener
import com.anadolstudio.adelaide.domain.editphotoprocessor.EditProcessorIml
import com.anadolstudio.adelaide.domain.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.domain.utils.BitmapHelper
import com.anadolstudio.adelaide.domain.utils.FunctionItem
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.REQUEST_STORAGE_PERMISSION
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.STORAGE_PERMISSION
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.hasPermission
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.requestPermission
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.showSettingsSnackbar
import com.anadolstudio.adelaide.domain.utils.TimeHelper
import com.anadolstudio.adelaide.view.animation.AnimateUtil
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListFragment
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.view.screens.main.TypeKey
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.view.BaseActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.theartofdev.edmodo.cropper.CropImageView
import java.io.File
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditActivity : BaseActivity() {
    var currentFunction: FunctionItem? = null
    private var bottomFragment: BaseEditFragment? = null
    private var backPressed = 0L

    companion object {
        val TAG: String = EditActivity::class.java.name
        const val FUNCTION = "function"
        private const val IMAGE_PATH = "image_path"
        var callback: Callback? = null

        fun start(context: Context, key: String?, path: String?, callback: Callback) {
            val starter = Intent(context, EditActivity::class.java)
            starter.putExtra(EDIT_TYPE, key)
            starter.putExtra(IMAGE_PATH, path)
            context.startActivity(starter)
            Companion.callback = callback
        }
    }

    interface Callback {
        fun callback()
    }

    private lateinit var path: String
    lateinit var editProcessor: EditProcessorIml
    private lateinit var binding: ActivityEditBinding

    private fun useCallback() {
        callback?.let {
            it.callback()
            callback = null
        }
    }

    private fun failureLoadBitmap() {
        useCallback()
        hideLoadingDialog()
        Log.d(TAG, "onLoadFailed: Can\\'t open file")
        Toast.makeText(this, getText(R.string.cant_open_photo), Toast.LENGTH_SHORT).show()
        finish()
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
            if (hasPermission(this, STORAGE_PERMISSION)) {
                saveImage()
            } else {
                requestPermission(this, STORAGE_PERMISSION, REQUEST_STORAGE_PERMISSION)
            }
        }

        binding.applyBtn.setOnClickListener {
            bottomFragment?.let {
                if (!it.apply()) { //TODO
                    showWorkspace(false)
                    super.onBackPressed()
                }
            }
        }

        val key = intent.getStringExtra(EDIT_TYPE) ?: TypeKey.PHOTO_KEY
        bottomFragment =
            supportFragmentManager.findFragmentById(R.id.toolbar_fragment) as BaseEditFragment?

        addFragment(FunctionListFragment.newInstance(key))
        path = intent.getStringExtra(IMAGE_PATH).toString()
        Log.d(TAG, "init path: $path")

        editProcessor = EditProcessorIml(this, path, object : EditListener<Bitmap> {
            override fun onSuccess(t: Bitmap) {
                useCallback()
                BitmapHelper.getInfoOfBitmap(t)
                binding.mainImage.setImageBitmap(t)
            }

            override fun onFailure(ex: Throwable) {
                failureLoadBitmap()
            }
        })

        binding.cropImage.setMinCropResultSize(250, 250)


        /*editHelper.initPhotoEditor(photoEditorView)
        multiTouchListener = MyMultiTouchListener(binding.frameContentImageView, true)
        binding.frameContentImageView.setOnTouchListener(multiTouchListener)
        if (dialogIsShow) {
            createDialog(lastDialogIsAccept)
        }*/
    }

    override fun onBackPressed() {
        bottomFragment?.let {
            if (!it.onBackClick()) {
                //TODO Сделать утилиту
                if (currentFunction == null) { // Начальное состояние
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
                } else {
                    showWorkspace(false)
                    super.onBackPressed()
                }
            }
        }
    }

    fun cropView(): CropImageView = binding.cropImage

    fun showCropImage(show: Boolean) {
        binding.cropImage.visibility = if (show) VISIBLE else GONE
        showMainImage(show)

        if (!show) {
            binding.cropImage.clearImage()
            binding.cropImage.resetCropRect()
            binding.cropImage.setOnCropWindowChangedListener(null)
            binding.cropImage.setOnSetCropOverlayMovedListener(null)
        }

        binding.cropImage.isShowCropOverlay = false
    }

    private fun showMainImage(show: Boolean) {
        binding.mainImage.visibility = if (show) GONE else VISIBLE
    }

    fun showWorkspace(show: Boolean, needMoreSpace: Boolean = false) {
        if (!show) currentFunction = null

        binding.adView.visibility = if (needMoreSpace) GONE else VISIBLE
        if (needMoreSpace) binding.adView.clearAnimation()

        binding.saveBtn.visibility = if (show) GONE else VISIBLE
        binding.applyBtn.visibility = if (show) VISIBLE else GONE
    }

    fun replaceFragment(fragment: Fragment) {
        replaceFragment(fragment, true)
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        if (bottomFragment == fragment) return
        bottomFragment = fragment as BaseEditFragment

        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.toolbar_fragment, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        if (addToBackStack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }

    fun addFragment(fragment: Fragment) {
        addFragment(supportFragmentManager, fragment)
    }

    private fun addFragment(fm: FragmentManager, fragment: Fragment) {
        if (bottomFragment == fragment) return

        bottomFragment = fragment as BaseEditFragment
        fm.beginTransaction()
            .add(R.id.toolbar_fragment, fragment)
            .commit()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> {
                Log.d(TAG, "onRequestPermissionsResult: " + grantResults.contentToString())
                if (grantResults.isNotEmpty()
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    saveImage()
                } else {
                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        STORAGE_PERMISSION[1]
                    )
                    showSettingsSnackbar(this, binding.root)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveImage() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        editProcessor.saveAsFile(this, createAppDir(), object : EditListener<String> {
            override fun onSuccess(path: String) {
                SaveActivity.start(this@EditActivity, path)
            }

            override fun onFailure(ex: Throwable) {
                //TODO обработка исключения
                Log.d(TAG, "onFailure: ${ex.message}")
            }
        })
    }

    private fun getFileName(): String {
        val currentDate = Date()
        val timeFormat: DateFormat =
            SimpleDateFormat(TimeHelper.DEFAULT_FORMAT, Locale.getDefault())

        return "IMG_${timeFormat.format(currentDate)}.jpeg" // TODO JPEG?
    }

    private fun createAppDir(): File {
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator + getString(
                R.string.app_name
            )
        )

        if (!directory.exists() && !directory.isDirectory) {
            Log.d(TAG, "Not exist")

            // create empty directory
            if (!directory.mkdirs()) Log.d(TAG, "Unable to create app dir!")
        }

        return File(directory, getFileName())
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

    fun setupCropImage(function: TransformFunction) {
        binding.cropImage.setAspectRatio(
            if (function.fixAspectRatio) function.ratioItem.ratio.x else 1,
            if (function.fixAspectRatio) function.ratioItem.ratio.y else 1
        )

        binding.cropImage.setFixedAspectRatio(false)
        setupWindowCropImage(function)
//        binding.cropImage.isFlippedVertically = function.flipVertical
//        binding.cropImage.isFlippedHorizontally = function.flipHorizontal
//        binding.cropImage.rotatedDegrees = function.degrees
    }

    fun setupWindowCropImage(function: TransformFunction) {
        binding.cropImage.cropRect = function.cropRect ?: binding.cropImage.wholeImageRect
    }
}