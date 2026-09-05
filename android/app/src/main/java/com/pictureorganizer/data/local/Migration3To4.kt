package com.pictureorganizer.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** F11：images 增列 originalName（可空；历史行保持 null）。 */
val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE `images` ADD COLUMN `originalName` TEXT")
        }
    }
