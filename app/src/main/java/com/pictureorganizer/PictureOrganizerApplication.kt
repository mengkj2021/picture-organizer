package com.pictureorganizer

import android.app.Application
import com.pictureorganizer.data.local.AppDatabase
import com.pictureorganizer.data.repository.RoomImageRepository
import com.pictureorganizer.util.file.AppFileManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PictureOrganizerApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: AppDatabase by lazy { AppDatabase.build(this) }

    val fileManager: AppFileManager by lazy { AppFileManager(this) }

    val imageRepository: RoomImageRepository by lazy {
        RoomImageRepository(database.imageDao(), fileManager)
    }

    override fun onCreate() {
        super.onCreate()
        fileManager.ensureAllDirs()
        applicationScope.launch {
            imageRepository.migrateFlatPathsIfNeeded()
        }
    }
}
