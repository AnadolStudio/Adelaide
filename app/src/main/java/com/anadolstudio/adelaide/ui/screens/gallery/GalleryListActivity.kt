package com.anadolstudio.adelaide.ui.screens.gallery

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityGalleryBinding
import com.anadolstudio.adelaide.ui.ViewModelFactory
import com.anadolstudio.adelaide.ui.screens.BaseEditActivity
import com.anadolstudio.adelaide.ui.screens.edit.main_edit_screen.EditActivity
import com.anadolstudio.adelaide.ui.screens.main.EditType
import com.anadolstudio.core.activity.hasPermission
import com.anadolstudio.core.activity.requestPermission
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.adapters.ILoadMore
import com.anadolstudio.core.common_extention.onTrue
import com.anadolstudio.core.recycler.SpaceItemDecoration

class GalleryListActivity : BaseEditActivity(), ActionClick<String>, ILoadMore {

    companion object {
        const val TAG = "GalleryListActivity"
        const val REQUEST_STORAGE_PERMISSION = 1
        private const val REQUEST_CHOOSE_PHOTO = 1001
        private const val CHOOSE_PHOTO = "choose_photo"
        private val STORAGE_PERMISSION = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun start(context: Context, type: EditType) {
            val starter = Intent(context, GalleryListActivity::class.java)
            starter.putExtra(EditType::class.java.name, type)
            context.startActivity(starter)
        }
    }

    private val binding by lazy(LazyThreadSafetyMode.NONE) { ActivityGalleryBinding.inflate(layoutInflater) }
    private lateinit var galleryListAdapter: GalleryImageAdapter
    private val viewModel: GalleryListViewModel by viewModels { ViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.navigationTb)
        viewModel.screenState.observe(this, this::render)

        binding.navigationTb.setNavigationOnClickListener { onBackPressed() }
        binding.navigationTb.title = null

        initView()
        loadData()
    }

    private fun render(state: GalleryScreenState) = when (state) {
        is GalleryScreenState.Content -> showContent(state).also { /*hideLoadingDialog()*/ }
        is GalleryScreenState.Error -> initSpinner(emptyList()).also { /*hideLoadingDialog()*/ }
        is GalleryScreenState.Empty -> showEmptyText(true).also { /*hideLoadingDialog()*/ }
        is GalleryScreenState.Loading -> {}  /*showLoadingDialog()*/
    }

    private fun showContent(content: GalleryScreenState.Content) {
        content.folders?.toList()?.let(this::initSpinner)
        showEmptyText(false)

        when (content.isLoadMore) {
            true -> galleryListAdapter.addData(content.images)
            false -> galleryListAdapter.setData(content.images)
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        withPermission { viewModel.updateImages(this, binding.recyclerView.adapter?.itemCount) }
    }

    private fun initView() {
        galleryListAdapter = GalleryImageAdapter(mutableListOf<String>(), this, this)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryListActivity, 3)
            setItemViewCacheSize(50)
            addItemDecoration(SpaceItemDecoration(SpaceItemDecoration.NORMAL_SPACE))
            adapter = galleryListAdapter
        }
    }

    override fun loadMore() = loadImages(true)

    @SuppressLint("MissingPermission")
    private fun loadImages(loadMore: Boolean = false) {
        withPermission { viewModel.loadImages(this@GalleryListActivity, loadMore = loadMore) }
    }

    @SuppressLint("MissingPermission")
    private fun loadData() {
        val success = withPermission { viewModel.loadData(this) }

        if (!success) {
            showEmptyText(true)
            requestPermission(STORAGE_PERMISSION[0], REQUEST_STORAGE_PERMISSION)
        }
    }

    private fun initSpinner(folders: List<String>) {
        if (binding.spinner.adapter != null) return

        val data = mutableListOf<String>().apply {
            add(getString(R.string.gallery_spinner_title))
            addAll(folders)
        }
        binding.spinner.apply {
            adapter = ArrayAdapter(this@GalleryListActivity, R.layout.item_simple_list, data)
            onItemSelectedListener = ItemSelectedListener()
        }
    }

    inner class ItemSelectedListener : AdapterView.OnItemSelectedListener {

        @SuppressLint("MissingPermission")
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (adapterView?.adapter == null || view == null || adapterView.adapter.isEmpty) return

            val currentFolder = when (i) {
                0 -> null
                else -> adapterView.adapter.getItem(i).toString()
            }

            if (viewModel.folderChanged(currentFolder)) {
                loadImages()
            }
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {}
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) { // permission granted
                    loadData()
                } else if (PermissionUtil.ReadExternalStorage.shouldShowRequestPermissionRationale(this))
                    showSettingsSnackbar(binding.root)
                else finish() // Закрывает activity
                } else { // permission denied
                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(this, STORAGE_PERMISSION[0])

                    if (!shouldShow) showSettingsSnackbar()
                    else finish() // Закрывает activity
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showEmptyText(isShow: Boolean) {
        binding.emptyText.isVisible = isShow
        binding.recyclerView.isVisible = !isShow
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (intent.getIntExtra(CHOOSE_PHOTO, 0) != REQUEST_CHOOSE_PHOTO) {
            super.onSupportNavigateUp()
        } else {
            onBackPressed()
            true
        }
    }

    override fun action(data: String) {
        if (intent.getIntExtra(CHOOSE_PHOTO, 0) != REQUEST_CHOOSE_PHOTO) {
            val editType = intent.getSerializableExtra(EditType::class.java.name) as EditType
            EditActivity.start(this, editType, data)
        } else {
            setResult(RESULT_OK, Intent().putExtra(CHOOSE_PHOTO, data))
            finish()
        }
    }

    private fun withPermission(action: () -> Unit): Boolean =
            hasPermission(STORAGE_PERMISSION[0]).onTrue { action.invoke() }

    class GalleryResultContract : ActivityResultContract<String, String?>() {

        override fun createIntent(context: Context, input: String): Intent =
                Intent(context, GalleryListActivity::class.java).apply {
                    if (input == CHOOSE_PHOTO) putExtra(CHOOSE_PHOTO, REQUEST_CHOOSE_PHOTO)
                }

        override fun parseResult(resultCode: Int, intent: Intent?): String? = when {
            resultCode != RESULT_OK || intent == null -> null
            else -> intent.getStringExtra(CHOOSE_PHOTO)
        }
    }
}
