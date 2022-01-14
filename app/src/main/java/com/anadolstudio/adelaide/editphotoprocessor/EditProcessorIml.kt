package com.anadolstudio.adelaide.editphotoprocessor

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.editphotoprocessor.BitmapUtils.Companion.MAX_SIDE_COPY
import com.anadolstudio.adelaide.editphotoprocessor.BitmapUtils.Companion.saveBitmapAsFileQ
import com.anadolstudio.adelaide.fragments.LoadingDialog
import com.anadolstudio.adelaide.interfaces.LoadingView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.io.File


class EditProcessorIml(
    val activity: AppCompatActivity,
    path: String,
    val editListener: EditListener<Bitmap>?
) :
    EditProcessorContract {
    constructor(
        activity: AppCompatActivity,
        path: String,
    ) : this(activity, path, null)

    companion object {
        val TAG: String = EditProcessorIml::class.java.name
    }

    private var mLoadingView: LoadingView? = null
    private lateinit var path: String
    private var originalBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null
    private var disposable: Disposable? = null

    init {
        setImage(path)
    }

    private fun showLoadingDialog() {
        mLoadingView = LoadingDialog.view(activity.supportFragmentManager)
        mLoadingView?.showLoadingIndicator()
    }

    private fun hideLoadingDialog() {
        mLoadingView?.hideLoadingIndicator()
    }

    override val containerFunctions: LinkedHashMap<String, EditFunction> = LinkedHashMap()

    private fun LinkedHashMap<String, EditFunction>.add(func: EditFunction) {
        put(func.type, func)
    }


    override val applyFuncList: MutableSet<EditFunction> = mutableSetOf()// TODO нужен ли?

    override fun setImage(path: String) {
        showLoadingDialog()
        this.path = path
        try {
            originalBitmap =
                BitmapUtils.decodeBitmapFromContentResolverPath(activity, path, MAX_SIDE_COPY)
            originalBitmap?.let { editListener?.onSuccess(it) }
                ?: throw IllegalArgumentException("Bitmap is null")
        } catch (throwable: Throwable) {
            editListener?.onFailure(throwable)
        }
        hideLoadingDialog()
    }

    override fun saveAsBitmap(listener: EditListener<Bitmap>) {
        TODO("Not yet implemented")
    }


    override fun saveAsFile(context: Context, file: File, listener: EditListener<String>) {
        showLoadingDialog()
        var realImage = BitmapUtils.decodeBitmapFromContentResolverPath(activity, path)

        realImage?.let { realImage = processAll(it) }

        realImage?.let {
            disposable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val fileName = file.name
                saveBitmapAsFileQ(context, it, fileName)
            } else {
                BitmapUtils.saveBitmapAsFileBellowQ(context, it, file)
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { imagePath ->
                        listener.onSuccess(imagePath)
                        hideLoadingDialog()
                    },
                    { throwable: Throwable? ->
                        throwable?.let { thr -> listener.onFailure(thr) }
                        hideLoadingDialog()
                    })
        } ?: hideLoadingDialog()
        // TODO ?:
    }

    override fun processPreview() {
        currentBitmap = originalBitmap?.let { processAll(it) }
        currentBitmap?.let { editListener?.onSuccess(it) }
    }

    override fun process(bitmap: Bitmap, func: EditFunction): Bitmap {
        return func.process(bitmap)
    }

    override fun processAll(bitmap: Bitmap): Bitmap {
        showLoadingDialog()
        var result: Bitmap? = null
        for (f in containerFunctions.values) {
            (f as? TransformFunction)?.let {
                originalBitmap?.let {
                    f.scale = BitmapUtils.getScaleRatio(
                        bitmap.width.toFloat(),
                        bitmap.height.toFloat(),
                        it.width.toFloat(),
                        it.height.toFloat()
                    )
                }
            }
            result = f.process(bitmap)
        }
        hideLoadingDialog()
        return result ?: bitmap
    }

    override fun getOriginalImage(): Bitmap? = originalBitmap

    override fun getCurrentImage(): Bitmap? = currentBitmap ?: originalBitmap

    override fun clear() {
        originalBitmap?.recycle()
        currentBitmap?.recycle()
        disposable?.let { if (!it.isDisposed) disposable }
    }

    fun getFunction(type: String): EditFunction? = containerFunctions[type]

    fun add(func: EditFunction) {
        containerFunctions.add(func)
    }


}
