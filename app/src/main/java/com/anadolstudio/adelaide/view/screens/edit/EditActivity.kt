package com.anadolstudio.adelaide.view.screens.edit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.domain.editphotoprocessor.EditProcessorIml
import com.anadolstudio.adelaide.domain.editphotoprocessor.Mode
import com.anadolstudio.adelaide.domain.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.FileUtil
import com.anadolstudio.adelaide.view.adcontrollers.EditAdController
import com.anadolstudio.adelaide.view.animation.AnimateUtil
import com.anadolstudio.adelaide.view.screens.BaseEditActivity
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.crop.CropEditFragment
import com.anadolstudio.adelaide.view.screens.edit.enumeration.FuncItem
import com.anadolstudio.adelaide.view.screens.edit.enumeration.MainFunctions
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListFragment
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.view.screens.main.TypeKey
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.util.DoubleClickExit
import com.anadolstudio.core.util.PermissionUtil
import com.anadolstudio.core.util.PermissionUtil.Abstract.Companion.DEFAULT_REQUEST_CODE
import com.theartofdev.edmodo.cropper.CropImageView

class EditActivity : BaseEditActivity() {

    companion object {
        val TAG: String = EditActivity::class.java.name
        private const val IMAGE_PATH = "image_path"

        fun start(context: Context, key: String?, path: String?) {
            val starter = Intent(context, EditActivity::class.java)
            starter.putExtra(EDIT_TYPE, key)
            starter.putExtra(IMAGE_PATH, path)
            context.startActivity(starter)
        }
    }

    protected val doubleClickExit = DoubleClickExit.Base()
    private var bottomFragment: BaseEditFragment? = null
    private lateinit var currentMode: Mode
        private set

    lateinit var editProcessor: EditProcessorIml
    private lateinit var path: String
    private lateinit var binding: ActivityEditBinding

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
        EditAdController(binding).load(this)
    }

    private fun init() {
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null

        binding.saveBtn.setOnClickListener {
            if (PermissionUtil.WriteExternalStorage.checkPermission(this)) {
                saveImage()
            } else {
                PermissionUtil.WriteExternalStorage.requestPermission(this, DEFAULT_REQUEST_CODE)
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

        bottomFragment =
            supportFragmentManager.findFragmentById(R.id.toolbar_fragment) as BaseEditFragment?

        val key = intent.getStringExtra(EDIT_TYPE) ?: TypeKey.PHOTO_KEY
        setEditFragment(Mode.MAIN, FunctionListFragment.newInstance(key, FunctionItemClick()))

        path = intent.getStringExtra(IMAGE_PATH).toString()

        showLoadingDialog()
        editProcessor = EditProcessorIml(this)

        editProcessor.init(path)
            .onSuccess { bitmap -> showBitmap(bitmap) }
            .onError {
                showToast(R.string.edit_error_cant_open_photo)
                finish()
            }
            .onFinal { hideLoadingDialog() }

        binding.cropImage.setMinCropResultSize(250, 250)

        /*editHelper.initPhotoEditor(photoEditorView)
        multiTouchListener = MyMultiTouchListener(binding.frameContentImageView, true)
        binding.frameContentImageView.setOnTouchListener(multiTouchListener)
        if (dialogIsShow) {
            createDialog(lastDialogIsAccept)
        }*/
    }

    inner class FunctionItemClick : IDetailable<FuncItem> {

        override fun toDetail(data: FuncItem) {

            when (data) {
                MainFunctions.TRANSFORM -> {
//                    val function = editProcessor.getFunction(FunctionItem.TRANSFORM.name)
                    setEditFragment(Mode.TRANSFORM, CropEditFragment.newInstance())
                }

                else -> {}
                /*CUT -> setEditFragment(MODE_CUT, CutEditFragment.newInstance())
                FILTER -> setEditFragment(MODE_FILTER, FilterEditFragment.newInstance())
                EFFECT -> setEditFragment(MODE_EFFECT, EffectEditFragment.newInstance(callback))
                SPLASH -> setEditFragment(
                    MODE_SPLASH, SplashEditFragment.newInstance(callback, MONOCHROME_BACK)
                )
                BLUR -> setEditFragment(
                    MODE_BLUR, SplashEditFragment.newInstance(callback, BLUR_BACK)
                )
                BODY -> setEditFragment(MODE_BODY, BodyEditFragment.newInstance(callback))
                TEXT -> setEditFragment(MODE_TEXT, TextEditFragment.newInstance())
                STICKER -> setEditFragment(MODE_STICKER, StickerEditFragment.newInstance())
                UPGRADE -> setEditFragment(
                    MODE_UPGRADE,
                    AdjustmentEditFragment.newInstance(callback)
                )
                BRUSH -> setEditFragment(MODE_BRUSH, BrushEditFragment.newInstance())
                CROP -> setEditFragment(MODE_CROP, CropEditFragment.newInstance(callback))
                TURN -> setEditFragment(MODE_TURN, TurnEditFragment.newInstance())*/
            }


        }
    }

    private fun setEditFragment(mode: Mode, fragment: BaseEditFragment) {
        this.currentMode = mode
        bottomFragment = fragment
        replaceFragment(fragment, R.id.toolbar_fragment, mode != Mode.MAIN)
    }

    private fun showBitmap(bitmap: Bitmap) {
        binding.mainImage.setImageBitmap(bitmap)
    }

    override fun onBackPressed() {
        if (bottomFragment != null && !bottomFragment!!.onBackClick()) {

            if (currentMode == Mode.MAIN) { // Начальное состояние

                doubleClickExit.click { isTrue ->
                    if (isTrue) super.onBackPressed()
                    else showToast(R.string.edit_func_double_click_for_exit)
                }
            } else {
                showWorkspace(false)
                super.onBackPressed()
            }
        }
    }

    @Deprecated("Его место не тут")
    fun cropView(): CropImageView = binding.cropImage

    @Deprecated("Его место не тут")
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
        //TODO не хватает плавности для mainContainer
        val visible = if (needMoreSpace) GONE else VISIBLE

        if (binding.adView.visibility != visible && show) {
            val height = binding.adView.height.toFloat()

            AnimateUtil.showAnimY(
                binding.adView,
                if (needMoreSpace) 0F else -height,
                if (needMoreSpace) -height else 0F,
                visible
            )
        }

        binding.saveBtn.visibility = if (show) GONE else VISIBLE
        binding.applyBtn.visibility = if (show) VISIBLE else GONE
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            DEFAULT_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                } else {
                    val shouldShow = PermissionUtil.WriteExternalStorage
                        .shouldShowRequestPermissionRationale(this)

                    if (shouldShow) showSettingsSnackbar(binding.root)
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveImage() {
        if (!PermissionUtil.WriteExternalStorage.checkPermission(this))
            return

        showLoadingDialog()
        editProcessor.saveAsFile(
            this, FileUtil.createAppDir(this), loadingView!!
        )
            .onSuccess { imagePath -> SaveActivity.start(this@EditActivity, imagePath) }
            .onError { showToast(R.string.edit_error_failed_save_image) }
            .onFinal { hideLoadingDialog() }
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

    @Deprecated("Его место не тут")
    fun setupWindowCropImage(function: TransformFunction) {
        binding.cropImage.cropRect = function.cropRect ?: binding.cropImage.wholeImageRect
    }
}