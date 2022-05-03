package com.anadolstudio.adelaide.domain.editphotoprocessor

interface EditListener<T> {

    fun onSuccess(t: T)

    fun onFailure(ex: Throwable)

}