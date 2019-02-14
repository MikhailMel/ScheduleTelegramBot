package ru.scratty.mongo.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import java.util.*

data class Lesson(@BsonId var id: ObjectId = ObjectId(),
                  var name: String = "",
                  var weeks: ArrayList<Int> = ArrayList(),
                  var typeWeek: Int = 0,
                  var dayNumber: Int = 0,
                  var number: Int = 0,
                  var audience: String = "",
                  var type: String = "") {
}