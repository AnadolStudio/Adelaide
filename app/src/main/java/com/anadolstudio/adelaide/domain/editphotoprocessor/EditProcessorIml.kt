package com.anadolstudio.adelaide.domain.editphotoprocessor

import android.content.Context
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapUtils.MAX_SIDE_COPY
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapSaver
import com.anadolstudio.adelaide.domain.editphotoprocessor.util.BitmapUtils
import com.anadolstudio.core.dialogs.LoadingView
import com.anadolstudio.core.tasks.RxTask
import io.reactivex.disposables.Disposable
import java.io.File

class EditProcessorIml(
    val activity: AppCompatActivity,
    path: String,
    val editListener: EditListener<Bitmap>?
) : EditProcessorContract {
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
        mLoadingView = LoadingView.Base.view(activity.supportFragmentManager)
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
        this.path = path

        RxTask.Base.Quick {
            BitmapUtils.decodeBitmapFromContentResolverPath(
                activity,
                path,
                MAX_SIDE_COPY
            )
        }.onSuccess { bitmap ->
            originalBitmap = bitmap
            editListener?.onSuccess(bitmap)
        }.onError { ex ->
            editListener?.onFailure(ex)
        }
    }

    override fun saveAsBitmap(listener: EditListener<Bitmap>) {
        TODO("Not yet implemented")
    }

    override fun saveAsFile(context: Context, file: File, listener: EditListener<String>) {
        showLoadingDialog()

        RxTask.Progress.Quick(mLoadingView!!) { progressListener ->

            var bitmap = BitmapUtils.decodeBitmapFromContentResolverPath(activity, path)
            bitmap = processAll(bitmap)

            progressListener.onProgress(25)

            BitmapSaver.Factory.save(progressListener, context, bitmap, file)
        }
            .onSuccess { imagePath -> listener.onSuccess(imagePath) }
            .onError { throwable -> listener.onFailure(throwable) }
            .onFinal { hideLoadingDialog() }
    }

    override fun processPreview() {
        //TODO RxTask
        currentBitmap = originalBitmap?.let { processAll(it) }
        currentBitmap?.let { editListener?.onSuccess(it) }
    }

    override fun process(bitmap: Bitmap, func: EditFunction): Bitmap {
        return func.process(bitmap)
    }

    override fun processAll(bitmap: Bitmap): Bitmap {
//        showLoadingDialog()
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

//        hideLoadingDialog()
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
