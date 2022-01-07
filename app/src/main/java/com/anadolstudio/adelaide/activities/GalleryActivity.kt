package com.anadolstudio.adelaide.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.anadolstudio.adelaide.R
import com.anadolstudio.adelaide.activities.MainActivity.Companion.EDIT_TYPE
import com.anadolstudio.adelaide.adapters.GalleryListAdapter
import com.anadolstudio.adelaide.databinding.ActivityGalleryBinding
import com.anadolstudio.adelaide.helpers.PermissionHelper.*
import com.anadolstudio.adelaide.helpers.SpacesItemDecoration
import com.anadolstudio.adelaide.interfaces.IDetailable
import com.anadolstudio.adelaide.interfaces.ILoadMore
import com.anadolstudio.adelaide.interfaces.MainContract
import com.anadolstudio.adelaide.model.MainRepository.NULL
import com.anadolstudio.adelaide.presenters.GalleryPresenter
import com.anadolstudio.adelaide.presenters.GalleryPresenter.GalleryPresenterItem.TypeData.FOLDERS
import com.anadolstudio.adelaide.presenters.GalleryPresenter.GalleryPresenterItem.TypeData.IMAGES
import java.io.File
import java.util.*

class GalleryActivity : BaseActivity(),
    MainContract.View<GalleryPresenter.GalleryPresenterItem>,
    IDetailable<String>,
    ILoadMore {

    companion object {
        private val TAG = GalleryActivity::class.java.name
        private const val LIST_OF_PATH = "list_of_path"
        private const val CURRENT_FOLDERS = "current_folders"
        const val REQUEST_CHOOSE_PHOTO = 1001
        const val CHOOSE_PHOTO = "choose_photo"

        fun start(context: Context, type: String) {
            val starter = Intent(
                context,
                GalleryActivity::class.java
            )
            starter.putExtra(EDIT_TYPE, type)
            context.startActivity(starter)
        }
    }

    private lateinit var binding: ActivityGalleryBinding
    private lateinit var presenter: GalleryPresenter
    private lateinit var adapter: GalleryListAdapter
    private lateinit var folders: ArrayList<String>
    private var currentFolder: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.navigationToolbar)
        binding.navigationToolbar.setNavigationOnClickListener { onBackPressed() }
        binding.navigationToolbar.title = null
        /*val billingClientWrapper: BillingClientWrapper = BillingClientWrapper.Companion.get(this)
        billingClientWrapper.queryActivePurchases(this)*/
        init(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        /**val gridCount = getCurrentGridColumn(this)
        setupGridImage(gridCount)
        binding.gridBtn.setOnClickListener {
        changeGridColumn()
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, gridCount)
         */

        presenter = GalleryPresenter(this)
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
        binding.recyclerView.setItemViewCacheSize(50)
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(SpacesItemDecoration.NORMAL_SPACE))
        val listOfPaths: ArrayList<String> =
            savedInstanceState?.getStringArrayList(LIST_OF_PATH) ?: ArrayList()

        currentFolder = savedInstanceState?.getString(CURRENT_FOLDERS)
        adapter = GalleryListAdapter(this, listOfPaths, this, this)
        binding.recyclerView.adapter = adapter
    }

    /**private fun changeGridColumn() {
    val currentGrid =
    3 + getCurrentGridColumn(this) % GridColumn.values().size
    setupGridImage(currentGrid)
    setCurrentGridColumn(this, currentGrid)
    val indexOfFocusedChild = binding.recyclerView.indexOfChild(binding.recyclerView)
    binding.recyclerView.layoutManager = GridLayoutManager(this, currentGrid)
    binding.recyclerView.smoothScrollToPosition(max(indexOfFocusedChild, 0))
    }

    private fun setupGridImage(gridCount: Int) {
    when (gridCount) {
    3 -> binding.gridBtn.setImageDrawable(
    AppCompatResources.getDrawable(
    this,
    R.drawable.ic_grid_3
    )
    )
    4 -> binding.gridBtn.setImageDrawable(
    AppCompatResources.getDrawable(
    this,
    R.drawable.ic_grid_4
    )
    )
    }
    }*/

    private fun loadFolders() {
        if (hasPermission(this, STORAGE_PERMISSION[0])) {
            showLoadingDialog()
            presenter.getAllFolders(this)

        } else {
            showEmptyText()
            requestPermission(
                this, STORAGE_PERMISSION[0], REQUEST_STORAGE_PERMISSION
            )
        }

    }

    override fun onStart() {
        super.onStart()
        loadFolders()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(LIST_OF_PATH, adapter.data as ArrayList<String?>)
        outState.putString(CURRENT_FOLDERS, currentFolder)
    }

    /*fun onSuccess(activePurchase: List<Purchase?>) {
        var hasSub = false
        if (activePurchase.isNotEmpty()) {
            loop@ for (purchase in activePurchase) {
                for (s in purchase.getSkus()) {
                    //TODO Переписать через Regex
                    if (s == BillingClientWrapper.MONTH || s == BillingClientWrapper.SIX_MONTH || s == BillingClientWrapper.YEAR) {
                        hasSub = true
                        break@loop
                    }
                }
            }
        }
        SettingsPreference.setPremium(this, hasSub)
    }

    fun onFailure(error: BillingClientWrapper.Error) {
        Log.e(TAG, "PurchaseHistory onFailure : ${error.getDebugMessage()}")
        Log.e(TAG, "PurchaseHistory onFailure: ${error.getResponseCode()}")
    }*/

    override fun onSupportNavigateUp(): Boolean {
        return if (intent.getIntExtra(CHOOSE_PHOTO, 0) != REQUEST_CHOOSE_PHOTO) {
            super.onSupportNavigateUp()
        } else {
            onBackPressed()
            true
        }
    }

    private fun loadData(lastItemId: Long, folder: String?) {
        presenter.loadData(this, lastItemId, folder)
    }

    private fun getLastItemId(): Long {
        val data = adapter.data as ArrayList<String>
        if (data.isEmpty()) {
            return NULL
        }
        val toParseInt = File(Uri.parse(data.last()).path ?: return NULL).name
        return if (TextUtils.isDigitsOnly(toParseInt)) toParseInt.toLong() else NULL
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_STORAGE_PERMISSION -> if (grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                loadFolders()
                // permission granted
//                    presenter.loadData(this, getLastItemId(), null);
            } else {
                // permission denied
                val shouldShow = ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    STORAGE_PERMISSION[0]
                )
                if (!shouldShow) {
                    showSettingsSnackbar(this, binding.root)
                } else {
                    finish() // Закрывает activity
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onPreShow(): Boolean {
        return hasPermission(this, STORAGE_PERMISSION[0])
    }

    override fun showData(item: GalleryPresenter.GalleryPresenterItem) {
        when (item.typeData) {
            IMAGES -> {
                Log.d(TAG, "showData: GalleryPresenterItem$item")
                val paths: ArrayList<String> = item.paths
                if (paths.isNotEmpty()) {
                    Log.d(TAG, "showData: !paths.isEmpty()")
                    // Проверка на актуальность данных
                    if (item.lastItemId != NULL) {
                        // Null значит, что предыдущий элемент есть, т.е. это loadMore
                        adapter.addData(paths)
                    } else if (adapter.data.isEmpty() || paths != adapter.data) {
                        adapter.setData(paths)

                        //                binding.recyclerView.smoothScrollToPosition(0); //Не уверен в надобности
                    }
                }
            }
            FOLDERS -> {
                hideLoadingDialog()
                binding.recyclerView.scheduleLayoutAnimation()
                folders = ArrayList()
                folders.add(resources.getString(R.string.gallery))
                folders.addAll(item.folders)
                initSpinner()
                loadData(NULL, currentFolder)
            }
            else -> {
                Log.d(TAG, "showData: GalleryPresenterItem$item")
                val paths: ArrayList<String> = item.paths
                if (paths.isNotEmpty()) {
                    Log.d(TAG, "showData: !paths.isEmpty()")
                    if (item.lastItemId != NULL) {
                        adapter.addData(paths)
                    } else if (adapter.data.isEmpty() || paths != adapter.data) {
                        adapter.setData(paths)
                    }
                }
            }
        }
    }

    private fun initSpinner() {
        if (!::folders.isInitialized) return

        val arrayAdapter = ArrayAdapter<String>(this, R.layout.simple_list_item)
        arrayAdapter.addAll(folders)
        binding.spinner.adapter = arrayAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                i: Int,
                l: Long
            ) {
                arrayAdapter.let {
                    if (i >= it.count) return
                    currentFolder = if (i == 0) null else it.getItem(i)
                    loadData(NULL, currentFolder)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
    }

    override fun onPostShow(onPreShow: Boolean) {
        if (onPreShow) {
            showEmptyText()
        }
//        hideLoadingDialog()
    }

    private fun showEmptyText() {
        binding.emptyText.visibility = if (adapter.data.isNotEmpty()) View.GONE else View.VISIBLE
    }

    override fun toDetail(path: String) {

        val intent = intent
        if (intent.getIntExtra(CHOOSE_PHOTO, 0) == REQUEST_CHOOSE_PHOTO) {
            val resultIntent = Intent().putExtra(CHOOSE_PHOTO, path)
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            showLoadingDialog()
            val editType = intent.getStringExtra(EDIT_TYPE)
            EditActivity.start(this, editType, path, object : EditActivity.Callback {
                override fun callback() {
                    hideLoadingDialog()
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        hideLoadingDialog()
    }

    override fun loadMore() {
        loadData(getLastItemId(), currentFolder)
    }

    class GalleryResultContract : ActivityResultContract<String, String?>() {
        override fun createIntent(context: Context, input: String): Intent {
            val intent = Intent(context, GalleryActivity::class.java)
            if (input == CHOOSE_PHOTO) {
                intent.putExtra(CHOOSE_PHOTO, REQUEST_CHOOSE_PHOTO)
            }
            return intent
        }

        override fun parseResult(resultCode: Int, data: Intent?): String? {
            Log.i(TAG, "Result code: $resultCode")
            return if (resultCode != RESULT_OK || data == null) {
                null
            } else data.getStringExtra(CHOOSE_PHOTO)
        }
    }
}