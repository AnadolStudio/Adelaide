package com.anadolstudio.core.util

typealias DoubleClickListener = (Boolean) -> Unit

interface DoubleClickExit {

    fun click(listener: DoubleClickListener)

    class Base(val delay: Long = 2000) : DoubleClickExit {
        var backPressed: Long = 0L

        override fun click(listener: DoubleClickListener) {
            val currentTime = System.currentTimeMillis()
            listener.invoke(backPressed + delay > currentTime)
            backPressed = currentTime
        }
    }
}
