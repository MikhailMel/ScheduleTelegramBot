package ru.scratty.newbot.extensions

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message

interface BaseEvent {

    fun checkExit(message: Message): Boolean
    fun checkExit(callbackQuery: CallbackQuery): Boolean

    fun handleMessage(message: Message)
    fun handleCallbackQuery(callbackQuery: CallbackQuery)

}