package com.mcoolapp.menuhub.utils

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import com.google.gson.Gson

class Converters {
    companion object {
        @TypeConverter
        @JvmStatic
        fun stringListToJson(value: List<String>?): String {

            return Gson().toJson(value)
        }

        @TypeConverter
        @JvmStatic
        fun timestampToLong(value: Timestamp): Long? {
            return value.seconds
        }

        @TypeConverter
        @JvmStatic
        fun longToTimestamp(value: Long): Timestamp? {
            return Timestamp(value, 0)
        }

        @TypeConverter
        @JvmStatic
        fun jsonToStringList(value: String): List<String>? {

            val objects = Gson().fromJson(value, Array<String>::class.java) as Array<String>
            val list = objects.toList()
            return list
        }

        @TypeConverter
        @JvmStatic
        fun intListToJson(value: List<Int>?): String {

            return Gson().toJson(value)
        }

        @TypeConverter
        @JvmStatic
        fun jsonToIntList(value: String): List<Int>? {

            val objects = Gson().fromJson(value, Array<Int>::class.java) as Array<Int>
            val list = objects.toList()
            return list
        }
    }
}