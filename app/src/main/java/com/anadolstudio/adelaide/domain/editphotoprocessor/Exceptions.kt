package com.anadolstudio.adelaide.domain.editphotoprocessor

sealed class PhotoEditException(message: String) : Exception(message)

open class InvalidateBitmapException(message: String) : PhotoEditException(message)

class NullBitmapException : InvalidateBitmapException("Bitmap is null")