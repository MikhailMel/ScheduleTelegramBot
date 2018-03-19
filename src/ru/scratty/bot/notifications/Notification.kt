package ru.scratty.bot.notifications

import java.util.*

abstract class Notification(val time: Time) {

    fun start() {
        Timer().schedule(object: TimerTask() {

            override fun run() {
                if (time.check(GregorianCalendar())) {
                    execute()
                }
            }

        }, 0, 5000)
    }

    abstract fun execute()
}

