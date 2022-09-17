package com.anadolstudio.adelaide.view.screens.edit.main_edit_screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityEditBinding
import com.anadolstudio.adelaide.view.adcontrollers.EditAdController
import com.anadolstudio.adelaide.view.screens.BaseEditActivity
import com.anadolstudio.adelaide.view.screens.BaseEditFragment
import com.anadolstudio.adelaide.view.screens.edit.main.FunctionListFragment
import com.anadolstudio.adelaide.view.screens.main.EditType
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.common_util.PermissionUtil
import com.anadolstudio.core.common_util.PermissionUtil.Abstract.Companion.DEFAULT_REQUEST_CODE
import com.anadolstudio.core.common_util.doubleClickAction
import com.anadolstudio.core.common_util.throttleClick
import com.anadolstudio.core.livedata.SingleEvent
import com.anadolstudio.photoeditorcore.domain.edit_processor.EditMode
import com.anadolstudio.photoeditorcore.domain.edit_processor.PhotoEditException
import com.anadolstudio.photoeditorcore.domain.edit_processor.implementation.EditProcessorEvent
import com.anadolstudio.photoeditorcore.domain.functions.FuncItem
import com.anadolstudio.photoeditorcore.domain.util.FileUtil
import com.anadolstudio.photoeditorcore.view.PhotoEditorView

class EditActivity : BaseEditActivity() {

    companion object {
        val TAG: String = EditActivity::class.java.name
        private const val IMAGE_PATH = "image_path"
        const val EDIT_TYPE = "edit_type"

        fun start(context: Context, key: EditType, path: String?) {
            val starter = Intent(context, EditActivity::class.java)
            starter.putExtra(EDIT_TYPE, key)
            starter.putExtra(IMAGE_PATH, path)
            context.startActivity(starter)
        }
    }

    private lateinit var path: String
    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivityEditBinding.inflate(layoutInflater) }

    private val viewModel: EditActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState != null) {
            //Создаю активити заново ибо сохранять большой bitmap - проблема
            finish()
            startActivity(intent)
        }

        viewModelSubscribes()
        EditAdController(binding).load(this)
        setupView()
    }

    private fun viewModelSubscribes() {
        viewModel.currentBitmap.observe(this, this::render)
//        viewModel.event.observe(this, this::handleEvent)
        viewModel.editProcessorEvent.observe(this, this::handleEditProcessorEvent)
    }

    private fun handleEditProcessorEvent(event: EditProcessorEvent) {
        when (event) {
            is EditProcessorEvent.Error -> handleEditProcessorError(event.error)
            is EditProcessorEvent.Loading -> showLoading(event.isLoading)
            is EditProcessorEvent.Success -> handleEditProcessorSuccess(event)
        }
    }

    private fun handleEditProcessorSuccess(event: EditProcessorEvent.Success) {
        when (event) {
            is EditProcessorEvent.Success.ImageLoaded -> {}
            is EditProcessorEvent.Success.ImageSaved -> SaveActivity.start(this@EditActivity, event.path)
        }
    }

    private fun handleEditProcessorError(error: Throwable) {
        when (error) {
            is PhotoEditException.InvalidateBitmapException -> closeEditActivity()
            is PhotoEditException.FailedSaveException -> {}
            else -> {}
        }
    }

    override fun handleEvent(event: SingleEvent) {
        when (event) {
            is EditActivityEvent.LoadingEvent -> showLoading(event.isLoading)
            else -> super.handleEvent(event)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.applyBtn.isEnabled = !isLoading
        binding.saveBtn.isEnabled = !isLoading
    }

    private fun closeEditActivity() {
        showToast(R.string.edit_error_cant_open_photo)
        finish()
    }

    private fun setupView() {
        viewModel.bindWithPhotoEditorView(PhotoEditorView(binding.mainImage))

        setSupportActionBar(binding.navigationToolbar)
        setContentView(binding.root)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null

        binding.saveBtn.throttleClick { saveImage() }
        binding.applyBtn.throttleClick { }

        (intent.getSerializableExtra(EDIT_TYPE) as? EditType)
                ?.let { key ->
                    setEditFragment(EditMode.MAIN, FunctionListFragment.newInstance(key, FunctionItemClick()))
                }
                ?: finish()

        path = intent.getStringExtra(IMAGE_PATH).toString()

        viewModel.initEditProcessor(this, path)

    }

    private fun render(state: EditActivityViewState) {
        when (state) {
            is EditActivityViewState.Content -> {}
            is EditActivityViewState.Error -> state.error.printStackTrace()
        }
    }

    private fun setEditFragment(editMode: EditMode, fragment: BaseEditFragment) {
        replaceFragment(fragment, R.id.toolbar_fragment, editMode != EditMode.MAIN)
    }

    override fun onBackPressed() {
        doubleClickAction(
                onSimpleClick = { showToast(R.string.edit_func_double_click_for_exit) },
                onDoubleClick = { super.onBackPressed() }
        )
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
                FileUtil.createAppDir(getString(R.string.app_name))
        )
    }

    inner class FunctionItemClick : ActionClick<FuncItem> {

        override fun action(data: FuncItem) {
            /*viewController.showWorkspace(true, needMoreSpace = false)

            val (mode, fragment) = when (data) {
                FuncItem.MainFunctions.TRANSFORM -> EditMode.TRANSFORM to CropEditFragment.newInstance()
                FuncItem.MainFunctions.EFFECT -> {
                    viewController.setupSupportImage(currentEditMode)
                    EditMode.EFFECT to EffectEditFragment.newInstance()
                }
                FuncItem.MainFunctions.CUT -> EditMode.CUT to CutEditFragment.newInstance()
                FuncItem.MainFunctions.STICKER -> EditMode.STICKER to StickerEditFragment.newInstance()
                else *//*FuncItem.MainFunctions.BRUSH*//* -> EditMode.BRUSH to BrushEditFragment.newInstance()
            }

            setEditFragment(mode, fragment)*/
        }
    }
}
