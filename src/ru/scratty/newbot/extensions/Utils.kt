package ru.scratty.newbot.extensions

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException

fun sendMessage(sender: AbsSender, chatId: Long, text: String, replyKeyboard: ReplyKeyboard = ReplyKeyboard {}) {
    val sendMessage = SendMessage().apply {
        enableMarkdown(true)
        this.chatId = chatId.toString()
        this.text = text
        replyMarkup = replyKeyboard
    }

    try {
        sender.execute(sendMessage)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}

fun updateMessage(sender: AbsSender, message: Message, text: String, replyKeyboard: ReplyKeyboard = ReplyKeyboard {}) {
    val editMessage = EditMessageText().apply {
        chatId = message.chatId.toString()
        messageId = message.messageId
        this.text = text
        replyMarkup = replyKeyboard as InlineKeyboardMarkup
    }

    try {
        sender.execute(editMessage)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }
}