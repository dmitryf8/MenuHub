package com.mcoolapp.menuhub.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Menu() {
    @PrimaryKey var id: String = ""

    @ColumnInfo(name = "menuItemsIdList") var menuItemsIdList: List<String> = listOf()
    @ColumnInfo(name = "createTimestamp")var createTimestamp: String = ""
    @ColumnInfo(name = "updateTimestamp")var updateTimestamp: String = ""
    @ColumnInfo(name = "version" )var version: String? = "1"
    @ColumnInfo(name = "ownerId") var ownerId: String? = ""
    @ColumnInfo(name = "sectionList") var sectionList: List<String>? = arrayListOf()
    @ColumnInfo(name = "tableIDList") var tableIDList: List<String> = listOf()

    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        menuItemsIdList = hMap["menuItemsIdList"] as List<String>
        createTimestamp = hMap["createTimestamp"] as String
        updateTimestamp = hMap["updateTimestamp"] as String
        version = hMap["version"] as String
        ownerId = hMap["ownerId"] as String
        sectionList = hMap["sectionList"] as List<String>
        tableIDList = hMap["tableIDList"] as List<String>
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["menuItemsIdList"] = menuItemsIdList
        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = updateTimestamp
        hMap["version"] = version!!
        hMap["ownerId"] = ownerId!!
        hMap["sectionList"] = sectionList!!
        hMap["tableIDList"] = tableIDList

        return hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["menuItemsIdList"] = menuItemsIdList
        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = updateTimestamp
        hMap["version"] = version!!
        hMap["ownerId"] = ownerId!!
        hMap["sectionList"] = sectionList!!
        hMap["tableIDList"] = tableIDList

        return hMap
    }


}