package com.mcoolapp.menuhub.model.chat.com.mcoolapp.menuhub.model.user

import androidx.room.ColumnInfo
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mcoolapp.menuhub.utils.Converters

class OrganizationData() {
    @TypeConverters(Converters::class)

    @PrimaryKey
    var id: String = ""

    @ColumnInfo(name = "ownerID") var ownerID: String = ""

    init {

    }

    @Ignore
    constructor(id:String): this(){
        this.id = id
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

        return hMap
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["ownerID"] = ownerID

        return hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["ownerID"] = ownerID


        return hMap
    }
}