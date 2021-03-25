package com.mcoolapp.menuhub.model.comments

import androidx.room.Ignore
import com.google.firebase.Timestamp
import java.util.*

class Review() {
    var id: String = ""
    var menuItemID: String = ""
    var menuItemOwnerID: String = ""
    var reviewAuthorID: String = ""
    var reviewAuthorName: String = ""
    var createTimestamp: Timestamp = Timestamp.now()
    var updateTimestamp: Timestamp = Timestamp.now()
    var reviewText: String = ""
    var ratingScore: Int = 0

    private var likedIDList: List<String> = listOf()
    private var commentIDList: List<String> = listOf()

    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        menuItemID = hMap["menuItemID"] as String
        menuItemOwnerID = hMap["menuItemOwnerID"] as String
        createTimestamp = hMap["createTimestamp"] as Timestamp
        updateTimestamp = hMap["updateTimestamp"] as Timestamp
        reviewAuthorID = hMap["reviewAuthorID"] as String
        reviewAuthorName = hMap["reviewAuthorName"] as String
        reviewText = hMap["reviewText"] as String
        ratingScore = hMap["ratingScore"] as Int
        likedIDList = hMap["likedIDList"] as List<String>
        commentIDList = hMap["commentIDList"] as List<String>
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["menuItemID"] = menuItemID
        hMap["menuItemOwnerID"] = menuItemOwnerID
        hMap["createTimestamp"] = Timestamp.now()
        hMap["updateTimestamp"] = Timestamp.now()
        hMap["reviewAuthorID"] = reviewAuthorID
        hMap["reviewAuthorName"] = reviewAuthorName
        hMap["reviewText"] = reviewText
        hMap["ratingScore"] = ratingScore
        hMap["likedIDList"] = likedIDList
        hMap["commentIDList"] = commentIDList

        return hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["menuItemID"] = menuItemID
        hMap["menuItemOwnerID"] = menuItemOwnerID
        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = Timestamp.now()
        hMap["reviewAuthorID"] = reviewAuthorID
        hMap["reviewAuthorName"] = reviewAuthorName
        hMap["reviewText"] = reviewText
        hMap["ratingScore"] = ratingScore
        hMap["likedIDList"] = likedIDList
        hMap["commentIDList"] = commentIDList

        return hMap
    }



}