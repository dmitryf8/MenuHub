package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.chat

import androidx.room.Entity
import androidx.room.Ignore
import com.google.firebase.Timestamp
import com.google.firebase.database.ServerValue
import com.google.firebase.database.core.ServerValues


class Message() {



    var id: String = ""
    var senderID = ""
    var userIDList = listOf<String>()
    var messageType: String = ""
    var messageContent: String = ""
    var messageStatus: String = ""
    var description: String = ""
    var timestamp: Timestamp = Timestamp.now()
    var senderName: String = ""

    @Ignore
    constructor(id:String): this(){
        this.id = id
    }
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        senderID = hMap["senderID"] as String
        senderName = hMap["senderName"] as String
        userIDList = hMap["userIDList"] as List<String>
        messageType = hMap["messageType"] as String
        messageContent = hMap["messageContent"] as String
        messageStatus = hMap["messageStatus"] as String
        description = hMap["description"] as String
        timestamp = hMap["timestamp"] as Timestamp
    }

    fun toHashMap(): HashMap<String, Any> {
        val hMap = HashMap<String, Any>()
        hMap["id"] = id
        hMap["senderID"] = senderID
        hMap["senderName"] = senderName
        hMap["userIDList"] = userIDList
        hMap["messageType"] = messageType
        hMap["messageContent"] = messageContent
        hMap["messageStatus"] = messageStatus
        hMap["description"] = description
        hMap["timestamp"] = timestamp
        return hMap
    }

    fun addUserListToList(uIDList: List<String>) {
        userIDList = userIDList.plus(uIDList)
    }
}