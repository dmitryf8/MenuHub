package com.mcoolapp.menuhub.model.user

import androidx.room.*
import com.google.firebase.Timestamp
import com.mcoolapp.menuhub.utils.Converters
import kotlin.collections.HashMap


@Entity

class User () {

    @TypeConverters(Converters::class)

    @PrimaryKey var id: String = ""

    @ColumnInfo(name = "userName") var userName: String = ""
    @ColumnInfo(name = "name") var name: String = ""
    @ColumnInfo(name = "aboutUser") var aboutUser: String = ""
    @ColumnInfo(name = "organizationName") var organizationName: String = ""
    @ColumnInfo(name = "contactCardID") var contactCardID: String = "" // id контактов
    @ColumnInfo(name = "eMail") var eMail: String = ""
    @ColumnInfo(name = "userPhotoId") var userPhotoId: String = ""
    @ColumnInfo(name = "fcmToken")var fcmToken: String = ""
    @ColumnInfo(name = "confidentialID")var confidentialID: String = ""

    @ColumnInfo(name = "subscribersIDList") var subscribersIDList: List<String>  = listOf("0") //список подписчиков
    @ColumnInfo(name = "observableIDList") var observableIDList: List<String> = listOf("0")//список наблюдаемых

    @ColumnInfo(name = "userPostIDList") var userPostIDList: List<String> = listOf("0") //список постов пользователя
    @ColumnInfo(name = "userStateVersion") var userStateVersion: Long = 1//версия состояния пользователя, меняется при изменении списка постов, подписчиков, имени и т.д.
    @ColumnInfo(name = "userMenuID") var userMenuID: String = ""//меню для продавца
    @ColumnInfo(name = "organizationDataID")var organizationDataID: String = ""



    @ColumnInfo(name = "userCommunicationDataID") var userCommunicationDataID: String = ""//отзывы и комментарии пользователя
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "createdTimestamp") var createdTimeStamp: Timestamp = Timestamp.now()
    @TypeConverters(Converters::class)
    @ColumnInfo(name = "updatedTimestamp") var updatedTimeStamp: Timestamp = Timestamp.now()

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
        name = hMap["name"] as String
        userName = hMap["userName"] as String
        userPhotoId = hMap["userPhotoId"] as String
        aboutUser = hMap["aboutUser"] as String
        eMail = hMap["eMail"] as String
        organizationName = hMap["organizationName"] as String
        subscribersIDList = hMap["subscribersIDList"] as List<String>
        observableIDList = hMap["observableIDList"] as List<String>
        userPostIDList = hMap["userPostIDList"] as List<String>
        userStateVersion = hMap["userStateVersion"] as Long
        userMenuID = hMap["userMenuID"] as String
        userCommunicationDataID = hMap["userCommunicationDataID"] as String
        contactCardID = hMap["contactCardID"] as String
        fcmToken = hMap["fcmToken"] as String
        createdTimeStamp = hMap["createdTimestamp"] as Timestamp
        updatedTimeStamp = hMap["updatedTimestamp"] as Timestamp
        confidentialID = hMap["confidentialID"] as String
        organizationDataID = hMap["organizationDataID"] as String
    }

    fun toHashMap(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["name"] = name
        hMap["userName"] = userName
        hMap["aboutUser"] = aboutUser
        hMap["eMail"] = eMail
        hMap["organizationName"] = organizationName
        hMap["subscribersIDList"] = subscribersIDList
        hMap["observableIDList"] = observableIDList
        hMap["userPostIDList"] = userPostIDList
        hMap["userStateVersion"] = userStateVersion
        hMap["userMenuID"] = userMenuID

        hMap["userCommunicationDataID"] = userCommunicationDataID
        hMap["contactCardID"] = contactCardID
        hMap["createdTimestamp"] = createdTimeStamp
        hMap["updatedTimestamp"] = updatedTimeStamp
        hMap["userPhotoId"] = userPhotoId
        hMap["fcmToken"] = fcmToken
        hMap["confidentialID"] =  confidentialID
        hMap["organizationDataID"] = organizationDataID
        return hMap
    }

    fun toHashMapForCreate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["name"] = name
        hMap["userName"] = userName
        hMap["aboutUser"] = aboutUser
        hMap["eMail"] = eMail
        hMap["organizationName"] = organizationName
        hMap["subscribersIDList"] = subscribersIDList
        hMap["observableIDList"] = observableIDList
        hMap["userPostIDList"] = userPostIDList
        hMap["userStateVersion"] = userStateVersion
        hMap["userMenuID"] = userMenuID

        hMap["userCommunicationDataID"] = userCommunicationDataID
        hMap["contactCardID"] = contactCardID
        hMap["createdTimestamp"] = Timestamp.now()
        hMap["updatedTimestamp"] = updatedTimeStamp
        hMap["userPhotoId"] = userPhotoId
        hMap["fcmToken"] = fcmToken

        hMap["confidentialID"] =  confidentialID
        hMap["organizationDataID"] = organizationDataID

        return hMap
    }

    fun toHashMapForUpdate(): HashMap<String, Any>{
        val hMap = HashMap<String,Any>()

        hMap["id"] = id
        hMap["name"] = name
        hMap["userName"] = userName
        hMap["aboutUser"] = aboutUser
        hMap["eMail"] = eMail
        hMap["organizationName"] = organizationName
        hMap["subscribersIDList"] = subscribersIDList
        hMap["observableIDList"] = observableIDList
        hMap["userPostIDList"] = userPostIDList
        hMap["userStateVersion"] = userStateVersion
        hMap["userMenuID"] = userMenuID

        hMap["userCommunicationDataID"] = userCommunicationDataID
        hMap["contactCardID"] = contactCardID
        hMap["createdTimestamp"] = createdTimeStamp
        hMap["updatedTimestamp"] = Timestamp.now()
        hMap["userPhotoId"] = userPhotoId
        hMap["fcmToken"] = fcmToken
        hMap["confidentialID"] =  confidentialID
        hMap["organizationDataID"] = organizationDataID

        return hMap
    }
}