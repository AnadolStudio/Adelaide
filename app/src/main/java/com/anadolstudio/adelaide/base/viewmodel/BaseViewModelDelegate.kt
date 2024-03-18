package com.anadolstudio.adelaide.base.viewmodel

import com.anadolstudio.ui.SingleMessageSnack
import com.anadolstudio.ui.viewmodel.livedata.SingleEvent
import com.anadolstudio.ui.viewmodel.livedata.SingleLiveEvent
import com.anadolstudio.ui.viewmodel.livedata.onNext

interface BaseViewModelDelegate {
    fun showTodo()

    class Delegate(private val singleLiveEvent: SingleLiveEvent<SingleEvent>) : BaseViewModelDelegate {

        private val todoMessages = listOf(
                "Извините, этот функционал пока не реализован \uD83D\uDE43",
                "Мы работаем над этим функционалом, следите за обновлениями \uD83D\uDC40",
                "Эта функция будет доступна в ближайшее время \uD83C\uDFC3",
        )

        override fun showTodo() {
            singleLiveEvent.onNext(SingleMessageSnack.Short(todoMessages.random()))
        }
    }

}
