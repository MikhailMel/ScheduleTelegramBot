package ru.scratty.bot.notifications

import java.util.*

data class Time(val hour: Int,
                val minute: Int) {

    private var sent = false

    init {
        val cal = GregorianCalendar()
        val h = cal.get(Calendar.HOUR_OF_DAY)
        val m = cal.get(Calendar.MINUTE)

        sent = (hour == h && minute <= m || hour < h )
    }

    fun check(calendar: Calendar): Boolean {
        val h = calendar.get(Calendar.HOUR_OF_DAY)
        val m = calendar.get(Calendar.MINUTE)

        if (!sent) {
            if (hour == h && (m - minute in -1..1)) {
                sent = true
                return true
            }
        } else if (hour < h) {
            sent = false
        }
        return false
    }

}