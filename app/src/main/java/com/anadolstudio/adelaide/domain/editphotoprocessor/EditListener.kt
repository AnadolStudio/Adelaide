package com.anadolstudio.adelaide.domain.editphotoprocessor

interface EditListener<Data> {

    fun onSuccess(data: Data)

    fun onFailure(ex: Throwable)

}