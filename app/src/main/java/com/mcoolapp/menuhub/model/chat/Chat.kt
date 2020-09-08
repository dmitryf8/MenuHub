package com.mcoolapp.menuhub.model.chat

import androidx.room.Ignore

class Chat() {
    var id: String = ""
    var userIDList= listOf<String>()
    var messageIDList = listOf<String>()
    var lastMessageID: String = ""

    @Ignore
    constructor(id:String): this(){
        this.id = id
    }
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        userIDList = hMap["userIDList"] as List<String>
        messageIDList = hMap["messageIDList"] as List<String>
        lastMessageID = hMap["lastMessageID"] as String
    }

    fun toHashMap(): HashMap<String, Any> {
        val hMap = HashMap<String, Any>()
        hMap["id"] = id
        hMap["userIDList"] = userIDList
        hMap["messageIDList"] = messageIDList
        hMap["lastMessageID"] = lastMessageID
        return hMap
    }

}