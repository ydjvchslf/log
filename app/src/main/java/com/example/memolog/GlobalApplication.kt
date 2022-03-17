package com.example.memolog

import android.app.Application
import androidx.room.Room
import com.example.memolog.repository.db.AppDatabase

class GlobalApplication: Application() {
    companion object{
        lateinit var appInstance: GlobalApplication
            private set

        lateinit var databaseInstance: AppDatabase
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        databaseInstance = Room.databaseBuilder(
            appInstance.applicationContext,
            AppDatabase::class.java, "exampleApp.db"
        )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }
}