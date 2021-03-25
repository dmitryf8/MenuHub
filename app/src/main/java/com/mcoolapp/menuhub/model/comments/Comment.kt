package com.mcoolapp.menuhub.model.comments

import androidx.room.Ignore
import com.google.firebase.Timestamp
import java.util.HashMap

class Comment() {
    var id: String = ""
    var commentAuthorID: String = ""
    var postID: String = ""
    var commentAuthorName: String = ""
    var createTimestamp: Timestamp = Timestamp.now()
    var updateTimestamp: Timestamp = Timestamp.now()
    var commentText: String = ""

    var likedIDList: List<String> = listOf()
    var commentIDList: List<String> = listOf()

    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        postID = hMap["postID"] as String
        commentAuthorName = hMap["commentAuthorName"] as String
        commentAuthorID = hMap["commentAuthorID"] as String
        createTimestamp = hMap["createTimestamp"] as Timestamp
        updateTimestamp = hMap["updateTimestamp"] as Timestamp
        commentText = hMap["commentText"] as String
        likedIDList = hMap["likedIDList"] as List<String>
        commentIDList = hMap["commentIDList"] as List<String>
    }

    fun toHashMapForCreate(): HashMap<String, Any> {
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["commentAuthorID"] = commentAuthorID
        hMap["commentAuthorName"] = commentAuthorName
        hMap["postID"] = postID
        hMap["createTimestamp"] = Timestamp.now()
        hMap["updateTimestamp"] = Timestamp.now()
        hMap["commentText"] = commentText
        hMap["likedIDList"] = likedIDList
        hMap["commentIDList"] = commentIDList

        return hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any> {
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["commentAuthorID"] = commentAuthorID
        hMap["commentAuthorName"] = commentAuthorName
        hMap["postID"] = postID
        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = Timestamp.now()
        hMap["commentText"] = commentText
        hMap["likedIDList"] = likedIDList
        hMap["commentIDList"] = commentIDList

        return hMap
    }
}