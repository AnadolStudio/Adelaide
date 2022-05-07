package com.anadolstudio.adelaide.domain.editphotoprocessor

sealed class PhotoEditException(message: String) : Exception(message)

class InvalidateBitmapException(message: String) : PhotoEditException(message)