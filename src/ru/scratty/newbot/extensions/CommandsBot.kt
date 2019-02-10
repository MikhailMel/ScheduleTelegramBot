package ru.scratty.newbot.extensions

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

abstract class CommandsBot(
        val name: String,
        val token: String): TelegramLongPollingBot() {

    companion object {
        const val COMMAND_CHAR = '/'
    }

    private val simpleCommands = HashMap<Regex, ISimpleCommand>()
    private val callbackCommands = HashMap<Regex, ICallbackCommand>()

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

    protected fun registerCommand(command: ICommandWithPattern) {
        if (command is ISimpleCommand) {
            simpleCommands[command.commandPattern] = command
        }
        if (command is ICallbackCommand) {
            callbackCommands[command.commandPattern] = command
        }
    }

    private fun handleCommand(message: Message): Boolean {
        if (message.hasText()) {
            var command = if (message.text.startsWith(COMMAND_CHAR))
                message.text.substring(1)
            else
                message.text
            command = command.toLowerCase().trim()

            for ((key, value) in simpleCommands) {
                if (key.containsMatchIn(command)) {
                    value.handleMessage(this, message)

                    return true
                }
            }
        }
        return false
    }

    private fun handleCallbackCommand(callbackQuery: CallbackQuery) {
        val command = callbackQuery.data

        for ((key, value) in callbackCommands) {
            if (key.containsMatchIn(command)) {
                value.handleCallbackMessage(this, callbackQuery)
                break
            }
        }
    }

    abstract fun incorrectCommand(message: Message)
}