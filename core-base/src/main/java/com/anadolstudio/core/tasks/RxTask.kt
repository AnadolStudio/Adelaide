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


    open class Base<T : Any>(private val callback: RxDoMainCallback<T>) : RxTask<T> {

        protected var result: Result<T> = Result.Loading()
        protected val disposable: Disposable

        init {
            val observable: Observable<T> = Observable.create { emitter ->
                try {
                    emitter.onNext(callback.invoke())
                    emitter.onComplete()
                } catch (ex: Exception) {
                    emitter.onError(ex)
                }
            }

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
            if (!disposable.isDisposed) disposable.dispose()
        }

        private fun notifyListener() {
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


    //TODO ProgressTask
}