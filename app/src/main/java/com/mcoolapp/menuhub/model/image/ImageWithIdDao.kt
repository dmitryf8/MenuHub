package com.mcoolapp.menuhub.model.image

import androidx.room.*

@Dao
interface ImageWithIdDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(imageWithId: ImageWithId)

    @Update
    fun update(imageWithId: ImageWithId)

    @Query("SELECT * FROM imagewithid WHERE id = :id")
    fun getImage(id: String): ImageWithId

    @Query("SELECT * FROM imagewithid WHERE EXISTS (SELECT * FROM imagewithid WHERE id = :id)")
    fun isExist(id: String): Boolean
}