package com.anadolstudio.photoeditorprocessor.data.photo_segmenter

sealed class SegmenterException(message: String) : Exception(message)

class EmptyMaskException : SegmenterException("Empty mask")
