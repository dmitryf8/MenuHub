package com.mcoolapp.menuhub.model.menu

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.firebase.database.ServerValue
import com.mcoolapp.menuhub.model.image.ImageWithBucket

@Entity
class MenuItem() {
    @PrimaryKey
    var id: String = ""
    @ColumnInfo(name = "itemName" ) var itemName: String? = ""
    @ColumnInfo(name = "ingredients" ) var ingredients: List<String>? = listOf()
    @ColumnInfo(name = "aboutItem") var aboutItem: String? = ""
    @ColumnInfo(name = "weight") var weight: Double? = 0.0
    @ColumnInfo(name = "weightUnit") var weightUnit: String? = ""
    @ColumnInfo(name = "price") var price: Double? = 0.0
    @ColumnInfo(name = "priceUnit") var priceUnit: String? = ""
    @ColumnInfo(name = "photoId" ) var photoId: String? = ""
    @ColumnInfo(name = "communicationPartId") var communicationPartId: String? = ""
    @ColumnInfo(name = "sectionName" ) var sectionName: String? = ""

    @ColumnInfo(name = "version" )var version: String? = "1"

    @ColumnInfo(name = "createTimestamp")var createTimestamp: String? = ""
    @ColumnInfo(name = "updateTimestamp")var updateTimestamp: String? = ""
    @ColumnInfo(name = "ownerId") var ownerId: String? = ""
    @ColumnInfo(name = "ownerName") var ownerName: String? = ""
    @ColumnInfo(name = "visible")var visible: Boolean? = true

    @Ignore
    @Suppress("UNCHECKED_CAST")
    constructor(hMap: Map<String?, Any?>): this() {
        id = hMap["id"] as String
        itemName = hMap["itemName"] as String
        ingredients = hMap["ingredients"] as List<String>
        aboutItem = hMap["aboutItem"] as String
        weight = hMap["weight"] as Double
        weightUnit = hMap["weightUnit"] as String
        price = hMap["price"] as Double
        priceUnit = hMap["priceUnit"] as String
        photoId = hMap["photoId"] as String
        communicationPartId = hMap["communicationPartId"] as String
        sectionName = hMap["sectionName"] as String
        version = hMap["version"] as String
        createTimestamp = hMap["createTimestamp"] as String
        updateTimestamp = hMap["updateTimestamp"] as String
        ownerId = hMap["ownerId"] as String
        ownerName = hMap["ownerName"] as String
        visible = hMap["visible"] as Boolean
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["itemName"] = itemName!!
        hMap["ingredients"] = ingredients!!
        hMap["aboutItem"] = aboutItem!!
        hMap["weight"] = weight!!
        hMap["weightUnit"] = weightUnit!!
        hMap["price"] = price!!
        hMap["priceUnit"] = priceUnit!!
        hMap["photoId"] = photoId!!
        hMap["communicationPartId"] = communicationPartId!!
        hMap["sectionName"] = sectionName!!
        hMap["version"] = version!!
        hMap["ownerId"] = ownerId!!
        hMap["ownerName"] = ownerName!!
        hMap["createTimestamp"] = createTimestamp!!
        hMap["updateTimestamp"] = updateTimestamp!!
        hMap["visible"] = visible!!

        return hMap
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["itemName"] = itemName!!
        hMap["ingredients"] = ingredients!!
        hMap["aboutItem"] = aboutItem!!
        hMap["weight"] = weight!!
        hMap["weightUnit"] = weightUnit!!
        hMap["price"] = price!!
        hMap["priceUnit"] = priceUnit!!
        hMap["photoId"] = photoId!!
        hMap["communicationPartId"] = communicationPartId!!
        hMap["sectionName"] = sectionName!!
        hMap["version"] = version!!
        hMap["ownerId"] = ownerId!!
        hMap["ownerName"] = ownerName!!
        hMap["createTimestamp"] = createTimestamp!!
        hMap["updateTimestamp"] = updateTimestamp!!
        hMap["visible"] = visible!!

        return hMap
    }

    fun getImageWithBucket(): ImageWithBucket {
        return ImageWithBucket(photoId, "bucket" + ownerId)
    }
}