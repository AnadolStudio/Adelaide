package com.anadolstudio.adelaide.view.screens.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels

import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.databinding.ActivityGalleryBinding
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.REQUEST_STORAGE_PERMISSION
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.STORAGE_PERMISSION
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.hasPermission
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.requestPermission
import com.anadolstudio.adelaide.domain.utils.PermissionHelper.showSettingsSnackbar
import com.anadolstudio.adelaide.view.ViewModelFactory
import com.anadolstudio.adelaide.view.screens.edit.EditActivity
import com.anadolstudio.adelaide.view.screens.main.TypeKey
import com.anadolstudio.core.adapters.BaseSpaceItemDecoration
import com.anadolstudio.core.interfaces.IDetailable
import com.anadolstudio.core.interfaces.ILoadMore
import com.anadolstudio.core.tasks.Result
import com.anadolstudio.core.view.BaseActivity

import java.io.File

class GalleryListActivity : BaseActivity(), IDetailable<String>, ILoadMore {

    companion object {
        private const val TAG = "GalleryListActivity"
        private const val CURRENT_FOLDERS = "current_folders"
        private const val REQUEST_CHOOSE_PHOTO = 1001
        private const val CHOOSE_PHOTO = "choose_photo"

        fun start(context: Context, key: String?) {
            val starter = Intent(context, GalleryListActivity::class.java)
            starter.putExtra(TypeKey::class.java.name, key)
            context.startActivity(starter)
        }
    }

    //TODO Есть баг с дубликатами

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var galleryListAdapter: GalleryAdapter
    private var currentFolder: String? = null
    private var loadingMore = false
    private val viewModel: GalleryListViewModel by viewModels { ViewModelFactory() }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CURRENT_FOLDERS, currentFolder)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.navigationTb)

        binding.navigationTb.setNavigationOnClickListener { onBackPressed() }
        binding.navigationTb.title = null

        init(savedInstanceState)

        loadData()
    }

    private fun init(savedInstanceState: Bundle?) {
        galleryListAdapter = GalleryAdapter(mutableListOf(), this, this)
        currentFolder = savedInstanceState?.getString(CURRENT_FOLDERS)

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryListActivity, 3)
            setItemViewCacheSize(50)
            addItemDecoration(BaseSpaceItemDecoration.All())
            adapter = galleryListAdapter
        }

        viewModel.folders.observe(this) { result ->
            when (result) {
                is Result.Success -> initSpinner(result.data.toList())
                is Result.Loading -> showLoadingDialog()
                is Result.Error -> result.error.printStackTrace()
                is Result.Empty -> initSpinner(listOf())
            }
        }

        viewModel.images.observe(this) {
            hideAll()
            when (it) {
                is Result.Success -> {
                    binding.recyclerView.visibility = View.VISIBLE
                    if (loadingMore) galleryListAdapter.addData(it.data as ArrayList<String>)
                    else galleryListAdapter.setData(it.data as ArrayList<String>)

                    loadingMore = false
                }

                is Result.Error -> it.error.printStackTrace()
                is Result.Loading -> showLoadingDialog()

                is Result.Empty ->
                    if (loadingMore) loadingMore = false
                    else showEmptyText()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun loadMore() {
        if (hasPermission(this@GalleryListActivity, STORAGE_PERMISSION[0])) {
            loadingMore = true
            viewModel.loadImages(this@GalleryListActivity, currentFolder, getLastItemId())
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadData() {
        if (hasPermission(this, STORAGE_PERMISSION[0])) {
            viewModel.loadData(this)
        } else {
            showEmptyText()
            requestPermission(this, STORAGE_PERMISSION[0], REQUEST_STORAGE_PERMISSION)
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

    private fun getLastItemId(): Long {
        val data = galleryListAdapter.getData()
        if (data.isEmpty()) return -1

        val toParseInt = File(Uri.parse(data.last()).path!!).name
        return if (TextUtils.isDigitsOnly(toParseInt)) toParseInt.toLong() else -1L
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION ->
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    // permission granted
                    loadData()
                } else {
                    // permission denied
                    val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        STORAGE_PERMISSION[0]
                    )
                    if (!shouldShow) showSettingsSnackbar(this, binding.root)
                    else finish() // Закрывает activity
                }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun showEmptyText() {
        binding.emptyText.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.INVISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        return if (intent.getIntExtra(CHOOSE_PHOTO, 0) != REQUEST_CHOOSE_PHOTO) {
            super.onSupportNavigateUp()
        } else {
            onBackPressed()
            true
        }
    }

    override fun toDetail(path: String) {

        if (intent.getIntExtra(CHOOSE_PHOTO, 0) != REQUEST_CHOOSE_PHOTO) {
            val editType = intent.getStringExtra(TypeKey::class.java.name)
            showLoadingDialog()
            EditActivity.start(this, editType, path)
        } else {
            setResult(RESULT_OK, Intent().putExtra(CHOOSE_PHOTO, path))
            finish()
        }
    }

    private fun hideAll() {
        binding.emptyText.visibility = View.INVISIBLE
        hideLoadingDialog()
    }

    inner class ItemSelectedListener : AdapterView.OnItemSelectedListener {

        @SuppressLint("MissingPermission")
        override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, i: Int, l: Long) {
            if (adapterView?.adapter == null || view == null || adapterView.adapter.isEmpty)
                return

            currentFolder =
                if (i == 0) null
                else adapterView.adapter.getItem(i).toString()

            if (hasPermission(this@GalleryListActivity, STORAGE_PERMISSION[0])) {
                viewModel.loadImages(this@GalleryListActivity, currentFolder)
            }
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

        override fun parseResult(resultCode: Int, data: Intent?): String? {
            Log.i(TAG, "Result code: $resultCode")
            return if (resultCode != RESULT_OK || data == null) null
            else data.getStringExtra(CHOOSE_PHOTO)
        }
    }
}