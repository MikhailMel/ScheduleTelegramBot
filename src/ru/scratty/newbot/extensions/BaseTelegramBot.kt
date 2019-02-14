package ru.scratty.newbot.extensions

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import ru.scratty.mongo.models.User

abstract class BaseTelegramBot(
        val name: String,
        val token: String): TelegramLongPollingBot() {

    companion object {
        const val COMMAND_CHAR = '/'
    }

    private val simpleCommands = HashMap<Regex, ISimpleCommand>()
    private val callbackCommands = HashMap<Regex, ICallbackCommand>()

    private val events = HashMap<Regex, (bot: BaseTelegramBot, chatId: Long) -> BaseEvent>()
    private val activeEvents = HashMap<Long, BaseEvent>()

    override fun getBotUsername() = name

    override fun getBotToken() = token

    override fun onUpdateReceived(update: Update?) {
        if (update!!.hasMessage() && update.message!!.hasText()) {
            if (!handleCommand(update.message)) {
                incorrectCommand(update.message)
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackCommand(update.callbackQuery)
        }
    }

    fun stopEvent(user: User) {
        activeEvents.remove(user.id)
    }

    protected fun registerCommand(command: ICommandWithPattern) {
        if (command is ISimpleCommand) {
            simpleCommands[command.commandPattern] = command
        }
        if (command is ICallbackCommand) {
            callbackCommands[command.commandPattern] = command
        }
    }

    protected fun registerEvent(startEventRegex: Regex, eventCreator: (bot: BaseTelegramBot, chatId: Long) -> BaseEvent) {
        events[startEventRegex] = eventCreator
    }

    private fun handleCommand(message: Message): Boolean {
        if (message.hasText()) {
            var command = if (message.text.startsWith(COMMAND_CHAR))
                message.text.substring(1)
            else
                message.text
            command = command.toLowerCase().trim()

            val activeEvent = activeEvents
                    .filter { it.key == message.chatId }
                    .map { it.value }
            if (activeEvent.isNotEmpty()) {
                if (activeEvent[0].checkExit(message)) {
                    activeEvents.remove(message.chatId)
                    sendMessage(this, message.chatId, "Вы возвращены в меню", menuKeyboard())
                } else {
                    activeEvent[0].handleMessage(message)
                }
                return true
            }

            val event = events
                    .filter { it.key.containsMatchIn(command) }
                    .map { it.value }
            if (event.isNotEmpty()) {
                activeEvents[message.chatId] = event[0](this, message.chatId)
                activeEvents[message.chatId]!!.handleMessage(message)
                return true
            }

            val cmd = simpleCommands
                    .filter { it.key.containsMatchIn(command) }
                    .map { it.value }
            if (cmd.isNotEmpty()) {
                cmd[0].handleMessage(this, message)
                return true
            }
        }
        return false
    }

    private fun handleCallbackCommand(callbackQuery: CallbackQuery) {
        val command = callbackQuery.data

        val activeEvent = activeEvents
                .filter { it.key == callbackQuery.message.chatId }
                .map { it.value }
        if (activeEvent.isNotEmpty()) {
            if (activeEvent[0].checkExit(callbackQuery)) {
                activeEvents.remove(callbackQuery.message.chatId)
                sendMessage(this, callbackQuery.message.chatId, "Вы возвращены в меню", menuKeyboard())
            } else {
                activeEvent[0].handleCallbackQuery(callbackQuery)
            }
            return
        }

        val event = events
                .filter { it.key.containsMatchIn(command) }
                .map { it.value }
        if (event.isNotEmpty()) {
            activeEvents[callbackQuery.message.chatId] = event[0](this, callbackQuery.message.chatId)
            activeEvents[callbackQuery.message.chatId]!!.handleCallbackQuery(callbackQuery)
            return
        }

        val cmd = callbackCommands
                .filter { it.key.containsMatchIn(command) }
                .map { it.value }
        if (cmd.isNotEmpty()) {
            cmd[0].handleCallbackMessage(this, callbackQuery)
            return
        }
    }

    abstract fun incorrectCommand(message: Message)

    abstract fun menuKeyboard(): ReplyKeyboard
}