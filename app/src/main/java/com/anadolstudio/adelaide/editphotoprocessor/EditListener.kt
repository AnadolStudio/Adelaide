package com.anadolstudio.adelaide.editphotoprocessor

interface EditListener<T> {

    fun onSuccess(t: T)

    fun onFailure(ex: Throwable)

}