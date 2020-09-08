package com.mcoolapp.menuhub.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
class Table() {
    @PrimaryKey var id: String = ""
    @ColumnInfo(name = "tableName") var tableName: String = ""
    @ColumnInfo(name = "menuID") var menuID: String = ""
    @ColumnInfo(name = "createTimestamp")var createTimestamp: String = ""
    @ColumnInfo(name = "updateTimestamp")var updateTimestamp: String = ""
    @ColumnInfo(name = "ownerId") var ownerId: String? = ""
    @ColumnInfo(name = "qrCodeImageId") var qrCodeImageId: String = ""

    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        tableName = hMap["tableName"] as String
        menuID = hMap["menuID"] as String
        createTimestamp = hMap["createTimestamp"] as String
        updateTimestamp = hMap["updateTimestamp"] as String
        ownerId = hMap["ownerId"] as String
        qrCodeImageId = hMap["qrCodeImageId"] as String
    }

    fun toHashMapForCreate(): HashMap<String, Any> {
        val hMap = HashMap<String, Any>()

        hMap["id"] = id
        hMap["tableName"] = tableName
        hMap["menuID"] = menuID
        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = updateTimestamp
        hMap["ownerId"] = ownerId!!
        hMap["qrCodeImageId"] = qrCodeImageId

        return  hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any> {
        val hMap = HashMap<String, Any>()

        hMap["id"] = id
        hMap["tableName"] = tableName
        hMap["menuID"] = menuID
        hMap["createTimestamp"] = createTimestamp
        hMap["updateTimestamp"] = updateTimestamp
        hMap["ownerId"] = ownerId!!
        hMap["qrCodeImageId"] = qrCodeImageId

        return  hMap
    }
}