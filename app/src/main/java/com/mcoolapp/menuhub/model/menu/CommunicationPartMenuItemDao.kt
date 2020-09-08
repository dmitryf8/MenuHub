package com.mcoolapp.menuhub.model.menu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mcoolapp.menuhub.model.user.User

@Dao
interface CommunicationPartMenuItemDao {

    @Insert
    fun insert(communicationPartMenuItem: CommunicationPartMenuItem)

    @Update
    fun update(communicationPartMenuItem: CommunicationPartMenuItem)

    @Query("SELECT * FROM  'communicationpartmenuitem' WHERE id IS :id")
    fun getCommPartMenuItem(id: String): CommunicationPartMenuItem

    @Query("SELECT * FROM 'communicationpartmenuitem' WHERE EXISTS (SELECT * FROM 'communicationpartmenuitem' WHERE id IS :id)")
    fun isExist(id: String): Boolean
}