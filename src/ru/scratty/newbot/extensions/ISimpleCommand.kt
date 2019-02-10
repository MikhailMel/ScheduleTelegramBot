package ru.scratty.newbot.extensions

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

interface ISimpleCommand {

    fun handleMessage(sender: AbsSender, message: Message)
}