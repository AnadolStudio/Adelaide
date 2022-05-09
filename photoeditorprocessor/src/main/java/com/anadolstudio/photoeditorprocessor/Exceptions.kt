package com.anadolstudio.photoeditorprocessor

sealed class PhotoEditException(message: String) : Exception(message)

open class InvalidateBitmapException(message: String) : PhotoEditException(message)

class NullBitmapException : InvalidateBitmapException("Bitmap is null")

open class FailedSaveException(message: String) : PhotoEditException(message)

class FileParentException : FailedSaveException("File parent is null")