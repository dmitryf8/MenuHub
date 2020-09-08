package com.mcoolapp.menuhub.model.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {

    @Insert
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * FROM 'user' WHERE id IS :id")
    fun getUser(id: String): User


    @Query("SELECT * FROM 'user' WHERE EXISTS (SELECT * FROM 'user' WHERE id IS :id)")
    fun isExist(id: String): Boolean
}
