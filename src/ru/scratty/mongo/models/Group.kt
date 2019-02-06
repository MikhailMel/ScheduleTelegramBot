package ru.scratty.mongo.models

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Group(@BsonId var id: ObjectId = ObjectId(),
                 var authorId: Long = 0L,
                 var name: String = "",
                 var lessons: ArrayList<String> = ArrayList())