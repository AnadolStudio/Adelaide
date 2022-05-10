package com.anadolstudio.adelaide.view.screens.edit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.view.adcontrollers.EditAdController
import com.anadolstudio.adelaide.view.screens.BaseEditActivity
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.brush.BrushEditFragment
import com.anadolstudio.adelaide.view.screens.edit.crop.CropEditFragment
import com.anadolstudio.adelaide.view.screens.edit.cut.CutEditFragment
import com.anadolstudio.adelaide.view.screens.edit.effect.EffectEditFragment
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListFragment
import com.anadolstudio.adelaide.view.screens.edit.stiker.StickerEditFragment
import com.anadolstudio.adelaide.view.screens.main.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.view.screens.main.TypeKey
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.util.DoubleClickExit
import com.anadolstudio.core.util.PermissionUtil
import com.anadolstudio.core.util.PermissionUtil.Abstract.Companion.DEFAULT_REQUEST_CODE
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.processor.Mode
import com.anadolstudio.photoeditorprocessor.util.FileUtil

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

    private lateinit var path: String
    private lateinit var binding: ActivityEditBinding
    private lateinit var viewController: EditViewController

    private val viewModel: EditActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            //Создаю активити заново ибо сохранять большой bitmap - проблема
            val intent = intent
            finish()
            startActivity(intent)
        }

        binding = ActivityEditBinding.inflate(layoutInflater)
        viewController = EditViewController(this, binding)
        viewModel.setEditViewController(viewController)
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
            bottomFragment?.also {
                if (it.isReadyToApply) { //TODO правильно, ли такое обращение?
                    currentMode = Mode.MAIN
                    it.apply()
                    super.onBackPressed()
                }
            }
        }

        val key = intent.getStringExtra(EDIT_TYPE) ?: TypeKey.PHOTO_KEY
        setEditFragment(Mode.MAIN, FunctionListFragment.newInstance(key, FunctionItemClick()))

        viewModel.currentBitmapCommunication.observe(this) { result ->
            when (result) {
                is Result.Success -> {
                    viewController.setMainBitmap(this, result.data)

                    if (currentMode == Mode.CUT) {
                        viewController.setupMainImage(this, result.data)
                    } else {
                        viewController.resetWorkSpace()
                    }
                }
                is Result.Error -> result.error.printStackTrace()
                is Result.Loading -> showLoadingDialog()
                else -> {}
            }
            if (result !is Result.Loading) hideLoadingDialog()
        }

        path = intent.getStringExtra(IMAGE_PATH).toString()

        viewModel.initEditProcessor(this, path)
            .onError { ex ->
                ex.printStackTrace()
                showToast(R.string.edit_error_cant_open_photo)
                finish()
            }

    }

    private fun setEditFragment(mode: Mode, fragment: BaseEditFragment) {
        this.currentMode = mode
        bottomFragment = fragment
        replaceFragment(fragment, R.id.toolbar_fragment, mode != Mode.MAIN)
    }

    override fun onBackPressed() {
        if (bottomFragment != null && bottomFragment!!.backClick()) {

            if (currentMode == Mode.MAIN) { // Начальное состояние

                doubleClickExit.click { isTrue ->
                    if (isTrue) super.onBackPressed()
                    else showToast(R.string.edit_func_double_click_for_exit)
                }

            } else {
                viewController.resetWorkSpace()
                super.onBackPressed()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            DEFAULT_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
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

        val file = FileUtil.createAppDir(getString(R.string.app_name))

        viewModel.saveAsFile(this, file, loadingView!!)
            .onSuccess { imagePath -> SaveActivity.start(this@EditActivity, imagePath) }
            .onError { showToast(R.string.edit_error_failed_save_image) }
            .onFinal { hideLoadingDialog() }
    }

    fun getProgressListener(): ProgressListener<String>? = loadingView

    inner class FunctionItemClick : IDetailable<FuncItem> {

        override fun toDetail(data: FuncItem) {
            viewController.showWorkspace(true, needMoreSpace = false)
            when (data) {//TODO упростить через Pair<Mode, Fragment>
                FuncItem.MainFunctions.TRANSFORM -> setEditFragment(
                    Mode.TRANSFORM,
                    CropEditFragment.newInstance()
                )
                FuncItem.MainFunctions.EFFECT -> {
                    setEditFragment(
                        Mode.EFFECT,
                        EffectEditFragment.newInstance()
                    )
                    viewController.setupSupportImage(currentMode)
                }
                FuncItem.MainFunctions.CUT -> {
                    setEditFragment(
                        Mode.CUT,
                        CutEditFragment.newInstance()
                    )
                }
                FuncItem.MainFunctions.STICKER -> {
                    setEditFragment(
                        Mode.STICKER,
                        StickerEditFragment.newInstance()
                    )
                }
                FuncItem.MainFunctions.BRUSH -> {
                    setEditFragment(
                        Mode.BRUSH,
                        BrushEditFragment.newInstance()
                    )
                }
                else -> {}
            }
        }
    }

}