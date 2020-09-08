package com.mcoolapp.menuhub.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.ServerValue

@Entity
class CommunicationPartMenuItem(){
    @PrimaryKey var id: String = ""
    @ColumnInfo(name = "commentIdList" ) var commentIdList: List<String>? = listOf()
    @ColumnInfo(name = "ratingList" ) var ratingList: List<Int>? = listOf()
    @ColumnInfo(name = "likedUserIdList" ) var likedUserIdList: List<String>? = listOf()


    @ColumnInfo(name = "createTimestamp")var createTimestamp: String = ""
    @ColumnInfo(name = "updateTimestamp")var updateTimestamp: String = ""

    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        likedUserIdList = hMap["likedUserIdList"] as List<String>
        commentIdList = hMap["commentIdList"] as List<String>
        ratingList = hMap["ratingList"] as List<Int>
        createTimestamp = hMap["createTimestamp"] as String
        updateTimestamp = hMap["updateTimestamp"] as String
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["likedUserIdList"] = likedUserIdList!!
        hMap["commentIdList"] = commentIdList!!
        hMap["ratingList"] = ratingList!!

        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = ServerValue.TIMESTAMP

        return hMap
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["likedUserIdList"] = likedUserIdList!!
        hMap["commentIdList"] = commentIdList!!
        hMap["ratingList"] = ratingList!!

        hMap["createTimestamp"] = ServerValue.TIMESTAMP
        hMap["updateTimestamp"] = updateTimestamp

        return hMap
    }
}