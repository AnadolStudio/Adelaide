package com.anadolstudio.adelaide.view.screens.edit.main_edit_screen

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
import com.anadolstudio.adelaide.view.screens.main.EditType
import com.anadolstudio.adelaide.view.screens.save.SaveActivity
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.common_util.PermissionUtil
import com.anadolstudio.core.common_util.PermissionUtil.Abstract.Companion.DEFAULT_REQUEST_CODE
import com.anadolstudio.core.common_util.doubleClickAction
import com.anadolstudio.core.livedata.SingleEvent
import com.anadolstudio.photoeditorprocessor.functions.FuncItem
import com.anadolstudio.photoeditorprocessor.processor.EditMode
import com.anadolstudio.photoeditorprocessor.util.FileUtil

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
        viewModelSubscribes()
        EditAdController(binding).load(this)
        setupView()
    }

    private fun viewModelSubscribes() {
        viewModel.currentBitmap.observe(this, this::render)
        viewModel.event.observe(this, this::handleEvent)
    }

    override fun handleEvent(event: SingleEvent) {
        when (event) {
            is EditActivityEvent.CantOpenPhotoEvent -> closeEditActivity()
            is EditActivityEvent.LoadingEvent -> showLoading(event.isLoading)
            is EditActivityEvent.SuccessSaveEvent -> SaveActivity.start(this@EditActivity, event.path)
            else -> super.handleEvent(event)
        }
    }

    private fun showLoading(isLoading: Boolean) {
    }

    private fun closeEditActivity() {
        showToast(R.string.edit_error_cant_open_photo)
        finish()
    }

    private fun setupView() {
        setSupportActionBar(binding.navigationToolbar)
        setContentView(binding.root)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null

        binding.saveBtn.setOnClickListener { saveImage() }

        binding.applyBtn.setOnClickListener {
            bottomFragment?.also {
                if (it.isReadyToApply()) { //TODO правильно, ли такое обращение?
                    currentEditMode = EditMode.MAIN
                    it.apply()
                    super.onBackPressed()
                }
            }
        }

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
            is EditActivityViewState.Content -> {
                viewController.setMainBitmap(this, state.bitmap)

                if (currentEditMode == EditMode.CUT) {
                    viewController.setupMainImage(this, state.bitmap)
                } else {
                    viewController.resetWorkSpace()
                }
            }
            is EditActivityViewState.Error -> state.error.printStackTrace()
        }
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
                FileUtil.createAppDir(getString(R.string.app_name))
        )
    }

    inner class FunctionItemClick : ActionClick<FuncItem> {

        override fun action(data: FuncItem) {
            viewController.showWorkspace(true, needMoreSpace = false)

            val (mode, fragment) = when (data) {
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
