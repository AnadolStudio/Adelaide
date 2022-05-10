package com.anadolstudio.photoeditorprocessor.data.segmenter

sealed class SegmenterException(message: String) : Exception(message)

class EmptyMaskException : SegmenterException("Empty mask")

class SmallMaskException : SegmenterException("Small mask")