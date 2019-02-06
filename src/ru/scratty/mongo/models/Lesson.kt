package ru.scratty.mongo.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import ru.scratty.utils.Config
import java.util.*

data class Lesson(@BsonId var id: ObjectId = ObjectId(),
                  var name: String = "",
                  var weeks: ArrayList<Int> = ArrayList(),
                  var typeWeek: Int = 0,
                  var dayNumber: Int = 0,
                  var number: Int = 0,
                  var audience: String = "",
                  var type: String = "") {

    fun check(calendar: Calendar, number: Int): Boolean {
        if (number == this.number && calendar.get(Calendar.DAY_OF_WEEK) == dayNumber) {
            if (weeks.size == 0) {
                if ((calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) % 2 == typeWeek % 2) {
                    return true
                }
            } else {
                return weeks.any { (calendar.get(Calendar.WEEK_OF_YEAR) - Config.WEEKS_SHIFT) == it }
            }
        }
        return false
    }

}