package com.mcoolapp.menuhub.model.menu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MenuDao {

    @Insert
    fun insert(menu: Menu)

    @Update
    fun update(menu: Menu)

    @Query("SELECT * FROM menu WHERE id IS :id")
    fun getMenu(id: String): Menu

    @Query("SELECT * FROM menu WHERE EXISTS (SELECT * FROM menu WHERE id IS :id)")
    fun isExist(id: String): Boolean
}