package com.anadolstudio.adelaide.view.screens.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityGalleryBinding
import com.anadolstudio.adelaide.view.ViewModelFactory
import com.anadolstudio.adelaide.view.screens.BaseEditActivity
import com.anadolstudio.adelaide.view.screens.edit.EditActivity
import com.anadolstudio.adelaide.view.screens.main.EditType
import com.anadolstudio.core.adapters.ActionClick
import com.anadolstudio.core.adapters.ILoadMore
import com.anadolstudio.core.adapters.util.BaseSpaceItemDecoration
import com.anadolstudio.core.util.PermissionUtil
import com.anadolstudio.core.util.PermissionUtil.Abstract.Companion.DEFAULT_REQUEST_CODE
import com.anadolstudio.photoeditorprocessor.util.BitmapCommonUtil
import java.io.File

class GalleryListActivity : BaseEditActivity(), ActionClick<String>, ILoadMore {

    companion object {
        private const val TAG = "GalleryListActivity"
        private const val CURRENT_FOLDERS = "current_folders"
        private const val REQUEST_CHOOSE_PHOTO = 1001
        private const val CHOOSE_PHOTO = "choose_photo"

        fun start(context: Context, key: String?) {
            val starter = Intent(context, GalleryListActivity::class.java)
            starter.putExtra(EditType::class.java.name, key)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var galleryListAdapter: GalleryImageAdapter
    private var currentFolder: String? = null
    private var loadingMore = false
    private val viewModel: GalleryListViewModel by viewModels { ViewModelFactory() }

    private val spinnerAdapterData = mutableListOf<String>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FOLDERS, currentFolder)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.folders.observe(this, ::showFolderProgress)
        viewModel.images.observe(this, ::showImageProgress)

        setupView(savedInstanceState)
    }

    private fun showImageProgress(result: Result<List<String>>) {
        when (result) {
            is Result.Success -> {
                showEmptyText(false)

                result.data.toMutableList().also { data ->
                    if (loadingMore) galleryListAdapter.addData(data)
                    else galleryListAdapter.setData(data)
                }

                loadingMore = false
            }

            is Result.Error -> result.error.printStackTrace()

            is Result.Empty ->
                if (loadingMore) loadingMore = false
                else showEmptyText(true)
        }

        showLoadingDialog(result is Result.Loading)
    }

    private fun showFolderProgress(result: Result<Set<String>>) {
        when (result) {
            is Result.Success -> setDataToSpinnerAdapter(result.data.toList())
            is Result.Error -> result.error.printStackTrace()
            is Result.Empty -> setDataToSpinnerAdapter(listOf())
        }

        showLoadingDialog(result is Result.Loading)
    }

    private fun setupView(savedInstanceState: Bundle?) {
        setSupportActionBar(binding.navigationTb)
        binding.navigationTb.setNavigationOnClickListener { onBackPressed() }
        binding.navigationTb.title = null

        galleryListAdapter = GalleryImageAdapter(mutableListOf(), this, this)
        currentFolder = savedInstanceState?.getString(CURRENT_FOLDERS)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryListActivity, 3)
            setItemViewCacheSize(50)
            addItemDecoration(BaseSpaceItemDecoration.All())
            adapter = galleryListAdapter
        }

        binding.spinner.onItemSelectedListener = ItemSelectedListener()
    }

    private fun setDataToSpinnerAdapter(folders: List<String>) {
        val data = folders.toMutableList().apply {
            add(0, getString(R.string.gallery_spinner_title))
        }
        binding.spinner.adapter = ArrayAdapter(this@GalleryListActivity, R.layout.item_simple_list, data)
    }

    override fun onStart() {
        super.onStart()
        loadData()
    }

    @SuppressLint("MissingPermission")
    override fun loadMore() {
        if (loadImages(getLastItemId())) {
            loadingMore = true
        }
    }

    private fun loadImages(lastItem: Long = -1L) = PermissionUtil.ReadExternalStorage.checkPermission(this).apply {
        if (this) viewModel.loadImages(this@GalleryListActivity, currentFolder, lastItem)
    }

    private fun loadFolders() = PermissionUtil.ReadExternalStorage.checkPermission(this).apply {
        if (this) viewModel.loadFolders(this@GalleryListActivity)
    }

    private fun loadData() {
        if (loadFolders()) {
            loadImages()
        } else {
            showEmptyText(true)
            PermissionUtil.ReadExternalStorage.requestPermission(this, DEFAULT_REQUEST_CODE)
        }
    }

    private fun getLastItemId(): Long {
        galleryListAdapter.getData().apply {
            if (isEmpty()) return -1L

            val path = Uri.parse(last()).path!!
            val toParseInt = File(path).name

            return if (TextUtils.isDigitsOnly(toParseInt)) toParseInt.toLong() else -1L
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            DEFAULT_REQUEST_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    // permission granted
                    loadData()
                } else if (PermissionUtil.ReadExternalStorage.shouldShowRequestPermissionRationale(this))
                    showSettingsSnackbar()
                else finish() // Закрывает activity
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showEmptyText(isShow: Boolean) {
        binding.emptyText.isVisible = isShow
        binding.recyclerView.isVisible = !isShow
    }

    override fun onSupportNavigateUp(): Boolean =
            if (intent.getIntExtra(CHOOSE_PHOTO, 0) != REQUEST_CHOOSE_PHOTO) {
                super.onSupportNavigateUp()
            } else {
                onBackPressed()
                true
            }

    override fun action(data: String) = if (BitmapCommonUtil.validateUri(this, data)) {
        showNextActivity(data)
    } else {
        showToast(R.string.edit_error_cant_open_photo)
    }

    private fun showNextActivity(data: String) {
        val requestCode = intent.getIntExtra(CHOOSE_PHOTO, 0)

        if (requestCode == REQUEST_CHOOSE_PHOTO) {
            setResult(RESULT_OK, Intent().putExtra(CHOOSE_PHOTO, data))
            finish()
        } else {
            val editType = intent.getStringExtra(EditType::class.java.name)
            EditActivity.start(this, editType, data)
        }
    }

    inner class ItemSelectedListener : AdapterView.OnItemSelectedListener {

        @SuppressLint("MissingPermission")
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (adapterView?.adapter == null || view == null || adapterView.adapter.isEmpty)
                return

            currentFolder = if (i == 0) null else adapterView.adapter.getItem(i).toString()
            loadImages()
        }

        override fun onNothingSelected(p0: AdapterView<*>?) {
        }
    }

    class GalleryResultContract : ActivityResultContract<String, String?>() {

        override fun createIntent(context: Context, input: String): Intent {
            val intent = Intent(context, GalleryListActivity::class.java)
            if (input == CHOOSE_PHOTO) intent.putExtra(CHOOSE_PHOTO, REQUEST_CHOOSE_PHOTO)
            return intent
        }

        override fun parseResult(resultCode: Int, data: Intent?): String? =
                if (resultCode != RESULT_OK || data == null) null
                else data.getStringExtra(CHOOSE_PHOTO)
    }
}
