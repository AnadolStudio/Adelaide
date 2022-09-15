package com.anadolstudio.adelaide.view.screens.edit

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.common_util.doubleClickAction
import com.anadolstudio.core.tasks.ProgressListener
import com.anadolstudio.core.util.PermissionUtil
import com.anadolstudio.core.util.PermissionUtil.Abstract.Companion.DEFAULT_REQUEST_CODE
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.processor.EditMode
import com.anadolstudio.photoeditorprocessor.util.FileUtil

class EditActivity : BaseEditActivity() {

    companion object {
        val TAG: String = EditActivity::class.java.name
        private const val IMAGE_PATH = "image_path"
        const val EDIT_TYPE = "edit_type"

        fun start(context: Context, key: String?, path: String?) {
            val starter = Intent(context, EditActivity::class.java)
            starter.putExtra(EDIT_TYPE, key)
            starter.putExtra(IMAGE_PATH, path)
            context.startActivity(starter)
        }
    }

    private var bottomFragment: BaseEditFragment? = null
    private lateinit var currentEditMode: EditMode

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
        viewModel.currentBitmapCommunication.observe(this, ::showChangeBitmapProgress)
        viewModel.saveBitmapPath.observe(this, ::showSaveBitmapProgress)
        EditAdController(binding).load(this)
        setupView()
    }

    private fun setupView() {
        setSupportActionBar(binding.navigationToolbar)
        setContentView(binding.root)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null

        binding.saveBtn.setOnClickListener { saveImage() }

        binding.applyBtn.setOnClickListener {
            bottomFragment?.also {
                if (it.isReadyToApply) { //TODO правильно, ли такое обращение?
                    currentEditMode = EditMode.MAIN
                    it.apply()
                    super.onBackPressed()
                }
            }
        }

        intent.getStringExtra(EDIT_TYPE)
                ?.let { key ->
                    setEditFragment(EditMode.MAIN, FunctionListFragment.newInstance(key, FunctionItemClick()))
                }
                ?: finish()

        path = intent.getStringExtra(IMAGE_PATH).toString()

        viewModel.initEditProcessor(this, path)
                .onError { ex ->
                    ex.printStackTrace()
                    showToast(R.string.edit_error_cant_open_photo)
                    finish()
                }

    }

    private fun showSaveBitmapProgress(result: Result<String>) {
        when (result) {
            is Result.Success -> SaveActivity.start(this@EditActivity, result.data)
            is Result.Error -> showToast(R.string.edit_error_failed_save_image)
            else -> {}
        }
    }

    private fun showChangeBitmapProgress(result: Result<Bitmap>?) {
        when (result) {
            is Result.Success -> {
                viewController.setMainBitmap(this, result.data)

                if (currentEditMode == EditMode.CUT) {
                    viewController.setupMainImage(this, result.data)
                } else {
                    viewController.resetWorkSpace()
                }
            }
            is Result.Error -> result.error.printStackTrace()
            else -> {}
        }

        showLoadingDialog(result is Result.Loading)
    }

    private fun setEditFragment(editMode: EditMode, fragment: BaseEditFragment) {
        this.currentEditMode = editMode
        bottomFragment = fragment
        replaceFragment(fragment, R.id.toolbar_fragment, editMode != EditMode.MAIN)
    }

    override fun onBackPressed() {
        if (bottomFragment != null && bottomFragment!!.backClick()) {

            if (currentEditMode == EditMode.MAIN) { // Начальное состояние

                doubleClickAction(
                        onSimpleClick = { showToast(R.string.edit_func_double_click_for_exit) },
                        onDoubleClick = { super.onBackPressed() }
                )

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
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    saveImage()
                } else if (PermissionUtil.WriteExternalStorage.shouldShowRequestPermissionRationale(this)) {
                    showSettingsSnackbar()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun saveImage() {
        if (!PermissionUtil.WriteExternalStorage.checkPermission(this)) {
            PermissionUtil.WriteExternalStorage.requestPermission(this, DEFAULT_REQUEST_CODE)
            return
        }

        viewModel.saveAsFile(
                this,
                FileUtil.createAppDir(getString(R.string.app_name)),
                loadingView
        )
    }

    fun getProgressListener(): ProgressListener<String>? = loadingView

    inner class FunctionItemClick : ActionClick<FuncItem> {

        override fun action(data: FuncItem) {
            viewController.showWorkspace(true, needMoreSpace = false)

            val (mode, fragment) = when (data) {//TODO упростить через Pair<Mode, Fragment>
                FuncItem.MainFunctions.TRANSFORM -> EditMode.TRANSFORM to CropEditFragment.newInstance()
                FuncItem.MainFunctions.EFFECT -> {
                    viewController.setupSupportImage(currentEditMode)
                    EditMode.EFFECT to EffectEditFragment.newInstance()
                }
                FuncItem.MainFunctions.CUT -> EditMode.CUT to CutEditFragment.newInstance()
                FuncItem.MainFunctions.STICKER -> EditMode.STICKER to StickerEditFragment.newInstance()
                else /*FuncItem.MainFunctions.BRUSH*/ -> EditMode.BRUSH to BrushEditFragment.newInstance()
            }

            setEditFragment(mode, fragment)
        }
    }
}
