package com.example.soundmixer

import android.app.Application
import androidx.room.Room
import com.example.soundmixer.data_base.AppDatabase

open class MyApplication: Application() {
    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

}