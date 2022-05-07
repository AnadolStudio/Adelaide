package com.anadolstudio.core.tasks

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

interface RxTask<T> {

    fun onSuccess(callback: RxCallback<T>): RxTask<T>

    fun onError(callback: RxCallback<Throwable>): RxTask<T>

    fun onFinal(callback: RxCallback<Boolean>): RxTask<T>

    fun cancel()

    abstract class Abstract<T : Any> : RxTask<T> {

        protected var result: Result<T> = Result.Loading()
        protected var disposable: Disposable? = null

        protected fun start(): RxTask<T> {
            val observable = createObservable()
            subscribe(observable)
            return this
        }

        abstract fun createObservable(): Observable<T>

        abstract fun subscribe(observable: Observable<T>)

        protected var valueCallback: RxCallback<T>? = null
        protected var errorCallback: RxCallback<Throwable>? = null
        protected var finalCallback: RxCallback<Boolean>? = null

        override fun onSuccess(callback: RxCallback<T>): RxTask<T> {
            valueCallback = callback
            notifyListener()
            return this
        }

        override fun onError(callback: RxCallback<Throwable>): RxTask<T> {
            errorCallback = callback
            notifyListener()
            return this
        }

        override fun cancel() {
            disposable?.let {
                if (!it.isDisposed) it.dispose()
            }
        }

        protected fun notifyListener() {
            val result = this.result
            val callback = this.valueCallback
            val errorCallback = this.errorCallback

            if (result is Result.Success && callback != null) {
                callback(result.data)
                finalCallback?.invoke(true)
            } else if (result is Result.Error && errorCallback != null) {
                errorCallback(result.error)
                finalCallback?.invoke(false)
            }
        }

        override fun onFinal(callback: RxCallback<Boolean>): RxTask<T> {
            finalCallback = callback
            return this
        }
    }

    abstract class SimpleStart<T : Any>(immediately: Boolean) : Abstract<T>() {

        init {
            if (immediately) start()
        }

        override fun subscribe(observable: Observable<T>) {
            disposable = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result = Result.Success(it) },
                    {
                        result = Result.Error(it)
                        notifyListener()
                    },
                    { notifyListener() }
                )
        }
    }

    open class Base<T : Any>(
        immediately: Boolean,
        private val callback: RxDoMainCallback<T>
    ) : SimpleStart<T>(immediately) {

        override fun createObservable(): Observable<T> = Observable.create { emitter ->
            try {
                emitter.onNext(callback.invoke())
                emitter.onComplete()
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }

        class Quick<T : Any>(callback: RxDoMainCallback<T>) : Base<T>(true, callback)

        class Lazy<T : Any>(callback: RxDoMainCallback<T>) : Base<T>(false, callback)
    }

    open class Progress<TaskData : Any, ProgressData>(
        immediately: Boolean,
        protected val progressListener: ProgressListener<ProgressData>,
        private val callback: RxProgressCallback<TaskData,ProgressData>
    ) : SimpleStart<TaskData>(immediately) {

        override fun createObservable(): Observable<TaskData> = Observable.create { emitter ->
            try {
                emitter.onNext(callback.invoke(progressListener))
                emitter.onComplete()
            } catch (ex: Exception) {
                emitter.onError(ex)
            }
        }

        class Quick<TaskData : Any, ProgressData>(
            progressListener: ProgressListener<ProgressData>,
            callback: RxProgressCallback<TaskData,ProgressData>
        ) : Progress<TaskData,ProgressData>(true, progressListener, callback)

        class Lazy<TaskData : Any, ProgressData>(
            progressListener: ProgressListener<ProgressData>,
            callback: RxProgressCallback<TaskData,ProgressData>
        ) : Progress<TaskData,ProgressData>(false, progressListener, callback)
    }
}