package com.anadolstudio.adelaide.view.screens.edit

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
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
import com.anadolstudio.adelaide.domain.editphotoprocessor.FunctionItem
import com.anadolstudio.adelaide.domain.editphotoprocessor.TransformFunction
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.FileUtil
import com.anadolstudio.adelaide.domain.utils.BitmapUtil
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.REQUEST_STORAGE_PERMISSION
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.STORAGE_PERMISSION
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.hasPermission
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.requestPermission
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.showSettingsSnackbar
import com.anadolstudio.adelaide.view.adcontrollers.EditAdController
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListFragment
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.view.screens.main.TypeKey
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.util.DoubleClickExit
import com.anadolstudio.core.view.BaseActivity
import com.theartofdev.edmodo.cropper.CropImageView

class EditActivity : BaseActivity() {
    var currentFunction: FunctionItem? = null
    private var bottomFragment: BaseEditFragment? = null
    protected val doubleClickExit = DoubleClickExit.Base()

    companion object {
        val TAG: String = EditActivity::class.java.name
        const val FUNCTION = "function"
        private const val IMAGE_PATH = "image_path"

        fun start(context: Context, key: String?, path: String?) {
            val starter = Intent(context, EditActivity::class.java)
            starter.putExtra(EDIT_TYPE, key)
            starter.putExtra(IMAGE_PATH, path)
            context.startActivity(starter)
        }
    }

    inner class EditListenerCallback : EditListener<Bitmap> {
        // TODO стремное решение
        override fun onSuccess(data: Bitmap) {
            hideLoadingDialog()
            BitmapUtil.getInfoOfBitmap(data)
            binding.mainImage.setImageBitmap(data)
        }

        override fun onFailure(ex: Throwable) {
            hideLoadingDialog()

            Toast.makeText(
                this@EditActivity, getText(R.string.edit_error_cant_open_photo), Toast.LENGTH_SHORT
            ).show()

            finish()
        }
    }

    private lateinit var path: String
    lateinit var editProcessor: EditProcessorIml
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

        bottomFragment =
            supportFragmentManager.findFragmentById(R.id.toolbar_fragment) as BaseEditFragment?

        val key = intent.getStringExtra(EDIT_TYPE) ?: TypeKey.PHOTO_KEY
        addFragment(FunctionListFragment.newInstance(key))

        path = intent.getStringExtra(IMAGE_PATH).toString()

        showLoadingDialog()
        editProcessor = EditProcessorIml(this, path, EditListenerCallback())

        binding.cropImage.setMinCropResultSize(250, 250)

        /*editHelper.initPhotoEditor(photoEditorView)
        multiTouchListener = MyMultiTouchListener(binding.frameContentImageView, true)
        binding.frameContentImageView.setOnTouchListener(multiTouchListener)
        if (dialogIsShow) {
            createDialog(lastDialogIsAccept)
        }*/
    }

    override fun onBackPressed() {

        // TODO Переписать логику навигации
        bottomFragment?.let {
            if (!it.onBackClick()) {

                if (currentFunction == null) { // Начальное состояние
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
        //TODO не хватает плавности
        if (!show) currentFunction = null

        binding.adView.visibility = if (needMoreSpace) GONE else VISIBLE
        if (needMoreSpace) binding.adView.clearAnimation()

        binding.saveBtn.visibility = if (show) GONE else VISIBLE
        binding.applyBtn.visibility = if (show) VISIBLE else GONE
    }

    @Deprecated("В класс Base")
    fun replaceFragment(fragment: Fragment) {
        replaceFragment(fragment, true)
    }

    @Deprecated("В класс Base")
    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean) {
        if (bottomFragment == fragment) return
        bottomFragment = fragment as BaseEditFragment

        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.toolbar_fragment, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE)
        if (addToBackStack) transaction.addToBackStack(fragment.javaClass.name)
        transaction.commit()
    }

    @Deprecated("В класс Base")
    fun addFragment(fragment: Fragment) {
        addFragment(supportFragmentManager, fragment)
    }

    @Deprecated("В класс Base")
    private fun addFragment(fm: FragmentManager, fragment: Fragment) {
        if (bottomFragment == fragment) return

        bottomFragment = fragment as BaseEditFragment
        fm.beginTransaction()
            .add(R.id.toolbar_fragment, fragment)
            .commit()
    }

    //TODO ООП Решение
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
                    if (shouldShow) showSettingsSnackbar(this, binding.root)
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

        editProcessor.saveAsFile(
            this, FileUtil.createAppDir(this), object : EditListener<String> {

                override fun onSuccess(data: String) {
                    SaveActivity.start(this@EditActivity, data)
                }

                override fun onFailure(ex: Throwable) {
                    showToast(R.string.edit_error_failed_save_image)
                }
            })
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