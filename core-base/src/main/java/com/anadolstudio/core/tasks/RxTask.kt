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

        protected var valueCallbacks: MutableList<RxCallback<T>> = mutableListOf()
        protected var errorCallbacks: MutableList<RxCallback<Throwable>> = mutableListOf()
        protected var finalCallbacks: MutableList<RxCallback<Boolean>> = mutableListOf()

        override fun onSuccess(callback: RxCallback<T>): RxTask<T> {
            valueCallbacks.add(callback)

            val result = this.result

            if (result is Result.Success) callback.invoke(result.data)

            return this
        }

        override fun onError(callback: RxCallback<Throwable>): RxTask<T> {
            errorCallbacks.add(callback)
            val result = this.result

            if (result is Result.Error) callback.invoke(result.error)

            return this
        }

        override fun onFinal(callback: RxCallback<Boolean>): RxTask<T> {
            finalCallbacks.add(callback)

            when (this.result) {
                is Result.Success -> callback.invoke(true)
                is Result.Error -> callback.invoke(false)
                else -> {}
            }

            return this
        }

        override fun cancel() {
            disposable?.apply {
                if (!isDisposed) dispose()
            }
        }

        protected fun notifyAllListeners() {
            val result = this.result
            val valueCallbacks = this.valueCallbacks
            val errorCallbacks = this.errorCallbacks

            if (result is Result.Success && valueCallbacks.isNotEmpty()) {
                valueCallbacks.forEach { it.invoke(result.data) }
                finalCallbacks.forEach { it.invoke(true) }
            } else if (result is Result.Error && errorCallbacks.isNotEmpty()) {
                errorCallbacks.forEach { it.invoke(result.error) }
                finalCallbacks.forEach { it.invoke(false) }
            }
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
                        notifyAllListeners()
                    },
                    { notifyAllListeners() }
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
        private val callback: RxProgressCallback<TaskData, ProgressData>
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
            callback: RxProgressCallback<TaskData, ProgressData>
        ) : Progress<TaskData, ProgressData>(true, progressListener, callback)

        class Lazy<TaskData : Any, ProgressData>(
            progressListener: ProgressListener<ProgressData>,
            callback: RxProgressCallback<TaskData, ProgressData>
        ) : Progress<TaskData, ProgressData>(false, progressListener, callback)
    }
}