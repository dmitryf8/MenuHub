package com.mcoolapp.menuhub.model.menu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TableDao {

    @Insert
    fun insert(table: Table)

    @Update
    fun update(table: Table)

    @Query("SELECT * FROM 'table' WHERE id IS :id")
    fun getTable(id: String): Table

    @Query("SELECT * FROM 'table' WHERE EXISTS (SELECT * FROM 'table' WHERE id IS :id)")
    fun isExist(id: String): Boolean
}