package ru.scratty.mongo.models

import org.bson.codecs.pojo.annotations.BsonId


data class User(@BsonId var id: Long = -1L,
                var name: String = "",
                var username: String = "",
                var groupId: String = "",
                var settings: String = "111")