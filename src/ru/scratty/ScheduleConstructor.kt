package ru.scratty

import ru.scratty.mongo.models.Lesson
import ru.scratty.utils.Config
import ru.scratty.utils.filterByCalendar
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*

class ScheduleConstructor(private val lessons: List<Lesson>) {

    fun getDaySchedule(number: Int): String {
        val calendar = GregorianCalendar.getInstance(Locale("ru", "RU"))
        calendar.set(DAY_OF_YEAR, number)

        val format = SimpleDateFormat("dd.MM.yy")

        val sb = StringBuilder()
        sb.append(getDayName(calendar.get(DAY_OF_WEEK))).append(" (${format.format(calendar.time)})").append(":\n")
        sb.append(getDaySchedule(calendar))

        return sb.toString()
    }

    fun getTodaySchedule(): String {
            val calendar = GregorianCalendar.getInstance(Locale("ru", "RU"))

            val sb = StringBuilder()
            sb.append("Сегодня:\n")
            sb.append(getDaySchedule(calendar))

            return sb.toString()
        }

    fun getTomorrowSchedule(): String {
            val calendar = GregorianCalendar.getInstance(Locale("ru", "RU"))
            calendar.add(Calendar.DAY_OF_YEAR, 1)

            val sb = StringBuilder()
            sb.append("Завтра:\n")
            sb.append(getDaySchedule(calendar))

            return sb.toString()
        }

    fun getWeekSchedule(increment: Int): String {
        val calendar = GregorianCalendar.getInstance(Locale("ru", "RU"))
        calendar.add(WEEK_OF_YEAR, increment)
        calendar.set(DAY_OF_WEEK, 2)

        val format = SimpleDateFormat("dd.MM.yy")

        val sb = StringBuilder()
        sb.append("Неделя ").append(calendar.get(WEEK_OF_YEAR) - Config.WEEKS_SHIFT).append("\n")
        for (i in 0..6) {
            val dateStr = format.format(calendar.time)
            sb.append(getDayName(calendar.get(DAY_OF_WEEK))).append(" ($dateStr)").append(":\n")
            sb.append(getDaySchedule(calendar))
            sb.append("\n")
            calendar.add(DAY_OF_WEEK, 1)
        }

        return sb.toString()
    }

    private fun getDaySchedule(calendar: Calendar): String {
        var sb = StringBuilder()
        var flag = true

        val filteredLessons = lessons.filterByCalendar(calendar)
        for (i in 1..6) {
            val lesson = filteredLessons.find { it.number == i }
            if (lesson != null) {
                sb.appendln(String.format("%d) %s (%s/%s)", i, lesson.name, lesson.type, lesson.audience))

                flag = false
            } else {
                sb.appendln(String.format("%d)", i))
            }
        }

        if (flag) {
            sb = StringBuilder("В этот день пар нет\n")
        }

        return sb.toString()
    }

    private fun getDayName(day: Int): String {
        return when (day) {
            MONDAY -> "Понедельник"
            TUESDAY -> "Вторник"
            WEDNESDAY -> "Среда"
            THURSDAY -> "Четверг"
            FRIDAY -> "Пятница"
            SATURDAY -> "Суббота"
            SUNDAY -> "Воскресенье"
            else -> ""
        }
    }
}