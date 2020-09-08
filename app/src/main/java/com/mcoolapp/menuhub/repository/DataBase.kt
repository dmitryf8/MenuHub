package com.mcoolapp.menuhub.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mcoolapp.menuhub.model.menu.Table
import com.mcoolapp.menuhub.model.menu.TableDao
import com.mcoolapp.menuhub.model.image.ImageWithId
import com.mcoolapp.menuhub.model.image.ImageWithIdDao
import com.mcoolapp.menuhub.model.menu.*

import com.mcoolapp.menuhub.model.user.User
import com.mcoolapp.menuhub.model.user.UserDao
import com.mcoolapp.menuhub.utils.Converters


@Database(entities = [User::class, ImageWithId::class, Menu::class, MenuItem::class, CommunicationPartMenuItem::class, Table::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DataBase: RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun imageWithIdDao(): ImageWithIdDao
    abstract fun menuDao(): MenuDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun communicationPartMenuItemDao(): CommunicationPartMenuItemDao
    abstract fun tableDao(): TableDao

    companion object {
        var INSTANCE: DataBase? = null

        fun getAppDataBase(context: Context): DataBase? {
            if (INSTANCE == null){
                synchronized(DataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, DataBase::class.java, "mh.db")
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }

}