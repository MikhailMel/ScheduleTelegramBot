package ru.scratty.newbot.extensions

abstract class Command(final override val commandPattern: Regex) : ICommandWithPattern {

    init {
        if (commandPattern.toString().isEmpty()) {
            throw IllegalArgumentException("")
        }
    }
}