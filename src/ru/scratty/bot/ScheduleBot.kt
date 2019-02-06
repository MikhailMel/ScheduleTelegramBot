package ru.scratty.bot

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import ru.scratty.BotConfig
import ru.scratty.bot.commands.*
import ru.scratty.bot.notifications.LessonNotification
import ru.scratty.mongo.DBService
import ru.scratty.mongo.models.User
import ru.scratty.utils.Config
import java.text.SimpleDateFormat
import java.util.*

class ScheduleBot : TelegramLongPollingBot() {

    private val dbService = DBService.INSTANCE

    private val commands = arrayListOf(SelectGroupCommand(), DayScheduleCommand(), CreateGroupCommand(),
            AddLessonCommand(), WeekScheduleCommand(), CallsScheduleCommand(), SettingsCommand(), SendMessageById())

    init {
        for (i in Config.LESSONS.indices) {
            LessonNotification(Config.LESSONS[i], i + 1, ::sendMsg).start()
        }
    }

    override fun getBotToken() = BotConfig.BOT_TOKEN

    override fun getBotUsername() = BotConfig.BOT_USERNAME

    override fun onUpdateReceived(update: Update?) {
        if (update!!.hasMessage())
            log(update.message)
        else if (update.hasCallbackQuery()) {
            log(update.callbackQuery)
        }

        val chatId = if (update.hasMessage()) update.message.chatId else update.callbackQuery.message.chatId

        if (!dbService.userIsExists(chatId)) {
            val user = User(chatId, update.message.chat.firstName + " " + update.message.chat.lastName, update.message.chat.userName)
            dbService.addUser(user)

            val selectGroup = SelectGroupCommand()
            sendMsg(chatId, selectGroup.handleCommand(update, user), selectGroup.getKeyboard())
        }

        val user = dbService.getUser(chatId)

        for (command in commands) {
            if (command.checkCommand(update)) {
                val msg = command.handleCommand(update, user)

                if (command.isSendMessage) {
                    val pair = command.getMessageForSendPair()
                    sendMsg(pair.first, pair.second)
                }

                if (command.isUpdate) {
                    updMsg(update.callbackQuery.message, msg, command.getKeyboard() as InlineKeyboardMarkup)
                } else {
                    sendMsg(chatId, msg, command.getKeyboard())
                }

                break
            }
        }
    }

    private fun sendMsg(chatId: Long, text: String, replyKeyboard: ReplyKeyboard) {
        val sendMessage = SendMessage()
        sendMessage.enableMarkdown(true)
        sendMessage.chatId = chatId.toString()
        sendMessage.text = text
        sendMessage.replyMarkup = replyKeyboard

        log(chatId, text)

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    private fun sendMsg(chatId: Long, text: String) {
        val sendMessage = SendMessage()
        sendMessage.enableMarkdown(true)
        sendMessage.chatId = chatId.toString()
        sendMessage.text = text

        log(chatId, text)

        try {
            execute(sendMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    private fun updMsg(message: Message, text: String, replyKeyboard: InlineKeyboardMarkup) {
        val editMessage = EditMessageText()
                .setChatId(message.chatId)
                .setMessageId(message.messageId)
                .setText(text)
                .setReplyMarkup(replyKeyboard)

        try {
            execute(editMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
        }
    }

    private fun log(chatId: Long, botMessage: String) {
        println("\n ----------------------------")
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        println(dateFormat.format(date))

        println("Message for $chatId")
        println("Bot message: \n $botMessage")
    }

    private fun log(msg: Message) {
        println("\n ----------------------------")
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        println(dateFormat.format(date))

        println("Message from ${msg.chatId}")
        println("Text:\n ${msg.text}")
    }

    private fun log(callbackQuery: CallbackQuery) {
        println("\n ----------------------------")
        val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        val date = Date()
        println(dateFormat.format(date))

        println("Message from ${callbackQuery.message.chatId}")
        println("Text:\n ${callbackQuery.message.text}")
        println("Data:\n ${callbackQuery.data}")
    }
}