package com.mcoolapp.menuhub.model.image

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ImageWithId () {

    @PrimaryKey
    public var id: String = ""
    @ColumnInfo(name = "data", typeAffinity = ColumnInfo.BLOB)
    public var data: ByteArray = ByteArray(0)
}