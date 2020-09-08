package com.mcoolapp.menuhub.model.menu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface MenuItemDao {

    @Insert
    fun insert(menuItem: MenuItem)

    @Update
    fun update(menuItem: MenuItem)

    @Query("SELECT * FROM menuitem WHERE id IS :id")
    fun getMenuItem(id: String): MenuItem

    @Query("SELECT * FROM menuitem WHERE EXISTS (SELECT * FROM menuitem WHERE id IS :id)")
    fun isExist(id: String): Boolean
}