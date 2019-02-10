package ru.scratty.newbot.extensions

import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.bots.AbsSender

interface ICallbackCommand {

    fun handleCallbackMessage(sender: AbsSender, callbackQuery: CallbackQuery)
}