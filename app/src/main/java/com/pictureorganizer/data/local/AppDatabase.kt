package com.pictureorganizer.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pictureorganizer.data.local.dao.ImageDao
import com.pictureorganizer.data.local.entity.ImageEntity

@Database(
    entities = [ImageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao

    companion object {
        private const val DATABASE_NAME = "picture_organizer.db"

        fun build(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}
