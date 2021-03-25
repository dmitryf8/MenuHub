package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.user

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.firebase.Timestamp
import com.mcoolapp.menuhub.utils.Converters

class UserConfidentialInfo() {
    @TypeConverters(Converters::class)

    @PrimaryKey
    var id: String = ""

    @ColumnInfo(name = "ownerID") var ownerID: String = ""
    @ColumnInfo(name = "chatIDList") var chatIDList: List<String> = listOf()//список чатов

    init {

    }


    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        System.out.println(hMap)
        id = hMap["id"] as String
        ownerID = hMap["ownerID"] as String

    }

    fun toHashMap(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["ownerID"] = ownerID
        hMap["chatIDList"] = chatIDList

        return hMap
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["ownerID"] = ownerID
        hMap["chatIDList"] = chatIDList

        return hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["ownerID"] = ownerID
        hMap["chatIDList"] = chatIDList


        return hMap
    }
}