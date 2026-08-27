package com.pictureorganizer

import android.app.Application
import com.pictureorganizer.data.local.AppDatabase

class PictureOrganizerApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.build(this) }
}
